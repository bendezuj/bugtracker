package com.example.bugtracker.services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

import com.example.bugtracker.facade.IssueTrackerFacade;

@Service
public class XlsxIssueTrackerService implements IssueTrackerFacade {

    @Value("${xlsx.file.path}")
    String xlsxFilePath;

    @Value("${id.file.path}")
    String idFilePath;

    @Override
    public String createIssue(String parentIssueId, String description, String link) throws IOException {
        Path xlsxPath = Paths.get(xlsxFilePath);

        // Ensure the XLSX file exists
        if (Files.notExists(xlsxPath)) {
            createNewXlsxFile(xlsxPath);
        }

        String newId = generateNewId();

        // Open the existing XLSX file for reading
        try (FileInputStream fis = new FileInputStream(xlsxFilePath);
            Workbook workbook = new XSSFWorkbook(fis)) {

            // Get the first sheet
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                sheet = workbook.createSheet("Issues");
            }

            // Find the next available row
            int rowCount = sheet.getLastRowNum() + 1;
            Row row = sheet.createRow(rowCount);

            // Populate the row with issue data
            row.createCell(0).setCellValue(newId);
            row.createCell(1).setCellValue(description);
            row.createCell(2).setCellValue(parentIssueId);
            row.createCell(3).setCellValue("Open");
            row.createCell(4).setCellValue(LocalDateTime.now().toString());
            row.createCell(5).setCellValue(link);

            // Write the updated workbook back to the file
            try (FileOutputStream fos = new FileOutputStream(xlsxFilePath)) {
                workbook.write(fos);
            }
        }

        return newId;
    }


    @Override
    public void closeIssue(String issueId) throws IOException {
        Path xlsxPath = Paths.get(xlsxFilePath);

        if (Files.notExists(xlsxPath)) {
            throw new FileNotFoundException("The XLSX file does not exist.");
        }

        try (FileInputStream fis = new FileInputStream(xlsxFilePath);
            Workbook workbook = new XSSFWorkbook(fis);
            FileOutputStream fos = new FileOutputStream(xlsxFilePath)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                Cell idCell = row.getCell(0);
                if (idCell != null && idCell.getStringCellValue().equals(issueId)) {
                    row.getCell(3).setCellValue("Closed");
                    break;
                }
            }

            workbook.write(fos);
        }
    }

    private void createNewXlsxFile(Path path) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
            FileOutputStream fos = new FileOutputStream(path.toFile())) {
            Sheet sheet = workbook.createSheet("Issues");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Description");
            header.createCell(2).setCellValue("ParentId");
            header.createCell(3).setCellValue("Status");
            header.createCell(4).setCellValue("CreationTimestamp");
            header.createCell(5).setCellValue("Link");

            workbook.write(fos);
        }
    }

    private String generateNewId() throws IOException {
        int lastId = 0;
        Path idPath = Paths.get(idFilePath);

        // Create file if it does not exist
        if (Files.notExists(idPath)) {
            Files.createFile(idPath);
        }

        if (Files.exists(idPath)) {
            try (BufferedReader reader = Files.newBufferedReader(idPath)) {
                String line = reader.readLine();
                if (line != null) {
                    lastId = Integer.parseInt(line);
                }
            }
        }

        int newId = lastId + 1;
        Files.write(idPath, Integer.toString(newId).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        return String.valueOf(newId);
    }
}

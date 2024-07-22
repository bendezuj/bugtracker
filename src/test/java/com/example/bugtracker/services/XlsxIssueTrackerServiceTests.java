package com.example.bugtracker.services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class XlsxIssueTrackerServiceTests {

    @InjectMocks
    private XlsxIssueTrackerService xlsxIssueTrackerService;

    private final String xlsxFilePath = "test_issues.xlsx";
    private final String idFilePath = "test_id_file.txt";

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        // Create test XLSX file
        try (Workbook workbook = new XSSFWorkbook();
            FileOutputStream fos = new FileOutputStream(xlsxFilePath)) {
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

        // Create test ID file
        Files.write(Paths.get(idFilePath), "0".getBytes());

        xlsxIssueTrackerService.xlsxFilePath = xlsxFilePath;
        xlsxIssueTrackerService.idFilePath = idFilePath;
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(xlsxFilePath));
        Files.deleteIfExists(Paths.get(idFilePath));
    }

    @Test
    void testCreateIssue() {
        assertTimeout(Duration.ofSeconds(10), () -> {
            // Arrange
            String parentIssueId = "";
            String description = "Test Issue Description";
            String link = "http://example.com/log";

            // Act
            String generatedId = xlsxIssueTrackerService.createIssue(parentIssueId, description, link);

            // Assert
            assertEquals("1", generatedId, "The new issue ID should be '1'");

            // Verify the file contents
            try (FileInputStream fis = new FileInputStream(xlsxFilePath);
                Workbook workbook = new XSSFWorkbook(fis)) {
                Sheet sheet = workbook.getSheetAt(0);
                Row row = sheet.getRow(1); // First row after header
                assertEquals("1", row.getCell(0).getStringCellValue(), "ID should match the generated ID.");
                assertEquals(description, row.getCell(1).getStringCellValue(), "Description should match.");
                assertEquals(parentIssueId, row.getCell(2).getStringCellValue(), "Parent ID should match.");
                assertEquals("Open", row.getCell(3).getStringCellValue(), "Status should be 'Open'.");
                // Additional assertions can be added for timestamp and link
            }
        });
    }

    @Test
    void testCloseIssue() {
        assertTimeout(Duration.ofSeconds(10), () -> {
            // Arrange
            String issueId = "1";

            // Act
            xlsxIssueTrackerService.createIssue("", "Test Issue Description", "http://example.com/log");
            xlsxIssueTrackerService.closeIssue(issueId);

            // Assert
            // Verify that the issue is marked as closed in the XLSX file
            try (FileInputStream fis = new FileInputStream(xlsxFilePath);
                Workbook workbook = new XSSFWorkbook(fis)) {
                Sheet sheet = workbook.getSheetAt(0);
                for (Row row : sheet) {
                    Cell idCell = row.getCell(0);
                    if (idCell != null && idCell.getStringCellValue().equals(issueId)) {
                        Cell statusCell = row.getCell(3);
                        assertEquals("Closed", statusCell.getStringCellValue(), "Issue should be marked as closed.");
                    }
                }
            }
        });
    }
}

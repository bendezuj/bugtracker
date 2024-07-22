package com.example.bugtracker.controllers;

import com.example.bugtracker.services.XlsxIssueTrackerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Scanner;

@Component
public class BugTrackerCLI implements CommandLineRunner {

    @Autowired
    private XlsxIssueTrackerService issueTrackerService;

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("Bug Tracker CLI");
            System.out.println("1. Create Issue");
            System.out.println("2. Close Issue");
            System.out.println("3. Exit");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    createIssue(scanner);
                    break;
                case "2":
                    closeIssue(scanner);
                    break;
                case "3":
                    running = false;
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }
        }

        scanner.close();
    }

    private void createIssue(Scanner scanner) {
        try {
            System.out.print("Enter parent issue ID (or leave blank if none): ");
            String parentIssueId = scanner.nextLine();

            System.out.print("Enter issue description: ");
            String description = scanner.nextLine();

            System.out.print("Enter log link: ");
            String link = scanner.nextLine();

            String newId = issueTrackerService.createIssue(parentIssueId, description, link);
            System.out.println("Issue created with ID: " + newId);
        } catch (IOException e) {
            System.err.println("Error creating issue: " + e.getMessage());
        }
    }

    private void closeIssue(Scanner scanner) {
        try {
            System.out.print("Enter issue ID to close: ");
            String issueId = scanner.nextLine();

            issueTrackerService.closeIssue(issueId);
            System.out.println("Issue " + issueId + " closed.");
        } catch (IOException e) {
            System.err.println("Error closing issue: " + e.getMessage());
        }
    }
}

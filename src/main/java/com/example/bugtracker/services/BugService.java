package com.example.bugtracker.services;

import com.example.bugtracker.facade.IssueTrackerFacade;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class BugService {

    private final IssueTrackerFacade issueTrackerFacade;

    public BugService(IssueTrackerFacade issueTrackerFacade) {
        this.issueTrackerFacade = issueTrackerFacade;
    }

    public String createIssue(String parentIssueId, String description, String link) {
        try {
            return issueTrackerFacade.createIssue(parentIssueId, description, link);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create issue", e);
        }
    }

    public void closeIssue(String issueId) {
        try {
            issueTrackerFacade.closeIssue(issueId);
        } catch (IOException e) {
            throw new RuntimeException("Failed to close issue", e);
        }
    }
}

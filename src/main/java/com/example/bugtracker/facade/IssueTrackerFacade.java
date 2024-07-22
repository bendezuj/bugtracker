package com.example.bugtracker.facade;

import java.io.IOException;

public interface IssueTrackerFacade {

    String createIssue(String parentIssueId, String description, String link) throws IOException;

    void closeIssue(String issueId) throws IOException;
}

package com.iumw.watm.database;

// remote class for managing tasks completion details from Firebase
public class CompletionsStructure
{
    private String completedAs;
    private String dateTime;
    private String from;
    private String type;
    private String taskTitle;
    private String expectedCompletion;
    private String fromExactNameEmail;

    // empty constructor
    public CompletionsStructure() {   }

    // mandatory constructor
    public CompletionsStructure(String completedAs, String dateTime, String from, String type, String taskTitle, String expectedCompletion, String fromExactNameEmail) {
        this.completedAs = completedAs;
        this.dateTime = dateTime;
        this.from = from;
        this.type = type;
        this.taskTitle = taskTitle;
        this.expectedCompletion = expectedCompletion;
        this.fromExactNameEmail = fromExactNameEmail;
    }

    // getters and setters
    public String getCompletedAs() {
        return completedAs;
    }

    public void setCompletedAs(String completedAs) {
        this.completedAs = completedAs;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getExpectedCompletion() {
        return expectedCompletion;
    }

    public void setExpectedCompletion(String expectedCompletion) {
        this.expectedCompletion = expectedCompletion;
    }

    public String getFromExactNameEmail() {
        return fromExactNameEmail;
    }

    public void setFromExactNameEmail(String fromExactNameEmail) {
        this.fromExactNameEmail = fromExactNameEmail;
    }
}

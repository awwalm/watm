package com.iumw.watm.database;

public class HelpRequestsStructure
{
    String
        from,
        fromExactNameEmail,
        type,
        taskTitle,
        requestedAs,
        dateTime;

    public HelpRequestsStructure() { }

    public HelpRequestsStructure(String from, String fromExactNameEmail, String type, String taskTitle, String requestedAs, String dateTime) {
        this.from = from;
        this.fromExactNameEmail = fromExactNameEmail;
        this.type = type;
        this.taskTitle = taskTitle;
        this.requestedAs = requestedAs;
        this.dateTime = dateTime;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFromExactNameEmail() {
        return fromExactNameEmail;
    }

    public void setFromExactNameEmail(String fromExactNameEmail) {
        this.fromExactNameEmail = fromExactNameEmail;
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

    public String getRequestedAs() {
        return requestedAs;
    }

    public void setRequestedAs(String requestedAs) {
        this.requestedAs = requestedAs;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}

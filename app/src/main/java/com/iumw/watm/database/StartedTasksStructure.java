package com.iumw.watm.database;

// remote class for managing completed tasks details from Firebase
public class StartedTasksStructure
{
    private String
            startedAs,
            dateTime,
            from,
            type,
            taskTitle,
            fromExactNameEmail;

    // empty constructor
    public StartedTasksStructure() {    }

    public StartedTasksStructure(String startedAs, String dateTime, String from, String type, String taskTitle, String fromExactNameEmail) {
        this.startedAs = startedAs;
        this.dateTime = dateTime;
        this.from = from;
        this.type = type;
        this.taskTitle = taskTitle;
        this.fromExactNameEmail = fromExactNameEmail;
    }

    // getters and setters

    public String getStartedAs() {
        return startedAs;
    }

    public void setStartedAs(String startedAs) {
        this.startedAs = startedAs;
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

    public String getFromExactNameEmail() {
        return fromExactNameEmail;
    }

    public void setFromExactNameEmail(String fromExactNameEmail) {
        this.fromExactNameEmail = fromExactNameEmail;
    }
}

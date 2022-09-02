package com.iumw.watm.database;

// remote class for managing tasks notifications details from Firebase
public class NotificationsStructure
{
    private String
            dateTime,
            from,
            type,
            taskTitle,
            fromExactNameEmail;

    // empty constructor
    public NotificationsStructure() {   }

    // main constructor
    public NotificationsStructure(String dateTime, String from, String type, String taskTitle, String fromExactNameEmail) {
        this.dateTime = dateTime;
        this.from = from;
        this.type = type;
        this.taskTitle = taskTitle;
        this.fromExactNameEmail = fromExactNameEmail;
    }

    // getters and setters
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

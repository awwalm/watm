package com.iumw.watm.database;

// skeleton class for structuring the Task database
public class TaskStructure {
    // properties
    String taskId;
    String creatorEmail;
    String creatorName;
    String targetUserName;
    String targetUserEmail;
    String collaboratorEmail;
    String taskTitle;
    String taskDescription;
    String startDate;
    String endDate;
    String startTime;
    String endTime;
    String planningDuration;
    String extraDuration;
    String rawDuration;
    String priority;
    String notation;
    String status;
    String exactDueTime;
    String exactDueDate;
    String isTargetUserAskingForHelp;
    String isCollaboratorAskingForHelp;

    // mandatory empty constructor
    public TaskStructure() {    }

    // main constructor with all 18 arguments to be sent to the database

    public TaskStructure(String taskId, String creatorEmail, String creatorName, String targetUserName, String targetUserEmail, String collaboratorEmail, String taskTitle, String taskDescription, String startDate, String endDate, String startTime, String endTime, String planningDuration, String extraDuration, String rawDuration, String priority, String notation, String status, String exactDueTime, String exactDueDate, String isTargetUserAskingForHelp, String isCollaboratorAskingForHelp) {
        this.taskId = taskId;
        this.creatorEmail = creatorEmail;
        this.creatorName = creatorName;
        this.targetUserName = targetUserName;
        this.targetUserEmail = targetUserEmail;
        this.collaboratorEmail = collaboratorEmail;
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.planningDuration = planningDuration;
        this.extraDuration = extraDuration;
        this.rawDuration = rawDuration;
        this.priority = priority;
        this.notation = notation;
        this.status = status;
        this.exactDueTime = exactDueTime;
        this.exactDueDate = exactDueDate;
        this.isTargetUserAskingForHelp = isTargetUserAskingForHelp;
        this.isCollaboratorAskingForHelp = isCollaboratorAskingForHelp;
    }

    public String getIsTargetUserAskingForHelp() {
        return isTargetUserAskingForHelp;
    }

    public void setIsTargetUserAskingForHelp(String isTargetUserAskingForHelp) {
        this.isTargetUserAskingForHelp = isTargetUserAskingForHelp;
    }

    public String getIsCollaboratorAskingForHelp() {
        return isCollaboratorAskingForHelp;
    }

    public void setIsCollaboratorAskingForHelp(String isCollaboratorAskingForHelp) {
        this.isCollaboratorAskingForHelp = isCollaboratorAskingForHelp;
    }

    public String getExactDueTime() {
        return exactDueTime;
    }

    public void setExactDueTime(String exactDueTime) {
        this.exactDueTime = exactDueTime;
    }

    public String getExactDueDate() {
        return exactDueDate;
    }

    public void setExactDueDate(String exactDueDate) {
        this.exactDueDate = exactDueDate;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getTargetUserName() {
        return targetUserName;
    }

    public void setTargetUserName(String targetUserName) {
        this.targetUserName = targetUserName;
    }

    public String getTargetUserEmail() {
        return targetUserEmail;
    }

    public void setTargetUserEmail(String targetUserEmail) {
        this.targetUserEmail = targetUserEmail;
    }

    public String getCollaboratorEmail() {
        return collaboratorEmail;
    }

    public void setCollaboratorEmail(String collaboratorEmail) {
        this.collaboratorEmail = collaboratorEmail;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getPlanningDuration() {
        return planningDuration;
    }

    public void setPlanningDuration(String planningDuration) {
        this.planningDuration = planningDuration;
    }

    public String getExtraDuration() {
        return extraDuration;
    }

    public void setExtraDuration(String extraDuration) {
        this.extraDuration = extraDuration;
    }

    public String getRawDuration() {
        return rawDuration;
    }

    public void setRawDuration(String rawDuration) {
        this.rawDuration = rawDuration;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getNotation() {
        return notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
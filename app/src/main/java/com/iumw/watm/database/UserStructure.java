package com.iumw.watm.database;

public class UserStructure
{
    // properties
    public String userID;
    public String name;
    public String email;
    public String company;
    public String department;
    public String expertise;
    public String interests;
    public String deviceToken;

    // empty constructor
    public UserStructure() {    }

    // main constructor
    public UserStructure (String userID, String name, String email, String company,
                          String department, String expertise, String interests)
    {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.company = company;
        this.department = department;
        this.expertise = expertise;
        this.interests = interests;
    }





    // optional constructor
    public UserStructure (String userID, String name, String email, String company,
                          String department, String expertise, String interests, String deviceToken)
    {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.company = company;
        this.department = department;
        this.expertise = expertise;
        this.interests = interests;
        this.deviceToken = deviceToken;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }
}

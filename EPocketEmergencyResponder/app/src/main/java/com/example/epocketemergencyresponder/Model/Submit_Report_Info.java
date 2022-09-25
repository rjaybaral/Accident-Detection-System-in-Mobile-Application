package com.example.epocketemergencyresponder.Model;

public class Submit_Report_Info {

    private String ID_report;
    private String ID_user;
    private String ID_other;
    private String ID_otw;
    private String ID_emergency;
    private String Latitude;
    private String Longitude;
    private String Type_Rescue;
    private String Message_Report;
    private String Subject;
    private String Victim_Fullname;
    private String Date_Report;
    private String Time_Report;
    private String Responder_Name;

    public Submit_Report_Info(String ID_report, String ID_user, String ID_other, String ID_otw, String ID_emergency, String latitude, String longitude, String type_Rescue, String message_Report, String subject, String victim_Fullname, String date_Report, String time_Report, String responder_Name) {
        this.ID_report = ID_report;
        this.ID_user = ID_user;
        this.ID_other = ID_other;
        this.ID_otw = ID_otw;
        this.ID_emergency = ID_emergency;
        Latitude = latitude;
        Longitude = longitude;
        Type_Rescue = type_Rescue;
        Message_Report = message_Report;
        Subject = subject;
        Victim_Fullname = victim_Fullname;
        Date_Report = date_Report;
        Time_Report = time_Report;
        Responder_Name = responder_Name;
    }

    public String getResponder_Name() {
        return Responder_Name;
    }

    public void setResponder_Name(String responder_Name) {
        Responder_Name = responder_Name;
    }

    public Submit_Report_Info() {
    }

    public String getID_report() {
        return ID_report;
    }

    public void setID_report(String ID_report) {
        this.ID_report = ID_report;
    }

    public String getID_user() {
        return ID_user;
    }

    public void setID_user(String ID_user) {
        this.ID_user = ID_user;
    }

    public String getID_other() {
        return ID_other;
    }

    public void setID_other(String ID_other) {
        this.ID_other = ID_other;
    }

    public String getID_otw() {
        return ID_otw;
    }

    public void setID_otw(String ID_otw) {
        this.ID_otw = ID_otw;
    }

    public String getID_emergency() {
        return ID_emergency;
    }

    public void setID_emergency(String ID_emergency) {
        this.ID_emergency = ID_emergency;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getType_Rescue() {
        return Type_Rescue;
    }

    public void setType_Rescue(String type_Rescue) {
        Type_Rescue = type_Rescue;
    }

    public String getMessage_Report() {
        return Message_Report;
    }

    public void setMessage_Report(String message_Report) {
        Message_Report = message_Report;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getVictim_Fullname() {
        return Victim_Fullname;
    }

    public void setVictim_Fullname(String victim_Fullname) {
        Victim_Fullname = victim_Fullname;
    }

    public String getDate_Report() {
        return Date_Report;
    }

    public void setDate_Report(String date_Report) {
        Date_Report = date_Report;
    }

    public String getTime_Report() {
        return Time_Report;
    }

    public void setTime_Report(String time_Report) {
        Time_Report = time_Report;
    }
}

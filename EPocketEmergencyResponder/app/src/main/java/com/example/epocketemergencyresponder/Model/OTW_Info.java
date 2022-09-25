package com.example.epocketemergencyresponder.Model;

public class OTW_Info {

    private String ID_otw;
    private String ID_emergency;
    private String ID_user;
    private String ID_response;
    private String First_Name;
    private String Last_Name;
    private String Birthday;
    private String Contact_No;
    private String Address;
    private String Current_Location_Address;
    private String Latitude;
    private String Longitude;
    private String Type_Rescue;

    public OTW_Info(String ID_otw, String ID_emergency, String ID_user, String ID_response, String first_Name, String last_Name, String birthday, String contact_No, String address, String current_Location_Address, String latitude, String longitude, String type_Rescue) {
        this.ID_otw = ID_otw;
        this.ID_emergency = ID_emergency;
        this.ID_user = ID_user;
        this.ID_response = ID_response;
        First_Name = first_Name;
        Last_Name = last_Name;
        Birthday = birthday;
        Contact_No = contact_No;
        Address = address;
        Current_Location_Address = current_Location_Address;
        Latitude = latitude;
        Longitude = longitude;
        Type_Rescue = type_Rescue;
    }

    public OTW_Info() {
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

    public String getID_user() {
        return ID_user;
    }

    public void setID_user(String ID_user) {
        this.ID_user = ID_user;
    }

    public String getID_response() {
        return ID_response;
    }

    public void setID_response(String ID_response) {
        this.ID_response = ID_response;
    }

    public String getFirst_Name() {
        return First_Name;
    }

    public void setFirst_Name(String first_Name) {
        First_Name = first_Name;
    }

    public String getLast_Name() {
        return Last_Name;
    }

    public void setLast_Name(String last_Name) {
        Last_Name = last_Name;
    }

    public String getBirthday() {
        return Birthday;
    }

    public void setBirthday(String birthday) {
        Birthday = birthday;
    }

    public String getContact_No() {
        return Contact_No;
    }

    public void setContact_No(String contact_No) {
        Contact_No = contact_No;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getCurrent_Location_Address() {
        return Current_Location_Address;
    }

    public void setCurrent_Location_Address(String current_Location_Address) {
        Current_Location_Address = current_Location_Address;
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
}

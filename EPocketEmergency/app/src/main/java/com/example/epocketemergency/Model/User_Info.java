package com.example.epocketemergency.Model;

import android.location.Location;

public class User_Info {
    private String ID_user;
    private String First_Name;
    private String Last_Name;
    private String Birthday;
    private String Address;
    private String Contact;
    private String Gender;
    private String Email;
    private String Latitude;
    private String Longitude;
    private String Valid_ID_URL;
    private String Profile_URL;
    private String Current_Location_Address;
    private String DirectionFront;
    private String IDType;
    private String Type;
    private String Status;

    public User_Info(String ID_user, String first_Name, String last_Name, String birthday, String address, String contact, String gender, String email, String latitude, String longitude, String valid_ID_URL, String profile_URL, String current_Location_Address, String directionFront, String IDType, String type) {
        this.ID_user = ID_user;
        First_Name = first_Name;
        Last_Name = last_Name;
        Birthday = birthday;
        Address = address;
        Contact = contact;
        Gender = gender;
        Email = email;
        Latitude = latitude;
        Longitude = longitude;
        Valid_ID_URL = valid_ID_URL;
        Profile_URL = profile_URL;
        Current_Location_Address = current_Location_Address;
        DirectionFront = directionFront;
        this.IDType = IDType;
        Type = type;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getIDType() {
        return IDType;
    }

    public void setIDType(String IDType) {
        this.IDType = IDType;
    }

    public String getDirectionFront() {
        return DirectionFront;
    }

    public void setDirectionFront(String directionFront) {
        DirectionFront = directionFront;
    }

    public String getCurrent_Location_Address() {
        return Current_Location_Address;
    }

    public void setCurrent_Location_Address(String current_Location_Address) {
        Current_Location_Address = current_Location_Address;
    }

    public User_Info() {
    }

    public String getID_user() {
        return ID_user;
    }

    public void setID_user(String ID_user) {
        this.ID_user = ID_user;
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

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
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

    public String getValid_ID_URL() {
        return Valid_ID_URL;
    }

    public void setValid_ID_URL(String valid_ID_URL) {
        Valid_ID_URL = valid_ID_URL;
    }

    public String getProfile_URL() {
        return Profile_URL;
    }

    public void setProfile_URL(String profile_URL) {
        Profile_URL = profile_URL;
    }
}

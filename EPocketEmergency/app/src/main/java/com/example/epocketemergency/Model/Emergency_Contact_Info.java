package com.example.epocketemergency.Model;

public class Emergency_Contact_Info {

    private String ID_contact;
    private String Contact_Number;
    private String Name;

    public Emergency_Contact_Info(String ID_contact, String contact_Number, String name) {
        this.ID_contact = ID_contact;
        Contact_Number = contact_Number;
        Name = name;
    }

    public Emergency_Contact_Info() {
    }

    public String getID_contact() {
        return ID_contact;
    }

    public void setID_contact(String ID_contact) {
        this.ID_contact = ID_contact;
    }

    public String getContact_Number() {
        return Contact_Number;
    }

    public void setContact_Number(String contact_Number) {
        Contact_Number = contact_Number;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}

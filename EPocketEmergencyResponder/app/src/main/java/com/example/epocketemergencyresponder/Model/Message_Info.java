package com.example.epocketemergencyresponder.Model;

public class Message_Info {
    private String ID_message;
    private String Sender;
    private String Receiver;
    private String Message;
    private String Message_Time;
    private String Message_Date;
    private String Message_Seen;
    private String Picture_URL;

    public Message_Info(String ID_message, String sender, String receiver, String message, String message_Time, String message_Date, String message_Seen, String picture_URL) {
        this.ID_message = ID_message;
        Sender = sender;
        Receiver = receiver;
        Message = message;
        Message_Time = message_Time;
        Message_Date = message_Date;
        Message_Seen = message_Seen;
        Picture_URL = picture_URL;
    }

    public Message_Info() {
    }

    public String getID_message() {
        return ID_message;
    }

    public void setID_message(String ID_message) {
        this.ID_message = ID_message;
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String sender) {
        Sender = sender;
    }

    public String getReceiver() {
        return Receiver;
    }

    public void setReceiver(String receiver) {
        Receiver = receiver;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getMessage_Time() {
        return Message_Time;
    }

    public void setMessage_Time(String message_Time) {
        Message_Time = message_Time;
    }

    public String getMessage_Date() {
        return Message_Date;
    }

    public void setMessage_Date(String message_Date) {
        Message_Date = message_Date;
    }

    public String getMessage_Seen() {
        return Message_Seen;
    }

    public void setMessage_Seen(String message_Seen) {
        Message_Seen = message_Seen;
    }

    public String getPicture_URL() {
        return Picture_URL;
    }

    public void setPicture_URL(String picture_URL) {
        Picture_URL = picture_URL;
    }
}

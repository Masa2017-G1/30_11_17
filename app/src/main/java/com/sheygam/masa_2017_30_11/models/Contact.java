package com.sheygam.masa_2017_30_11.models;

/**
 * Created by gregorysheygam on 04/12/2017.
 */

public class Contact {
    private long contactId;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String description;
    private String address;

    public Contact() {
    }

    public Contact(long contactId, String email, String fullName, String phoneNumber, String description, String address) {
        this.contactId = contactId;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.address = address;
    }

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

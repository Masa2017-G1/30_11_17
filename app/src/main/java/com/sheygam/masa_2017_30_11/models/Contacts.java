package com.sheygam.masa_2017_30_11.models;

import java.util.ArrayList;

/**
 * Created by gregorysheygam on 04/12/2017.
 */

public class Contacts {
    private ArrayList<Contact> contacts;

    public Contacts() {
    }

    public Contacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }

    public ArrayList<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }
}

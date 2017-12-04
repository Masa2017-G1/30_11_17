package com.sheygam.masa_2017_30_11.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sheygam.masa_2017_30_11.R;
import com.sheygam.masa_2017_30_11.models.Contact;

import java.util.ArrayList;

/**
 * Created by gregorysheygam on 04/12/2017.
 */

public class ContactsListAdapter extends BaseAdapter {
    private ArrayList<Contact> contacts;

    public ContactsListAdapter(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.my_row,parent,false);
        }

        Contact contact = contacts.get(position);
        TextView nameTxt = convertView.findViewById(R.id.name_txt);
        TextView phoneTxt = convertView.findViewById(R.id.phone_txt);
        nameTxt.setText(contact.getFullName());
        phoneTxt.setText(contact.getPhoneNumber());
        return convertView;
    }
}

package com.sheygam.masa_2017_30_11;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sheygam.masa_2017_30_11.adapters.ContactsListAdapter;
import com.sheygam.masa_2017_30_11.models.Contact;
import com.sheygam.masa_2017_30_11.providers.HttpProvider;

import java.io.IOException;
import java.util.ArrayList;

public class ListActivitySync extends AppCompatActivity {

    private ListView myList;
    private FrameLayout progressFrame;
    private TextView emptyTxt;
    private String token;
    private ContactsListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        myList = findViewById(R.id.my_list);
        progressFrame = findViewById(R.id.progress_frame);
        progressFrame.setOnClickListener(null);
        emptyTxt = findViewById(R.id.empty_txt);
        token = getSharedPreferences("AUTH",MODE_PRIVATE).getString("TOKEN",null);
        if(token == null){
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        new GetContactsTask().execute();
    }

    class GetContactsTask extends AsyncTask<Void,Void,String>{

        private ArrayList<Contact> contacts;
        private boolean isSuccess = true;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressFrame.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = "";
            try{
                contacts = HttpProvider.getInstance().getContacts(token);
            }catch (IOException ex){
                result = "Connection error! Check your internet!";
                isSuccess = false;
            }catch (Exception ex){
                ex.printStackTrace();
                result = ex.getMessage();
                isSuccess = false;
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(isSuccess){
                if(contacts == null){
                    adapter = new ContactsListAdapter(new ArrayList<Contact>());
                    myList.setAdapter(adapter);
                    progressFrame.setVisibility(View.GONE);
                    emptyTxt.setVisibility(View.VISIBLE);
                }else{
                    adapter = new ContactsListAdapter(contacts);
                    myList.setAdapter(adapter);
                    progressFrame.setVisibility(View.GONE);
                    emptyTxt.setVisibility(View.GONE);
                }
            }else{
                adapter = new ContactsListAdapter(new ArrayList<Contact>());
                myList.setAdapter(adapter);
                progressFrame.setVisibility(View.GONE);
                emptyTxt.setVisibility(View.VISIBLE);
                Toast.makeText(ListActivitySync.this, s, Toast.LENGTH_SHORT).show();
            }
        }
    }
}

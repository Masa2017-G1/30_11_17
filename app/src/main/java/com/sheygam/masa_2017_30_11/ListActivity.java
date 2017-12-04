package com.sheygam.masa_2017_30_11;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sheygam.masa_2017_30_11.adapters.ContactsListAdapter;
import com.sheygam.masa_2017_30_11.models.Contact;
import com.sheygam.masa_2017_30_11.models.Contacts;
import com.sheygam.masa_2017_30_11.providers.HttpProvider;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ListActivity extends AppCompatActivity {
    private ListView myList;
    private FrameLayout progressFrame;
    private TextView emptyTxt;
    private ContactsListAdapter adapter;
    private Handler handler;
    private String token;
    private Gson gson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        gson = new Gson();
        myList = findViewById(R.id.my_list);
        progressFrame = findViewById(R.id.progress_frame);
        progressFrame.setOnClickListener(null);
        emptyTxt = findViewById(R.id.empty_txt);
        handler = new Handler();
        token = getSharedPreferences("AUTH",MODE_PRIVATE).getString("TOKEN",null);
        if(token == null){
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateList();
    }

    private void showProgress(boolean show){
        progressFrame.setVisibility(show ? View.VISIBLE:View.GONE);
    }

    private void showEmpty(boolean show){
        emptyTxt.setVisibility(show ? View.VISIBLE:View.GONE);
    }

    private Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            handler.post(new ResponseError("Connection error! Check your internet"));
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if(response.code() < 400){
                handler.post(new ResponsSuccess(response.body().string()));
            }else if(response.code() == 401){
                handler.post(new ResponseError("Authorization error!"));
            }else{
                Log.d("MY_TAG", "onResponse: " + response.body().string());
                handler.post(new ResponseError("Server error! Call to support!"));
            }
        }
    };

    private void updateList(){
        showProgress(true);
        HttpProvider.getInstance().getContacts(token,callback);
    }

    private class ResponsSuccess implements Runnable{
        private String msg;

        public ResponsSuccess(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            Contacts contacts = gson.fromJson(msg,Contacts.class);

            if(contacts.getContacts() == null){
                showEmpty(true);
                adapter = new ContactsListAdapter(new ArrayList<Contact>());
                myList.setAdapter(adapter);
            }else{
                showEmpty(false);
                adapter = new ContactsListAdapter(contacts.getContacts());
                myList.setAdapter(adapter);
            }
            showProgress(false);
        }
    }

    private class ResponseError implements Runnable{
        private String msg;

        public ResponseError(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            adapter = new ContactsListAdapter(new ArrayList<Contact>());
            myList.setAdapter(adapter);
            showEmpty(true);
            showProgress(false);
            Toast.makeText(ListActivity.this, "Error: " + msg, Toast.LENGTH_SHORT).show();
        }
    }
}

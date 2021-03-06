package com.sheygam.masa_2017_30_11.providers;

import android.util.Log;

import com.google.gson.Gson;
import com.sheygam.masa_2017_30_11.models.Auth;
import com.sheygam.masa_2017_30_11.models.Contact;
import com.sheygam.masa_2017_30_11.models.Contacts;
import com.sheygam.masa_2017_30_11.models.Token;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by gregorysheygam on 30/11/2017.
 */

public class HttpProvider {
    public static final String TAG = "MY_TAG";
    public static final String BASE_URL = "https://telranstudentsproject.appspot.com/_ah/api/contactsApi/v1";
    private static final HttpProvider ourInstance = new HttpProvider();

    private Gson gson;
    private OkHttpClient client;
    private MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static HttpProvider getInstance() {
        return ourInstance;

    }

    private HttpProvider() {
        gson = new Gson();
        client = new OkHttpClient.Builder()
                .readTimeout(15,TimeUnit.SECONDS)
                .connectTimeout(15,TimeUnit.SECONDS)
                .build();
    }


    public Token registration(Auth auth) throws Exception {
        String data = gson.toJson(auth);
        Log.d(TAG, "registration: " + data);
        URL url = new URL(BASE_URL + "/registration");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setRequestProperty("Content-Type","application/json");

//        connection.connect(); For get request
        connection.setDoOutput(true);
        connection.setDoInput(true);

        OutputStream out = connection.getOutputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
        bw.write(data);
        bw.flush();
        bw.close();

        BufferedReader br;
        String line = "";
        String result = "";

        if(connection.getResponseCode() < 400){
            br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = br.readLine()) != null){
                result += line;
            }
            Log.d(TAG, "registration: response : " + result);
            Token token = gson.fromJson(result,Token.class);
            return token;
        }else if(connection.getResponseCode() == 409){
            throw new Exception("User already exist");
        }else{
            br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            while((line = br.readLine())!= null){
                result += line;
            }
            Log.d(TAG, "registration: error: " + result);
            throw new Exception("Server error! call to support!");
        }

    }


    public Token login(Auth auth) throws Exception {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String data = gson.toJson(auth);

        RequestBody body = RequestBody.create(JSON, data);

        Request request = new Request.Builder()
                .url(BASE_URL + "/login")
                .post(body)
//                .addHeader("Authorization","[token]")
                .build();

        Response response = client.newCall(request).execute();
        if (response.code() < 400) {
            String json = response.body().string();
            Token token = gson.fromJson(json, Token.class);
            return token;
        } else if (response.code() == 401) {
            throw new Exception("Wrong login or password!");
        } else {
            Log.d(TAG, "login: error: " + response.body().string());
            throw new Exception("Server error! Call to support");
        }
    }

    public ArrayList<Contact> getContacts(String token) throws Exception {
        Request request = new Request.Builder()
                .url(BASE_URL+"/contactsarray")
                .addHeader("Authorization",token)
                .build();
        Response response = client.newCall(request).execute();
        if(response.code() < 400){
            String data = response.body().string();
            Log.d(TAG, "getContacts: " + data);
            Contacts contacts = gson.fromJson(data,Contacts.class);
            return contacts.getContacts();
        }else if(response.code() == 401){
            throw new Exception("Authorization error!");
        }else{
            Log.d(TAG, "getContacts: " + response.body().string());
            throw new Exception("Server error! Call to support!");
        }
    }

    public void getContacts(String token, Callback callback){
        Request request = new Request.Builder()
                .url(BASE_URL + "/contactsarray")
                .addHeader("Authorization",token)
                .build();
        newCall(request,callback);
    }

    private void newCall(Request request, Callback callback){
        client.newCall(request).enqueue(callback);
    }

    public String getContactsUrlCon(String token) throws Exception {
        URL url = new URL(BASE_URL+"/contactsarray");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization",token);
        connection.setRequestProperty("Content-Type", "application/json");

        connection.connect();

        if(connection.getResponseCode() < 400){
            int size = Integer.parseInt(connection.getHeaderField("Content-length"));
            String line ;
            String result = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while((line = br.readLine())!=null){
                result += line;
            }
            return result;
        }else if(connection.getResponseCode() == 401){
            throw new Exception("Authorization error!");
        }else{
            throw new Exception("Server error! Call to support!");
        }

    }
}

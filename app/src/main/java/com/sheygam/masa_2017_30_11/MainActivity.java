package com.sheygam.masa_2017_30_11;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sheygam.masa_2017_30_11.models.Auth;
import com.sheygam.masa_2017_30_11.models.Token;
import com.sheygam.masa_2017_30_11.providers.HttpProvider;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText inputEmail, inputPassword;
    private ProgressBar myProgress;
    private LinearLayout loginForm;
    private Button loginBtn, regBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        myProgress = findViewById(R.id.my_progress);
        loginBtn = findViewById(R.id.login_btn);
        regBtn = findViewById(R.id.reg_btn);
        loginForm = findViewById(R.id.login_form);
        regBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.login_btn){
//            new LoginTask().execute();
            login();
        }else if(v.getId() == R.id.reg_btn){
            new RegistrationTask().execute();
        }
    }

    private void login(){
        showProgress(true);
        final Handler handler = new Handler();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        Auth auth = new Auth(email,password);
        final Gson gson = new Gson();
        String data = gson.toJson(auth);

        OkHttpClient client = new OkHttpClient();
        MediaType  JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON,data);
        Request request = new Request.Builder()
                .url(HttpProvider.BASE_URL + "/login")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        showProgress(false);
                        Toast.makeText(MainActivity.this, "Connection error! Check your internet!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.code() < 400){
                    String json = response.body().string();
                    Token token = gson.fromJson(json,Token.class);
                    SharedPreferences sharedPreferences = getSharedPreferences("AUTH",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("TOKEN",token.getToken());
                    editor.commit();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            showProgress(false);
                            Toast.makeText(MainActivity.this, "Login OK!", Toast.LENGTH_SHORT).show();
                            //Todo start next activity
                        }
                    });
                }else if(response.code() == 401){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            showProgress(false);
                            Toast.makeText(MainActivity.this, "Wrong login or password", Toast.LENGTH_SHORT).show();
                        }
                    });

                }else{
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Server error! Call to support!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.d("MY_TAG", "onResponse: error: " + response.body().string());
                }
            }
        });
    }

    private void showProgress(boolean show){
        myProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    class RegistrationTask extends AsyncTask<Void,Void,String>{
        private Auth auth;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();
            auth = new Auth(email,password);
            showProgress(true);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = "Registration OK!";
            try {
                Token token = HttpProvider.getInstance().registration(auth);
                SharedPreferences sharedPreferences = getSharedPreferences("AUTH",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("TOKEN",token.getToken());
                editor.commit();
            }catch (IOException ex){
                ex.printStackTrace();
                result = "Connection error! Check your internet!";
            }catch (Exception ex){
                result = ex.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
            showProgress(false);
            //ToDo Start next Activity
        }
    }

    class LoginTask extends AsyncTask<Void,Void,String>{
        private Auth auth;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();
            auth = new Auth(email,password);
            showProgress(true);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = "Login OK!";
            try{
                Token token = HttpProvider.getInstance().login(auth);
                SharedPreferences sharedPreferences = getSharedPreferences("AUTH",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("TOKEN",token.getToken());
                editor.commit();
            }catch (IOException ex){
                ex.printStackTrace();
                result = "Connection error! Check your internet";
            }catch (Exception ex){
                result = ex.getMessage();

            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
            showProgress(false);
            //ToDo start next Activity
        }
    }
}

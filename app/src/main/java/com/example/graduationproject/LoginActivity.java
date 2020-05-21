package com.example.graduationproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView registerButton = (TextView) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });

        final EditText idText= (EditText) findViewById(R.id.idText);
        final EditText passwordText= (EditText) findViewById(R.id.passwordText);
        final Button loginButton= (Button) findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               final String userID= idText.getText().toString();
                String userPassword= passwordText.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success =  jsonResponse.getBoolean("success");
                            if(success){
                                AlertDialog.Builder builder = new  AlertDialog.Builder(LoginActivity.this);
                                dialog = builder.setMessage("You have successfully logged in.")
                                        .setPositiveButton("ok",null)
                                        .create();
                                dialog.show();
                                Intent intent=  new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("userID",userID);
                                LoginActivity.this.startActivity(intent);
                                finish();
                            }
                            else {
                                AlertDialog.Builder builder = new  AlertDialog.Builder(LoginActivity.this);
                                dialog = builder.setMessage("Username or password error")
                                        .setNegativeButton("retry",null)
                                        .create();
                                dialog.show();

                            }

                        } catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                };
                LoginRequest loginRequest = new LoginRequest(userID,userPassword,responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });
    }

    @Override
    protected  void  onStop(){
        super.onStop();
        if(dialog !=null){
            dialog.dismiss();
            dialog = null;

        }
    }
}

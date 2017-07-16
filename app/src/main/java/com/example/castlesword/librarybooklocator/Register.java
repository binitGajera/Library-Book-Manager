package com.example.castlesword.librarybooklocator;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity{
    private EditText name;
    private EditText username;
    private EditText password;
    private EditText email;
    private Button btnAdd;
    private Calendar mCalendar;
    private EditText mBirthdayEditText;

    //private DatePickerDialog mDatePickerDialog = null;

    private static final String REGISTER_URL = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name = (EditText) findViewById(R.id.name);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        email = (EditText) findViewById(R.id.email);
       // btnAdd = (Button) findViewById(R.id.btnAdd);
        // btnAdd.setOnClickListener(this);

        android.widget.Button btnAdd= (android.widget.Button)findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name1 = name.getText().toString().trim().toLowerCase();
                String username1 = username.getText().toString().trim().toLowerCase();
                String password1 = password.getText().toString().trim().toLowerCase();
                String email1 = email.getText().toString().trim().toLowerCase();
                if (!name1.isEmpty() && !username1.isEmpty() && !password1.isEmpty() && !email1.isEmpty()) {
                    register(name1, username1, password1, email1);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /*public void onClick(View v) {
        if(v == btnAdd){
            registerUser();
        }
    }*/

    private void registerUser() {

    }

    private void register(final String name,final String username,final String password,final String branch) {
        // Tag used to cancel the request
        StringRequest strReq = new StringRequest(Request.Method.POST,
                "http://bin2580.16mb.com/library_services/nirav/registration.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Log.d(TAG, "Register Response: " + response.toString());
                String we;
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Toast.makeText(getApplicationContext(), "User Successfully Registered!", Toast.LENGTH_SHORT);
                        Intent intent = new Intent(Register.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username",username);
                params.put("password", password);
                params.put("name",name);
                params.put("branch",branch);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }
}


package com.example.castlesword.librarybooklocator;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
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


public class AddBooks extends AppCompatActivity {
    private String TAG="AddBooks";
    private ActionBarDrawerToggle mToggle;
    private DrawerLayout mDrawer;
    Button btn;
    String q;
    private NavigationView navView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_books);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(AddBooks.this, mDrawer, R.string.open, R.string.close);
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigate();
        SessionManager sm= new SessionManager(getApplicationContext());
        String userid1= sm.getUserID();
        if(userid1=="")
        {
            Intent i= new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(i);
        }
        final android.widget.TextView editname =(android.widget.TextView)findViewById(R.id.textView9);
        final android.widget.EditText editdate =(android.widget.EditText)findViewById(R.id.editText1);
        final android.widget.EditText editquantity =(android.widget.EditText)findViewById(R.id.editText2);

        android.widget.Button btnAdd= (android.widget.Button)findViewById(R.id.buttonadd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int vount=0;
                Intent i= getIntent();

                String name =i.getStringExtra("bookname");
                String date1 = ((EditText)findViewById(R.id.editText1)).getText().toString();
                String quantity = "1";
                SessionManager sm= new SessionManager(getApplicationContext());
                String userid= sm.getUserID();
                if (!name.isEmpty() && !date1.isEmpty() && !quantity.isEmpty() && !userid.isEmpty()) {
                    addBook(name, date1, quantity,userid);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG).show();
                }
            }
        });

        android.widget.Button btnlogout= (android.widget.Button)findViewById(R.id.blogout);
        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionManager sm = new SessionManager(getApplicationContext());
                sm.removeUser();
                Toast.makeText(getApplicationContext(),
                            "Succesully Logout!", Toast.LENGTH_LONG).show();
                Intent io= new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(io);
            }
        });
        Intent dept = getIntent();
        q = dept.getStringExtra("bookname");
        editname.setText(q);
    }


    public void navigate(){
        navView = (NavigationView) findViewById(R.id.nvView);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if(menuItem.isChecked())
                    menuItem.setChecked(false);
                else
                    menuItem.setChecked(true);

                mDrawer.closeDrawers();

                switch (menuItem.getItemId()){

                    case R.id.nav_first_fragment:
                        Intent k = new Intent(AddBooks.this, MainActivity.class);
                        startActivity(k);
                        return true;
                    case R.id.nav_second_fragment:
                        Intent bi = new Intent(AddBooks.this, SearchActivity.class);
                        startActivity(bi);
                        return true;
                    case R.id.nav_third_fragment:
                        SessionManager smt = new SessionManager(getApplicationContext());
                        smt.removeUser();
                        Intent j = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(j);
                        return true;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addBook(final String name, final String date, final String quantity, final String userid) {
        // Tag used to cancel the request
        StringRequest strReq = new StringRequest(Request.Method.POST,
                "http://bin2580.16mb.com/library_services/nirav/addBook.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
               // Log.d(TAG, "Register Response: " + response.toString());
                String we;
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Intent myIntent = new Intent(getApplicationContext(), NotifyService.class);
                        Toast.makeText(getApplicationContext(), "Book Successfully added!", Toast.LENGTH_LONG).show();
                        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY,10);
                        calendar.set(Calendar.MINUTE,0);
                        calendar.set(Calendar.SECOND,0);
                        EditText editdate= (EditText)findViewById(R.id.editText1);
                        String date= editdate.getText().toString();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                        Date dobj=null;
                        try {
                            dobj = sdf.parse(date);
                        }
                        catch(Exception e)
                        {}
                        int issueddate=Integer.parseInt(new SimpleDateFormat("dd").format(dobj));
                        int issuedmonth=Integer.parseInt(new SimpleDateFormat("MM").format(dobj));
                        if(issueddate+21>30)
                        {
                            calendar.set(Calendar.DAY_OF_MONTH,(issueddate+21)%30);
                            calendar.set(Calendar.MONTH,issuedmonth);
                        }
                        else
                        {
                            calendar.set(Calendar.DAY_OF_MONTH,issueddate+21);
                            calendar.set(Calendar.MONTH,issuedmonth-1);
                        }
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);
                        Intent intent = new Intent(
                                AddBooks.this,
                                MainActivity.class);
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
                Log.e(TAG, "Book adding error : " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("date", date);
                params.put("quantity",quantity);
                params.put("userid",userid);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }

}

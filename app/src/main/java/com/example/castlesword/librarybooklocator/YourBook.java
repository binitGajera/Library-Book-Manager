package com.example.castlesword.librarybooklocator;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

//import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YourBook extends AppCompatActivity {
    ImageButton FAB;
    InputStream is = null;
    private ActionBarDrawerToggle mToggle;
    private DrawerLayout mDrawer;
    private NavigationView navView;
    SearchView searchView = null;
    SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_book);
        Toolbar mToolbar;
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(YourBook.this, mDrawer, R.string.open, R.string.close);
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigate();
        sm = new SessionManager(getApplicationContext());
        String userid1 = sm.getUserID();
        if (userid1 == "") {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
        }
        timepass();

        FAB = (ImageButton) findViewById(R.id.imageButton);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(YourBook.this, AddBooks.class);
                startActivity(i);
            }
        });
    }
public void timepass() {
    StringRequest strReq = new StringRequest(Request.Method.POST,
            "http://bin2580.16mb.com/library_services/nirav/manageBook.php", new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
                   String we;
            try {
                JSONObject jArray = new JSONObject(response);
                boolean error = jArray.getBoolean("error");
                TableLayout tv = (TableLayout) findViewById(R.id.table);
                tv.removeAllViewsInLayout();
                int flag = 1;
                String index = "0";
                for (int i = 0; i < jArray.length() ; i++) {
                    TableRow tr = new TableRow(YourBook.this);
                    tr.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    if (flag == 1) {
                        TextView b6 = new TextView(YourBook.this);
                        b6.setText("Name");
                        b6.setTextColor(Color.BLUE);
                        b6.setTextSize(15);
                        tr.addView(b6);
                        TextView b19 = new TextView(YourBook.this);
                        b19.setPadding(10, 0, 0, 0);
                        b19.setTextSize(15);
                        b19.setText("Author");
                        b19.setTextColor(Color.BLUE);
                        tr.addView(b19);
                        TextView b29 = new TextView(YourBook.this);
                        b29.setPadding(10, 0, 0, 0);
                        b29.setText("ISBN");
                        b29.setTextColor(Color.BLUE);
                        b29.setTextSize(15);
                        tr.addView(b29);
                        TextView b30 = new TextView(YourBook.this);
                        b19.setPadding(10, 0, 0, 0);
                        b19.setTextSize(15);
                        b19.setText("Date");
                        b19.setTextColor(Color.BLUE);
                        tr.addView(b30);
                        tv.addView(tr);
                        final View vline = new View(YourBook.this);
                        vline.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2));
                        vline.setBackgroundColor(Color.BLUE);
                        tv.addView(vline);
                        flag = 0;
                    } else {
                        JSONObject json_data = jArray.getJSONObject(index);
                        TextView b = new TextView(YourBook.this);
                        JSONObject json_data1=json_data.getJSONObject("book");
                        String stime = json_data1.getString("name");
                        b.setText(stime);
                        b.setTextColor(Color.RED);
                        b.setTextSize(15);
                        tr.addView(b);
                        TextView b1 = new TextView(YourBook.this);
                        b1.setPadding(10, 0, 0, 0);
                        b1.setTextSize(15);
                        JSONObject json_data2=json_data.getJSONObject("book");
                        String stime1 = json_data2.getString("author");
                        b1.setText(stime1);
                        b1.setTextColor(Color.BLACK);
                        tr.addView(b1);
                        TextView b2 = new TextView(YourBook.this);
                        b2.setPadding(10, 0, 0, 0);
                        JSONObject json_data3=json_data.getJSONObject("book");
                        String stime2 = String.valueOf(json_data3.getInt("isbn"));
                        b2.setText(stime2);
                        b2.setTextColor(Color.BLACK);
                        b2.setTextSize(15);
                        tr.addView(b2);
                        TextView b3 = new TextView(YourBook.this);
                        b1.setPadding(10, 0, 0, 0);
                        b1.setTextSize(15);
                        JSONObject json_data4=json_data.getJSONObject("book");
                        String stime3 = json_data4.getString("date");
                        b1.setText(stime3);
                        b1.setTextColor(Color.BLACK);
                        tr.addView(b3);
                        tv.addView(tr);
                        final View vline1 = new View(YourBook.this);
                        vline1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
                        vline1.setBackgroundColor(Color.WHITE);
                        tv.addView(vline1);
                    }
                    index = String.valueOf(i);
                }
                if (!error) {
                    Toast.makeText(getApplicationContext(), "Book Successfully added!", Toast.LENGTH_LONG).show();
                    //  finish();
                } else {
                     }
            } catch (JSONException e) {
                Log.e("log_tag", "Error parsing data" + e.toString());
                Toast.makeText(getApplicationContext(), "JsonArray fail", Toast.LENGTH_SHORT).show();
            }
        }
    }, new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            //Log.e(TAG, "Book adding error : " + error.getMessage());
            Toast.makeText(getApplicationContext(),
                    error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }) {

        @Override
        protected Map<String, String> getParams() {
            // Posting params to register url
            Map<String, String> params = new HashMap<String, String>();
            params.put("userid", sm.getUserID());
            return params;
        }

    };
    RequestQueue requestQueue = Volley.newRequestQueue(this);
    requestQueue.add(strReq);
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
                        Intent k = new Intent(YourBook.this, MainActivity.class);
                        startActivity(k);
                        return true;
                    case R.id.nav_second_fragment:
                        Intent bi = new Intent(YourBook.this, SearchActivity.class);
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
}

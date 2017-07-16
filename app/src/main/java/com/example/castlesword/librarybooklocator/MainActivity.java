package com.example.castlesword.librarybooklocator;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ImageButton b;
    private ActionBarDrawerToggle mToggle;
    private DrawerLayout mDrawer;
    private NavigationView navView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(MainActivity.this, mDrawer, R.string.open, R.string.close);
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
        Intent del = getIntent();
        if(del.getStringExtra("delete")!=null) {
            Toast.makeText(MainActivity.this, del.getStringExtra("delete"), Toast.LENGTH_LONG).show();
        }
            loadBook();
        Button bin = (Button)findViewById(R.id.button2);
        bin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,SearchActivity.class);
                startActivity(i);
            }
        });
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
                        Intent k = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(k);
                        return true;
                    case R.id.nav_second_fragment:
                        Intent bi = new Intent(MainActivity.this, SearchActivity.class);
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

    public void loadBook()
    {
        final Context con = this;
        final SessionManager sm = new SessionManager(getApplicationContext());
        final String userid1 = sm.getUserID();
        final TableLayout ll = (TableLayout) findViewById(R.id.tablebook);
        ll.setColumnStretchable(0, true);
        ll.setColumnStretchable(1, true);

        TableRow row = new TableRow(this);
        final TextView nameheader = new TextView(this);
        nameheader.setGravity(Gravity.CENTER);
        nameheader.setText("Book Name");
        nameheader.setTypeface(null, Typeface.BOLD_ITALIC);
        nameheader.setTextColor(Color.BLUE);
        nameheader.setTextSize(26);
        //nameheader.setWidth(View.);
        TextView dateheader = new TextView(this);
        dateheader.setText("Issued Date");
        dateheader.setTextSize(26);
        dateheader.setTypeface(null, Typeface.BOLD_ITALIC);
        dateheader.setTextColor(Color.BLUE);
        dateheader.setGravity(Gravity.CENTER);
        row.addView(nameheader);
        row.addView(dateheader);
        ll.addView(row);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                "http://bin2580.16mb.com/library_services/nirav/manageBook.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    int size=0;
                    JSONObject jObj = new JSONObject(response);
                  //  var lastKey = Object.keys(jObj).sort().reverse()[0];
                    Iterator iter = jObj.keys();
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Toast.makeText(getApplicationContext(), "Welcome !", Toast.LENGTH_SHORT).show();
                        final List<String> books = new ArrayList<String>();
                        final List<String> dates = new ArrayList<String>();
                        String index = "0";
                        int i = 0;
                        while(iter.hasNext())
                        {
                            size++;
                            iter.next();
                        }
                        size=size-2;

                        while (i<size)
                        {
                            JSONObject obj1 = jObj.getJSONObject(index);
                            JSONObject obj2 = obj1.getJSONObject("book");
                            books.add(obj2.getString("name"));
                            i += 6;
                            index = String.valueOf(i);
                        }
                        i = 3;
                        index = String.valueOf(i);
                        while (i<size) {
                            JSONObject obj1 = jObj.getJSONObject(index);
                            JSONObject obj2 = obj1.getJSONObject("book");
                            dates.add(obj2.getString("date"));
                            i += 6;
                            index = String.valueOf(i);
                        }

                        for (int j = 0; j < books.size(); j++) {
                            TableRow tr = new TableRow(getApplicationContext());
                            TextView namevalue = new TextView(getApplicationContext());
                            namevalue.setGravity(Gravity.CENTER);
                            namevalue.setText(books.get(j).toString());
                            final String bookname=books.get(j).toString();
                            namevalue.setTextColor(Color.BLACK);
                            TextView datevalue = new TextView(getApplicationContext());
                            datevalue.setText(dates.get(j).toString());
                            datevalue.setGravity(Gravity.CENTER);
                            datevalue.setTextColor(Color.BLACK);
                            tr.addView(namevalue);
                            tr.addView(datevalue);
                            ll.addView(tr);
                            tr.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    Intent intent = new Intent(
                                            MainActivity.this,
                                            UserBookDetails.class);
                                    intent.putExtra("bookName",bookname);
                                    startActivity(intent);
                                }
                            });
                        }
                    } else {

                        if(sm.getUserID() ==null) {
                            String errorMsg = jObj.getString("error_msg");
                            Toast.makeText(getApplicationContext(),
                                    errorMsg, Toast.LENGTH_LONG).show();
                        }
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
                params.put("userid", userid1);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }
}

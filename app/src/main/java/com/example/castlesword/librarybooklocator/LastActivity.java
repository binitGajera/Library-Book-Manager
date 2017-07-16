package com.example.castlesword.librarybooklocator;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

public class LastActivity extends AppCompatActivity {

    TextView tv0, tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8, tv9;
    private Context context;
    String url = "http://bin2580.16mb.com/library_services/last_one.php";
    private ActionBarDrawerToggle mToggle;
    private DrawerLayout mDrawer;
    private NavigationView navView;
    private static final String ARRAY_NAME="DATA";
    ImageButton FAB;
    String ISBN;
    String BOOK_NAME;
    String AUTHOR;
    String PUBLISHER;
    String RACK_NO;
    SessionManager sm;
    String total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(LastActivity.this, mDrawer, R.string.open, R.string.close);
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigate();
        sm = new SessionManager(getApplicationContext());
        String userid1= sm.getUserID();
        if(userid1=="")
        {
            Intent i= new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(i);
        }
        tv0 = (TextView) findViewById(R.id.textView0);
        tv1 = (TextView) findViewById(R.id.textView1);
        tv2 = (TextView) findViewById(R.id.textView2);
        tv3 = (TextView) findViewById(R.id.textView3);
        tv4 = (TextView) findViewById(R.id.textView4);
        tv5 = (TextView) findViewById(R.id.textView5);
        tv6 = (TextView) findViewById(R.id.textView6);
        tv7 = (TextView) findViewById(R.id.textView7);
        tv8 = (TextView) findViewById(R.id.textView8);
        tv9 = (TextView) findViewById(R.id.textView9);


        context = this;

        Intent i = getIntent();
        BOOK_NAME = i.getStringExtra("extra");


        new LoadDetails().execute(BOOK_NAME);

        FAB = (ImageButton) findViewById(R.id.imageButton);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(total) > 2){
                    Toast.makeText(LastActivity.this, "You have already issued 3 books !", Toast.LENGTH_LONG).show();
                }else{
                    Intent i = new Intent(LastActivity.this, AddBooks.class);
                    i.putExtra("bookname",tv3.getText().toString());
                    i.putExtra("isbn",tv1.getText().toString());
                    i.putExtra("author",tv5.getText().toString());
                    i.putExtra("publisher",tv7.getText().toString());
                    startActivity(i);
                }
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
                        Intent k = new Intent(LastActivity.this, MainActivity.class);
                        startActivity(k);
                        return true;
                    case R.id.nav_second_fragment:
                        Intent bi = new Intent(LastActivity.this, SearchActivity.class);
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

    private class LoadDetails extends AsyncTask<String, Void, Void> {

        ProgressDialog pDialog;
        JSONArray branches,count;
        LinkedHashMap<String, String> p;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(LastActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }


        @Override
        protected Void doInBackground(String... params) {

            p = new LinkedHashMap<>();
            p.put("NAME",params[0]);
            p.put("userid",sm.getUserID());
            WebRequest webreq = new WebRequest();
            String jsonStr = webreq.makeWebServiceCall(url, WebRequest.POST,p);
            ParseJSON(jsonStr);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();


            runOnUiThread(new Runnable() {
                public void run() {

                    tv1.setText(ISBN);
                    tv3.setText(BOOK_NAME);
                    tv5.setText(AUTHOR);
                    tv7.setText(PUBLISHER);
                    tv9.setText(RACK_NO);
                }
            });
        }


        private void ParseJSON(String json) {

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    branches = jsonObj.getJSONArray(ARRAY_NAME);
                    count = jsonObj.getJSONArray("TOTAL");
                    JSONObject o = branches.getJSONObject(0);
                    JSONObject t = count.getJSONObject(0);
                    ISBN = o.getString("ISBN");
                    BOOK_NAME =o.getString("BOOK_NAME");
                    AUTHOR = o.getString("AUTHOR");
                    PUBLISHER = o.getString("PUBLISHER");
                    RACK_NO =o.getString("SHELF");
                    total = t.getString("count");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
        }
    }
}

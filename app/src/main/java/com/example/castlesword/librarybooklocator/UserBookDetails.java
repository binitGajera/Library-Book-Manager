package com.example.castlesword.librarybooklocator;

import android.app.ProgressDialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.LinkedHashMap;

public class UserBookDetails extends AppCompatActivity {
    TextView tv12, tv13, tv14, tv15, tv16, tv17, tv18, tv19, tv20, tv21;
    private ActionBarDrawerToggle mToggle;
    private DrawerLayout mDrawer;
    private NavigationView navView;
    private String url = "http://bin2580.16mb.com/library_services/userbooks.php";
    public static final String ARRAY_NAME="BOOKS";
    private final static String TAG="SID";
    SessionManager sm;
    String namebook;
    String name;
    String date;
    String quantity;
    String author;
    String publisher;
    String delete;
    Button remove;
    Button renew;
    String date1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_book_details);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(UserBookDetails.this, mDrawer, R.string.open, R.string.close);
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigate();
        tv13 = (TextView) findViewById(R.id.textView13);
        tv15 = (TextView) findViewById(R.id.textView15);
        tv17 = (TextView) findViewById(R.id.textView17);
        tv19 = (TextView) findViewById(R.id.textView19);
        tv21 = (TextView) findViewById(R.id.textView21);
        sm = new SessionManager(getApplicationContext());
        String userid1= sm.getUserID();
        if(userid1=="")
        {
            Intent i= new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(i);
        }
        remove = (Button) findViewById(R.id.button6);
        /*renew = (Button) findViewById(R.id.button5);
        renew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date1 = tv15.getText().toString();
                Intent ui= new Intent(UserBookDetails.this,RenewBook.class);
                ui.putExtra("bookname",tv13.getText().toString());
                ui.putExtra("bookdate",tv15.getText().toString());
                startActivity(ui);
            }
        });*/
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeleteBook().execute(namebook);
            }
        });
        Intent books = getIntent();
        namebook = books.getStringExtra("bookName");
        new LoadDetails().execute(namebook);
        Toast.makeText(UserBookDetails.this, delete, Toast.LENGTH_SHORT).show();
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
                        Intent k = new Intent(UserBookDetails.this, MainActivity.class);
                        startActivity(k);
                        return true;
                    case R.id.nav_second_fragment:
                        Intent bi = new Intent(UserBookDetails.this, SearchActivity.class);
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
        JSONArray branches;
        LinkedHashMap<String, String> p;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(UserBookDetails.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }


        @Override
        protected Void doInBackground(String... params) {

            p = new LinkedHashMap<>();
            p.put("bookName",params[0]);
            p.put("userid", sm.getUserID());
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

                    tv13.setText(name);
                    tv15.setText(date);
                    tv17.setText(quantity);
                    tv19.setText(author);
                    tv21.setText(publisher);
                }
            });
        }


        private void ParseJSON(String json) {

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    branches = jsonObj.getJSONArray(ARRAY_NAME);
                    JSONObject o = branches.getJSONObject(0);

                    name = o.getString("name");
                    date =o.getString("date");
                    quantity = o.getString("quantity");
                    author = o.getString("Author");
                    publisher =o.getString("Publisher");



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
        }



    }

    private class DeleteBook extends AsyncTask<String, Void, Void> {

        ProgressDialog pDialog;
        LinkedHashMap<String, String> p;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(UserBookDetails.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }


        @Override
        protected Void doInBackground(String... params) {

            p = new LinkedHashMap<>();
            p.put("bookName",params[0]);
            p.put("userid", sm.getUserID());
            WebRequest webreq = new WebRequest();
            String jsonStr = webreq.makeWebServiceCall("http://bin2580.16mb.com/library_services/deletebook.php", WebRequest.POST,p);
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
                    //Toast.makeText(getApplicationContext(), delete, Toast.LENGTH_LONG).show();
                    Intent io= new Intent(UserBookDetails.this,MainActivity.class);
                    io.putExtra("delete", delete);
                    startActivity(io);
                }

            });
        }


        private void ParseJSON(String json) {

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    delete = jsonObj.getString("error");
                    //delete = error;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
        }



    }
}

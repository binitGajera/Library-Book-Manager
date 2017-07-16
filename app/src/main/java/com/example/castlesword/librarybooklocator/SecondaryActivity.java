package com.example.castlesword.librarybooklocator;

import android.app.ProgressDialog;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.view.MenuItemCompat;
//import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;


public class SecondaryActivity extends AppCompatActivity {

    //private static String url = "http://192.168.43.128:8888/android_connect/f.php";
    private static String url = "http://bin2580.16mb.com/library_services/search_function.php";

    private final static String TAG="SID";
    private static final String TAG_STATUS = "status";
    private static final String TAG_ERROR = "error_msg";
    private static final String TAG_SINGLE_BRANCH = "SINGLE_BRANCH";
    public static final String ARRAY_NAME="DATA";
    private Context context;
    private ListView lv;
    String[] al ;
    String q;
    String a;
    private ActionBarDrawerToggle mToggle;
    private DrawerLayout mDrawer;
    private NavigationView navView;
    private String errorMsg;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(SecondaryActivity.this, mDrawer, R.string.open, R.string.close);
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
        lv=(ListView)findViewById(R.id.listView);
        context = this;
        Intent in =  getIntent();
        q=in.getStringExtra("category");
        a=in.getStringExtra("query");


    //    Toast.makeText(this,q + " " + a,Toast.LENGTH_LONG).show();
        new LoadData().execute(q,a);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                int itemPosition     = position;
                String  itemValue    = (String) lv.getItemAtPosition(position);
                Intent last = new Intent(context,LastActivity.class);
                last.putExtra("extra",itemValue);
                startActivity(last);

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
                        Intent k = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(k);
                        return true;
                    case R.id.nav_second_fragment:
                        Intent bi = new Intent(getApplicationContext(), SearchActivity.class);
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






    private class LoadData extends AsyncTask<String, Void, Boolean> {

        ProgressDialog pDialog;
        JSONArray branches = null;
        LinkedHashMap<String, String> p;




        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(SecondaryActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
      //     Toast.makeText(SecondaryActivity.this,url, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            p = new LinkedHashMap<>();
            p.put("SEARCHBY",params[0]);
            p.put("VALUE",params[1]);
            WebRequest webreq = new WebRequest();
            String jsonStr = webreq.makeWebServiceCall(url, WebRequest.POST,p);
            Log.d(TAG,"json string"+jsonStr);
            Boolean bl =  ParseJSON(jsonStr);
            return bl;





        }

        @Override
        protected void onPostExecute(final Boolean result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();


            runOnUiThread(new Runnable() {
                public void run() {

                  //  if(result) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SecondaryActivity.this,
                                android.R.layout.simple_list_item_1, al);

                        lv.setAdapter(adapter);

            //        }
              //      else
                //        Toast.makeText(SecondaryActivity.this,"Sorry !! Requested Book could not be found !!! ",Toast.LENGTH_LONG).show();



                }
            });


        //    Toast.makeText(SecondaryActivity.this,result, Toast.LENGTH_SHORT).show();

        }


        private Boolean ParseJSON(String json) {

            if (json != null) {

                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if(jsonObj.getInt("success") == 1) {
                        branches = jsonObj.getJSONArray(ARRAY_NAME);

                        al = new String[branches.length()];
                        for (int i = 0; i < branches.length(); i++) {
                            JSONObject c = branches.getJSONObject(i);
                            String b = c.getString("name");
                            al[i] = b;

                        }
                    }
                    else
                    {
                        al=new String[1];
                        al[0]="sorry !! no related book found !!!";
                    }




                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return true;
            }
            return false;
        }



    }
}

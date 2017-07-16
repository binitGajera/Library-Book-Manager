package com.example.castlesword.librarybooklocator;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
//import org.codersvilla.sdpadmin.Event.AddEvent;

import java.util.ArrayList;
import java.util.List;

import android.widget.AdapterView.OnItemSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, OnItemSelectedListener //implements AdapterView.OnItemSelectedListener
{
    String department=null;
    private ActionBarDrawerToggle mToggle;
    SearchView searchView =null;
    String spinner_choice=null;
    private static String url = "http://bin2580.16mb.com/library_services/f.php";
    private DrawerLayout mDrawer;
    private NavigationView navView;
    private final static String TAG="SID";
    private static final String TAG_STATUS = "status";
    private static final String TAG_ERROR = "error_msg";
    private static final String TAG_SINGLE_BRANCH = "SINGLE_BRANCH";
    public static final String ARRAY_NAME="BRANCHES";
    private Context context;
    private ListView lv;
    String[] al;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(SearchActivity.this, mDrawer, R.string.open, R.string.close);
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
        lv=(ListView)findViewById(R.id.listView0);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        context= this;



        //spinner listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Book_Name");
        categories.add("Author");
        categories.add("ISBN");
        categories.add("Publisher");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        new LoadData().execute();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                int itemPosition  = position;
                String  itemValue = (String) lv.getItemAtPosition(position);
                Intent dept = new Intent(context,DepartmentActivity.class);
                dept.putExtra("dept",itemValue);
                startActivity(dept);


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
                        Intent k = new Intent(SearchActivity.this, MainActivity.class);
                        startActivity(k);
                        return true;
                    case R.id.nav_second_fragment:
                        Intent bi = new Intent(SearchActivity.this, SearchActivity.class);
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

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.search);

        searchView =(SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query){

        Intent search_data = new Intent(this,SecondaryActivity.class);
        search_data.putExtra("category",spinner_choice);
        search_data.putExtra("query",query);
        startActivity(search_data);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText){

        return false;
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        spinner_choice = parent.getItemAtPosition(position).toString();

    }


    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    private class LoadData extends AsyncTask<Void, Void, Void> {

        ProgressDialog pDialog;
        JSONArray branches = null;



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(SearchActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
//            Toast.makeText(SecondaryActivity.this,url, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            WebRequest webreq = new WebRequest();
            String jsonStr = webreq.makeWebServiceCall(url, WebRequest.GET);
            ParseJSON(jsonStr);
            Log.d(TAG,"json string"+jsonStr);

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
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchActivity.this,
                            android.R.layout.simple_list_item_1,al);

                    lv.setAdapter(adapter);
                }
            });

            //     Toast.makeText(DepartmentActivity.this,url, Toast.LENGTH_SHORT).show();

        }


        private void ParseJSON(String json) {


            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    branches = jsonObj.getJSONArray(ARRAY_NAME);

                    al=new String[branches.length()];
                    for (int i = 0; i < branches.length(); i++) {
                        JSONObject c = branches.getJSONObject(i);
                        String b = c.getString("name");
                        al[i]=b;

                    }




                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
        }



    }
}

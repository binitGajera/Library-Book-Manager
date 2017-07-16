package com.example.castlesword.librarybooklocator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RenewBook extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renew_book);
        final Intent ui= getIntent();
        String bookname = ui.getStringExtra("bookname");
        String bookdate = ui.getStringExtra("bookdate");
        TextView name= (TextView)findViewById(R.id.textView9);
        name.setText(bookname);
        Button renew= (Button)findViewById(R.id.renew);

        renew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name =ui.getStringExtra("bookname");
                String date1 = ((EditText)findViewById(R.id.editText1)).getText().toString();
                String quantity = "1";
                SessionManager sm= new SessionManager(getApplicationContext());
                String userid= sm.getUserID();
                if (!name.isEmpty() && !date1.isEmpty() && !quantity.isEmpty() && !userid.isEmpty()) {

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

package com.example.miroslavsanader.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    EditText emplId, name, email, acode, ccode;
    Spinner depts;
    RadioButton msex, fsex;
    Button reset, submit, login;
    CheckBox ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Database Login");
        emplId = (EditText) findViewById(R.id.editText11);
        name = (EditText) findViewById(R.id.editText12);
        email = (EditText) findViewById(R.id.editText13);
        acode = (EditText) findViewById(R.id.editText14);
        ccode = (EditText) findViewById(R.id.editText15);

        depts = (Spinner) findViewById(R.id.spinner2);

        msex = (RadioButton) findViewById(R.id.radioButton5);
        msex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fsex.setChecked(false);
            }
        });
        fsex = (RadioButton) findViewById(R.id.radioButton6);
        fsex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msex.setChecked(false);
            }
        });

        reset = (Button) findViewById(R.id.button6);
        submit = (Button) findViewById(R.id.button5);
        login = (Button) findViewById(R.id.login);
        reset.setOnClickListener(this);
        submit.setOnClickListener(this);
        login.setOnClickListener(this);

        ad = (CheckBox) findViewById(R.id.checkBox3);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        getContentResolver().delete(MyContentProvider.CONTENT_URI, null, null);
        super.onDestroy();
    }

    @Override
    public void onClick(View view){
        if(view == reset){
            reset();
        }
        else if(view == submit){
            if(!checkContents()){
                // Print toast, do not query database
                Log.i("db.MainActivity", "User failed to provide proper input for register.\n");
            }
            else{
                // User provided valid input, so we can now query the database
                String sex;
                String adaccess;
                String dept;
                if(msex.isChecked()){ sex = "MALE"; }
                else{ sex = "FEMALE"; }

                if(ad.isChecked()){ adaccess = "TRUE"; }
                else{ adaccess = "FALSE"; }

                dept = depts.getSelectedItem().toString();

                ContentValues vals = new ContentValues();
                vals.put(MyContentProvider.COLUMN_EMPLID, emplId.getText().toString());
                vals.put(MyContentProvider.COLUMN_NAME, name.getText().toString());
                vals.put(MyContentProvider.COLUMN_SEX, sex);
                vals.put(MyContentProvider.COLUMN_EMAIL, email.getText().toString().trim());
                vals.put(MyContentProvider.COLUMN_DEPT, dept);
                vals.put(MyContentProvider.COLUMN_ACCESS, acode.getText().toString());
                vals.put(MyContentProvider.COLUMN_AD, adaccess);

                String[] column_emplid = {MyContentProvider.COLUMN_EMPLID};
                String select_statement = MyContentProvider.COLUMN_EMPLID + " =?";
                String[] args = {emplId.getText().toString()};

                // If the user exists
                Cursor result = getContentResolver().query(MyContentProvider.CONTENT_URI, column_emplid,
                        select_statement, args ,null);

                if(result != null && result.getCount() > 0){
                    // If the user does exist
                    Toast.makeText(this, "Whoops, attempting to add the same user to the DB.", Toast.LENGTH_SHORT).show();
                    //reset();
                }
                else{
                    Uri temp = getContentResolver().insert(MyContentProvider.CONTENT_URI, vals);
                    Toast.makeText(this, "Added user to the DB!", Toast.LENGTH_SHORT).show();
                    //reset();
                }
                result.close();
            }
        }
        else{
            // Login query, simply query and see if the user exists
            if(!checkContents()) {
                Log.i("db.MainActivity", "User failed to provide proper input for login.\n");
            }
            else{
                // Check the user credentials and log them in
                String sex;
                String adaccess;
                String dept;
                if(msex.isChecked()){ sex = "MALE"; }
                else{ sex = "FEMALE"; }

                if(ad.isChecked()){ adaccess = "TRUE"; }
                else{ adaccess = "FALSE"; }

                dept = depts.getSelectedItem().toString();

                ContentValues vals = new ContentValues();
                vals.put(MyContentProvider.COLUMN_EMPLID, emplId.getText().toString().trim());
                vals.put(MyContentProvider.COLUMN_NAME, name.getText().toString().trim());
                vals.put(MyContentProvider.COLUMN_EMAIL, email.getText().toString().trim());
                vals.put(MyContentProvider.COLUMN_SEX, sex);
                vals.put(MyContentProvider.COLUMN_DEPT, dept);
                vals.put(MyContentProvider.COLUMN_ACCESS, acode.getText().toString().trim());
                vals.put(MyContentProvider.COLUMN_AD, adaccess);

                String[] column_contents = {MyContentProvider.COLUMN_EMPLID, MyContentProvider.COLUMN_NAME,
                        MyContentProvider.COLUMN_EMAIL, MyContentProvider.COLUMN_SEX, MyContentProvider.COLUMN_DEPT,
                        MyContentProvider.COLUMN_ACCESS, MyContentProvider.COLUMN_AD};
                String select_statement = MyContentProvider.COLUMN_EMPLID + " =?";
                String[] args = {emplId.getText().toString()};

                Cursor result = getContentResolver().query(MyContentProvider.CONTENT_URI, column_contents,
                        select_statement, args, null);
                boolean equals = true;

                if(result != null && result.getCount() > 0){
                    result.moveToFirst();
                    if(!result.getString(1).equals(name.getText().toString())){
                        equals = false;
                        Toast.makeText(this, "Names do not match.", Toast.LENGTH_SHORT).show();
                    }
                    if(!result.getString(2).equals(email.getText().toString())){
                        equals = false;
                        Toast.makeText(this, "Emails do not match.", Toast.LENGTH_SHORT).show();
                    }
                    if(!result.getString(3).equals(sex)){
                        equals = false;
                        Toast.makeText(this, "Sexes do not match.", Toast.LENGTH_SHORT).show();
                    }
                    if(!result.getString(4).equals(depts.getSelectedItem().toString())){
                        equals = false;
                        Toast.makeText(this, "Departments do not match.", Toast.LENGTH_SHORT).show();
                    }
                    if(!result.getString(5).equals(acode.getText().toString())){
                        equals = false;
                        Toast.makeText(this, "Access Code does not match.", Toast.LENGTH_SHORT).show();
                    }
                    if(!result.getString(6).equals(adaccess)){
                        equals = false;
                        Toast.makeText(this, "AD Access does not match.", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(this, "Error, invalid user credentials!", Toast.LENGTH_SHORT).show();
                    equals = false;
                }

                if(equals){
                    Toast.makeText(this, "User is now logged in!", Toast.LENGTH_SHORT).show();
                    reset();
                }
                result.close();
            }
        }
    }

    private boolean checkContents(){
        String toastOutput = "";
        if(!checkName(name.getText().toString())) {
            toastOutput += "Error! First/last name must be capitalized.\n";
            name.setTextColor(Color.RED);
        }

        if(!allCaps(emplId.getText().toString())) {
            toastOutput += "Error! EmployeeID must be all caps.\nIncorrect EmployeeID.\n";
            emplId.setTextColor(Color.RED);
        }

        if(msex.isChecked() || fsex.isChecked()){ /* Do nothing, one is valid */ }
        else{ toastOutput += "Error! Must select a sex.\n"; }

        if(!acode.getText().toString().equals(ccode.getText().toString())){
            toastOutput += "Error! Access codes do not match.\n";
            acode.setTextColor(Color.RED);
            ccode.setTextColor(Color.RED);
        }

        if(!toastOutput.equals("")){
            Toast.makeText(this, toastOutput, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean checkName(String s){
        String temp;
        StringTokenizer strtok = new StringTokenizer(s, " ");
        while(strtok.hasMoreTokens()){
            temp = strtok.nextToken();
            if(temp != null && Character.isUpperCase(temp.charAt(0))){continue;}
            else{return false;}
        }
        return true;
    }

    private boolean allCaps(String s){
        for(int i = 0; i < s.length(); i++){
            if(Character.isLowerCase(s.charAt(i)) && !Character.isDigit(s.charAt(i))){ return false; }
        }
        if(s == null){return false;}
        return true;
    }

    private void reset(){
        acode.setText("");
        acode.setTextColor(Color.BLACK);
        ccode.setText("");
        ccode.setTextColor(Color.BLACK);
        name.setText("");
        name.setTextColor(Color.BLACK);
        email.setText("");
        emplId.setText("");
        emplId.setTextColor(Color.BLACK);
        msex.setChecked(false);
        fsex.setChecked(false);
    }
}

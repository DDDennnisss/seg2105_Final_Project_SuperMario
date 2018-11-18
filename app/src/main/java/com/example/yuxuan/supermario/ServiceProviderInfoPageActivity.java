package com.example.yuxuan.supermario;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.common.internal.AccountType;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServiceProviderInfoPageActivity extends AppCompatActivity {


    private EditText editAddress;
    private EditText editPhoneNum;
    private EditText editNameOfCompany;
    private EditText editGeneralInfo;
    private EditText editLicensed;
    private Button saveButton;


    //FirebaseAuth mAuth;
    //FirebaseDatabase mDatabase;
    DatabaseReference databaseInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_info_page);
        editAddress = (EditText) findViewById(R.id.EditAddress);
        editPhoneNum = (EditText) findViewById(R.id.EditPhoneNum);
        editNameOfCompany = (EditText) findViewById(R.id.EditNameOfCompany);
        editGeneralInfo = (EditText) findViewById(R.id.EditGeneralInfo);
        editLicensed = (EditText) findViewById(R.id.BooleanLicensed);

        saveButton=(Button)findViewById(R.id.SaveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addInfo();
            }
        });

    }

    //TODO: add this service provider to database
    public void serviceProviderJump(){
        Intent intent = new Intent(getApplicationContext(),ServiceProviderMainPage.class);
        startActivityForResult(intent,0);
    }

    private void addInfo() {
        //getting the values to save
        String address = editAddress.getText().toString().trim();
        String phoneNum = editPhoneNum.getText().toString().trim();
        String nameOfCompany = editNameOfCompany.getText().toString().trim();
        String generalInfo = editGeneralInfo.getText().toString().trim();
        String licensed = editLicensed.getText().toString().trim();

        //checking if the value is provided
        if (!TextUtils.isEmpty(address)||!TextUtils.isEmpty(phoneNum)||!TextUtils.isEmpty(nameOfCompany)||
                !TextUtils.isEmpty(generalInfo)||!TextUtils.isEmpty(licensed)) {

            //getting a unique id using push().getKey() method
            //it will create a unique id and we will use it as the Primary Key for our Product
            String id = databaseInfo.push().getKey();

            //creating an Object
            ServiceProviderInfo info = new ServiceProviderInfo(id,address,phoneNum,nameOfCompany,generalInfo,licensed);

            //Saving the Product
            databaseInfo.child(id).setValue(info);

            //setting edittext to blank again
            editAddress.setText("");
            editPhoneNum.setText("");
            editNameOfCompany.setText("");
            editGeneralInfo.setText("");
            editLicensed.setText("");


            //displaying a success toast
            Toast.makeText(this, "info added", Toast.LENGTH_LONG).show();
        } else {
            //if the value is not given displaying a toast
            Toast.makeText(this, "Please enter a necessary text(s)", Toast.LENGTH_LONG).show();
        }
    }
}

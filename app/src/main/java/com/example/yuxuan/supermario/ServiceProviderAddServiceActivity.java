package com.example.yuxuan.supermario;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/*
We are going to use Lab5 productCatalog code as our source code
 */


public class ServiceProviderAddServiceActivity extends AppCompatActivity {
    EditText editTextService;
    EditText editTextHourRate;
    Button buttonAddService;
    ListView listViewServices;

    Intent intentss;
    List<Service> services;
    DatabaseReference databaseServices;
    DatabaseReference databaseProviderService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_add_service);

        //FirebaseOptions options = new FirebaseOptions.Builder().setApplicationId("1:361969841457:android:3614f17d290290cc").setApiKey("AIzaSyA4IKqllj3j0Bv1gjDjIrlQBIS8AYSuasE").setDatabaseUrl("https://seg2105finalprojectsupermario.firebaseio.com").build();
        //FirebaseApp.initializeApp(this,options,"Services");
        //FirebaseApp secondary = FirebaseApp.getInstance("Services");
        //databaseServices = FirebaseDatabase.getInstance(secondary).getReference("Services");
        databaseServices = FirebaseDatabase.getInstance().getReference("Services");
        databaseProviderService = FirebaseDatabase.getInstance().getReference("ProviderServices");

        editTextService = (EditText)findViewById(R.id.editTextService);
        editTextHourRate = (EditText)findViewById(R.id.editTextHourRate);
        listViewServices = (ListView)findViewById(R.id.listViewServices);
        buttonAddService = (Button)findViewById(R.id.addButton);
        intentss = getIntent();

        services = new ArrayList<>();

        //adding an onClickListener to button

        buttonAddService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addService();
            }
        });

        //adding an onClickListener to the item in the item view list
        listViewServices.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Service service = services.get(position);
                showUpdateDeleteDialog(service);

                return true;
            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();
        databaseServices.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                services.clear();
                //update every Service Snapshot
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    String serviceId =snapshot.child("serviceId").getValue(String.class);
                    String typeOfService =snapshot.child("typeOfService").getValue(String.class);
                    Double hourRate =snapshot.child("hourRate").getValue(Double.class);
                    Service service = new Service(serviceId,typeOfService,hourRate);
                    //Service service = snapshot.getValue(Service.class);
                    services.add(service);
                }

                ServiceList servicesAdapter = new ServiceList(ServiceProviderAddServiceActivity.this, services);
                listViewServices.setAdapter(servicesAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseProviderService.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //update every Service Snapshot
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    //showing the service is updated or deleted
    private void showUpdateDeleteDialog(Service service){
        final Service service1 = service;
        final String sId = service.getServiceId();
        String serv = service.getTypeOfService();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.servicesprovider_services_add,null);
        dialogBuilder.setView(dialogView);

        final EditText editTextService = (EditText) dialogView.findViewById(R.id.editTextService);
        final TextView textViewProductname = (TextView) dialogView.findViewById(R.id.textViewServices);
        textViewProductname.setText("Services Name:"+serv);
        final TextView textViewPrices = (TextView) dialogView.findViewById(R.id.textViewPrices);
        textViewPrices.setText("prices: "+service.getHourRate());
        final EditText editTextHourRate = (EditText) dialogView.findViewById(R.id.editTextHourRate);
        final EditText editTextStart = (EditText) dialogView.findViewById(R.id.editTextStart);
        final EditText editTextEnd = (EditText) dialogView.findViewById(R.id.editTextEnd);
        final Button buttonUpdateService = (Button) dialogView.findViewById(R.id.buttonUpdateService);
        final Button buttonDeleteService = (Button) dialogView.findViewById(R.id.buttonDeleteService);
        final Button buttonAddtoserver = (Button) dialogView.findViewById(R.id.buttonAddtoService);
        final Spinner spinner = (Spinner) dialogView.findViewById(R.id.spinner_date) ;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.planets, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        dialogBuilder.setTitle(serv);
        final AlertDialog alert = dialogBuilder.create();
        alert.show();

        buttonUpdateService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String serviceName = editTextService.getText().toString().trim();
                double serviceHourRate = Double.parseDouble(String.valueOf(editTextHourRate.getText().toString()));
                if(!TextUtils.isEmpty(serviceName)){
                    updateService(sId,serviceName,serviceHourRate);
                    alert.dismiss();
                }
            }
        });

        buttonDeleteService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteService(sId);
                alert.dismiss();
            }
        });
        buttonAddtoserver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dateService = editTextStart.getText().toString().trim();
                String dateEnd = editTextEnd.getText().toString().trim();
                String date = String.valueOf(spinner.getSelectedItem());
                if(!TextUtils.isEmpty(dateService)||!TextUtils.isEmpty(dateEnd)){
                    try {
                        addtoService(service1,dateService, dateEnd, date);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    alert.dismiss();
                }
            }
        });
    }

    //TODO:create a service in create button //lab5
    //TODO:delete a service in delete button //lab5
    //TODO:edit a service in update button //lab5
    //Adding service to the item view list
    private void addService(){
        String serviceName = editTextService.getText().toString().trim();
        double hourRate = Double.parseDouble(String.valueOf(editTextHourRate.getText().toString()));

        if(!TextUtils.isEmpty(serviceName)){
            String serviceID = databaseServices.push().getKey();
            Service service = new Service(serviceID,serviceName,hourRate);
            databaseServices.child(serviceID).setValue(service);
            editTextService.setText("");
            editTextHourRate.setText("");
            Toast.makeText(this,"Service added",Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this,"Please enter a service",Toast.LENGTH_LONG).show();
        }
    }



    //Updating a service, mostly the price
    /**private void updateService(String sId, String serv, double price){

     }**/
    private void updateService(String sId, String serv, double price) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Services").child(sId);
        Service service = new Service(sId, serv, price);
        dR.setValue(service);
        Toast.makeText(getApplicationContext(), "Services Updated", Toast.LENGTH_LONG).show();
    }

    //Delete a service

    private void deleteService(String sId) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Services").child(sId);
        //Product product = new Product(id, name, price);
        dR.removeValue();
        Toast.makeText(getApplicationContext(), "Services Deleted", Toast.LENGTH_LONG).show();
        //return true;
    }

    public void addtoService(Service service,String dateStart,String dateEnd, String date) throws UnsupportedEncodingException {
        //todo: unfinished
        String provider = intentss.getStringExtra("username");
        String[] dates = new String[]{date,dateStart,dateEnd};
        Log.d("adtoServices", provider.toString());
        Log.d("DateInfo", dates.toString());
        String reference = "ProviderServices/"+Sha1.hash(provider.toString());
        Log.d("DateInfo", reference);
        ProSer Proser = new ProSer(provider,service,date, dateStart,dateEnd );

        DatabaseReference dF = FirebaseDatabase.getInstance().getReference(reference);
        String ProviderID = dF.push().getKey();
        dF.child(ProviderID).setValue(provider);
        dF.child(ProviderID).push().setValue(Proser);




    }

}

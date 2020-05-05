package com.example.clinincapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SecondActivity extends AppCompatActivity {
    private TextView upperLabel;
    private RadioGroup dateRadioGroup,slotRadioGroup;
    private Button checkSlotButton, bookSlotButton,checkAppStatusButton;
    private boolean firstTimeSelect;
    private String tomorrowStartBookingTime = "14:00:00";
//    private String slo1FinishTime ="14:00:00";
//    private String slot2FinishTime = "22:00:00";
    private final int maxSlot1Today = 8;
    private final int maxSlot2Today = 6;
    private final int maxSlot1Tomorrow = 5;
    private final int maxSlot2Tomorrow = 5;
    private int slot;
    private String day;

    private final int noOfPatientsCheckedInOneHour=4;
    private DBHelper db ;
    private patient currentPatient;
    private String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        upperLabel = findViewById(R.id.upperLabel);
        dateRadioGroup = findViewById(R.id.radioGroup);
        slotRadioGroup = findViewById(R.id.slotRadioGroup);
        checkSlotButton = findViewById(R.id.checkSlotButton);
        bookSlotButton = findViewById(R.id.bookSlotButton);
        checkAppStatusButton = findViewById(R.id.checkAppStatus);
        db = new DBHelper(this);

        name = getIntent().getStringExtra("Name");
        upperLabel.setText("Hello "+name+"!\nPlease select a date.");
        firstTimeSelect = true;
        slotRadioGroup.setVisibility(View.INVISIBLE);
        bookSlotButton.setVisibility(View.INVISIBLE);
        checkAppStatusButton.setVisibility(View.INVISIBLE);

        dateRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                slotRadioGroup.setVisibility(View.INVISIBLE);
                bookSlotButton.setVisibility(View.INVISIBLE);
                checkAppStatusButton.setVisibility(View.INVISIBLE);
                if(firstTimeSelect==false){
                    upperLabel.setText("Please select a date");
                }
            }
        });

        slotRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checkAppStatusButton.setVisibility(View.INVISIBLE);
                upperLabel.setText("Please select a slot");

            }
        });

        checkSlotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstTimeSelect = false;
                int dateSelectedId = dateRadioGroup.getCheckedRadioButtonId();
                if (dateSelectedId == -1) {
                    upperLabel.setText("Please select a date");
                }
                else if(dateSelectedId == R.id.tomorrow){
                    Date date = new Date(System.currentTimeMillis());
                    DateFormat df = new SimpleDateFormat("HH:mm:ss");
                    String timeOnly = df.format(date);
                    Log.d("CURRENT TIME",timeOnly);

                    if(tomorrowStartBookingTime.compareTo(timeOnly) > 0){
                        upperLabel.setText("Sorry! You can only book tomorrow's slot after "+tomorrowStartBookingTime);
                    }
                    else{
                        upperLabel.setText("Please select a slot");
                        slotRadioGroup.setVisibility(View.VISIBLE);
                        bookSlotButton.setVisibility(View.VISIBLE);
                    }
                }
                // ADD TOMORROW TIMING CONDITION FOR BOOKING TOMORROW'S SLOT
                else {
                    upperLabel.setText("Please select a slot");
                    slotRadioGroup.setVisibility(View.VISIBLE);
                    bookSlotButton.setVisibility(View.VISIBLE);
                }
            }
        });

        bookSlotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int slotSelectId = slotRadioGroup.getCheckedRadioButtonId();
                if(slotSelectId== -1){
                    upperLabel.setText("Please select a slot");

                }

                else{
                    int max;
                    if(slotSelectId==R.id.slotOne){

                        slot = 1;

                        if(dateRadioGroup.getCheckedRadioButtonId()==R.id.today){
                            max = maxSlot1Today;
                            day = "Today";

                        }
                        else{
                            max = maxSlot1Tomorrow;
                            day = "Tomorrow";
                        }
                    }
                    else{
                        slot = 2;
                        if(dateRadioGroup.getCheckedRadioButtonId()==R.id.today){
                            max = maxSlot2Today;
                            day = "Today";

                        }
                        else{
                            max = maxSlot2Tomorrow;
                            day = "Tomorrow";
                        }
                    }


                    // Check in database that the count of the patients in the particular slot is less than the
                    //maxSlot value of that slot.
                    //IF YES then ask to pick another slot.
                    //else book the patients's slot.
                    //For booking , create patient object and insert into database.

                    if(db.getSlotCount(slot,day) > max){
                        Log.d("Slot count",String.valueOf(db.getSlotCount(slot,day)));
                        upperLabel.setText(day+"'s slot "+slot+ " is full. Try booking another slot.");
//                        db.deleteAll();
//                        Log.d("Slot count",String.valueOf(db.getSlotCount(slot,day)));

                    }
                    else{
                        final patient patient = new patient();
                        patient.setName(name);
                        patient.setSlot(slot);
                        patient.setCheckedUp(0);
                        if(dateRadioGroup.getCheckedRadioButtonId()== R.id.today){
                            patient.setDay("Today");
                        }
                        else{
                            patient.setDay("Tomorrow");
                        }
                        db.addPatient(patient);
//                        db.deleteAll();

                        List<patient> patientList = db.getPatientList();
                        Log.d("CheckPoint","Patient List");
                        for(patient pat: patientList){
                            Log.d("Name ",pat.getName());
                            Log.d("No.",String.valueOf(pat.getPatientNo()));
                            Log.d("Slot",String.valueOf(pat.getSlot()));
                            Log.d("Day",pat.getDay());
                            Log.d("Checked up",String.valueOf(pat.isCheckedUp()));
                        }


                        currentPatient = db.getCurrentPatient();
                        if(currentPatient==null){
                            upperLabel.setText("Error!Please Try again.");
                            Toast.makeText(SecondActivity.this,"ERROR!",Toast.LENGTH_LONG).show();
                        }
                        else{
                            checkAppStatusButton.setVisibility(View.VISIBLE);
                            bookSlotButton.setVisibility(View.INVISIBLE);

                            upperLabel.setText("We booked your appointment with Dr. X\nPatient ID "+currentPatient.getPatientNo());
                        }



      //                  db.deleteAll();
                    }
                }
            }
        });

        checkAppStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPatient!=null){
                    Intent intent = new Intent(SecondActivity.this,WaitingStatus.class);
                    intent.putExtra("DAY",day);
                    intent.putExtra("SLOT",slot);
                    intent.putExtra("PATIENT ID",currentPatient.getPatientNo());
                    intent.putExtra("PATIENT NAME",currentPatient.getName());
                    intent.putExtra("noOfPatientsCheckedInOneHour",noOfPatientsCheckedInOneHour);
                    startActivity(intent);
                }
                else{
                    upperLabel.setText("Error!");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intent;
        switch (item.getItemId()) {

            case R.id.clearDataBase:
              db.deleteAll();
                intent = new Intent(SecondActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.checkPatient:
//              check patient today, of selected slot
                db.checkPatient(slot,"Today");
                return true;

            case R.id.bookAnotherApp:
                intent = new Intent(SecondActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

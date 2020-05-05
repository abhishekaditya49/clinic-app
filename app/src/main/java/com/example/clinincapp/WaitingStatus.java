package com.example.clinincapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class WaitingStatus extends AppCompatActivity {
private ListView waitingList;
private TextView currentPatientInfo;
private String day;
private int patient_ID;
private int slot;
private String patientName;
private DBHelper db;
private List<patient> patientList1=null;
private int noOfPatientsCheckedInOneHour;
private int waitingTimeOffset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_status);
        waitingList = findViewById(R.id.waitingList);
        currentPatientInfo = findViewById(R.id.currPatientInfo);
        db = new DBHelper(WaitingStatus.this);

        day = getIntent().getStringExtra("DAY");
        slot = getIntent().getIntExtra("SLOT",0);
        patient_ID=getIntent().getIntExtra("PATIENT ID",0);
        patientName = getIntent().getStringExtra("PATIENT NAME");
        noOfPatientsCheckedInOneHour = getIntent().getIntExtra("noOfPatientsCheckedInOneHour",0);
        currentPatientInfo.setText("Hi "+patientName+"! Your patient ID is "+patient_ID+"\n"+ day +", Slot No = "+slot);

        waitingTimeOffset = 60/noOfPatientsCheckedInOneHour;
//
//        List<patient> patientList = db.getPatientList();
//        Log.d("CheckPoint","Patient List in waiting activity");
//        for(patient pat: patientList){
//            Log.d("Name ",pat.getName());
//            Log.d("No.",String.valueOf(pat.getPatientNo()));
//            Log.d("Slot",String.valueOf(pat.getSlot()));
//            Log.d("Day",pat.getDay());
//            Log.d("Checked up",String.valueOf(pat.isCheckedUp()));
//        }
            patientList1 = db.getUnCheckedPatients(slot,day);
            if(patientList1.size()==0){
                currentPatientInfo.setText("Hi "+patientName+"!\nSorry we found no records. Try booking another appointment");

            }
            else{
                currentPatientInfo.setText("Hi "+patientName+"! Your patient ID is "+patient_ID+"\n"+ day +", Slot No = "+slot);
                customListAdapter customListAdapter = new customListAdapter();
                waitingList.setAdapter(customListAdapter);

            }






    }


    public class customListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
           return patientList1.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.waitinglistitem,null);
            TextView patientName = convertView.findViewById(R.id.patientName);
            TextView patientID = convertView.findViewById(R.id.patientID);
            TextView waitingTime = convertView.findViewById(R.id.waitingTime);
            ConstraintLayout constraintLayout = convertView.findViewById(R.id.waitingLayout);


            patientName.setText("Name: "+patientList1.get(position).getName());
            patientID.setText("Patient ID: "+patientList1.get(position).getPatientNo());

            if(position==0){
                if(patientList1.get(0).getDay().equals("Today")){
                    waitingTime.setText("Patient is inside doctor X's cabin.  ");

                }
                else if(patientList1.get(0).getDay().equals("Tomorrow")){
                    waitingTime.setText("Waiting time: 0 minutes");

                }
            }
            else{
                waitingTime.setText("Waiting time: "+(position*waitingTimeOffset)+" minutes.");
            }

            if(patientList1.get(position).getPatientNo()==patient_ID){
                constraintLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                patientName.setTextColor(getResources().getColor(R.color.white));
                patientID.setTextColor(getResources().getColor(R.color.white));
                waitingTime.setTextColor(getResources().getColor(R.color.white));
            }
            return convertView;
        }
    }
}

package com.example.clinincapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "CLINIC_APP";
    private static final String TABLE_NAME = "patientInfo";
    private static final String KEY_NAME = "name";
    private static final String SLOT = "slot";
    private static final String PATIENT_NO = "patientNo";
    private static final String CHECKED_UP = "checkedUp";
    private static final String DAY = "day";


    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + PATIENT_NO + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT NOT NULL,"
                + DAY + " TEXT NOT NULL,"
                + SLOT + " INT NOT NULL,"
                + CHECKED_UP + " INT NOT NULL);";
        db.execSQL(CREATE_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);
    }

    public void addPatient(patient patient) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(("SELECT* FROM " + TABLE_NAME), null);

        ContentValues values = new ContentValues();
        values.put(PATIENT_NO,cursor.getCount()+1);
        values.put(KEY_NAME, patient.getName());
        values.put(DAY, patient.getDay());
        values.put(SLOT, patient.getSlot());
        values.put(CHECKED_UP, patient.isCheckedUp());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<patient> getPatientList() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<patient> patientList = new ArrayList<>();
        Cursor cursor = db.rawQuery(("SELECT* FROM " + TABLE_NAME), null);
        if(cursor.getCount()==0){
            Log.d("Empty","Empty");
        }
        if (cursor.moveToFirst()) {
           do{
               patient patient = new patient();
               patient.setPatientNo(cursor.getInt(0));
               patient.setName(cursor.getString(1));
               patient.setDay(cursor.getString(2));
               patient.setSlot(cursor.getInt(3));
               patient.setCheckedUp(cursor.getInt(4));

               patientList.add(patient);

           }while(cursor.moveToNext());
        }

        return patientList;
    }
    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null,null);
        db.close();
    }

    public int getSlotCount(int slot,String day){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(("SELECT* FROM "+TABLE_NAME+" WHERE "+SLOT+"="+slot+" and "+DAY+" LIKE '%"+day+"'"),null);
       int count = cursor.getCount();
       db.close();
       return count;
    }

    public patient getCurrentPatient(){
        SQLiteDatabase  db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT* FROM "+TABLE_NAME+" WHERE "+PATIENT_NO+"=(SELECT MAX(" + PATIENT_NO + ") FROM " +TABLE_NAME+")",null);
        if(cursor.getCount()!=0){
            cursor.moveToFirst();
            Log.d("Cursor count",String.valueOf(cursor.getCount()));
            patient patient = new patient();
            patient.setPatientNo(cursor.getInt(cursor.getColumnIndex(PATIENT_NO)));
            patient.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            patient.setDay(cursor.getString(cursor.getColumnIndex(DAY)));
            patient.setSlot(cursor.getInt(cursor.getColumnIndex(SLOT)));
            patient.setCheckedUp(cursor.getInt(cursor.getColumnIndex(CHECKED_UP)));
            db.close();
            return patient;
        }
        else {
            db.close();
            return null;
        }
    }

    public patient getPatient(int patient_ID){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(("SELECT * FROM "+TABLE_NAME+" WHERE "+PATIENT_NO+"="+patient_ID),null);
        if(cursor!=null){
            patient patient = new patient();
            patient.setPatientNo(cursor.getInt(0));
            patient.setName(cursor.getString(1));
            patient.setDay(cursor.getString(2));
            patient.setSlot(cursor.getInt(3));
            patient.setCheckedUp(cursor.getInt(4));
            db.close();

            return patient;
        }


        else {
            db.close();
            return null;
        }
    }

    public List<patient> getUnCheckedPatients(int slot, String day){
        List<patient> patientList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(("SELECT* FROM "+TABLE_NAME+" WHERE "+SLOT+"="+slot+" and "+DAY+" LIKE '%"+day+"'"),null);
        if (cursor.moveToFirst()) {
            do{
                if(cursor.getInt(4)!=1){
                    patient patient = new patient();
                    patient.setPatientNo(cursor.getInt(0));
                    patient.setName(cursor.getString(1));
                    patient.setDay(cursor.getString(2));
                    patient.setSlot(cursor.getInt(3));
                    patient.setCheckedUp(cursor.getInt(4));

                    patientList.add(patient);
                }
            }while(cursor.moveToNext());
        }
        db.close();
        return patientList;
    }

    public void checkPatient(int slot, String day) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(("SELECT* FROM "+TABLE_NAME+" WHERE "+SLOT+"="+slot+" and "+DAY+" LIKE '%"+day+"'"),null);
        if(cursor.moveToFirst()){
            while(cursor.getInt(4)==1){
                    cursor.moveToNext();
            }
        }
        int pat_ID = cursor.getInt(0);
        Log.d("PAT ID",String.valueOf(pat_ID));

        db.execSQL("UPDATE "+TABLE_NAME+" SET "+CHECKED_UP+" = ? WHERE "+PATIENT_NO+"="+pat_ID,new String[]{String.valueOf(1)});
        db.close();

        List<patient> patientList = getPatientList();
        Log.d("inside checkPatient","Patient List");
        for(patient pat: patientList){
            Log.d("Name ",pat.getName());
            Log.d("No.",String.valueOf(pat.getPatientNo()));
            Log.d("Slot",String.valueOf(pat.getSlot()));
            Log.d("Day",pat.getDay());
            Log.d("Checked up",String.valueOf(pat.isCheckedUp()));
        }


    }
}

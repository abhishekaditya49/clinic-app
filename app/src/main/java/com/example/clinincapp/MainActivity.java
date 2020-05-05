package com.example.clinincapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String name;
    private EditText nameText;
    private Button nextButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nameText = findViewById(R.id.name);
        nextButton = findViewById(R.id.nextButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameText.getText().toString();
                if(!name.isEmpty()){
                    nameText.setText("");
                    Intent intent = new Intent(MainActivity.this,SecondActivity.class);
                    intent.putExtra("Name",name);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(MainActivity.this,"Please enter your name!",Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}

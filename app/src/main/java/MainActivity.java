package com.example.example7;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText editTextName;
    TextView etDate;
    Button buttonAddArtist,btnview;
    ListView listViewArtists;

    List<Artist> artists;

    //our database reference object
    DatabaseReference databaseArtists;

    private static final String TAG = "MainActivity";

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        databaseArtists = FirebaseDatabase.getInstance().getReference("artists");

        //getting views
        editTextName = (EditText) findViewById(R.id.editTextName);
        etDate = (TextView) findViewById(R.id.editDate);

       // listViewArtists = (ListView) findViewById(R.id.listViewArtists);

        buttonAddArtist = (Button) findViewById(R.id.buttonAddArtist);
        btnview = (Button)findViewById(R.id.btnview);

        //list to store artists
        //artists = new ArrayList<>();

        buttonAddArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //calling the method addArtist()
                //the method is defined below
                //this method is actually performing the write operation
                addArtist();
            }
        });

        mDisplayDate = findViewById(R.id.editDate);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal =Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month=month+1;
                Log.d(TAG, "onDateSet: mm/dd/yy: " + month + "/" + dayOfMonth + "/" + year);
                String date = dayOfMonth + "/" + month + "/" + year;
                mDisplayDate.setText(date);
            }
        };



        btnview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),showlist.class);
                startActivity(i);
            }
        });
    }
        private void addArtist() {
            //getting the values to save
            String name = editTextName.getText().toString().trim();
            String date = etDate.getText().toString().trim();

            //checking if the value is provided
            if (!TextUtils.isEmpty(name)) {

                //getting a unique id using push().getKey() method
                //it will create a unique id and we will use it as the Primary Key for our Artist
                String id = databaseArtists.push().getKey();

                //creating an Artist Object
                Artist artist = new Artist(name, date);

                //Saving the Artist
                databaseArtists.child(id).setValue(artist);

                //setting edittext to blank again
                editTextName.setText("");

                //displaying a success toast
                Toast.makeText(this, "Artist added", Toast.LENGTH_LONG).show();
            } else {
                //if the value is not given displaying a toast
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
            }

        }

    /*@Override
    protected void onStart() {
        super.onStart();
        databaseArtists.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous artist list
                artists.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Artist artist = postSnapshot.getValue(Artist.class);
                    //adding artist to the list
                    artists.add(artist);
                }

                //creating adapter
                ArtistList artistAdapter = new ArtistList(MainActivity.this, artists);
                //attaching adapter to the listview
                listViewArtists.setAdapter(artistAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/
}


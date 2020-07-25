package com.example.example7;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class showlist extends AppCompatActivity {

    ListView listViewArtists;

    List<Artist> artists = new ArrayList<>();

    //our database reference object
    DatabaseReference databaseArtists;


    private static final String TAG = "MainActivity";
    Button selectDate;
    TextView Date;
    DatePickerDialog.OnDateSetListener mDateSetListener;

    ArtistList artistAdapter; //object of adapter class



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showlist);

        selectDate = findViewById(R.id.btnDate);
        Date = findViewById(R.id.tvSelectedDate);

        databaseArtists = FirebaseDatabase.getInstance().getReference("artists");
        listViewArtists = (ListView) findViewById(R.id.listViewArtists);


        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal =Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(showlist.this,
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
                Date.setText(date);
            }
        };


        //EditText code
        Date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                artistAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
     //method for showing details from firebase to listview
    @Override
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
                artistAdapter =new ArtistList(showlist.this, artists);
                //attaching adapter to the listview
                listViewArtists.setAdapter(artistAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

   //Adapter class for custom view

    public class ArtistList extends BaseAdapter implements Filterable {
        List<Artist> mDisplay;
        List<Artist> mOriginal;
        LayoutInflater inflater;

        public ArtistList(Context context, List<Artist> artists) {
            inflater = LayoutInflater.from(context);
            this.mDisplay = artists;
            this.mOriginal = artists;
        }


        @Override
        public int getCount() {
            return mDisplay.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        private class ViewHolder {
            LinearLayout llContainer;
            TextView textViewName, textViewGenre;
        }


        //view methode for custom view

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {

                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.layout_artist_list, null);
                holder.llContainer = (LinearLayout) convertView.findViewById(R.id.llContainer);
                holder.textViewName = (TextView) convertView.findViewById(R.id.textViewName);
                holder.textViewGenre = (TextView) convertView.findViewById(R.id.textViewGenre);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.textViewName.setText(mDisplay.get(position).artistName);
            holder.textViewGenre.setText(mDisplay.get(position).date);

            holder.llContainer.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    // Toast.makeText(this, mDisplay.get(position).artistName, Toast.LENGTH_SHORT).show();
                }
            });

            return convertView;

        }

        //filter methode for filter listview

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    mDisplay = (List<Artist>) results.values; // has the filtered values
                    notifyDataSetChanged();
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                    List<Artist> FilteredArrList = new ArrayList<>();

                    if (mOriginal == null) {
                        mOriginal = new ArrayList<>(mDisplay); // saves the original data in mOriginalValues
                    }

                    if (constraint == null || constraint.length() == 0) {

                        // set the Original result to return
                        results.count = mOriginal.size();
                        results.values = mOriginal;
                    } else {

                        constraint = constraint.toString();
                        for (int i = 0; i < mOriginal.size(); i++) {
                            String data = mOriginal.get(i).date;
                            if (data.startsWith(constraint.toString())) {
                                FilteredArrList.add(new Artist(mOriginal.get(i).artistName, mOriginal.get(i).date));
                            }
                        }

                        results.count = FilteredArrList.size();
                        results.values = FilteredArrList;
                    }
                    return results;
                }
            };
            return filter;
        }
    }
}




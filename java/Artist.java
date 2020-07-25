package com.example.example7;


public class Artist {

    public String artistName;
    public String date;


    public Artist( String artistName, String date) {

        this.artistName = artistName;
        this.date = date;
    }


    public String getArtistName() {
        return artistName;
    }

    public String getDate() {
        return date;
    }

    public Artist() {
    }
}

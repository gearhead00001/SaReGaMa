package com.example.saregama.sr_essentials;

public class MusicNotes {
    public double duration; // s
    public double freq; // hz
    public MusicNotes[] teamNotes;

    MusicNotes(double freq, double duration){
        this.freq = freq;
        this.duration = duration;
        this.teamNotes = null;
    }

    MusicNotes(MusicNotes[] teamNotes){
        this.teamNotes = teamNotes;
    }

}

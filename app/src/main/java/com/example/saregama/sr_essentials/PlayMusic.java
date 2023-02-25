package com.example.saregama.sr_essentials;

import android.util.Log;

public class PlayMusic {
    public MusicNotes[] musicNotes;

    public PlayMusic(){
        musicNotes = this.getMusic();
    }

    public MusicNotes[] getMusic(){
        int totalNotes = 16;
        MusicNotes[] anyMusic = new MusicNotes[totalNotes];
        anyMusic[0] = new MusicNotes(196, 0.25);
        anyMusic[1] = new MusicNotes(247, 0.5);
        anyMusic[2] = new MusicNotes(262, 0.25);
        anyMusic[3] = new MusicNotes(294, 0.5);
        anyMusic[4] = new MusicNotes(262, 0.25);
        anyMusic[5] = new MusicNotes(247, 0.5);
        anyMusic[6] = new MusicNotes(220, 0.25);
        anyMusic[7] = new MusicNotes(247, 0.5);

        anyMusic[8] = new MusicNotes(247, 0.25);
        anyMusic[9] = new MusicNotes(247, 0.5);
        anyMusic[10] = new MusicNotes(220, 0.25);
        //sim Note
        anyMusic[11] = new MusicNotes(new MusicNotes[]{
            new MusicNotes(262, 0.125),
            new MusicNotes(247, 0.125)
        });
        anyMusic[12] = new MusicNotes(220, 0.25);
        anyMusic[13] = new MusicNotes(196, 0.5);
        anyMusic[14] = new MusicNotes(175, 0.25);
        anyMusic[15] = new MusicNotes(196, 0.5);


        return anyMusic;
    }

    public void leaveGap(int millis){
        Log.d("Gap: ",""+millis);
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void playSequence(MusicNotes[] Notes, int curr, int end){
        if(curr < end){
            if(Notes[curr].teamNotes == null){
                Log.d("Music - ","Freq : "+ Notes[curr].freq+" Dur :"+Notes[curr].duration+" "+curr+" "+ end +" ");
                PlayTones tone = new PlayTones(Notes[curr]);
                tone.playTone();
                switch (String.valueOf(Notes[curr].duration)){
                    case "0.125" : if(curr!=end-1 && Notes[curr+1].duration == 0.125){
                                        leaveGap(62);
                                     }else{
                                         leaveGap(125);
                                    }
                                break;
                    case "0.25" : leaveGap(125);
                        break;
                    default: leaveGap(250);
                }
            }else{
                playSequence(Notes[curr].teamNotes, 0, Notes[curr].teamNotes.length);
            }
            playSequence(Notes, curr+1, end );
        }
    }

    public void startMusic(){
        playSequence(musicNotes, 0, musicNotes.length);
        Log.d("Hey", "End reached...");
    }
}

package com.example.saregama.sr_essentials;

public class PlayTones {
    public static int sampleRate = 8000; //kHz
    private double sample[];
    private byte generatedSnd[] ;
    private MusicNotes musicNote;
    private int numSamples;

    public PlayTones(MusicNotes musicNote){
        this.musicNote = musicNote;
        this.createTone();
    }

    private void createTone(){
        this.generateSamples();
        this.doPCM16bitEncoding();
    }

    private void generateSamples(){
        numSamples = (int) Math.ceil(musicNote.duration * sampleRate);
        sample = new double[numSamples];
        generatedSnd = new byte[2 * numSamples];

        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            // sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/musicNote.freq));
            // sample[i] = Math.sin((2 * Math.PI - .001) * i / (sampleRate/musicNote.freq));
            sample[i] = 2*(i%(sampleRate/musicNote.freq))/(sampleRate/musicNote.freq)-1;
        }

    }

    private void doPCM16bitEncoding(){

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        int ramp = numSamples / 20;

        for (int i = 0; i < ramp; i++) {
            // scale to maximum amplitude
            final short val = (short) ((sample[i] * 32767) * i / ramp);
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }

        for (int i = ramp; i < numSamples - ramp; i++) {
            // scale to maximum amplitude
            final short val = (short) ((sample[i] * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }

        for (int i = numSamples - ramp; i < numSamples; i++) {
            // scale to maximum amplitude
            final short val = (short) ((sample[i] * 32767) * (numSamples - i) / ramp);
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }

    }

    public void playTone(){


    }
}

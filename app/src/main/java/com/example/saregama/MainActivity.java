package com.example.saregama;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.saregama.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                Log.d("DBG - ","**************** INVOKED *****************");

                double[][] notes = new double[][]{{196, 0.25}, {247, 0.5}, {262, 0.25}, {294, 0.5}, {262, 0.25}, {247, 0.5}, {262, 0.25}, {247, 0.5}};
                for(int i=0; i<notes.length;i++) {
                    Log.d("DBG - ","Exec "+ (i+1) +": "+ notes[i][0] + " " + notes[i][1] + "");
                    playTones(notes[i][0], notes[i][1]);
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void playTones(double freq, double t){

        /* START*/

        double duration = t;            // seconds
        double freqOfTone = freq;       // hz
        int sampleRate = 44100;          // a number

        double dnumSamples = duration * sampleRate;
        dnumSamples = Math.ceil(dnumSamples);
        int numSamples = (int) dnumSamples;
        double sample[] = new double[numSamples];
        byte generatedSnd[] = new byte[2 * numSamples];


        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            // sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone));
            // sample[i] = Math.sin((2 * Math.PI - .001) * i / (sampleRate/freqOfTone));
            sample[i] = 2*(i%(sampleRate/freqOfTone))/(sampleRate/freqOfTone)-1;
        }

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

        new Thread(){
            @Override
            public void run() {

                AudioTrack audioTrack = null;                                    // Get audio track
                try {
                    audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                            sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                            AudioFormat.ENCODING_PCM_16BIT, (int)numSamples*2,
                            AudioTrack.MODE_STATIC);
                    audioTrack.write(generatedSnd, 0, generatedSnd.length);        // Load the track
                    audioTrack.play();                                             // Play the track
                }
                catch (Exception e){
                    Log.d("DBG - ","**************** ERR OCCURRED *****************");
                    return;
                }

                int x =0;
                do{                                                              // Monitor playback to find when done
                    if (audioTrack != null)
                        x = audioTrack.getPlaybackHeadPosition();
                    else
                        x = numSamples;
                } while (x<numSamples);

                if (audioTrack != null) audioTrack.release();

            }
        }.start();


        /*END*/

    }

}


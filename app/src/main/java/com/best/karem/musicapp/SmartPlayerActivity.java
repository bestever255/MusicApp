package com.best.karem.musicapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class SmartPlayerActivity extends AppCompatActivity {

    private RelativeLayout relativeLayout;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;


    private String keeper = "";

    private TextView songNameTxt;
    private ImageView previous, pausePlay, next;
    private Button voiceModeBtn;
    private LinearLayout lowerLayout;

    private String mode = "on";

    private MediaPlayer mediaPlayer;
    private int position;
    private ArrayList<File> mySongs;
    private String songName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkVoiceCommandPermission();
        relativeLayout = findViewById(R.id.parentRelativeLayout);

        songNameTxt = findViewById(R.id.song_name);
        previous = findViewById(R.id.previous);
        pausePlay = findViewById(R.id.play_pause);
        next = findViewById(R.id.next);
        voiceModeBtn = findViewById(R.id.voice_enabled_btn);
        lowerLayout = findViewById(R.id.linear_lower);


        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(SmartPlayerActivity.this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        getIntentValuesAndPlay();

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mediaPlayer.getCurrentPosition() > 0) {

                    playPreviousSong();

                }

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mediaPlayer.getCurrentPosition() > 0) {
                    playNextSong();

                }
            }
        });

        pausePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PlayPauseSong();

            }
        });

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {

                ArrayList<String> matchesFound = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if (matchesFound != null) {
                    // We have a command

                    if (mode.equals("on")) {

                        keeper = matchesFound.get(0);

                        if (keeper.equals("pause the song")) {

                            PlayPauseSong();
                            Toast.makeText(SmartPlayerActivity.this, "Song Paused", Toast.LENGTH_SHORT).show();

                        } else if (keeper.equals("play the song")) {

                            PlayPauseSong();
                            Toast.makeText(SmartPlayerActivity.this, "Song Played", Toast.LENGTH_SHORT).show();
                        } else if (keeper.equals("play next song")) {

                            playNextSong();

                        } else if (keeper.equals("play previous song")) {

                            playPreviousSong();

                        }
                    }
                }

            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        speechRecognizer.startListening(speechRecognizerIntent);
                        keeper = "";
                        break;

                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                        break;


                }

                return false;

            }
        });

        voiceModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mode.equals("on")) {

                    mode = "off";
                    voiceModeBtn.setText("Voice Enabled Mode - OFF");
                    lowerLayout.setVisibility(View.VISIBLE);
                } else if (mode.equals("off")) {

                    mode = "on";
                    voiceModeBtn.setText("Voice Enabled Mode - ON");
                    lowerLayout.setVisibility(View.INVISIBLE);
                }

            }
        });
    }

    private void checkVoiceCommandPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!(ContextCompat.checkSelfPermission(SmartPlayerActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {

                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package: " + getPackageName()));
                startActivity(intent);
                finish();
            }

        }

    }

    private void getIntentValuesAndPlay() {

        if (mediaPlayer != null) {

            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("song");
        mySongs.get(position).getName();

        String songName = intent.getStringExtra("name");
        songNameTxt.setText(songName);
        songNameTxt.setSelected(true);

        position = bundle.getInt("position", 0);
        Uri uri = Uri.parse(mySongs.get(position).toString());

        mediaPlayer = MediaPlayer.create(SmartPlayerActivity.this, uri);
        mediaPlayer.start();


    }

    private void PlayPauseSong() {

        if (mediaPlayer.isPlaying()) {

            pausePlay.setImageResource(R.drawable.play);
            mediaPlayer.pause();

        } else {

            pausePlay.setImageResource(R.drawable.pause);
            mediaPlayer.start();
        }

    }

    private void playNextSong() {

        mediaPlayer.pause();
        mediaPlayer.stop();
        mediaPlayer.release();

        position = ((position + 1) % mySongs.size());

        Uri uri = Uri.parse(mySongs.get(position).toString());

        mediaPlayer = MediaPlayer.create(SmartPlayerActivity.this, uri);

        songName = mySongs.get(position).toString();
        songNameTxt.setText(songName);


        mediaPlayer.start();

        if (mediaPlayer.isPlaying()) {

            pausePlay.setImageResource(R.drawable.pause);

        } else {

            pausePlay.setImageResource(R.drawable.play);
        }

    }

    private void playPreviousSong() {

        mediaPlayer.pause();
        mediaPlayer.stop();
        mediaPlayer.release();

        position = ((position - 1) < 0 ? (mySongs.size() - 1) : (position - 1));

        Uri uri = Uri.parse(mySongs.get(position).toString());

        mediaPlayer = MediaPlayer.create(SmartPlayerActivity.this, uri);

        songName = mySongs.get(position).toString();
        songNameTxt.setText(songName);

        mediaPlayer.start();

        if (mediaPlayer.isPlaying()) {

            pausePlay.setImageResource(R.drawable.pause);

        } else {

            pausePlay.setImageResource(R.drawable.play);
        }


    }

}

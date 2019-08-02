package com.best.karem.musicapp;

import android.Manifest;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private String[] allItems;
    private ListView songsList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        songsList = findViewById(R.id.home_list);
        appExternalStoragePermission();


    }


    private void appExternalStoragePermission() {

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        displayAudioSongsName();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private ArrayList<File> readOnlyAudioSongs(File file) {

        ArrayList<File> arrayList = new ArrayList<>();

        File[] allfiles = file.listFiles();

        for (File indivdualFile : allfiles) {

            if (indivdualFile.isDirectory() && !indivdualFile.isHidden()) {

                arrayList.addAll(readOnlyAudioSongs(indivdualFile));

            } else if (indivdualFile.getName().endsWith(".mp3") || indivdualFile.getName().endsWith(".aac") || indivdualFile.getName().endsWith("wav")
                    || indivdualFile.getName().endsWith(".wma")) {

                arrayList.add(indivdualFile);
            }

        }

        return arrayList;

    }

    private void displayAudioSongsName() {

        final ArrayList<File> audioSongs = readOnlyAudioSongs(Environment.getExternalStorageDirectory());

        allItems = new String[audioSongs.size()];

        for (int songCounter = 0; songCounter < audioSongs.size(); songCounter++) {

            allItems[songCounter] = audioSongs.get(songCounter).getName();

        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(HomeActivity.this, android.R.layout.simple_list_item_1, allItems);
        songsList.setAdapter(arrayAdapter);

        songsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                String songName = songsList.getItemAtPosition(i).toString();

                Intent smartPlayerIntent = new Intent(HomeActivity.this, SmartPlayerActivity.class);
                smartPlayerIntent.putExtra("song", audioSongs);
                smartPlayerIntent.putExtra("name", songName);
                smartPlayerIntent.putExtra("position", i);
                startActivity(smartPlayerIntent);

            }
        });


    }


}

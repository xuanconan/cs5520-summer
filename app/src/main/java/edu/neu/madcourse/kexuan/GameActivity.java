package edu.neu.madcourse.kexuan;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GameActivity extends Activity {

    static private int mLargeIds[] = {R.id.large1, R.id.large2, R.id.large3, R.id.large4,
            R.id.large5, R.id.large6, R.id.large7, R.id.large8, R.id.large9,};
    static private int mSmallIds[] = {R.id.small1, R.id.small2, R.id.small3, R.id.small4,
            R.id.small5, R.id.small6, R.id.small7, R.id.small8, R.id.small9,};

    private ArrayList<String> targetString = new ArrayList<String>();
    private ArrayList<String> wordString = new ArrayList<String>();
    private ArrayList<String> dictionaryList = new ArrayList<String>();
    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Load wordlist into dictionaryList
        InputStream inputStream = getResources().openRawResource(R.raw.wordlist);
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        BufferedReader br = new BufferedReader(inputStreamReader);
        String rline;
        try {
            while ((rline = br.readLine()) != null) {
                dictionaryList.add(rline);
            }
            br.close();
            inputStreamReader.close();
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        //set nine words to store words with 9-12 chars
        for (String s : dictionaryList) {
            if (s.length() >= 9 && s.length() <= 12) {
                char[] arr = s.toCharArray();

                // shuffle the char in each word
                Collections.shuffle(Arrays.asList(arr));
                StringBuilder sb = new StringBuilder();
                for (char a : arr) {
                    sb.append(a);
                }
                targetString.add(sb.toString());
            }
        }

        Set<Integer> usedWord = new HashSet<Integer>();
        for (int i = 0; i < 9; i++) {
            int index = (int) Math.floor(Math.random() * targetString.size());

            while (usedWord.contains(index)) {
                index = (int) Math.floor(Math.random() * targetString.size());
            }
            usedWord.add(index);
            String c = targetString.get(index);
            wordString.add(c);
        }

        // start the game
        startGame();

        //set toggleButton for music
        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.button_toggle);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    mediaPlayer.setVolume(0.7f, 0.7f);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                } else {
                    mediaPlayer.pause();
                }
            }

        });
    }

    // start the game with 9 grids
    public void startGame() {
        for (int i = 0; i < 9; i++) {
            View outer = (View) findViewById(mLargeIds[i]);
            for (int j = 0; j < 9; j++) {
                Button small = (Button) outer.findViewById(mSmallIds[j]);
                small.setText(String.valueOf(wordString.get(i).charAt(j)));
            }
        }
    }

    // cancel the game when go back
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    //music control
    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer = MediaPlayer.create(this, R.raw.mario);
        mediaPlayer.setVolume(0.7f, 0.7f);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }



}

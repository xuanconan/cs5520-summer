package edu.neu.madcourse.kexuan;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class GameFragment extends Fragment {
    static private int mLargeIds[] = {R.id.large1, R.id.large2, R.id.large3, R.id.large4,
            R.id.large5, R.id.large6, R.id.large7, R.id.large8, R.id.large9,};
    static private int mSmallIds[] = {R.id.small1, R.id.small2, R.id.small3, R.id.small4,
            R.id.small5, R.id.small6, R.id.small7, R.id.small8, R.id.small9,};

    private TextView currWordView;
    private TextView totalScoreView;

    private ArrayList<String> selectedChar = new ArrayList<String>();
    private int totalScore = 0;

    private String currWord = "";
    private HashSet<String> wordSet = new HashSet<String>();

    private View rootView;
    private Button phase2;
    private int completedLargeBorad = 0;

    private List<Integer> groupList;
    private List<Integer> preButtonTag;

    private int currGroup = -1;
    private String currChar;
    private int currGroup2 = -1;
    private String currWord2 = "";
    private int phase1Score;

    private int soundWarning, soundInvalid, soundClick, soundSuccess, mariohurry;
    private SoundPool soundPool;
    private float mVolume = 0.7f;

    private Timer myTimer;
    private Chronometer chronometer;
    private boolean valid = true;
    private int phase1Time = 120;
    private int phase2Time = 60;

    private int highestScore=0;
    private String highestscoreWord="";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain Instance
        setRetainInstance(true);
        groupList = new ArrayList<>();
        preButtonTag = new ArrayList<>();

        // load different sound effects
        soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        soundSuccess = soundPool.load(getActivity(), R.raw.soundsuccess, 1);
        soundClick = soundPool.load(getActivity(), R.raw.soundclick, 1);
        soundWarning = soundPool.load(getActivity(), R.raw.soundwarning, 1);
        soundInvalid = soundPool.load(getActivity(), R.raw.soundinvalid, 1);
        mariohurry = soundPool.load(getActivity(), R.raw.mariohurry, 1);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.large_board, container, false);
        this.rootView = rootView;

        InputStream inputStream = getResources().openRawResource(R.raw.wordlist);
        InputStreamReader inputStreamReader = null;

        initViews(rootView);

        return rootView;
    }


    private void initViews(final View rootView) {

        currWordView = rootView.findViewById(R.id.text_word);
        totalScoreView = rootView.findViewById(R.id.text_score);

        chronometer = rootView.findViewById(R.id.chronometer_1);
        chronometer.start();

        myTimer = rootView.findViewById(R.id.timer);
        myTimer.initTime(phase1Time);
        myTimer.start();

        phase2 = (Button) rootView.findViewById(R.id.button_phase2);
        phase2.setVisibility(View.INVISIBLE);

        //when click each character
        for (int i = 0; i < 9; i++) {
            View outer = rootView.findViewById(mLargeIds[i]);
            for (int j = 0; j < 9; j++) {
                Button small = (Button) outer.findViewById(mSmallIds[j]);
                small.setTag(i + j * 10);
                small.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Button currButton = (Button) view;
                        int group = (int) currButton.getTag() % 10;

                        if (currGroup == -1) {

                            currGroup = group;
                            currButton.setSelected(true);
                            currChar = currButton.getText().toString();
                            selectedChar.add(currChar);
                            currWordView.setText(currChar);
                            currButton.setTextSize(20);
                            preButtonTag.add((int) currButton.getTag() / 10);

                            soundPool.play(soundClick, mVolume, mVolume, 1, 0, 1f);
                        }
                        else if (currGroup == group) {
                            currChar = currButton.getText().toString();

                            if (currButton.isSelected()) {

                                // remove the latest selected char
                                if (currChar == selectedChar.get(selectedChar.size() - 1)) {

                                    currButton.setSelected(false);
                                    selectedChar.remove(selectedChar.size() - 1);
                                    StringBuilder sb = new StringBuilder();
                                    for (String s : selectedChar) {
                                        sb.append(s);
                                    }
                                    currWord = sb.toString();
                                    currWordView.setText(currWord);
                                    currButton.setTextSize(14);

                                    //update the preButton
                                    preButtonTag.remove(preButtonTag.size() - 1);
                                    if (selectedChar.isEmpty()) {
                                        currGroup = -1;
                                    }
                                } else {
                                    // play non last button sound, same as non adjacent
                                    soundPool.play(soundWarning, mVolume, mVolume, 1, 0, 1f);
                                    String warning = "Delete from last character.";
                                    Toast.makeText(getActivity(), warning, Toast.LENGTH_SHORT).show();
                                }
                                return;

                            } else {
                                if (isNextButton((int) currButton.getTag() / 10, preButtonTag.get(preButtonTag.size() - 1))) {
                                    selectedChar.add(currChar);
                                    StringBuilder sb = new StringBuilder();
                                    for (String s : selectedChar) {
                                        sb.append(s);
                                    }
                                    currWord = sb.toString();
                                    currWordView.setText(currWord);

                                    soundPool.play(soundClick, mVolume, mVolume, 1, 0, 1f);
                                    currButton.setSelected(true);
                                    currButton.setTextSize(20);
                                    preButtonTag.add((int) currButton.getTag() / 10);

                                } else {
                                    soundPool.play(soundWarning, mVolume, mVolume, 1, 0, 1f);
                                    String warning = "Only adjacent button is allowed.";
                                    Toast.makeText(getActivity(), warning, Toast.LENGTH_SHORT).show();
//                                    System.out.println("Only adjacent button is allowed.");
                                }
                            }
                        }
                    }
                });

            }
        }

        //when click check word button
        rootView.findViewById(R.id.button_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDictionary();


                if (selectedChar.size() < 3) {
                    soundPool.play(soundInvalid, mVolume, mVolume, 1, 0, 1f);
                    currWordView.setText("Too short!");
                    return;
                }

                if (wordSet.contains(currWord) || wordSet.contains(currWord2)) {


                    int score = getTotalScore();

                    int tempHigh = highestScore;

                    if(score > tempHigh){
                        highestScore = score;
                        highestscoreWord = currWord;
                    }

                    if(!currWord2.equals("") && score > tempHigh) highestscoreWord = currWord2;

                    totalScore += getTotalScore();

                    totalScoreView.setText(String.valueOf(totalScore));
                    selectedChar.clear();

                    completedLargeBorad++;
                    soundPool.play(soundSuccess, mVolume, mVolume, 1, 0, 1f);

                    //reset current word when success
                    currWord = "";

                    //show phase2
                    if (phase2.getVisibility() == View.VISIBLE) {

                        for (int i = 0; i < 9; i++) {
                            View outer2 = rootView.findViewById(mLargeIds[i]);
                            for (int j = 0; j < 9; j++) {
                                Button small = (Button) outer2.findViewById(mSmallIds[j]);
                                small.setTextSize(20);

                                if (!small.isSelected()) {
                                    small.setText("");
                                }
                                if (small.isSelected()) {
                                    small.setTextColor(Color.GREEN);
                                }
                                small.setEnabled(false);
                            }
                        }

                        String gameEnd = getString(R.string.phase2_end);
                        Toast.makeText(getActivity(), gameEnd, Toast.LENGTH_LONG).show();

                        rootView.findViewById(R.id.button_phase2).setClickable(false);

                        GameEnd();

                    } else {

                        //only show selected char
                        View outer = rootView.findViewById(mLargeIds[currGroup]);

                        // reset the text size when phase 1 end
                        for (int i = 0; i < 9; i++) {
                            Button small = (Button) outer.findViewById(mSmallIds[i]);
                            small.setTextSize(20);
                        }

                        for (int i = 0; i < 9; i++) {
                            Button small = (Button) outer.findViewById(mSmallIds[i]);
                            if (!small.isSelected()) {
                                small.setText("");
                                small.setEnabled(false);
                            }
                            if (small.isSelected()) {
                                small.setTextColor(Color.GREEN);
                                small.setEnabled(false);
                            }
                        }

                        //when finish all grids, go to phase2
                        if (completedLargeBorad == 9) {

                            // hide all the unselected key
                            for (int i = 0; i < 9; i++) {
                                View outer3 = rootView.findViewById(mLargeIds[i]);
                                for (int j = 0; j < 9; j++) {
                                    Button small = (Button) outer3.findViewById(mSmallIds[j]);
                                    small.setTextSize(20);

                                    if (!small.isSelected()) {
                                        small.setText("");
                                    }
                                    small.setEnabled(false);
                                }
                            }

                            myTimer.stop();
                            chronometer.stop();
                            phase2Start();
                            String phase2Start = getString(R.string.phase2_begin);
                            Toast.makeText(getActivity(), phase2Start, Toast.LENGTH_LONG).show();
                        }
                    }
                    currGroup = -1;
                } else {
                    currWordView.setText("Invalid!");
                    soundPool.play(soundInvalid, mVolume, mVolume, 1, 0, 1f);
                }
            }
        });

        //when click pause button
        rootView.findViewById(R.id.button_pauseGame).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Button pauseGameButton = (Button) view;
                if (pauseGameButton.getText().equals("Pause")) {
                    pauseGameButton.setText("Resume");
                    hideGame(true);
                    myTimer.onPause();
                } else {
                    pauseGameButton.setText("Pause");
                    hideGame(false);
                    myTimer.onResume();
                }
            }
        });

        //Phase1  ends
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                //run until 60 seconds left
                if (SystemClock.elapsedRealtime() - chronometer.getBase()>= (phase1Time-phase2Time) * 1000) {
                    chronometer.stop();
                    myTimer.stop();

                    //start phase2
                    phase2Start();
                    String phase2Start = getString(R.string.phase2_begin);
                    Toast.makeText(getActivity(), phase2Start, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), chronometer.getText(), Toast.LENGTH_SHORT).show();

                    
                    for (int i = 0; i < 9; i++) {
                        View outer4 = rootView.findViewById(mLargeIds[i]);
                        for (int j = 0; j < 9; j++) {
                            Button small = (Button) outer4.findViewById(mSmallIds[j]);
                            small.setTextSize(20);
                            if (!small.isSelected() || (int) small.getTag() % 10 == currGroup) {
                                small.setText("");
                            }
                            small.setEnabled(false);
                        }
                    }
                    // clear the select char
                    selectedChar.clear();
                }
            }
        });
    }




    //hide game when pause, unhide when resume
    public void hideGame(boolean isHide) {
        for (int i = 0; i < 9; i++) {
            View outer = rootView.findViewById(mLargeIds[i]);
            if (isHide) {
                outer.setVisibility(View.INVISIBLE);
            } else {
                outer.setVisibility(View.VISIBLE);
            }
        }
    }

    //check if the button is adjacent to previous one
    public boolean isNextButton(int currButtPos, int preButtPos) {
        int preButtonX = preButtPos / 3;
        int preButtonY = preButtPos % 3;
        int currButtonX = currButtPos / 3;
        int currButtonY = currButtPos % 3;
        int x = currButtonX - preButtonX;
        int y = currButtonY - preButtonY;
        if (x * x + y * y <= 2) {
            return true;
        } else {
            return false;
        }
    }



    // Start phase2
    public void phase2Start() {

        phase1Score = totalScore;
        rootView.findViewById(R.id.button_phase2).setVisibility(View.VISIBLE);
        currWordView.setText("");
        soundPool.play(mariohurry, mVolume, mVolume, 1, 0, 1f);
        rootView.findViewById(R.id.button_phase2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                view.setEnabled(false);
                myTimer.initTime(phase2Time);
                myTimer.start();

                for (int i = 0; i < 9; i++) {
                    View outer = rootView.findViewById(mLargeIds[i]);

                    for (int j = 0; j < 9; j++) {
                        final Button small = (Button) outer.findViewById(mSmallIds[j]);

                        small.setTag(i);

                        //reset selected button size
                        if (!small.getText().toString().equals("")) {
                            small.setEnabled(true);
                            small.setTextSize(14);
                            small.setSelected(false);
                        }
                        if (small.getText().toString().equals("")) {
                            small.setTextSize(14);
                        }

//                        set up what happened when we click phase 2 button
                        small.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Button curButtonPhase2 = (Button) view;
                                currChar = curButtonPhase2.getText().toString();
                                int group = (int) small.getTag();

                                //when click phase2 button
                                if (currGroup2 == -1) {
                                    selectedChar.add(currChar);
                                    currGroup2 = group;
                                    groupList.add(group);
                                    currWordView.setText(currChar);
                                    curButtonPhase2.setSelected(true);
                                    curButtonPhase2.setTextSize(20);
                                    currGroup2 = group;

                                    soundPool.play(soundClick, mVolume, mVolume, 1, 0, 1f);

                                }
                                //if the button we click is in the same group of the last button
                                else if (group == currGroup2) {
                                    //if the button is selected
                                    if (curButtonPhase2.isSelected()) {

                                        // if the button is selected and is the same as the last one
                                        if (currChar.equals(selectedChar.get(selectedChar.size() - 1))) {
                                            //we can play a remove sound here

                                            curButtonPhase2.setTextSize(14);
                                            curButtonPhase2.setSelected(false);

                                            selectedChar.remove(selectedChar.size() - 1);
                                            StringBuilder sb = new StringBuilder();

                                            for (String s : selectedChar) {
                                                sb.append(s);
                                            }
                                            currWord2 = sb.toString();
                                            currWordView.setText(currWord2);
                                            groupList.remove(groupList.size() - 1);

                                            if (groupList.isEmpty()) {
                                                currGroup2 = -1;
                                            } else {
                                                currGroup2 = groupList.get(groupList.size() - 1);
                                            }
                                        } else {
                                            soundPool.play(soundWarning, mVolume, mVolume, 1, 0, 1f);
                                        }
                                    }
                                    else {
                                        soundPool.play(soundWarning, mVolume, mVolume, 1, 0, 1f);
                                        String warning = "Select one character in each grid.";
                                        Toast.makeText(getActivity(), warning, Toast.LENGTH_SHORT).show();
                                    }

                                }
                                // if the button is not in the same group as the last selected button
                                else if (group != currGroup2) {

                                    // You can't select button from same group
                                    if (curButtonPhase2.isSelected()) {
                                        soundPool.play(soundWarning, mVolume, mVolume, 1, 0, 1f);
                                        String warning = "Delete from the last character.";
                                        Toast.makeText(getActivity(), warning, Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    else {
                                        selectedChar.add(currChar);
                                        groupList.add(group);
                                        StringBuilder sb = new StringBuilder();

                                        for (String s : selectedChar) {
                                            sb.append(s);
                                        }

                                        currWord2 = sb.toString();
                                        currWordView.setText(currWord2);

                                        curButtonPhase2.setSelected(true);
                                        curButtonPhase2.setTextSize(20);
                                        currGroup2 = group;
                                        soundPool.play(soundClick, mVolume, mVolume, 1, 0, 1f);
                                    }
                                }
                            }
                        });

                    }

                }

            }
        });
    }


    //get the total valid words score, based on frequency of characters and length
    public int getTotalScore() {
        int score = 0;
        int l = selectedChar.size();
        for (String s : selectedChar) {
            for (int i = 0; i< s.length(); i++ ) {
                score += getCharScore(s.charAt(i))*l;
            }
        }
        return score;
    }

    // Score based on character frequency in English
    public int getCharScore(Character c) {

        if (c == 'e' || c == 't' || c == 'a' || c == 'o' || c == 'i' || c == 'n' || c == 's' || c == 'r' )
            return 1;

        if (c == 'h' || c == 'd' || c == 'l' || c == 'u' || c == 'c' || c == 'm' || c == 'f' )
            return 2;

        if (c == 'y' || c == 'w' || c == 'g' || c == 'p' || c == 'b' || c =='v')
            return 3;

        if (c == 'f' || c == 'h' || c == 'v' || c == 'w' || c == 'y' || c=='u' || c == 'k')
            return 4;

        if (c == 'x' || c == 'q')
            return 6;

        if (c == 'j' || c == 'z')
            return 8;

        return 0;
    }

    //load dictionary
    public void getDictionary() {

        InputStream is = this.getResources().openRawResource(R.raw.wordlist);
        wordSet = new HashSet<String>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();
            while (line != null) {
                wordSet.add(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void GameEnd() {
        SubmitScoreActivity submitScore = new SubmitScoreActivity();
        if(highestScore < 1){
            highestscoreWord = "N/A";
        }
        System.out.println("total score= "+totalScore);
        System.out.println("phase1 = "+phase1Score);
        System.out.println("highest score= "+highestScore);
        System.out.println("highest score word= "+highestscoreWord);
        System.out.println("end");

        Intent i = new Intent(getActivity(), SubmitScoreActivity.class);
        i.putExtra("totalScore",String.valueOf(totalScore));
        i.putExtra("phaseoneScore",String.valueOf(phase1Score));
        i.putExtra("highestScore",String.valueOf(highestScore));
        i.putExtra("highestscoreWord",highestscoreWord);
        startActivity(i);

        myTimer.initTime(0);
        myTimer.stop();
        chronometer.stop();
    }


}

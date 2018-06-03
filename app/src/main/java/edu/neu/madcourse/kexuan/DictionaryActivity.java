package edu.neu.madcourse.kexuan;

import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;


public class DictionaryActivity extends AppCompatActivity{

    private static String TAG = "DictionaryActivity";
    private EditText inputText;
    private Button clearButton;
    private Button acknowledgeButton;
    private ListView validWordListView;
    private HashSet<String> wordList = new HashSet<>();
    private ArrayList<String> validWordsList = new ArrayList<String>();
    private ListView inputTextListView;
    private ArrayAdapter<String> adapter;

    //Create activity
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Title bar
        setTitle("Test Dictionary");
        setContentView(R.layout.activity_dictionary);

        // Load dictionary
        loadDictionary();

        //Text input
        inputText = (EditText) findViewById(R.id.inputEditText);
        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String inputString = inputText.getText().toString();

                if(validWord(inputString.toLowerCase())) {
                    beep();
                    validWordsList.add(inputString.toLowerCase());

                    //To show the latest input on top
                    ArrayList<String> temp =(ArrayList<String>) validWordsList.clone();
                    Collections.reverse(temp);

                    adapter = new ArrayAdapter<String>(DictionaryActivity.this,
                            R.layout.activity_validword, temp);

                    inputTextListView = (ListView) findViewById(R.id.validWordListView);
                    inputTextListView.setFastScrollEnabled(true);
                    inputTextListView.setAdapter(adapter);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Go to acknowledgements page
        acknowledgeButton = (Button) findViewById(R.id.acknowledgementsButton);
        acknowledgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent acknowledge = new Intent(DictionaryActivity.this,
                        AcknowledgementsActivity.class);
                startActivity(acknowledge);
            }
        });


        //Clear input
        clearButton = (Button) findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                clear(view);
            }
        });
    }


    // load the dictionary into a hashset
    private void loadDictionary() {
        InputStream is = this.getResources().openRawResource(R.raw.wordlist);

        BufferedReader bf = null;
        try {
            bf = new BufferedReader(new InputStreamReader(is));
            String current = bf.readLine();
            while(current != null) {
                wordList.add(current);
                current = bf.readLine();
            }
            bf.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.v(DictionaryActivity.TAG,"Dictionary loaded.");
    }


    //Check if the word is valid
    private boolean validWord(String word) {
        return wordList.contains(word);
    }


    // Make a beep
    private void beep() {
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
        toneG.release();
    }


    // Clear editText and validWordList
    private void clear(View v) {
        validWordsList.clear();
        adapter = new ArrayAdapter<String>(DictionaryActivity.this, R.layout.activity_validword,validWordsList);
        inputTextListView = (ListView) findViewById(R.id.validWordListView);
        inputTextListView.setAdapter(adapter);
        inputText.getText().clear();
    }
}


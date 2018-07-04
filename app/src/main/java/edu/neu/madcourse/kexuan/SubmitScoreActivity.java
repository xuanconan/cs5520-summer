package edu.neu.madcourse.kexuan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class SubmitScoreActivity extends Activity {

    private static final String SERVER_KEY = "key=AAAARuVgC18:APA91bFrAzk7wRIoTSSCDEQZzB_2CQmP17ECfcRajldc3uewYLi0LMIi0tSCdkfhBcXM2fDhNRjfwfI5ibMJ2cY2MhMYzCjMm87K5CykXTxSI2ZSKgqNP0dvuCa44btct8oUhY8T8eoZ";
    private DatabaseReference db;
    private String token;
    private int totalScore;
    private int highestScore;
    private int phaseoneScore;
    private TextView score_text;
    private TextView name_text;
    private EditText username;
    private String highestscoreWord;
    private Button button_sumbit;
    private Button button_close;
    private Set<String> current_names;
    private Map<String, List<Player>> content_map;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);
        Bundle bundle = getIntent().getExtras();

        //get data from activity
        totalScore = Integer.valueOf(bundle.getString("totalScore"));
        phaseoneScore = Integer.valueOf(bundle.getString("phaseoneScore"));
        highestScore = Integer.valueOf(bundle.getString("highestScore"));
        highestscoreWord = bundle.getString("highestscoreWord");

        button_sumbit = (Button) findViewById(R.id.button_submit);
        button_close = (Button) findViewById(R.id.button_close);

        score_text = (TextView) findViewById(R.id.textView_score);
        name_text = (TextView) findViewById(R.id.textView_name);

        username = (EditText) findViewById(R.id.editText);

        db = FirebaseDatabase.getInstance().getReference();
        token = FirebaseInstanceId.getInstance().getToken();

        if (token.isEmpty()) {
            name_text.setText("No token found");
            button_sumbit.setEnabled(false);
        }

        score_text.setText("Phase 1: " + phaseoneScore + ", phase 2:  " + (totalScore - phaseoneScore) + "\nTotal score: " + totalScore);

        //get date information
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        final Player player = new Player();
        player.highestScoreWord = highestscoreWord;
        player.phaseoneScore = phaseoneScore;
        player.totalScore = totalScore;
        player.highestScore = highestScore;
        player.highestScoreWord = highestscoreWord;
        player.time = df.format(date);
        db.child("scoreboardUser").child(token).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, List<Player>>> t = new GenericTypeIndicator<HashMap<String, List<Player>>>() {
                };
                content_map = (Map<String, List<Player>>) dataSnapshot.getValue(t);
                if (content_map == null) {
                    current_names = new HashSet<>();
                    content_map = new HashMap<>();
                } else
                    current_names = content_map.keySet();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //save the username, check highest score
        button_sumbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = username.getText().toString();

                db.child("leaderboardUser").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        GenericTypeIndicator<HashMap<String, Player>> t = new GenericTypeIndicator<HashMap<String, Player>>() {
                        };
                        Map<String, Player> name_map = dataSnapshot.getValue(t);
                        if (name_map == null)
                            name_map = new HashMap<>();
                        if (current_names.contains(name)) {
                            content_map.get(name).add(player);
                            db.child("scoreboardUser").child(token).setValue(content_map);
                            Player lastInfo = name_map.get(name);
                            if (player.totalScore > lastInfo.totalScore) {
                                //create a thread
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        onGetNewHighScore(totalScore, name);
                                    }
                                }).start();
                                player.token = token;
                                name_map.put(name, player);
                                db.child("leaderboardUser").setValue(name_map);
                            }
                        } else if (name_map.keySet().contains(name)) {
                            username.setText("");
                            name_text.setText("Name already exists");
                        } else {
                            List<Player> list = new ArrayList<>();
                            list.add(player);
                            content_map.put(name, list);
                            db.child("scoreboardUser").child(token).setValue(content_map);
                            player.token = token;
                            name_map.put(name, player);
                            db.child("leaderboardUser").setValue(name_map);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                Toast.makeText(getApplication(), "Score Submitted", Toast.LENGTH_LONG).show();

                // direct back to main game
                Intent mg = new Intent(SubmitScoreActivity.this, MainGameFragment.class);
                startActivity(mg);
            }
        });

        button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SubmitScoreActivity.this, MainGameFragment.class);
                startActivity(i);
            }
        });

    }

    private void onGetNewHighScore(int score, String name) {

        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        try {
            jNotification.put("title", "Congratulations!");
            jNotification.put("body", name + " made a new highest score of " + score + "!");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("click_action", "OPEN_ACTIVITY_1");
            System.out.println("token= " + token);
            jPayload.put("to", token);
            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            System.out.println("conn= " + conn);

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SERVER_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());
            outputStream.close();

            System.out.println("output finish ");
            InputStream inputStream = conn.getInputStream();
            System.out.println("is= " + inputStream);
            final String resp = convertStreamToString(inputStream);

            System.out.println("resp= " + resp);
            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    System.out.println("resp is: " + resp);
                }
            });
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }

}


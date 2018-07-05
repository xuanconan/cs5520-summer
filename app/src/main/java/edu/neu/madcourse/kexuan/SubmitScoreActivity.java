package edu.neu.madcourse.kexuan;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
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
    private Context mContext;


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
                mContext = v.getContext();
                final String name = username.getText().toString();

                db.child("leaderboardUser").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        GenericTypeIndicator<HashMap<String, Player>> t = new GenericTypeIndicator<HashMap<String, Player>>() {
                        };
                        Map<String, Player> name_map = dataSnapshot.getValue(t);
                        if (name_map == null)
                            name_map = new HashMap<>();

                        int highest = 0;
                        for(Player a : name_map.values()){
                            if (a.totalScore > highest) highest = a.totalScore;
                        }

                        if (current_names.contains(name)) {
                            content_map.get(name).add(player);
                            db.child("scoreboardUser").child(token).setValue(content_map);
                            Player lastInfo = name_map.get(name);

                            if(player.totalScore > highest){
                                System.out.println("----------player" + player.totalScore + "  name ------" + name);
                                //create a thread
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        onGetNewHighScore(player.totalScore, name);
                                    }
                                }).start();
                            }

                            if (player.totalScore > lastInfo.totalScore) {

                                player.token = token;
                                name_map.put(name, player);
                                db.child("leaderboardUser").setValue(name_map);
                            }
                        } else if (name_map.keySet().contains(name)) {
                            username.setText("");
                            name_text.setText("Name already exists");
                        } else {
                            if(player.totalScore > highest){
                                System.out.println("----------player" + player.totalScore + "  name ------" + name);
                                //create a thread
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        onGetNewHighScore(player.totalScore, name);
                                    }
                                }).start();
                            }

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "chat";
            String channelName = "聊天消息";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(channelId, channelName, importance);
            channelId = "subscribe";
            channelName = "订阅消息";
            importance = NotificationManager.IMPORTANCE_DEFAULT;
            createNotificationChannel(channelId, channelName, importance);
        }

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this, "chat")
                .setContentTitle("New highest score!")
                .setContentText(name + "just made: "+ score + "!")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.scoreicon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.scoreicon))
                .setAutoCancel(false)
                .build();
        manager.notify(1, notification);
    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }


    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

}


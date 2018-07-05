package edu.neu.madcourse.kexuan;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.support.v4.app.NotificationCompat;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.app.Notification;
import android.graphics.BitmapFactory;
import android.content.Intent;
import android.app.PendingIntent;
import android.net.Uri;
import android.app.NotificationChannelGroup;
import android.os.Build;
import android.annotation.TargetApi;


public class ScoreBoardActivity extends Activity {
    private DatabaseReference db;
    private String token;
    private ViewGroup vg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scoreboard);
        vg = (ViewGroup) findViewById(R.id.activity_scoreboard);
        db = FirebaseDatabase.getInstance().getReference();
        token = FirebaseInstanceId.getInstance().getToken();

        System.out.println("The token is ---------" + token);


        if(token.isEmpty()){
            System.out.println("Can't find token");
            TextView tv = new TextView(this);
            tv.setText("Can't find device token");
            vg.addView(tv);
            return;
        }

        // load data from database
        db.child("scoreboardUser").child(token).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, List<Player>>> t = new GenericTypeIndicator<HashMap<String, List<Player>>>() {};
                Map<String, List<Player>> map = dataSnapshot.getValue(t);
                if(map == null) map = new HashMap<>();
                int i=0;
                for(String key : map.keySet()) {
                    for(Player player : map.get(key)){
                        View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.score_list, null);

                        String score = player.phaseoneScore + "+" + (player.totalScore-player.phaseoneScore) + "=" + player.totalScore;
                        String word = player.highestScoreWord + " " + player.highestScore;
                        ((TextView)v.findViewById(R.id.username)).setText(key);
                        ((TextView)v.findViewById(R.id.userTime)).setText(player.time);
                        ((TextView)v.findViewById(R.id.userScore)).setText(score);
                        ((TextView)v.findViewById(R.id.userWord)).setText(word);
                        vg.addView(v);
                        i++;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }







    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

}

package edu.neu.madcourse.kexuan;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;

public class LeaderBoardActivity extends Activity {

    private DatabaseReference db;
    private String token;
    private static final String SERVER_KEY = "key=AAAAMnTp66M:APA91bGh9b4vywj77DIS6i13Zdhg2aewFwqYvvus4h8bqsCLlmlmshdIsrjZ4jmkfhvd_zPSU5uKUwRx1FUNu_zqo41ZYH_NPp-XNdxWbqbQ7rGaD4faJnXV4ZWPporb46x2NnfyJU4owiCQzSeeaxINHUvUjSViGQ";
    private ViewGroup vg;
    private String sender;
    private Button buttonCon;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TextView currentText = (TextView) msg.obj;
        }
    };

    // small top
    public class PlayerInfoComparator implements Comparator<MyEntry<String, Player>> {
        @Override
        public int compare(MyEntry<String, Player> player1, MyEntry<String, Player> player2) {
            if (player1.getValue().totalScore < player2.getValue().totalScore)
                return -1;
            else if (player1.getValue().totalScore == player2.getValue().totalScore)
                return 0;
            return 1;
        }
    }

    // send message
    private void sendCongrat(String to) {

        if (sender.isEmpty()) {
            db.child("scoreboardUser").child(token).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            GenericTypeIndicator<HashMap<String, List<Player>>> t = new GenericTypeIndicator<HashMap<String, List<Player>>>() {
                            };
                            Map<String, List<Player>> myMap = dataSnapshot.getValue(t);
                            String from = "nobody";
                            if (myMap != null) {
                                for (String name : myMap.keySet()) {
                                    from = name;
                                    break;
                                }
                            }
                            sender = from;
                            System.out.println("current sender:" + sender);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    }
            );
        }
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        try {
            jNotification.put("title", "Congratulations!");
            while (sender.isEmpty()) {
            }
            jNotification.put("body", sender + " sends you a congrat!");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("click_action", "OPEN_ACTIVITY_1");
            jPayload.put("to", to);
            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SERVER_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());
            outputStream.close();

            InputStream inputStream = conn.getInputStream();

            final String response = convertStreamToString(inputStream);

            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    System.out.println("response:" + response);
                    Toast.makeText(getApplication(), "Thank you :)", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        vg = (ViewGroup) findViewById(R.id.activity_leaderboard);
        db = FirebaseDatabase.getInstance().getReference();
        token = FirebaseInstanceId.getInstance().getToken();
        sender = "";
        System.out.println("token from: "+token);
        if (token.isEmpty()) {
            TextView tv = new TextView(this);
            tv.setText("No connection.");
            vg.addView(tv);
            return;
        }

        db.child("leaderboardUser").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                vg.removeAllViews();
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.leader_list, null);

                GenericTypeIndicator<HashMap<String, Player>> t = new GenericTypeIndicator<HashMap<String, Player>>() {
                };
                Map<String, Player> myMap = dataSnapshot.getValue(t);
                if (myMap == null) {
                    return;
                }
                Comparator<MyEntry<String, Player>> comparator = new PlayerInfoComparator();
                PriorityQueue<MyEntry<String, Player>> q = new PriorityQueue<>(10, comparator);
                for (String key : myMap.keySet()) {
                    if (q.size() == 10)
                        q.poll();
                    q.add(new MyEntry(key, myMap.get(key)));
                }
                List<MyEntry<String, Player>> list = new ArrayList<>();
                while (!q.isEmpty()) {
                    list.add(q.poll());
                }

                for (int i = list.size() - 1; i >= 0; i--) {
                    View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.leader_list, null);
                    final String token_to = myMap.get(list.get(i).getKey()).token;
                    System.out.println("token to= " + token_to);
                    ((TextView) v.findViewById(R.id.usernameLead)).setText(list.get(i).getKey());
                    buttonCon = (Button) v.findViewById(R.id.buttonConLead);
                    buttonCon.setOnClickListener(new Button.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    sendCongrat(token_to);
                                }
                            }).start();
                            Message msg = Message.obtain(mHandler);
                            msg.obj = v;
                            mHandler.sendMessageDelayed(msg, 500);
                            return;
                        }
                    });

                    String score = list.get(i).getValue().phaseoneScore
                            + "+" + (list.get(i).getValue().totalScore - list.get(i).getValue().phaseoneScore)
                            + "=" + list.get(i).getValue().totalScore;

                    String word = list.get(i).getValue().highestScoreWord + ": " + list.get(i).getValue().highestScore;

                    ((TextView) v.findViewById(R.id.userTimeLead)).setText(list.get(i).getValue().time);
                    ((TextView) v.findViewById(R.id.userScoreLead)).setText(score);
                    ((TextView) v.findViewById(R.id.userWordLead)).setText(word);
                    vg.addView(v);
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    final class MyEntry<K, V> implements Map.Entry<K, V> {
        private final K key;
        private V value;

        public MyEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }
    }
}

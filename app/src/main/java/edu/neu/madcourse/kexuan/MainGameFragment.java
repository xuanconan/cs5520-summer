package edu.neu.madcourse.kexuan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainGameFragment extends AppCompatActivity {

    private Button new_game;
    private Button score_board;
    private Button leader_board;
    private Button token_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_gamemain);

        new_game=(Button)findViewById(R.id.new_game);
        new_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainGameFragment.this, GameActivity.class);
                startActivity(i);
            }
        });

//        score_board=(Button)findViewById(R.id.score_board);
//        score_board.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(MainGameFragment.this, Scoreboard_Activity.class);
//                startActivity(i);
//            }
//        });
//
//        leader_board=(Button)findViewById(R.id.leader_board);
//        leader_board.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(MainGameFragment.this, Leaderboard_Activity.class);
//                startActivity(i);
//            }
//        });

//        final String token = FirebaseInstanceId.getInstance().getToken();
//        token_button=(Button)findViewById(R.id.token_button);
//        token_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplication(), token, Toast.LENGTH_LONG).show();
//            }
//        });
    }
}

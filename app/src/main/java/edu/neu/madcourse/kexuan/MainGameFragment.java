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



        // go to leader board
        leader_board=(Button)findViewById(R.id.leader_board);
        leader_board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lb = new Intent(MainGameFragment.this, LeaderBoardActivity.class);
                startActivity(lb);
            }
        });

        // start a game
        new_game=(Button)findViewById(R.id.new_game);
        new_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent game = new Intent(MainGameFragment.this, GameActivity.class);
                startActivity(game);
            }
        });

        // go to score board
        score_board=(Button)findViewById(R.id.score_board);
        score_board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sb = new Intent(MainGameFragment.this, ScoreBoardActivity.class);
                startActivity(sb);
            }
        });

    }
}

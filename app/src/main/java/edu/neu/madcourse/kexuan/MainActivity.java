package edu.neu.madcourse.kexuan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button aboutButton;
    private Button errorButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //A Titlebar with your name
        setTitle("Ke Xuan");

        setContentView(R.layout.activity_main);

        aboutButton = (Button) findViewById(R.id.aboutButton);

        // go to about page
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent about = new Intent(MainActivity.this, ActivityAbout.class);
                startActivity(about);
            }

        });

        errorButton = (Button) findViewById(R.id.errorButton);
        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                throw new RuntimeException("Oops, the app crashed.");
            }
        });
    }


}

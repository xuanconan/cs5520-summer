package edu.neu.madcourse.kexuan;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class AcknowledgementsActivity extends AppCompatActivity{
    private Button buttonClose;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acknowledgements);
        setTitle("Acknowledgements");

        Resources res = getResources();
        String[] acknowledgements = res.getStringArray(R.array.acknowledgementsList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AcknowledgementsActivity.this,
                R.layout.activity_acknowlist, acknowledgements);

        listView = (ListView) findViewById(R.id.acknowledgementsList);
        listView.setFastScrollEnabled(true);
        listView.setAdapter(adapter);

        buttonClose = (Button) findViewById(R.id.buttonClose);
        // close and return to dictionary page
        buttonClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
    }
}

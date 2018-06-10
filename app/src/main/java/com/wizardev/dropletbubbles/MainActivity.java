package com.wizardev.dropletbubbles;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.wizardev.dropletbubbles.view.DropletBubbles;

public class MainActivity extends AppCompatActivity {

    DropletBubbles mDropletBubbles1;
    DropletBubbles mDropletBubbles2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDropletBubbles1 = findViewById(R.id.dbMainBubbles1);
        mDropletBubbles2 = findViewById(R.id.dbMainBubbles2);

        mDropletBubbles1.setOnMeasureBaseLineCallback(new DropletBubbles.OnMeasureBaseLineCallback() {
            @Override
            public void measureHeightResult(int height) {
                mDropletBubbles1.setBaseLine(0.7f);
            }
        });

        mDropletBubbles2.setOnMeasureBaseLineCallback(new DropletBubbles.OnMeasureBaseLineCallback() {
            @Override
            public void measureHeightResult(int height) {
                mDropletBubbles2.setBaseLine(0.5f);
            }
        });


    }

}

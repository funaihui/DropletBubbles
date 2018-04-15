package com.wizardev.dropletbubbles;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.wizardev.dropletbubbles.view.DropletBubbles;

public class MainActivity extends AppCompatActivity {

    DropletBubbles mDropletBubbles1;
    DropletBubbles mDropletBubbles2;
    private int mHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDropletBubbles1 = findViewById(R.id.dbMainBubbles1);

//        mDropletBubbles1.updateXControl();
        mDropletBubbles2 = findViewById(R.id.dbMainBubbles2);
//        mDropletBubbles2.updateXControl();

        setBubblesHeight(mDropletBubbles1,0.9f);
        setBubblesHeight(mDropletBubbles2, 0.7f);

    }

    private void setBubblesHeight(final DropletBubbles view , final float value) {
        mDropletBubbles1.post(new Runnable() {
            @Override
            public void run() {
                mHeight = mDropletBubbles1.getHeight();

                view.setBaseLine(mHeight*value);

            }
        });
    }
}

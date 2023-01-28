package com.zzammo.calendar.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;

import com.zzammo.calendar.R;

public class slidingpanetest extends AppCompatActivity {

    private SlidingPaneLayout mSlidingPaneLayout;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slidingpanel);

        mSlidingPaneLayout = findViewById(R.id.sliding_pane_layout);
        mListView = findViewById(R.id.list_view);

        String[] items = {"Item 1", "Item 2", "Item 3", "Item 4", "Item 5"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        mListView.setAdapter(adapter);

        mSlidingPaneLayout.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                // Do something when the panel is sliding
                Log.d("minseok","panelslide");
                if(slideOffset > 0.5 && slideOffset < 0.7) {
                    // start animation
                }
                else {
                    // stop animation
                }
            }

            @Override
            public void onPanelOpened(View panel) {
                // Do something when the panel is opened
                Log.d("minseok","onPanelOpened");
            }

            @Override
            public void onPanelClosed(View panel) {
                Log.d("minseok","onPanelClosed");
                // Do something when the panel is closed
            }
        });
    }
}
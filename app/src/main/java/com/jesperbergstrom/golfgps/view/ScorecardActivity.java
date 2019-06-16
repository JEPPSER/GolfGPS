package com.jesperbergstrom.golfgps.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.jesperbergstrom.golfgps.R;

public class ScorecardActivity extends Activity {

    public FrameLayout historyTab;
    public FrameLayout scorecardTab;
    public LinearLayout historyView;
    public LinearLayout scorecardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scorecard);

        historyTab = findViewById(R.id.historyTab);
        scorecardTab = findViewById(R.id.scorecardTab);
        historyView = findViewById(R.id.historyView);
        scorecardView = findViewById(R.id.scorecardView);

        showScorecardTab();

        historyTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHistoryTab();
            }
        });

        scorecardTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showScorecardTab();
            }
        });
    }

    private void showHistoryTab() {
        historyTab.setBackgroundResource(R.drawable.selected);
        scorecardTab.setBackgroundResource(R.drawable.not_selected);
        historyView.setVisibility(View.VISIBLE);
        scorecardView.setVisibility(View.INVISIBLE);
    }

    private void showScorecardTab() {
        scorecardTab.setBackgroundResource(R.drawable.selected);
        historyTab.setBackgroundResource(R.drawable.not_selected);
        scorecardView.setVisibility(View.VISIBLE);
        historyView.setVisibility(View.INVISIBLE);
    }
}

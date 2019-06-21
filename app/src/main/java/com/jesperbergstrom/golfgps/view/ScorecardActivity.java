package com.jesperbergstrom.golfgps.view;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.jesperbergstrom.golfgps.R;
import com.jesperbergstrom.golfgps.entities.Course;
import com.jesperbergstrom.golfgps.entities.Scorecard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/*
 * TODO:
 * - Implement scorecard file.
 */

public class ScorecardActivity extends Activity {

    public FrameLayout historyTab;
    public FrameLayout scorecardTab;
    public LinearLayout historyView;
    public LinearLayout scorecardView;
    public TableLayout scoreTable;

    public Course course;
    public AssetManager assetManager;
    public Scorecard scorecard;

    public int scorecardId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scorecard);

        historyTab = findViewById(R.id.historyTab);
        scorecardTab = findViewById(R.id.scorecardTab);
        historyView = findViewById(R.id.historyView);
        scorecardView = findViewById(R.id.scorecardView);
        scoreTable = findViewById(R.id.scoreTable);

        File path = this.getFilesDir();
        //scorecardId = path.listFiles().length - 1;
        scorecardId = 0;

        assetManager = getAssets();
        course = new Course("rydo", assetManager);
        scorecard = new Scorecard(course);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scoreTable.getChildCount() > 0) {
            saveScore();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (scoreTable.getChildCount() > 0) {
            saveScore();
        }
        createScoreGUI();
    }

    private void createScoreGUI() {
        int boxSize = scorecardView.getWidth() / 10;
        scoreTable.removeAllViews();
        for (int i = 0; i < 12; i++) {
            TableRow tr = new TableRow(this);
            scoreTable.addView(tr);
            for (int j = 0; j < 10; j++) {
                TextView et;
                if (i == 1 || i == 7) {
                    et = new TextView(this);
                    et.setTypeface(null, Typeface.BOLD);
                    et.setBackgroundResource(R.drawable.par_box);
                    if (j == 0) {
                        et.setText("Par");
                    } else {
                        et.setText(String.valueOf(course.holes.get(j + 9 * (i / 6) - 1).par));
                    }
                } else if (i == 0 || i == 6) {
                    et = new TextView(this);
                    et.setTypeface(null, Typeface.BOLD);
                    et.setBackgroundResource(R.drawable.hole_box);
                    if (j == 0) {
                        et.setText("Hole");
                    } else {
                        et.setText(String.valueOf(j + 9 * (i / 6)));
                    }
                } else {
                    et = new EditText(this);
                    et.setBackgroundResource(R.drawable.score_box);
                    if (j == 0) {
                        et.setInputType(InputType.TYPE_CLASS_TEXT);
                    } else {
                        et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                    }
                }
                et.setTextSize(boxSize / 8);
                et.setGravity(Gravity.CENTER);
                et.setWidth(boxSize);
                et.setHeight(boxSize);
                tr.addView(et);
            }
        }
        updateTable();
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

    private void updateTable() {
        File path = this.getFilesDir();
        File file = new File(path, "scorecard_" + scorecardId);
        int length = (int) file.length();
        byte[] bytes = new byte[length];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(bytes);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String contents = new String(bytes);
        // Load scorecard
        String[] parts = contents.split("\n");
        if (parts.length == 8) {
            for (int i = 0; i < parts.length; i++) {
                int playerIndex = i;
                if (playerIndex > 3) {
                    playerIndex -= 4;
                }
                String[] holes = parts[i].split(" ");
                if (!holes[0].equals("")) {
                    scorecard.players[playerIndex].name = holes[0];
                }
                for (int j = 1; j < holes.length; j++) {
                    if (i > 3) {
                        scorecard.players[playerIndex].scores[j + 8] = Integer.parseInt(holes[j]);
                    } else {
                        scorecard.players[playerIndex].scores[j - 1] = Integer.parseInt(holes[j]);
                    }
                }
            }
        }

        for (int i = 2; i < 6; i++) {
            TableRow tr = (TableRow) scoreTable.getChildAt(i);
            for (int j = 0; j < tr.getChildCount(); j++) {
                EditText et = (EditText) tr.getChildAt(j);
                if (j == 0) {
                    et.setText(scorecard.players[i - 2].name);
                } else if (scorecard.players[i - 2].scores[j - 1] != 0){
                    et.setText(String.valueOf(scorecard.players[i - 2].scores[j - 1]));
                } else {
                    et.setText("");
                }
            }
        }

        for (int i = 8; i < 12; i++) {
            TableRow tr = (TableRow) scoreTable.getChildAt(i);
            for (int j = 0; j < tr.getChildCount(); j++) {
                EditText et = (EditText) tr.getChildAt(j);
                if (j == 0) {
                    et.setText(scorecard.players[i - 8].name);
                } else if (scorecard.players[i - 8].scores[j + 8] != 0){
                    et.setText(String.valueOf(scorecard.players[i - 8].scores[j + 8]));
                } else {
                    et.setText("");
                }
            }
        }
    }

    private void saveScore() {
        String output = "";
        for (int i = 2; i < 6; i++) {
            TableRow tr = (TableRow) scoreTable.getChildAt(i);
            for (int j = 0; j < tr.getChildCount(); j++) {
                EditText et = (EditText) tr.getChildAt(j);
                if (j == 0) {
                    output += et.getText().toString() + " ";
                } else if (!et.getText().toString().equals("")) {
                    output += et.getText().toString() + " ";
                } else if (et.getText().toString().equals("")) {
                    output += "0 ";
                }
            }
            output += "\n";
        }

        for (int i = 8; i < 12; i++) {
            TableRow tr = (TableRow) scoreTable.getChildAt(i);
            for (int j = 0; j < tr.getChildCount(); j++) {
                EditText et = (EditText) tr.getChildAt(j);
                if (j == 0) {
                    output += et.getText().toString() + " ";
                } else if (!et.getText().toString().equals("")) {
                    output += et.getText().toString() + " ";
                } else if (et.getText().toString().equals("")) {
                    output += "0 ";
                }
            }
            output += "\n";
        }

        File path = this.getFilesDir();
        File file = new File(path, "scorecard_" + scorecardId);

        FileOutputStream stream;
        try {
            stream = new FileOutputStream(file);
            stream.write(output.getBytes());
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

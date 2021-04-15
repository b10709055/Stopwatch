package com.adam.stopwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    boolean isStart;
    Button leftButton, rightButton;
    TextView clock;
    ListView lapList;
    int c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        leftButton = findViewById(R.id.leftButton);
        rightButton = findViewById(R.id.rightButton);
        clock = findViewById(R.id.clock);
        lapList = findViewById(R.id.lapList);
        initLapList();
    }

    private SimpleAdapter adapter;
    private LinkedList<HashMap<String, String>> data = new LinkedList<>();
    private String[] from={"item"};
    private int[] to = {R.id.item};

    private void initLapList() {
        adapter = new SimpleAdapter(this, data, R.layout.item, from, to);
        lapList.setAdapter(adapter);

    }

    public void leftButton(View view) {
        if (isStart) {
            doLap();
        } else {
            doReset();
        }
    }


    private void doReset() {
        counter = 0;
        uiHandler.sendEmptyMessage(0);
        data.clear();
        adapter.notifyDataSetChanged();
    }

    private void doLap() {
        c++;
        HashMap<String, String> row = new HashMap<>();
        row.put(from[0], clock.getText().toString());
        data.add(0, row);
        adapter.notifyDataSetChanged();
    }

    public void rightButton(View view) {
        isStart = !(isStart);
        if (isStart) {
            rightButton.setText("Stop");
            leftButton.setText("Lap");
            doStart();
        } else {
            rightButton.setText("Start");
            leftButton.setText("Reset");
            doStop();
        }
    }

    private int counter;
    private Timer timer = new Timer();
    private MyTask myTask;

    private class MyTask extends TimerTask {
        @Override
        public void run() {
            counter++;
            uiHandler.sendEmptyMessage(0);
        }
    }

    private UIHandler uiHandler = new UIHandler();

    private class UIHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            clock.setText(counterToClock(counter));
        }
    }

    private Spannable counterToClock(int c) {
        int ns = c % 100;   //百分之一秒
        int ts = c / 100;   //總秒數
        int hh = ts / (60 * 60);
        int mm = (ts - hh * 60 * 60) / 60;
        int ss = ts % 60;
        String s1 = String.format("%02d: %02d: %02d.%02d", hh, mm, ss, ns);
        Spannable s2 = new SpannableString(s1);
        s2.setSpan(new AbsoluteSizeSpan(80), 11, 13, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return s2;
    }

    private void doStart() {
        myTask = new MyTask();
        timer.schedule(myTask, 10, 10);

    }

    private void doStop() {
        if (myTask != null) {
            myTask.cancel();
            myTask = null;
        }

    }
}
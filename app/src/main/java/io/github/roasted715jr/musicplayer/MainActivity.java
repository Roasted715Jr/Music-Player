package io.github.roasted715jr.musicplayer;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "MainActivity";
    public static int SDK = Build.VERSION.SDK_INT;

    private Button playBtn, pauseBtn;
    private final Handler handler = new Handler();
    private int songProgress = 0, handlerDelay = 50;
    private MediaPlayer mediaPlayer;
    private Runnable runnable;
    private SeekBar seekBar;
    private String songDuration = "0:00";
    private TextView songTimeTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playBtn = (Button) findViewById(R.id.btn_play);
        pauseBtn = (Button) findViewById(R.id.btn_pause);
        playBtn.setOnClickListener(this);
        pauseBtn.setOnClickListener(this);

        HideBtn(pauseBtn);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.music1);
        songDuration = ToTime(mediaPlayer.getDuration());
        songTimeTxt = (TextView) findViewById(R.id.song_time);
        //UpdateSongTimeTxt();

        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                songProgress = seekBar.getProgress();
                UpdateSongTimeTxt();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer.pause();
                StopHandler();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
                mediaPlayer.start();
                StartHandler();
            }
        });

        //This needs to be here after the seekBar is initialized
        UpdateSongTimeTxt();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_play:
                mediaPlayer.seekTo(songProgress);
                mediaPlayer.start();

                runnable = new Runnable() {
                    @Override
                    public void run() {
                        //Log.d(TAG, ToTime(ToTime(mediaPlayer.getCurrentPosition()));
                        UpdateSongTimeTxt();
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                        handler.postDelayed(this, handlerDelay);
                    }
                };
                StartHandler();

                HideBtn(playBtn);
                ShowBtn(pauseBtn);
                break;
            case R.id.btn_pause:
                mediaPlayer.pause();
                songProgress = mediaPlayer.getCurrentPosition();

                StopHandler();

                HideBtn(pauseBtn);
                ShowBtn(playBtn);
                break;
        }
    }

    public void HideBtn(Button btn) {
        btn.setVisibility(View.GONE);
        btn.setEnabled(false);
    }

    public void ShowBtn(Button btn) {
        btn.setVisibility(View.VISIBLE);
        btn.setEnabled(true);
    }

    public void StartHandler() {
        handler.postDelayed(runnable, handlerDelay);
    }

    public void StopHandler() {
        handler.removeCallbacks(runnable);
    }

    public String ToTime(int ms) {
        int min = 0, sec;
        double splitSec;

        while (ms >= 60000) {
            min++;
            ms -= 60000;
        }

        splitSec = ms / 1000;
        splitSec = Math.floor(splitSec);
        sec = (int) splitSec;

        return min + ":" + (sec >= 10 ? sec : "0" + sec);
    }

    public void UpdateSongTimeTxt() {
        songTimeTxt.setText(ToTime(seekBar.getProgress()) + " / " + songDuration);
    }
}

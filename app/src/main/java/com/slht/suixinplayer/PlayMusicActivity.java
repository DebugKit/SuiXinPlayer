package com.slht.suixinplayer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.slht.suixinplayer.utils.MediaUtils;

/**
 * Created by LI on 2016/4/19.
 */
public class PlayMusicActivity extends BaseActivity implements View.OnClickListener {

    private TextView song;
    private TextView singer;
    private ImageView song_image;
    private SeekBar seekBar;
    private TextView start_time;
    private TextView end_time;
    private ImageView play_mode;
    private ImageView prev;
    private ImageView pause;
    private ImageView next;


    private int currentPosition;
    private int maxDuration;

    private boolean isPause;

    private <T extends View> T findView(int resId) {
        return (T) findViewById(resId);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);
//        currentPosition = getIntent().getIntExtra("currentPosition", 0);
        isPause = getIntent().getBooleanExtra("isPause", false);
        initView();
        this.bindPlayService();
        initData();
        initEvent();
    }

    private void initEvent() {
        prev.setOnClickListener(this);
        pause.setOnClickListener(this);
        next.setOnClickListener(this);
    }

    private void initData() {
    }

    private void initView() {
        song = findView(R.id.song);
        singer = findView(R.id.singer);
        song_image = findView(R.id.song_image);
        seekBar = findView(R.id.seekbar);
        start_time = findView(R.id.start_time);
        end_time = findView(R.id.end_time);
        play_mode = findView(R.id.play_mode);
        prev = findView(R.id.prev);
        pause = findView(R.id.pause);
        next = findView(R.id.next);
    }

    @Override
    public void publish(final int progress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                start_time.setText(MediaUtils.formartTime(progress));
                end_time.setText("-" + (MediaUtils.formartTime(maxDuration - progress)));
                seekBar.setProgress(progress);
            }
        });
    }

    @Override
    public void change(int position) {
        if (this.playService.isPlaying()) {
            song.setText(MediaUtils.MP3Infos.get(position).getTitle());
            singer.setText(MediaUtils.MP3Infos.get(position).getArtist());
            song_image.setImageBitmap(MediaUtils.MP3Infos.get(position).getBitImage());
            seekBar.setProgress(0);
            maxDuration = (int) MediaUtils.MP3Infos.get(position).getDuration();
            seekBar.setMax(maxDuration);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindPlayService();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next:
                playService.next();
                break;
            case R.id.pause:
                if (playService.isPlaying()) {
                    pause.setImageResource(R.mipmap.player_btn_play_normal);
                    playService.pause();
                    isPause = true;
                } else {
                    if (isPause) {
                        pause.setImageResource(R.mipmap.player_btn_pause_normal);
                        playService.start();
                    } else
                        playService.play(0);
                    isPause = false;
                }
                break;
            case R.id.prev:
                playService.prev();
                break;
        }
    }
}

package com.slht.suixinplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.widget.ImageView;

import com.slht.suixinplayer.Bean.MP3Info;
import com.slht.suixinplayer.R;
import com.slht.suixinplayer.utils.MediaUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayService extends Service {

    private MediaPlayer mPlayer;
    private int currentPosition;
    private List<MP3Info> mp3Infos = null;
    private MusicUpdateListener musicUpdateListener;

    public int getCurrentPosition() {
        return currentPosition;
    }

    Runnable updateStatusRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                if (musicUpdateListener != null) {
                    musicUpdateListener.onPublish(getCurrentProgress());
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    private ExecutorService es = Executors.newSingleThreadExecutor();

    public PlayService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        mp3Infos = MediaUtils.MP3Infos;
        return new PlayBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = new MediaPlayer();
        es.execute(updateStatusRunnable);
    }

    /**
     * 播放
     *
     * @param position
     */
    public void play(final int position) {

        if (position >= 0 && position < mp3Infos.size()) {
            try {
                mPlayer.reset();
                mPlayer.setDataSource(PlayService.this, Uri.parse(mp3Infos.get(position).getUrl()));
                mPlayer.prepare();
                mPlayer.start();
                currentPosition = position;
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (musicUpdateListener != null) {
                musicUpdateListener.onChange(currentPosition);
            }
        }
    }

    public boolean isPlaying() {
        return mPlayer.isPlaying() ? true : false;
    }

    /**
     * 暂停
     */
    public void pause() {
        mPlayer.pause();
    }

    /**
     * 下一首
     */
    public void next() {
        if (currentPosition + 1 >= mp3Infos.size() - 1) {
            currentPosition = 0;
        } else
            currentPosition++;

//        currentPosition = (currentPosition + 1 >= mp3Infos.size() - 1) ? 0 : currentPosition++;
        play(currentPosition);
    }

    /**
     * 上一首
     */
    public void prev() {
        if (currentPosition - 1 < 0) {
            currentPosition = mp3Infos.size() - 1;
        } else
            currentPosition--;
        play(currentPosition);
    }

    /**
     * 播放
     */
    public void start() {
        if (mPlayer != null && !mPlayer.isPlaying()) {
            mPlayer.start();
        }
    }

    public int getDuration() {
        return mPlayer.getDuration();
    }

    public void seekTo(int msec) {
        mPlayer.seekTo(msec);
    }

    public int getCurrentProgress() {
        if (mPlayer != null && mPlayer.isPlaying())
            return mPlayer.getCurrentPosition();
        return 0;
    }

    public void setMusicUpdateListener(MusicUpdateListener musicUpdateListener) {
        this.musicUpdateListener = musicUpdateListener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (es != null && !es.isShutdown()) {
            es.shutdown();
            es = null;
        }
    }

    public interface MusicUpdateListener {
        void onPublish(int progress);

        void onChange(int position);
    }

    public class PlayBinder extends Binder {
        public PlayService getPlayService() {
            return PlayService.this;
        }
    }
}

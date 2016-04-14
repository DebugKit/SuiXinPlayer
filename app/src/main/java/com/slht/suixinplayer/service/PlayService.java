package com.slht.suixinplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import com.slht.suixinplayer.Bean.MP3Info;
import com.slht.suixinplayer.utils.MediaUtils;

import java.io.IOException;
import java.util.List;

public class PlayService extends Service {

    private MediaPlayer mPlayer;
    private int currentPosition;
    private List<MP3Info> mp3Infos;

    public PlayService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new PlayBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = new MediaPlayer();
    }

    /**
     * 播放
     *
     * @param position
     */
    public void paly(final int position) {
        MediaUtils.getMP3Infos(this, new MediaUtils.QuerySuccess() {
            @Override
            public void querySuccess(List<MP3Info> data) {
                mp3Infos = data;
                if (position >= 0 && position < mp3Infos.size()) {
                    try {
                        mPlayer.reset();
                        mPlayer.setDataSource(PlayService.this, Uri.parse(data.get(position).getUrl()));
                        mPlayer.prepare();
                        mPlayer.start();
                        currentPosition = position;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 暂停
     */
    public void pause() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        }
    }

    /**
     * 下一首
     */
    public void next() {
//        if (currentPosition + 1 >= mp3Infos.size() - 1) {
//            currentPosition = 0;
//        } else
//            currentPosition++;

        currentPosition = (currentPosition + 1 >= mp3Infos.size() - 1) ? 0 : currentPosition++;
        paly(currentPosition);
    }

    /**
     * 上一首
     */
    public void prev() {
        if (currentPosition - 1 < 0) {
            currentPosition = mp3Infos.size() - 1;
        } else
            currentPosition--;
        paly(currentPosition);
    }

    /**
     * 播放
     */
    public void start() {
        if (mPlayer != null && !mPlayer.isPlaying()) {
            mPlayer.start();
        }
    }

    public class PlayBinder extends Binder {
        public PlayService getPlayService() {
            return PlayService.this;
        }
    }
}

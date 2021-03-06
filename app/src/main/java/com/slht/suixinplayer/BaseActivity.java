package com.slht.suixinplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.slht.suixinplayer.Bean.MP3Info;
import com.slht.suixinplayer.service.PlayService;

import java.util.ArrayList;

/**
 * Created by LI on 2016/4/14.
 */
public abstract class BaseActivity extends FragmentActivity {

    public PlayService playService;
    private boolean isBound = false;
    public  PlayService.PlayBinder playBinder;

    private PlayService.MusicUpdateListener musicUpdateListener = new PlayService.MusicUpdateListener() {
        @Override
        public void onPublish(int progress) {
            publish(progress);
        }

        @Override
        public void onChange(int position) {
            change(position);
        }
    };
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            playBinder = (PlayService.PlayBinder) service;
            playService = playBinder.getPlayService();
            playService.setMusicUpdateListener(musicUpdateListener);
            musicUpdateListener.onChange(playService.getCurrentPosition());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            playService = null;
            isBound = false;
        }
    };

    public abstract void publish(int progress);

    public abstract void change(int position);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 绑定服务
     */
    public void bindPlayService() {
        if (!isBound) {
            Intent intent = new Intent(this, PlayService.class);
//            intent.putExtra("mp3Infos",mp3Infos);
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("mp3Infos",mp3Infos);
//            intent.putExtras(bundle);
            Log.d("BaseActivity", "bindService(intent, conn, Context.BIND_AUTO_CREATE):" + bindService(intent, conn,
                    Context.BIND_AUTO_CREATE));

            isBound = true;
        }
    }

    /**
     * 解除服务
     */
    public void unbindPlayService() {
        if (isBound) {
            unbindService(conn);
            isBound = false;
        }
    }
}

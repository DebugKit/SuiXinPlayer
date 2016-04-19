package com.slht.suixinplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.slht.suixinplayer.Bean.MP3Info;
import com.slht.suixinplayer.adapter.MyMusicListAdapter;
import com.slht.suixinplayer.utils.MediaUtils;

import java.util.ArrayList;
import java.util.List;


public class MyMusicListFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private View parent;
    private MainActivity activity;
    private ListView listview;
    private MyMusicListAdapter adapter;

    private ImageView song_image;
    private TextView song;
    private TextView singer;
    private ImageView pause;
    private ImageView next;

    private ArrayList<MP3Info> mp3Infos;

    private int currentPosition;
    /**
     * 是否是暂停中
     */
    private boolean isPause = false;

    public static MyMusicListFragment newInstance() {
//        if (myMusicListFragment == null)
//            myMusicListFragment = new MyMusicListFragment();
        return new MyMusicListFragment();
    }

    private <T extends View> T findView(int resId) {
        return (T) parent.findViewById(resId);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parent = getView();
        activity = (MainActivity) getActivity();
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        listview.setOnItemClickListener(this);
        pause.setOnClickListener(this);
        next.setOnClickListener(this);
        song_image.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        activity.unbindPlayService();
    }

    private void initData() {
        MediaUtils.getMP3Infos(activity, new MediaUtils.QuerySuccess() {
            @Override
            public void querySuccess(boolean isSuccess) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mp3Infos = new ArrayList<MP3Info>();
                        mp3Infos.addAll(MediaUtils.MP3Infos);
                        adapter = new MyMusicListAdapter(activity, mp3Infos);
                        listview.setAdapter(adapter);
                        activity.bindPlayService();
                    }
                });
            }
        });
    }

    private void initView() {
        listview = findView(R.id.listview);
        song_image = findView(R.id.song_image);
        song = findView(R.id.song);
        singer = findView(R.id.singer);
        pause = findView(R.id.pause);
        next = findView(R.id.next);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parent = inflater.inflate(R.layout.fragment_my_music_list, container, false);
        return parent;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        activity.playService.play(position);
    }

    /**
     * 改变UI状态
     *
     * @param position
     */
    public void changeUiStatus(final int position) {
        currentPosition = position;
        song_image.setImageBitmap(mp3Infos.get(position).getBitImage());
        song.setText(mp3Infos.get(position).getTitle());
        singer.setText(mp3Infos.get(position).getArtist());
        pause.setImageResource(R.mipmap.player_btn_pause_normal);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pause:
                if (activity.playService.isPlaying()) {
                    pause.setImageResource(R.mipmap.player_btn_play_normal);
                    activity.playService.pause();
                    isPause = true;
                } else {
                    if (isPause) {
                        pause.setImageResource(R.mipmap.player_btn_pause_normal);
                        activity.playService.start();
                    } else
                        activity.playService.play(0);
                    isPause = false;
                }
                break;
            case R.id.next:
                activity.playService.next();
                break;
            case R.id.song_image:
                Intent intent = new Intent(activity,PlayMusicActivity.class);
                intent.putExtra("isPause",isPause);
                startActivity(intent);
                break;
        }
    }
}

package com.slht.suixinplayer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.slht.suixinplayer.Bean.MP3Info;
import com.slht.suixinplayer.adapter.MyMusicListAdapter;
import com.slht.suixinplayer.utils.MediaUtils;

import java.util.List;


public class MyMusicListFragment extends Fragment implements AdapterView.OnItemClickListener {

    private View parent;
    private MainActivity activity;
    private ListView listview;
    private MyMusicListAdapter adapter;

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
        activity.bindPlayService();
        initEvent();
    }

    private void initEvent() {
        listview.setOnItemClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        activity.unbindPlayService();
    }

    private void initData() {
        MediaUtils.getMP3Infos(activity, new MediaUtils.QuerySuccess() {
            @Override
            public void querySuccess(final List<MP3Info> data) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new MyMusicListAdapter(activity, data);
                        listview.setAdapter(adapter);
                    }
                });
            }
        });
    }

    private void initView() {
        listview = findView(R.id.listview);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parent = inflater.inflate(R.layout.fragment_my_music_list, container, false);
        return parent;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        activity.playService.paly(position);
    }

    public void changeUiStatus(int position) {

    }
}

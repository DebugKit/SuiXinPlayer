package com.slht.suixinplayer;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


public class MyMusicListFragment extends Fragment {

    private View parent;
    private Activity activity;

    private ListView listview;

    private <T extends View> T findView(int resId){
        return (T) parent.findViewById(resId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parent = getView();
        activity = getActivity();
        initView();
    }

    private void initView() {
        listview = findView(R.id.listview);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_music_list, container, false);
    }

}

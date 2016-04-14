package com.slht.suixinplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.slht.suixinplayer.Bean.MP3Info;
import com.slht.suixinplayer.R;
import com.slht.suixinplayer.utils.MediaUtils;

import java.util.List;

/**
 * Created by Li on 2016/4/13.
 */
public class MyMusicListAdapter extends BaseAdapter {

    private List<MP3Info> mp3Infos;
    private LayoutInflater inflater;

    public MyMusicListAdapter(Context context, List<MP3Info> mp3Infos) {
        inflater = LayoutInflater.from(context);
        this.mp3Infos = mp3Infos;
    }

    @Override
    public int getCount() {
        return mp3Infos.size();
    }

    @Override
    public Object getItem(int position) {
        return mp3Infos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_music, null);
            holder = new ViewHolder();
            holder.singer = findView(convertView, R.id.singer);
            holder.song_imags = findView(convertView, R.id.song_imags);
            holder.song = findView(convertView, R.id.song);
            holder.time = findView(convertView, R.id.time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MP3Info mp3Info = (MP3Info) getItem(position);
        holder.singer.setText(mp3Info.getArtist());
        holder.song.setText(mp3Info.getTitle());
        holder.time.setText(MediaUtils.formartTime(mp3Info.getDuration()));
        return convertView;
    }

    class ViewHolder {
        ImageView song_imags;
        TextView song;
        TextView singer;
        TextView time;
    }

    private <T extends View> T findView(View view, int resId) {
        return (T) view.findViewById(resId);
    }
}

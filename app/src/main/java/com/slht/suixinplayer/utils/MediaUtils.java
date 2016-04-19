package com.slht.suixinplayer.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;

import com.slht.suixinplayer.Bean.MP3Info;
import com.slht.suixinplayer.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Li on 2016/4/13.
 */
public class MediaUtils {
    private static final Uri ALBUMARTURI = Uri.parse("content://media/external/audio/albumart");
    public static List<MP3Info> MP3Infos  = null;

    /**
     * 根据歌曲id查询歌曲信息
     *
     * @param context
     * @param _id
     * @return
     */
    public static MP3Info getMP3Info(Context context, long _id) {
        System.out.println(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        Cursor cursor;
        cursor = context.getContentResolver()
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                        MediaStore.Audio.Media._ID + "=" + _id, null,
                        MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        MP3Info mp3Info = null;
        if (cursor.moveToNext()) {
            mp3Info = new MP3Info();
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio
                    .Media._ID));
            String title = cursor.getString(cursor.getColumnIndex(MediaStore
                    .Audio.Media.TITLE));
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore
                    .Audio.Media.ARTIST));
            String album = cursor.getString(cursor.getColumnIndex(MediaStore
                    .Audio.Media.ALBUM));
            long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore
                    .Audio.Media.ALBUM_ID));
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore
                    .Audio.Media.DURATION));
            long size = cursor.getLong(cursor.getColumnIndex(MediaStore
                    .Audio.Media.SIZE));
            String url = cursor.getString(cursor.getColumnIndex(MediaStore
                    .Audio.Media.DATA));
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore
                    .Audio.Media.IS_MUSIC));
            if (isMusic != 0) {
                mp3Info.setAlbum(album);
                mp3Info.setAlbumId(albumId);
                mp3Info.setArtist(artist);
                mp3Info.setDuration(duration);
                mp3Info.setId(id);
                mp3Info.setIsMusic(isMusic);
                mp3Info.setSize(size);
                mp3Info.setTitle(title);
                mp3Info.setUrl(url);
            }
        }
        cursor.close();
        return mp3Info;
    }

    /**
     * 格式化时间，将毫秒转换为分：秒格式
     *
     * @param time
     * @return
     */
    public static String formartTime(long time) {
        String min = time / (1000 * 60) + "";
        String sec = time % (1000 * 60) + "";

        if (min.length() < 2) {
            min = "0" + time / (1000 * 60) + "";
        } else {
            min = time / (1000 * 60) + "";
        }

        if (sec.length() == 4) {
            sec = "0" + time % (1000 * 60) + "";
        } else if (sec.length() == 3) {
            sec = "00" + time % (1000 * 60) + "";
        } else if (sec.length() == 2) {
            sec = "000" + time % (1000 * 60) + "";
        } else if (sec.length() == 1) {
            sec = "0000" + time % (1000 * 60) + "";
        }
        return min + ":" + sec.trim().substring(0, 2);
    }

    /**
     * 查询手机中歌曲长度大于3分钟的
     *
     * @param context
     * @return
     */
    public static void getMP3Infos(final Context context, final QuerySuccess success) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<MP3Info> mp3Infos = new ArrayList<MP3Info>();
                Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                        MediaStore.Audio.Media.DURATION + ">" + 180000, null, MediaStore.Audio.Media
                                .DEFAULT_SORT_ORDER);
                while (cursor.moveToNext()) {
                    MP3Info mp3Info = new MP3Info();
                    long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio
                            .Media._ID));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore
                            .Audio.Media.TITLE));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore
                            .Audio.Media.ARTIST));
                    String album = cursor.getString(cursor.getColumnIndex(MediaStore
                            .Audio.Media.ALBUM));
                    long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore
                            .Audio.Media.ALBUM_ID));
                    Bitmap bitImage = getBitmap(getAlbumArt(context, (int) albumId));
                    long duration = cursor.getLong(cursor.getColumnIndex(MediaStore
                            .Audio.Media.DURATION));
                    long size = cursor.getLong(cursor.getColumnIndex(MediaStore
                            .Audio.Media.SIZE));
                    String url = cursor.getString(cursor.getColumnIndex(MediaStore
                            .Audio.Media.DATA));
                    int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore
                            .Audio.Media.IS_MUSIC));

                    if (isMusic != 0) {
                        mp3Info.setAlbum(album);
                        mp3Info.setAlbumId(albumId);
                        mp3Info.setArtist(artist);
                        mp3Info.setDuration(duration);
                        mp3Info.setId(id);
                        mp3Info.setIsMusic(isMusic);
                        mp3Info.setSize(size);
                        mp3Info.setTitle(title);
                        mp3Info.setUrl(url);
                        mp3Info.setBitImage(bitImage);

                        mp3Infos.add(mp3Info);
                    }
                }
                cursor.close();
                MP3Infos =mp3Infos;
                success.querySuccess(true);
            }
        }).start();
    }

    private static String getAlbumArt(Context context, int albumId) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cursor = context.getContentResolver().query(Uri.parse(mUriAlbums + File.separator +
                albumId), projection, null, null, null);
        String album_art = null;
        if (cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
            cursor.moveToNext();
            album_art = cursor.getString(0);
        }
        cursor.close();
        cursor = null;
        return album_art;
    }

    private static Bitmap getBitmap(String album_art) {
        if (album_art == null)
            return BitmapFactory.decodeFile("drawable://" + R.mipmap.app_logo2);
        else {
            return BitmapFactory.decodeFile(album_art);
        }
    }

    public long[] getMp3InfosIds(Context context) {
        return null;
    }

    public interface QuerySuccess {
        void querySuccess(boolean isSuccess);
    }
}

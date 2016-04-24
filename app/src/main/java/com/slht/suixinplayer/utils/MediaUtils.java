package com.slht.suixinplayer.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;

import com.slht.suixinplayer.Bean.MP3Info;
import com.slht.suixinplayer.R;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Li on 2016/4/13.
 */
public class MediaUtils {
    private static final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
    public static List<MP3Info> MP3Infos = null;

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
                    Bitmap bitImage = getBitmap(context, getAlbumArt(context,
                            (int) albumId));
//                    Bitmap bitImage = getArtwork(context, id, albumId, true, true);
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
                MP3Infos = mp3Infos;
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

    private static Bitmap getBitmap(Context context, String album_art) {
        Bitmap bt = null;
        int i = 1;
        if (album_art == null) {
            bt = BitmapFactory.decodeResource(context.getResources(), R.mipmap
                    .app_logo2);
//            bt = BitmapFactory.decodeFile("mipmap://" + R.mipmap.app_logo2);
        } else {
            Log.d("MediaUtils", "i:" + i);
            i++;
            bt = BitmapFactory.decodeFile(album_art);
        }
        Log.d("MediaUtils", "bt.getByteCount():" + bt.getByteCount());
        return bt;
    }

    /**
     * 从文件当中获取专辑封面位图
     *
     * @param context
     * @param songid
     * @param albumid
     * @return
     */
    private static Bitmap getArtworkFromFile(Context context, long songid, long albumid) {
        Bitmap bm = null;
        if (albumid < 0 && songid < 0) {
            throw new IllegalArgumentException("Must specify an album or a song id");
        }
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            FileDescriptor fd = null;
            if (albumid < 0) {
                Uri uri = Uri.parse("content://media/external/audio/media/"
                        + songid + "/albumart");
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if (pfd != null) {
                    fd = pfd.getFileDescriptor();
                }
            } else {
                Uri uri = ContentUris.withAppendedId(albumArtUri, albumid);
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if (pfd != null) {
                    fd = pfd.getFileDescriptor();
                }
            }
            options.inSampleSize = 1;
// 只进行大小判断
            options.inJustDecodeBounds = true;
// 调用此方法得到options得到图片大小
            BitmapFactory.decodeFileDescriptor(fd, null, options);
// 我们的目标是在800pixel的画面上显示
// 所以需要调用computeSampleSize得到图片缩放的比例
            options.inSampleSize = 100;
// 我们得到了缩放的比例，现在开始正式读入Bitmap数据
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//根据options参数，减少所需要的内存
            bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bm;
    }

    /**
     * 获取专辑封面位图对象
     *
     * @param context
     * @param song_id
     * @param album_id
     * @param allowdefalut
     * @return
     */
    public static Bitmap getArtwork(Context context, long song_id, long album_id, boolean allowdefalut, boolean small) {
        if (album_id < 0) {
            if (song_id < 0) {
                Bitmap bm = getArtworkFromFile(context, song_id, -1);
                if (bm != null) {
                    return bm;
                }
            }
            if (allowdefalut) {
                return getDefaultArtwork(context, small);
            }
            return null;
        }
        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(albumArtUri, album_id);
        if (uri != null) {
            InputStream in = null;
            try {
                in = res.openInputStream(uri);
                BitmapFactory.Options options = new BitmapFactory.Options();
                //先制定原始大小
                options.inSampleSize = 1;
                //只进行大小判断
                options.inJustDecodeBounds = true;
                //调用此方法得到options得到图片的大小
                BitmapFactory.decodeStream(in, null, options);
                /** 我们的目标是在你N pixel的画面上显示。 所以需要调用computeSampleSize得到图片缩放的比例 **/
                /** 这里的target为800是根据默认专辑图片大小决定的，800只是测试数字但是试验后发现完美的结合 **/
                if (small) {
                    options.inSampleSize = computeSampleSize(options, 40);
                } else {
                    options.inSampleSize = computeSampleSize(options, 600);
                }
                // 我们得到了缩放比例，现在开始正式读入Bitmap数据
                options.inJustDecodeBounds = false;
                options.inDither = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                in = res.openInputStream(uri);
                return BitmapFactory.decodeStream(in, null, options);
            } catch (FileNotFoundException e) {
                Bitmap bm = getArtworkFromFile(context, song_id, album_id);
                if (bm != null) {
                    if (bm.getConfig() == null) {
                        bm = bm.copy(Bitmap.Config.RGB_565, false);
                        if (bm == null && allowdefalut) {
                            return getDefaultArtwork(context, small);
                        }
                    }
                } else if (allowdefalut) {
                    bm = getDefaultArtwork(context, small);
                }
                return bm;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 对图片进行合适的缩放
     *
     * @param options
     * @param target
     * @return
     */
    public static int computeSampleSize(BitmapFactory.Options options, int target) {
        int w = options.outWidth;
        int h = options.outHeight;
        int candidateW = w / target;
        int candidateH = h / target;
        int candidate = Math.max(candidateW, candidateH);
        if (candidate == 0) {
            return 1;
        }
        if (candidate > 1) {
            if ((w > target) && (w / candidate) < target) {
                candidate -= 1;
            }
        }
        if (candidate > 1) {
            if ((h > target) && (h / candidate) < target) {
                candidate -= 1;
            }
        }
        return candidate;
    }

    private static Bitmap getDefaultArtwork(Context context, boolean small) {
        return BitmapFactory.decodeResource(context.getResources(), R.mipmap
                .app_logo2);

    }


    public long[] getMp3InfosIds(Context context) {
        return null;
    }

    public interface QuerySuccess {
        void querySuccess(boolean isSuccess);
    }
}

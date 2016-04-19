package com.slht.suixinplayer.Bean;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by Li on 2016/4/13.
 */
public class MP3Info implements Parcelable {
    public static final Parcelable.Creator<MP3Info> CREATOR = new Creator<MP3Info>() {
        @Override
        public MP3Info createFromParcel(Parcel source) {
            MP3Info mp3Info = new MP3Info();
            mp3Info.id = source.readLong();
            mp3Info.title = source.readString();
            mp3Info.artist = source.readString();
            mp3Info.album = source.readString();
            mp3Info.albumId = source.readLong();
            mp3Info.duration = source.readLong();
            mp3Info.size = source.readLong();
            mp3Info.url = source.readString();
            mp3Info.isMusic = source.readInt();
            mp3Info.bitImage = Bitmap.CREATOR.createFromParcel(source);
            return mp3Info;
        }

        @Override
        public MP3Info[] newArray(int size) {
            return new MP3Info[size];
        }
    };
    private long id;
    private String title;
    private String artist;
    private String album;
    private long albumId;
    private long duration;
    private long size;
    private String url;
    private int isMusic;
    private Bitmap bitImage;

    public Bitmap getBitImage() {
        return bitImage;
    }

    public void setBitImage(Bitmap bitImage) {
        this.bitImage = bitImage;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIsMusic() {
        return isMusic;
    }

    public void setIsMusic(int isMusic) {
        this.isMusic = isMusic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(album);
        dest.writeLong(albumId);
        dest.writeLong(duration);
        dest.writeLong(size);
        dest.writeString(url);
        dest.writeInt(isMusic);
        bitImage.writeToParcel(dest, 0);
    }
}

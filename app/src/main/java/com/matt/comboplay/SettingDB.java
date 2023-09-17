package com.matt.comboplay;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SettingDB {
    private static SettingDB mInstance = null;
    public static SettingDB getInstance(Context c) {
        if(mInstance == null) {
            mInstance = new SettingDB(c);
        }
        return mInstance;
    }
    private SharedPreferences mSharedPref;
    private Context mContext;
    public SettingDB(Context c) {
        mContext = c;
        mSharedPref = c.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        if (!mSharedPref.contains(c.getString(R.string.preference_DeviceType))) {
            Log.d("Matt", "no preference_DeviceType");
            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putInt(c.getString(R.string.preference_DeviceType), 0);
            editor.commit();
        }
        if (!mSharedPref.contains(c.getString(R.string.preference_SyncEnable))) {
            Log.d("Matt", "no preference_SyncEnable");
            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putInt(c.getString(R.string.preference_SyncEnable), 0);
            editor.commit();
        }
        if (!mSharedPref.contains(c.getString(R.string.preference_debug))) {
            Log.d("Matt", "no preference_debug");
            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putInt(c.getString(R.string.preference_debug), 1);
            editor.commit();
        }
        if (!mSharedPref.contains(c.getString(R.string.preference_VideoPath))) {
            Log.d("Matt", "no preference_VideoPath");
            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putString(c.getString(R.string.preference_VideoPath), "/storage/emulated/0/Movies/video.mp4");
            editor.commit();
        }
    }

    public int getDeviceType() {
        return mSharedPref.getInt(mContext.getString(R.string.preference_DeviceType), 0);
    }

    public void setDeviceType(int type) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt(mContext.getString(R.string.preference_DeviceType), type);
        editor.commit();
    }

    public int getSyncEanble() {
        return mSharedPref.getInt(mContext.getString(R.string.preference_SyncEnable), 0);
    }

    public void setSyncEnable(int enable) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt(mContext.getString(R.string.preference_SyncEnable), enable);
        editor.commit();
    }

    public int getDebug() {
        return mSharedPref.getInt(mContext.getString(R.string.preference_debug), 0);
    }

    public void setDebug(int debug) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt(mContext.getString(R.string.preference_debug), debug);
        editor.commit();
    }

    public String getVideoPath() {
        return mSharedPref.getString(mContext.getString(R.string.preference_VideoPath), "/storage/emulated/0/Movies/video.mp4");
    }

    public void setVideoPath(String path) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(mContext.getString(R.string.preference_VideoPath), path);
        editor.commit();
    }
    public void Log(String msg) {
        if(getDebug()==1) {
            Log.d("Matt", msg);
        }
    }
}

package com.matt.comboplay;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.core.view.ViewCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends Activity {
    private final Activity mainActivity = this;
    private SettingDB mSettingDB = null;
    public static final int MSG_INITIAL_DONE = 1;
    private static final long TOTAL_DEVICE_NUM = 2;
    private ControlGPIO mControlGPIO;
    private TextView mDebugText;
    private StringBuffer mDbgText = new StringBuffer();
    private long mDebugTimeDelay;
    private Process mProcess;
    private SharedPreferences settings;
    private VideoView vid;
    private static int DEVICE_POSITION = 0;
    private static boolean UI_DEBUG = true;
    private static boolean VIDEO_SYNC_ENABLE = false;
    private static Status mVideoStatus = Status.NULL;
    public static int LEAVE_MONITOR = 0;
    public static int VID_PREPARED = 0;
    private final int MSG_START_PLAY = 2;
    private final int MSG_RESTART = 3;
    private Handler mHandler = new Handler() { // from class: syncvideo.android.mathias.syncvideo.MainActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (UI_DEBUG) {
                        showUIDebug("InitializeGPIO done");
                    }
                    MainActivity.this.vid.setVideoPath(SettingDB.getInstance(MainActivity.this).getVideoPath());
                    if (UI_DEBUG) {
                        showUIDebug("setVideoPath");
                    }
                    MainActivity.LEAVE_MONITOR = 0;
                    MainActivity.this.MonitorGPIOStart();
                    return;
                case 2:
                    if (UI_DEBUG) {
                        mDebugText.setBackgroundColor(-16711936);
                        showUIDebug("" + MainActivity.this.mDebugTimeDelay);
                    }
                    MainActivity.LEAVE_MONITOR = 1;
                    MainActivity.this.vid.start();
                    MainActivity.this.mControlGPIO.SetGPIO_OUT_HIGH();
                    return;
                case 3:
                    if (UI_DEBUG) {
                        mDebugText.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
                        showUIDebug("" + MainActivity.this.mDebugTimeDelay);
                    }
                    if (MainActivity.VIDEO_SYNC_ENABLE) {
                        Status unused = MainActivity.mVideoStatus = Status.NULL;
                        MainActivity.LEAVE_MONITOR = 0;
                        MainActivity.this.MonitorGPIOStart();
                        if (MainActivity.DEVICE_POSITION == 0) {
                            MainActivity.this.setReady();
                            return;
                        } else {
                            MainActivity.VID_PREPARED = 1;
                            return;
                        }
                    }
                    MainActivity.this.vid.start();
                    return;
                default:
                    return;
            }
        }
    };

    public void showUIDebug(String str) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDbgText.append(str+"\n");
                mDebugText.setText(mDbgText);
            }
        });
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettingDB = SettingDB.getInstance(this);
        setContentView(R.layout.activity_main);
        View decorView = getWindow().getDecorView();
// Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        mSettingDB.Log("Main");

        mVideoStatus = Status.NULL;
        VID_PREPARED = 0;

        DEVICE_POSITION = mSettingDB.getDeviceType();
        UI_DEBUG = mSettingDB.getDebug() != 0;
        VIDEO_SYNC_ENABLE = mSettingDB.getSyncEanble() != 0;
        this.mControlGPIO = new ControlGPIO(this.mHandler);
        this.mDebugText = (TextView) findViewById(R.id.textView);
        Button button = (Button) findViewById(R.id.home);
        button.setOnClickListener(new View.OnClickListener() { // from class: syncvideo.android.mathias.syncvideo.MainActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Thread thr = new Thread(new Runnable() { // from class: syncvideo.android.mathias.syncvideo.MainActivity.2.1
                    @Override // java.lang.Runnable
                    public void run() {
/* ActivityTaskManager: START u0 {act=android.intent.action.MAIN cat=[android.intent.category.HOME] flg=0x10000100 cmp=com.android.launcher3/.uioverrides.QuickstepLauncher (has extras)} from uid 0
[   93.313659@3]  type=1400 audit(1687272751.156:363): avc: denied { write } for comm="droid.launcher3" name="property_service" dev="tmpfs" ino=17593 scontext=u:r:priv_app:s0:c512,c768 tcontext=u:object_r:property_socket:s0 tclass=sock_file permissive=1 app=com.android.launcher3
                        */
                        try {
                            Process process = Runtime.getRuntime().exec("su");
                            OutputStream os = process.getOutputStream();
                            Log.d("MATT", "am start -n com.android.launcher3/.uioverrides.QuickstepLauncher\n");
                            os.write("am start -n com.android.launcher3/.uioverrides.QuickstepLauncher\n".getBytes());
                            os.flush();
                        } catch (IOException e) {
                            Log.d("MATT", "exeception");
                            e.printStackTrace();
                        }

                        /*
                        Intent i = new Intent(mainActivity, SettingsActivity.class);
                        startActivity(i);
                         */
                    }
                });
                thr.start();
            }
        });
        Button buttonS = (Button) findViewById(R.id.setting);
        buttonS.setOnClickListener(new View.OnClickListener() { // from class: syncvideo.android.mathias.syncvideo.MainActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Thread thr = new Thread(new Runnable() { // from class: syncvideo.android.mathias.syncvideo.MainActivity.2.1
                    @Override // java.lang.Runnable
                    public void run() {
                        Intent i = new Intent(mainActivity, SettingsActivity.class);
                        startActivity(i);
                    }
                });
                thr.start();
            }
        });
        this.vid = (VideoView) findViewById(R.id.videoView);
        this.vid.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: syncvideo.android.mathias.syncvideo.MainActivity.3
            @Override // android.media.MediaPlayer.OnPreparedListener
            public void onPrepared(MediaPlayer mp) {
                if (UI_DEBUG) {
                    showUIDebug("onPrepared");
                }
                if (MainActivity.VIDEO_SYNC_ENABLE) {
                    if (MainActivity.DEVICE_POSITION == 0) {
                        Log.d("Matt", "Master device video ready");
                        if (UI_DEBUG) {
                            showUIDebug("Master device video ready");
                        }
                        MainActivity.this.setReady();
                        return;
                    }
                    MainActivity.VID_PREPARED = 1;
                    return;
                }
                MainActivity.this.vid.start();
            }
        });
        this.vid.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: syncvideo.android.mathias.syncvideo.MainActivity.4
            @Override // android.media.MediaPlayer.OnCompletionListener
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d("Matt", "onCompletion");
                if (UI_DEBUG) {
                    showUIDebug("video complete");
                }
                MainActivity.this.mHandler.sendEmptyMessageDelayed(3, 3000L);
            }
        });

        if (UI_DEBUG) {
            showUIDebug("onCreate "+DEVICE_POSITION);
        }
        if(Environment.isExternalStorageManager() == false) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromParts("package", this.getPackageName(), null);
            intent.setData(uri);
            this.startActivity(intent);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        DEVICE_POSITION = mSettingDB.getDeviceType();
        if (UI_DEBUG) {
            showUIDebug("onResume device "+DEVICE_POSITION);
        }

        if (VIDEO_SYNC_ENABLE) {
            if (UI_DEBUG) {
                showUIDebug("InitializeGPIO");
            }
            this.mControlGPIO.InitializeGPIO();
        } else {
            this.vid.setVideoPath(SettingDB.getInstance(this).getVideoPath());
        }
    }

    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        VID_PREPARED = 0;
        LEAVE_MONITOR = 1;
        mVideoStatus = Status.NULL;
        this.vid.stopPlayback();
    }

    public void setReady() {
        if (UI_DEBUG) {
            showUIDebug("gpio ready");
        }
        mVideoStatus = Status.READY;
        this.mControlGPIO.SetReady();
    }

    public void setStart() {
        if (UI_DEBUG) {
            showUIDebug("video start");
        }
        mVideoStatus = Status.START;
        this.mControlGPIO.SetStart();
    }

    public void MonitorGPIOStart() {
        Thread thr = new Thread(new Runnable() { // from class: syncvideo.android.mathias.syncvideo.MainActivity.5
            @Override // java.lang.Runnable
            public void run() {
                try {
                    Log.d("Matt", "MonitorGPIOStart");
                    Runtime runtime = Runtime.getRuntime();
                    long readytime = 0;
                    while (MainActivity.LEAVE_MONITOR == 0) {
                        Process process = runtime.exec("cat /sys/class/gpio/gpio508/value");
                        InputStream is = process.getInputStream();
                        InputStreamReader isr = new InputStreamReader(is);
                        char[] chValue = new char[1];
                        isr.read(chValue);

                        if (MainActivity.DEVICE_POSITION == 0) {
                            //Log.d("Matt", "GPIO in "+chValue[0]);
                            //Log.d("Matt", "MainActivity.mVideoStatus="+MainActivity.mVideoStatus);
                            if (MainActivity.mVideoStatus != Status.READY) {
                                if (MainActivity.mVideoStatus == Status.START && chValue[0] == '1') {
                                    MainActivity.this.mHandler.sendEmptyMessage(2);
                                }
                            } else if (chValue[0] == '0') {
                                MainActivity.this.setStart();
                            }
                        } else if (MainActivity.mVideoStatus != Status.NULL) {
                            //Log.d("Matt", "GPIO in "+chValue[0]);
                            //Log.d("Matt", "MainActivity.mVideoStatus="+MainActivity.mVideoStatus);
                            if (MainActivity.mVideoStatus == Status.READY && chValue[0] == '1') {
                                long starttime = System.currentTimeMillis();
                                //Log.d("Matt", "starttime = " + starttime);
                                //Log.d("Matt", "ready - start = " + (starttime - readytime));
                                MainActivity.this.setStart();
                                if (MainActivity.DEVICE_POSITION == 1) {
                                    MainActivity.this.mDebugTimeDelay = ((starttime - readytime) / 2) * 1;
                                    //Log.d("Matt", "delayTime = " + MainActivity.this.mDebugTimeDelay);
                                    Thread.sleep(MainActivity.this.mDebugTimeDelay);
                                }
                                MainActivity.this.mHandler.sendEmptyMessage(2);
                            }
                        } else if (MainActivity.VID_PREPARED == 1 && chValue[0] == '0') {
                            readytime = System.currentTimeMillis();
                            //Log.d("Matt", "readytime = " + readytime);
                            MainActivity.this.setReady();
                        }
                        isr.close();
                        is.close();
                        Thread.sleep(50L);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
            }
        });
        thr.start();
    }
}
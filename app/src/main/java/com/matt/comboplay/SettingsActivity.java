package com.matt.comboplay;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SettingsActivity extends Activity implements RadioGroup.OnCheckedChangeListener {
    private SettingDB mSettingDB = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettingDB = SettingDB.getInstance(this);
        setContentView(R.layout.activity_settings);
        int deviceType = SettingDB.getInstance(this).getDeviceType();
        int syncEnable = SettingDB.getInstance(this).getSyncEanble();
        int dbg = SettingDB.getInstance(this).getDebug();

        RadioGroup groupDeviceType = findViewById(R.id.radioGroupDeviceType);
        groupDeviceType.check((deviceType==0)?R.id.radioButtonTypeSlave:R.id.radioButtonTypeMaster);
        RadioGroup groupSyncEnable = findViewById(R.id.radioGroupSyncEnable);
        groupSyncEnable.check((syncEnable==0)?R.id.radioButtonSyncOff:R.id.radioButtonSyncOn);
        RadioGroup groupDBG = findViewById(R.id.radioGroupDebug);
        groupDBG.check((dbg==0)?R.id.radioButtonDebugOff:R.id.radioButtonDebugOn);
        TextView videoPath = findViewById(R.id.textViewVideoPath);
        videoPath.setText(mSettingDB.getVideoPath());

        groupDeviceType.setOnCheckedChangeListener(this);
        groupSyncEnable.setOnCheckedChangeListener(this);
        groupDBG.setOnCheckedChangeListener(this);
//        ControlGPIO gpio = new ControlGPIO();
//        gpio.test();
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        if (checkedId == R.id.radioButtonTypeMaster) {
            mSettingDB.Log("Master");
            mSettingDB.setDeviceType(1);
        }
        else if (checkedId == R.id.radioButtonTypeSlave) {
            mSettingDB.Log("Slave");
            mSettingDB.setDeviceType(0);
        }
        else if (checkedId == R.id.radioButtonSyncOff) {
            mSettingDB.Log("Sync off");
            mSettingDB.setSyncEnable(0);
        }
        else if (checkedId == R.id.radioButtonSyncOn) {
            mSettingDB.Log("Sync on");
            mSettingDB.setSyncEnable(1);
        }
        else if (checkedId == R.id.radioButtonDebugOff) {
            mSettingDB.Log("debug off");
            mSettingDB.setDebug(0);
        }
        else if (checkedId == R.id.radioButtonDebugOn) {
            mSettingDB.setDebug(1);
            mSettingDB.Log("debug on");
        }
    }
}
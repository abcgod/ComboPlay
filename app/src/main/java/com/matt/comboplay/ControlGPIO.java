package com.matt.comboplay;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class ControlGPIO {
    private static final int GPIO_IN = 176;
    private static final int GPIO_OUT = 151;
    private static final int MSG_INIT = 1;
    private static final int MSG_SETREADY = 3;
    private static final int MSG_SETSTART = 2;
    private Handler mGpioHandler;
    private Handler mUIMainHandler;

    public ControlGPIO(Handler uiMainHandler) {
        this.mUIMainHandler = uiMainHandler;
        HandlerThread handlerThread = new HandlerThread("HandlerThread");
        handlerThread.start();
        this.mGpioHandler = new Handler(handlerThread.getLooper()) { // from class: syncvideo.android.mathias.syncvideo.ControlGPIO.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        ControlGPIO.this._InitializeGPIO();
                        return;
                    case 2:
                        ControlGPIO.this._SetStart();
                        return;
                    case 3:
                        ControlGPIO.this._SetReady();
                        return;
                    default:
                        return;
                }
            }
        };
/*
        Runtime r = Runtime.getRuntime();
        try {
            Process process = r.exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("echo 508 > /sys/class/gpio/export\n");//37	PIN.Z10	508
            os.flush();
            os.writeBytes("echo in > /sys/class/gpio/gpio508/direction\n");//Set GPIO input
            os.flush();

            os.writeBytes("echo 506 > /sys/class/gpio/export\n");//36	PIN.Z8	506
            os.flush();
            os.writeBytes("echo out > /sys/class/gpio/gpio506/direction\n");//Set GPIO output
            os.flush();
            os.writeBytes("echo 0 > /sys/class/gpio/gpio506/value\n");//Set GPIO output low
            os.flush();
            os.close();
        } catch (IOException e)
        {
            Log.d("Matt", "exception");
        }
 */
    }

    public void test() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int gpioValue = 1;
                while (true) {
                    try {
                        Runtime r = Runtime.getRuntime();
                        Process process = r.exec("su");
                        DataOutputStream os = new DataOutputStream(process.getOutputStream());
                        if(gpioValue == 1){
                            gpioValue = 0;
                            os.writeBytes("echo 0 > /sys/class/gpio/gpio506/value\n");//Set GPIO output low
                        } else {
                            gpioValue = 1;
                            os.writeBytes("echo 1 > /sys/class/gpio/gpio506/value\n");//Set GPIO output high
                        }
                        os.flush();
                        os.close();
                        Thread.sleep(2000);
//cat /sys/class/gpio/gpio506/value
//cat /sys/class/gpio/gpio508/value
                        process = r.exec("cat /sys/class/gpio/gpio508/value");
                        InputStream is = process.getInputStream();
                        InputStreamReader isr = new InputStreamReader(is);
                        char c = (char) isr.read();
                        Log.d("Matt", "get gpio : "+c);
                        is.close();
                    } catch (IOException e)
                    {
                        Log.d("Matt", "IOException");
                    }
                    catch (InterruptedException e)
                    {
                        Log.d("Matt", "InterruptedException");
                    }
                }
            }
        }).start();

    }

    public void SetStart() {
        this.mGpioHandler.sendEmptyMessage(2);
    }

    public void SetReady() {
        this.mGpioHandler.sendEmptyMessage(3);
    }

    public void InitializeGPIO() {
        this.mGpioHandler.sendEmptyMessage(1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _SetStart() {
        try {
            Process process = Runtime.getRuntime().exec("su");
            OutputStream os = process.getOutputStream();
            os.write("echo 1 > /sys/class/gpio/gpio506/value\n".getBytes());
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _SetReady() {
        try {
            Process process = Runtime.getRuntime().exec("su");
            OutputStream os = process.getOutputStream();
            os.write("echo 0 > /sys/class/gpio/gpio506/value\n".getBytes());
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _InitializeGPIO() {
        try {
            Log.d("Matt", "InitializeGPIO");
            Process process = Runtime.getRuntime().exec("su");
            OutputStream os = process.getOutputStream();
                os.write("echo 506 > /sys/class/gpio/export\n".getBytes());
            os.flush();
            Thread.sleep(100L);
            os.write("echo out > /sys/class/gpio/gpio506/direction\n".getBytes());
            os.flush();
            Thread.sleep(100L);
            os.write("echo 1 > /sys/class/gpio/gpio506/value\n".getBytes());
            os.flush();
            Thread.sleep(100L);
            os.write("echo 508 > /sys/class/gpio/export\n".getBytes());
            os.flush();
            Thread.sleep(100L);
            os.write("echo in > /sys/class/gpio/gpio508/direction\n".getBytes());
            os.flush();
            Thread.sleep(100L);
            os.close();
            Thread.sleep(10000L);
            this.mUIMainHandler.sendEmptyMessage(1);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        }
    }

    public void SetGPIO_OUT_LOW() {
        try {
            Process process = Runtime.getRuntime().exec("su");
            OutputStream os = process.getOutputStream();
            os.write("echo 0 > /sys/class/gpio/gpio506/value\n".getBytes());
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SetGPIO_OUT_HIGH() {
        try {
            Process process = Runtime.getRuntime().exec("su");
            OutputStream os = process.getOutputStream();
            os.write("echo 1 > /sys/class/gpio/gpio506/value\n".getBytes());
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

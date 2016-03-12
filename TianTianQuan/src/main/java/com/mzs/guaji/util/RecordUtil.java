package com.mzs.guaji.util;

import android.content.Context;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Environment;

import java.io.File;

/**
 * Created by wlanjie on 13-12-16.
 * 录音工具类
 */
public class RecordUtil {

    private static final int SAMPLE_RATE_IN_HZ = 8000;
    private Context context;
    private MediaRecorder mRecorder;
    private String mPath;
    private File mAudioFile;

    public RecordUtil(Context context) {
        this.context = context;
        mRecorder = new MediaRecorder();
        mPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Guaji/audio/";
        File mFile = new File(mPath);
        if(!mFile.exists()) {
            mFile.mkdirs();
        }
    }

    public void startRecord() {
        if(!StorageUtil.isExternalStorageAvailable()) {
           ToastUtil.showToast(context, "请检查sd卡是否可用");
            return;
        }
        mPath = mPath + +System.currentTimeMillis()+".amr";
        mAudioFile = new File(mPath);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mRecorder.setAudioChannels(AudioFormat.CHANNEL_CONFIGURATION_MONO);
        mRecorder.setAudioSamplingRate(SAMPLE_RATE_IN_HZ);
        mRecorder.setOutputFile(mPath);
        try {
            mRecorder.prepare();
            mRecorder.start();
        }catch (Exception e) {
            e.printStackTrace();
        }
     }

    public void stopRecord() {
        if(mRecorder != null) {
            mRecorder.stop();
//            mRecorder.release();
//            mRecorder = null;
        }
    }

    public void release() {
        try {
            if(mRecorder != null) {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            }
        }catch (Exception e) {
           mRecorder = null;
        }
    }

    public double getAudioTime() {
        if(mRecorder != null) {
            return mRecorder.getMaxAmplitude();
        }else {
            return 0;
        }
    }

    public File getRecordFile() {
        return mAudioFile;
    }
}

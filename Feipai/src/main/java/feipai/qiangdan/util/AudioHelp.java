package feipai.qiangdan.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.SoundPool;


/**
 * 播放声音工具类
 */
public class AudioHelp {

    private static SoundPool soundPool;
    private static int streamId;
    private static MediaPlayer mFirstPlayer;

    public static void systemSoud(Context mContext) {
        MediaPlayer mMediaPlayer = MediaPlayer.create(mContext,
                RingtoneManager.getActualDefaultRingtoneUri(mContext, RingtoneManager.TYPE_RINGTONE));
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();

    }

    /**
     * 播放声音 SoundPool实现
      * @param context 上下文
     * @param soundid 声音源
     */
    public static void SoundPool(Context context, int soundid) {
  /*      AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        final float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        final  float streamVolume2 = streamVolume/maxVolume;*/

        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 5);
        //载入音频流，返回在池中的id
        final int sourceid = soundPool.load(context, soundid, 0);
        //播放音频，第二个参数为左声道音量;第三个参数为右声道音量;第四个参数为优先级；第五个参数为循环次数，0不循环，-1循环,也可以指定具体次数;第六个参数为速率，速率最低0.5最高为2，1代表正常速度
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {

            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                // TODO Auto-generated method stub
                streamId = soundPool.play(sourceid, 1.0f, 1.0f, 1, 0, 1);//6指声音循环6次
            }
        });
    }

    public static void cancelSoundPool() {
        if (soundPool != null)
            soundPool.stop(streamId);
    }

    /**
     * 播放声音 MediaPlayer实现
     * @param context 上下文
     * @param voiceList 声音源
     */
    public static void playVoice(Context context, Integer... voiceList) {

        int size = voiceList.length;
        if(size == 0){
            return;
        }
         mFirstPlayer = MediaPlayer.create(context.getApplicationContext(), voiceList[0]);
        MediaPlayer mPlayer2 = null;
        for (int i = 1; i < voiceList.length; i++) {
            if(i == 1){
                mPlayer2 = playVoice(context,mFirstPlayer,voiceList[i]);
            }else{
                mPlayer2 = playVoice(context,mPlayer2,voiceList[i]);
            }

        }
        mFirstPlayer.start();

    }

    public static MediaPlayer playVoice(Context context,MediaPlayer mPlayer,Integer nextRec){
        MediaPlayer mPlayer2 = MediaPlayer.create(context.getApplicationContext(), nextRec);
        mPlayer.setNextMediaPlayer(mPlayer2);
        return mPlayer2;
    }

    /**
     * 释放声音源
     * release()可以释放播放器占用的资源，一旦确定不再使用播放器时应当尽早调用它释放资源。
     */
    public static void releaseMediaPlayer() {
        if (mFirstPlayer != null)
            mFirstPlayer.release();
    }
}

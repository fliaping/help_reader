package com.fliaping.help.reader.helpreader;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * Created by payne on 12/30/17.
 */

public final class ListenClipboardService extends Service {

    private static boolean isStarted = false;
    private ClipboardManager mClipboardManager;
    private TextToSpeech mSpeech;
    private ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
        public void onPrimaryClipChanged() {
            showAction();
        }
    };

    public static void start(final Context context) {
        if (!isStarted) {
            Intent serviceIntent = new Intent(context, ListenClipboardService.class);
            context.startService(serviceIntent);
        }
        Log.d(TAG, "start()");
    }

    public static void stop(Context context) {
        Intent serviceIntent = new Intent(context, ListenClipboardService.class);
        context.stopService(serviceIntent);
    }

    private void showAction() {
        ClipData primaryClip = mClipboardManager.getPrimaryClip();
        if (primaryClip != null && primaryClip.getItemCount() > 0 /*&& !"BigBang".equals(primaryClip.getDescription().getLabel())*/) {
            CharSequence text = primaryClip.getItemAt(0).coerceToText(this);

            Log.d(TAG, "粘贴板：" + text);
            mSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, System.currentTimeMillis() + "");

        }
    }

    @Override
    public void onCreate() {
        mClipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        mClipboardManager.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener);

        Log.d(TAG, "onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
        if (!isStarted) {
            // 创建TTS对象
            final Context context = this.getApplicationContext();
            mSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {

                @Override
                public void onInit(int status) {
                    // TODO Auto-generated method stub
                    if (status == TextToSpeech.SUCCESS) {
                        int result = mSpeech.setLanguage(Locale.CHINESE);
                        if (result == TextToSpeech.LANG_MISSING_DATA
                                || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Toast.makeText(context, "Language is not available.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            // 在API11之后构建Notification的方式
            Notification.Builder builder = new Notification.Builder
                    (this.getApplicationContext()); //获取一个Notification构造器
            Intent nfIntent = new Intent(this, MainActivity.class);

            builder.setContentIntent(PendingIntent.
                    getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
                    .setContentTitle("助读监听中") // 设置下拉列表里的标题
                    .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                    .setContentText("正在监听粘贴板") // 设置上下文内容
                    .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

            Notification notification = builder.build(); // 获取构建好的Notification
            notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音

            startForeground(812, notification);// 开始前台服务

            isStarted = true;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mClipboardManager.removePrimaryClipChangedListener(mOnPrimaryClipChangedListener);
        stopForeground(true);// 停止前台服务--参数：表示是否移除之前的通知
        isStarted = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}

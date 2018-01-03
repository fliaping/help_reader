package com.fliaping.help.reader.helpreader;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListenClipboardService.start(this);
        //mSpeech.speak("hello world", TextToSpeech.QUEUE_FLUSH, null, System.currentTimeMillis() + "");
    }
}

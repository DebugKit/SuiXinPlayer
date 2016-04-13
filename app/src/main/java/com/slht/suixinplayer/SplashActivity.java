package com.slht.suixinplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class SplashActivity extends Activity {

    private static final int START_ACTIVITY = 0x1;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == START_ACTIVITY) {
                Toast.makeText(SplashActivity.this, "this", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SplashActivity.this, com.astuetz.viewpager.extensions.sample.MainActivity
                        .class));
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler.sendEmptyMessageDelayed(START_ACTIVITY, 3000);
    }
}

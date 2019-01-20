package com.cartoon.fam.cartoon;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.VideoView;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Final extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        Intent intent = getIntent();
        String url = intent.getStringExtra(ShowActivity.lastUrl);

        VideoView videoView = findViewById(R.id.videoView);
        videoView.setVideoURI(Uri.parse(url));

        Map<String, String> headerHashMap = new HashMap<String, String>();
        //headerHashMap.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0");
        headerHashMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        headerHashMap.put("Accept-Language", "en-US,en;q=0.5");
        headerHashMap.put("DNT", "1");
        headerHashMap.put("Connection", "keep-alive");
        headerHashMap.put("Upgrade-Insecure-Requests", "1");
        headerHashMap.put("Cache-Control", "max-age=0");

        try {
            Field field = VideoView.class.getDeclaredField("mHeaders");
            field.setAccessible(true);
            field.set(videoView,  headerHashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

// the rest is just standard VideoView stuff
        MediaController mc = new MediaController(Final.this);
        mc.setAnchorView(videoView);
        videoView.setMediaController(mc);

        /*int videoWidth = videoView.getMeasuredWidth();
        int videoHeight = videoView.getMeasuredHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();

        float screenProportion = (float) screenWidth / (float) screenHeight;
        android.view.ViewGroup.LayoutParams lp = videoView.getLayoutParams();

        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }

        videoView.setLayoutParams(lp);*/

        videoView.start();
    }
}

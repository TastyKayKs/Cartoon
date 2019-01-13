package com.cartoon.fam.cartoon;

import android.os.AsyncTask;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class URLAsyncTask extends AsyncTask {
    public Document html = null;

    @Override
    protected Void doInBackground(Object... objects) {
        try {
            this.html = Jsoup.connect((String) objects[0]).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

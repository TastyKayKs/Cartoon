package com.cartoon.fam.cartoon;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

public class ShowActivity extends AppCompatActivity {
    String scrapedPage = null;
    Boolean started = false;
    Boolean finished = false;

    String firstHop = null;

    ArrayList<String> finalLinks = new ArrayList<String>();

    public static int delay = 3;

    public static String lastUrl = "com.cartoon.fam.cartoon.actualVid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        TextView textView = findViewById(R.id.textView);
        textView.bringToFront();

        textView.setText(("Grabbing links..."));

        final WebView browser = findViewById(R.id.webView);

        Intent intent = getIntent();
        String url = intent.getStringExtra(MainActivity.episodeFinal);

        final ListView vids = findViewById(R.id.listViewVids);

        vids.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ShowActivity.this, Final.class);

                intent.putExtra(lastUrl, vids.getItemAtPosition(position).toString());
                startActivity(intent);
            }
        });

        final ArrayAdapter<String> adaptLinks = new ArrayAdapter<String>(ShowActivity.this, android.R.layout.simple_dropdown_item_1line, finalLinks);
        vids.setAdapter(adaptLinks);

        /* An instance of this class will be registered as a JavaScript interface */
        class MyJavaScriptInterface {
            @JavascriptInterface
            @SuppressWarnings("unused")
            public void processHTML(String html) {
                try {
                    if (firstHop == null) {
                        //Log.d("HTMLTEST", "HTMLTEST");

                        try {
                            scrapedPage = html;
                            Document outHTML = Jsoup.parse(scrapedPage);

                            firstHop = ("https://www.thewatchcartoononline.tv" + (outHTML.getElementsByTag("iframe").get(1)).attr("src"));
                        } catch(Error e) {
                            TextView textView = findViewById(R.id.textView);
                            textView.setText(("DOCUMENT LOADED TOO SLOWLY!"));
                            textView.bringToFront();

                            Button retry = findViewById(R.id.buttonDelay);
                            retry.bringToFront();
                            retry.requestFocus();
                        }
                    } else if (!finished) {
                        //Log.d("HTMLTEST", "HTMLTEST");
                        try {
                            scrapedPage = html;
                            Document outHTML = Jsoup.parse(scrapedPage);

                            finalLinks.clear();

                            for (Element link : outHTML.getElementsByTag("script")) {
                                if (link.toString().contains("http") && !link.toString().contains("google-analytics.com/analytics") && link.toString().length() > 100) {
                                    for (String url : link.toString().split("'")) {
                                        if (url.contains("http")) {
                                            finalLinks.add(url);
                                        }
                                    }
                                }
                            }
                            finished = true;
                            //Log.d("HTMLTEST", "HTMLTEST");
                        } catch (Error e) {
                            finished = false;
                            TextView textView = findViewById(R.id.textView);
                            textView.bringToFront();

                            textView.setText(("Link grab failed, retrying with longer delay..."));

                            SystemClock.sleep(1000);

                            delay+=3;
                            recreate();
                        }
                    }
                } catch(Error e) {
                    TextView textView = findViewById(R.id.textView);
                    textView.bringToFront();

                    textView.setText(("Link grab failed, retrying with longer delay..."));

                    SystemClock.sleep(1000);

                    delay+=3;
                    recreate();
                }
            }
        }
        //browser.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0");

        /* JavaScript must be enabled if you want it to work, obviously */
        browser.getSettings().setJavaScriptEnabled(true);

        /* Register a new JavaScript interface called HTMLOUT */
        browser.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");

        /* WebViewClient must be set BEFORE calling loadUrl! */
        browser.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (!started) {
                    started = true;
                }
            }
        });

        AsyncTask reloader0 = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                while (!started) {
                    SystemClock.sleep(100);
                }
                SystemClock.sleep(delay * 1000);

                browser.post(new Runnable() {
                    @Override
                    public void run() {
                        browser.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                    }
                });

                return null;
            }
        };

        reloader0.execute();

        AsyncTask reloader1 = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                while (firstHop == null) {
                    SystemClock.sleep(100);
                }
                //SystemClock.sleep(3000);

                browser.post(new Runnable() {
                    @Override
                    public void run() {
                        //Log.d("HTMLFINAL", firstHop);
                        browser.loadUrl(firstHop);
                    }
                });

                started = false;

                return null;
            }
        };

        reloader1.execute();

        AsyncTask reloader2 = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                while (!started && firstHop == null) {
                    SystemClock.sleep(100);
                }
                SystemClock.sleep(delay * 1000);

                browser.post(new Runnable() {
                    @Override
                    public void run() {
                        browser.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                    }
                });

                return null;
            }
        };

        reloader2.execute();

        AsyncTask reloader3 = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                while (!finished) {
                    SystemClock.sleep(100);
                }
                //SystemClock.sleep(1000);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);

                if (finalLinks.isEmpty()) {
                    TextView textView = findViewById(R.id.textView);
                    textView.setText(("NO LINKS FOUND!"));
                    textView.bringToFront();

                    Button retry = findViewById(R.id.buttonDelay);
                    retry.bringToFront();
                    retry.requestFocus();
                } else {
                    vids.bringToFront();

                    adaptLinks.notifyDataSetChanged();

                    vids.requestFocus();
                }
            }
        };

        reloader3.execute();

        /* load a web page */
        browser.loadUrl(url);
    }

    public void Retry(View view) {
        delay+=3;
        recreate();
    }
}

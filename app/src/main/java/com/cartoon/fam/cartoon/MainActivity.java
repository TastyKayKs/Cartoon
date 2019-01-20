package com.cartoon.fam.cartoon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //DEFINING THE EPISODE TITLE PUBLICLY AND FINALLY SO WE CAN PASS IT TO THE NEXT ACTIVITY
    public static String episodeFinal = "com.cartoon.fam.cartoon.episode";

    //DEFINING THE SHOW LINKS PUBLICLY
    Elements showLinks;

    //MAIN LINKS PRIVATELY
    Elements mainLinks;

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItemsMain = new ArrayList<String>();
    ArrayList<String> listItemsShows = new ArrayList<String>();

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);

        //CREATE A CUSTOM ASYNCHRONOUS TASK AND PASS THE MAIN CARTOON URL TO IT
        URLAsyncTask urlAsyncTask = new URLAsyncTask();
        urlAsyncTask.execute("https://www.thewatchcartoononline.tv/cartoon-list");

        //WAIT UNTIL THE TASK FINISHES (BASICALLY MAKE IT SYNCHRONOUS BECAUSE YOU CAN'T DO A SYNCHRONOUS URL GRAB IN THE MAIN ACTIVITY PAGE)
        while(urlAsyncTask.html == null){
            SystemClock.sleep(500);
        }

        //READ THE OUTPUT FROM THE TASK
        Document mainHTML = urlAsyncTask.html;

        //GET THE LINKS
        mainLinks = mainHTML.select("a");

        //APPEND ONLY THE TEXT OF THE VALID LINKS TO THE ARRAYLIST listItems
        for (Element link : mainLinks) {
            //SINGLE CALL TO MAKE THE COMPARISON FASTER
            Attributes tempAttr = link.attributes();

            //VALID LINKS CONTAIN BOTH AN HREF AND A TITLE
            if(!tempAttr.get("href").isEmpty() && !tempAttr.get("title").isEmpty() && !(tempAttr.get("title") == null)){

                //ADD VALID LINK TO ARRAYLIST
                listItemsMain.add(tempAttr.get("title"));
            }
        }

        //CREATE THE ADAPTER
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, listItemsShows);

        //FIND THE TEXT BOX AND SET THE ADAPTER THEN SET THRESHOLD TO 2
        final EditText editText = findViewById(R.id.editText);
        //editText.setAdapter(adapter);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showLinks = new Elements();
                listItemsShows.clear();
                for(String item : listItemsMain) {
                    if(item.toLowerCase().contains(editText.getText().toString().toLowerCase())) {
                        listItemsShows.add(item);
                    }
                }
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //ADD THE ITEMS TO THE AUTOCOMPLETE AND REFRESH
        adapter.notifyDataSetChanged();

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Boolean testCase = false;

                for (String check : listItemsShows) {
                    if (check.toLowerCase().equals(editText.getText().toString().toLowerCase())) {
                        testCase = true;
                    }
                    editText.clearFocus();
                    listView.requestFocus();
                }
                if (testCase && !showLinks.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, ShowActivity.class);

                    String episodeTitle = listView.getItemAtPosition(position).toString();

                    for (Element link : showLinks) {
                        //SINGLE CALL TO MAKE THE COMPARISON FASTER
                        Attributes tempAttr = link.attributes();

                        //FIND URL
                        if (tempAttr.size() >= 4 && tempAttr.get("title").equals(episodeTitle)) {
                            intent.putExtra(episodeFinal, tempAttr.get("href"));
                            startActivity(intent);
                        }
                    }
                } else {
                    //String episodeTitle = episode.getItemAtPosition(position).toString();
                    editText.setText(listView.getItemAtPosition(position).toString());
                }

                InputMethodManager imm = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        });
    }

    public void go(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        if(showLinks.isEmpty()) {
            EditText editText = findViewById(R.id.editText);
            Element foundLink = null;

            for (Element link : mainLinks) {
                //SINGLE CALL TO MAKE THE COMPARISON FASTER
                Attributes tempAttr = link.attributes();

                //VALID LINKS CONTAIN BOTH AN HREF AND A TITLE
                if (!tempAttr.get("href").isEmpty() && !tempAttr.get("title").isEmpty() && (tempAttr.get("title").equals(editText.getText().toString()))) {

                    //ADD VALID LINK TO ARRAYLIST
                    foundLink = link;
                }
            }

            //MAKE SURE WE FOUND SOMETHING
            if (foundLink != null) {
                //CREATE A CUSTOM ASYNCHRONOUS TASK AND PASS THE MAIN CARTOON URL TO IT
                URLAsyncTask showAsyncTask = new URLAsyncTask();
                showAsyncTask.execute(foundLink.attributes().get("href"));

                //WAIT UNTIL THE TASK FINISHES (BASICALLY MAKE IT SYNCHRONOUS BECAUSE YOU CAN'T DO A SYNCHRONOUS URL GRAB IN THE MAIN ACTIVITY PAGE)
                while (showAsyncTask.html == null) {
                    SystemClock.sleep(500);
                }

                //READ THE OUTPUT FROM THE TASK
                Document showHTML = showAsyncTask.html;

                //GET THE LINKS
                showLinks = showHTML.select("a");

                //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
                ArrayList<String> listItemsEps = new ArrayList<String>();

                for (Element link : showLinks) {
                    //SINGLE CALL TO MAKE THE COMPARISON FASTER
                    Attributes tempAttr = link.attributes();

                    //VALID LINKS HAVE MORE THAN 4 PROPERTIES
                    if (tempAttr.size() >= 4) {

                        //ADD VALID LINK TO ARRAYLIST
                        listItemsEps.add(tempAttr.get("title"));
                    }
                }

                //CREATE THE ADAPTER
                ArrayAdapter<String> adapterEps = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, listItemsEps);

                //FIND THE TEXT BOX AND ADD ADAPTER
                listView.setAdapter(adapterEps);

                //ADD THE ITEMS TO THE AUTOCOMPLETE AND REFRESH
                adapterEps.notifyDataSetChanged();
            }
        }
    }

    public void clear(View view) {
        EditText editText = findViewById(R.id.editText);
        editText.setText("");
        editText.requestFocus();
    }
}



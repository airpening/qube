package com.example.pening.qb;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GalleryActivity extends AppCompatActivity {
    private ListView my_Listview;
    private GalleryCustomAdapter my_Adapter;
    String myJSON;

    private static final String TAG_RESULTS="result";
    private static final String TAG_CONTENTS = "CONTENTS";
    private static final String TAG_SQUARE = "SQUARE_COUNT";
    private static final String TAG_BEST_SCORE ="BEST_SCORE";
    private static final String TAG_BEST_SCORE_ID = "BEST_SCORE_ID";
    private static final String TAG_BEST_TIME = "BEST_TIME";
    private static final String TAG_BEST_TIME_ID = "BEST_TIME_ID";
    private static final String TAG_USE_COUNT = "USE_COUNT";

    private Data data;

    JSONArray peoples = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        my_Adapter = new GalleryCustomAdapter();

        my_Listview = (ListView) findViewById(R.id.gallerylistview);

        my_Listview.setAdapter(my_Adapter);

        getData("http://220.67.128.58/gallery_load.php");

    }

    protected void showList(){
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            for(int i=0;i<peoples.length();i++){
                JSONObject c = peoples.getJSONObject(i);
                String contents = c.getString(TAG_CONTENTS);
                int square_count = c.getInt(TAG_SQUARE);

                int score = c.getInt(TAG_BEST_SCORE);
                String best_score_id = c.getString(TAG_BEST_SCORE_ID);

                String time = c.getString(TAG_BEST_TIME);
                String best_time_id = c.getString(TAG_BEST_TIME_ID);

                int use_count = c.getInt(TAG_USE_COUNT);
                data = new Data();
                data.setData(contents,square_count,score,time);
                data.setScore_time(best_score_id,best_time_id);
                data.setUsecount(use_count);

                my_Adapter.add(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getData(String url){
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    StringBuilder sb = new StringBuilder();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while((json = bufferedReader.readLine())!= null){
                        sb.append(json+"\n");
                    }

                    return sb.toString().trim();

                }catch(Exception e){
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result){
                myJSON=result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }
}

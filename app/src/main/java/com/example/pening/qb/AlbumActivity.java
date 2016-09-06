package com.example.pening.qb;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class AlbumActivity extends AppCompatActivity {
    private ListView my_Listview;
    private AlbumCustomAdapter my_Adapter;
    String myJSON;

    private static final String TAG_RESULTS="result";
    private static final String TAG_CONTENTS = "CONTENTS";
    private static final String TAG_SQUARE = "SQUARE_COUNT";
    private static final String TAG_SCORE ="SCORE";
    private static final String TAG_TIME = "TIME";

    JSONArray peoples = null;
    private Data data;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        my_Adapter = new AlbumCustomAdapter();


        my_Listview = (ListView) findViewById(R.id.mylistview);

        my_Listview.setAdapter(my_Adapter);

        getData("http://220.67.128.58/my_album_load.php",getIntent().getStringExtra("id"));

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }
    public void OnClickGallery(View v){
        Intent intent = new Intent(this, GalleryActivity.class);
        startActivity(intent);
    }
    protected void showList(){
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            for(int i=0;i<=peoples.length();i++){
                JSONObject c = peoples.getJSONObject(i);

                String contents = c.getString(TAG_CONTENTS);
                int square_count = c.getInt(TAG_SQUARE);
                int score = c.getInt(TAG_SCORE);
                String time = c.getString(TAG_TIME);
                data = new Data();
                data.setId(getIntent().getStringExtra("id"));
                data.setData(contents,square_count,score,time);
                my_Adapter.add(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getData(String url, String id){
        class GetDataJSON extends AsyncTask<String, Void, String>{

            @Override
            protected String doInBackground(String... params) {

                try {
                    String uri = params[0];
                    String id = (String)params[1];
                    String data = URLEncoder.encode("Id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");

                    URL url = new URL(uri);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));


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
        g.execute(url,id);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Album Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.pening.qb/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Album Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.pening.qb/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}

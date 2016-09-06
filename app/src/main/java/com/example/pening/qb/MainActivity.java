package com.example.pening.qb;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.*;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    IntentIntegrator integrator = new IntentIntegrator(this);
    final int width = 330;
    final int height = 330;

    private Bitmap bitmap;
    private ImageView view;
    private ImageView preview_image;
    private TextView text;
    private TextView preview_description;
    private Scan scan;
    private BitMatrix bytemap;
    private int size = 0;
    private int min_size = 330;
    private ArrayList<android.graphics.Point> center;
    private ArrayList<android.graphics.Point> ButtonCenter;
    private ArrayList<Rect> SquareInfo;
    private Canvas tempCanvas;
    private IntentResult result;
    private TextCrawler textCrawler;
    private Bitmap[] currentImageset;
    private Bitmap currentImage;

    private String album_contents;
    private Boolean AlbumGet;

    private Button save;

    private String id;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        view  = (ImageView)findViewById(R.id.imageView);
        preview_image = (ImageView)findViewById(R.id.thumbnail);

        text = (TextView)findViewById(R.id.textView);
        preview_description = (TextView)findViewById(R.id.preview_desc);

        alert.setTitle("Login");
        // Set an EditText view to get user input
        // Set an EditText view to get user input
        final EditText id = new EditText(this);

        alert.setView(id);

        alert.setPositiveButton("Log in", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                MainActivity.this.id = id.getText().toString();
                // Do something with value!
            }
        });
        alert.setNeutralButton("Join", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                MainActivity.this.id = id.getText().toString();
                USER_insertToDatabase(MainActivity.this.id);
                // Do something with value!
            }
        });

        alert.show();
        save = (Button) findViewById(R.id.insert_album);
        save.setEnabled(false);

        textCrawler = new TextCrawler();
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();



    }
    public void OnClickAlbum(View v){
        Intent intent = new Intent(this, AlbumActivity.class);
        intent.putExtra("id",this.id);
        startActivity(intent);
    }
    public void OnClickScan(View v) {
        integrator.initiateScan();
    }
    public void OnClickSave(View v){
        QR_insertToDatabase(id, result.getContents(), SquareInfo.size(), 0, 0);
    }
    public void OnClickText(View v){
        callChrome(text.getText().toString());
    }
    public void OnClickContour(View v){
        center = new ArrayList<>();
        ButtonCenter = new ArrayList<>();
        SquareInfo = new ArrayList<>();
        scan = new Scan();


        tempCanvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);


            for (int i = 0; i < width; i = i + min_size) {
                for (int j = 0; j < height; j = j + min_size) {
                    if (bytemap.get(i, j)) {
                        if ((min_size / 2) % 2 == 1) {
                            //bitmap.setPixel(i + min_size / 2, j + min_size / 2, Color.GREEN);
                            center.add(new android.graphics.Point(i + min_size / 2, j + min_size / 2));
                        } else {
                            //bitmap.setPixel(i - min_size / 2, j - min_size / 2, Color.GREEN);
                            center.add(new android.graphics.Point(i - min_size / 2, j - min_size / 2));
                        }
                    }
                }
            }

            scan.ButtonFind(bytemap, min_size, ButtonCenter, center);
/*
        for(int i = 0;i<ButtonCenter.size();i++){
            bitmap.setPixel(ButtonCenter.get(i).x,ButtonCenter.get(i).y,Color.RED);
        }
        */
            scan.EraseButtonSide(min_size, ButtonCenter, center);

/*
        for(int i = 0;i<center.size();i++){
            bitmap.setPixel(center.get(i).x,center.get(i).y,Color.GREEN);
        }
*/

            scan.Make_Square(min_size, SquareInfo, center);

            bitmap.eraseColor(Color.TRANSPARENT);

            for (int i = 0; i < SquareInfo.size(); i++) {
                tempCanvas.drawRect(SquareInfo.get(i), paint);
            }

            //Toast.makeText(getApplicationContext(), "QR Code를 한번더 터치하면 게임 화면으로 넘어갑니다.", Toast.LENGTH_SHORT).show();


            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            Intent intent = new Intent(this, GameActivity.class);

            intent.putExtra("image", byteArray);
            intent.putExtra("id",id);

            if(AlbumGet){
                intent.putExtra("contents",album_contents);
            }
            else {
                intent.putExtra("contents", result.getContents());
            }

            intent.putExtra("min_size", min_size);
            intent.putExtra("button_size", scan.GetButton_size());

            intent.putParcelableArrayListExtra("Square", SquareInfo);
            intent.putParcelableArrayListExtra("Button", ButtonCenter);
            intent.putParcelableArrayListExtra("Center", center);

            startActivity(intent);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        min_size = 330;
        size = 0;

        result = integrator.parseActivityResult(requestCode, resultCode, data);
        if(result.getContents() == null){
            Toast.makeText(getApplicationContext(), "스캔 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        AlbumGet = false;
        Make_QR(result.getContents());
        textCrawler.makePreview(callback, result.getContents());

    }
    private LinkPreviewCallback callback = new LinkPreviewCallback() {
        @Override
        public void onPre() {
            currentImageset = null;
            currentImage = null;
        }

        @Override
        public void onPos(SourceContent sourceContent, boolean b) {
            currentImageset = new Bitmap[sourceContent.getImages().size()];

            if(sourceContent.getImages().size() > 0){
                UrlImageViewHelper.setUrlDrawable(preview_image, sourceContent.getImages().get(0), new UrlImageViewCallback() {
                    @Override
                    public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
                        if (loadedBitmap != null) {
                            currentImage = loadedBitmap;
                            currentImageset[0] = loadedBitmap;
                        }
                    }
                });
            }

            if(sourceContent.getTitle().equals("")){
                sourceContent.setTitle(("Contents is empty"));
            }
            if(sourceContent.getDescription().equals("")){
                sourceContent.setDescription("Contents is empty");
            }

            text.setText(sourceContent.getUrl());
            preview_description.setText(sourceContent.getDescription());

        }
    };

    public void onResume()
    {
        super.onResume();


    }
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
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
                "Main Page", // TODO: Define a title for the content shown.
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

    @Override
    public void onNewIntent(Intent newIntent){

        ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> info = am.getRunningTasks(1);
        ComponentName topActivity = info.get(0).topActivity;
        String name = topActivity.getClassName();

        album_contents = newIntent.getStringExtra("contents");
        text.setText(newIntent.getStringExtra("contents"));


        AlbumGet = true;
        if(name.equals("com.example.pening.qb.AlbumActivity")){
            Toast.makeText(getApplicationContext(), name , Toast.LENGTH_SHORT).show();
            Make_QR(album_contents);
            textCrawler.makePreview(callback, album_contents);
        }


    }
    private void Make_QR(String contents){

        MultiFormatWriter gen = new MultiFormatWriter();

        try {
            bytemap = gen.encode(contents, BarcodeFormat.QR_CODE, width, height);
            //Button pixel[] = new Button[250];

            bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
            boolean flag = false;
            for (int i = 0 ; i < width; ++i) {
                for (int j = 0; j < height; ++j) {
                    if(bytemap.get(j,i)){
                        bitmap.setPixel(j,i,Color.BLACK);
                        size++;
                        flag = true;
                    }
                    else{
                        bitmap.setPixel(j,i,Color.WHITE);
                        if(min_size >= size && flag){
                            min_size = size;
                            size = 0;
                            flag = false;
                        }
                    }
                }
            }
            view.setImageBitmap(bitmap);
            view.invalidate();

            text.setText(contents);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
    public void callChrome(String url){
        //크롬브라우저 패키지명
        String packageName = "com.android.chrome";

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setPackage(packageName); //바로 이 부분
        i.setData(Uri.parse(url));

        //크롬브라우저가 설치되어있으면 호출, 없으면 마켓으로 설치유도
        List<ResolveInfo> activitiesList = getPackageManager().queryIntentActivities(i, -1);
        if(activitiesList.size() > 0) {
            startActivity(i);
        }
        else {
            Intent playStoreIntent = new Intent(Intent.ACTION_VIEW);
            playStoreIntent.setData(Uri.parse("market://details?id="+packageName));
            playStoreIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(playStoreIntent);
        }
    }


    private void QR_insertToDatabase(String id, String contents, int square_count, int score, int time){

        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {

                try{
                    String id = (String)params[0];
                    String contents = (String)params[1];
                    String square_count = (String)params[2];
                    String score = (String)params[3];
                    String time = (String)params[4];

                    String link="http://220.67.128.58/my_album_insert.php";
                    String data  = URLEncoder.encode("Id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
                    data += "&" + URLEncoder.encode("CONTENTS", "UTF-8") + "=" + URLEncoder.encode(contents, "UTF-8");
                    data += "&" + URLEncoder.encode("SQUARE_COUNT", "UTF-8") + "=" + URLEncoder.encode(square_count, "UTF-8");
                    data += "&" + URLEncoder.encode("SCORE", "UTF-8") + "=" + URLEncoder.encode(score, "UTF-8");
                    data += "&" + URLEncoder.encode("TIME", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8");
                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write( data );
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                }
                catch(Exception e){
                    return new String("Exception: " + e.getMessage());
                }

            }
        }

        InsertData task = new InsertData();
        task.execute(id, contents, String.valueOf(square_count), String.valueOf(score), String.valueOf(time));
    }
    private void USER_insertToDatabase(String id){

        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {

                try{
                    String id = (String)params[0];

                    String link="http://220.67.128.58/user_insert.php";
                    String data  = URLEncoder.encode("Id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write( data );
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                }
                catch(Exception e){
                    return new String("Exception: " + e.getMessage());
                }

            }
        }

        InsertData task = new InsertData();
        task.execute(id);
    }
    public String GetId(){
        return this.id;
    }

}

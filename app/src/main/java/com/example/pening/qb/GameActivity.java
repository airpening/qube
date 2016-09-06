package com.example.pening.qb;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.plattysoft.leonids.ParticleSystem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;


public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String MODE[] = {"Break", "Memorial", "etc"};
    private static final int COLOR[] = {R.drawable.red, R.drawable.green, R.drawable.blue};
    private static final int COLOR_NUMBER[] = {Color.RED, Color.GREEN,Color.BLUE};
    private static final int BREAK = 0;
    private static final int MEMORIAL = 1;
    private static final int ETC = 2;


    private ImageView game_view;
    private ImageButton button[];
    private Game game;
    byte[] arr;
    private Chronometer stopwatch;
    private AlertDialog.Builder menu;
    private ArrayList<Point> ButtonCenter;
    private ArrayList<Point> Center;

    private ArrayList<Rect> SquareInfo;
    private ArrayList<Rect> tempSquare;

    private ArrayList<ImageView> bit;
    private ArrayList<ImageView> temp_bit;

    private int min_size;
    private int button_size;
    private FrameLayout game_layout;
    private FrameLayout.LayoutParams params;
    private FrameLayout.LayoutParams temp_params;

    private TextView score;
    private int set_score;
    private int Square_number;
    private int Color_number;
    private boolean isClick;

    private ParticleSystem particle;
    private ImageView test;
    private int temp_scale;

    private String id;
    private String contents;
    private int bit_count;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        arr = getIntent().getByteArrayExtra("image");

        ButtonCenter = getIntent().getParcelableArrayListExtra("Button");
        id = getIntent().getStringExtra("id");
        contents = getIntent().getStringExtra("contents");
        SquareInfo = getIntent().getParcelableArrayListExtra("Square");
        tempSquare = (ArrayList<Rect>)SquareInfo.clone();

        Center = getIntent().getParcelableArrayListExtra("Center");

        min_size = getIntent().getIntExtra("min_size", 0);
        button_size = getIntent().getIntExtra("button_size", 0);
        game_view = (ImageView) findViewById(R.id.gameview);
        stopwatch = (Chronometer) findViewById(R.id.Timer);

        game_layout = (FrameLayout) findViewById(R.id.gamelayout);
        score = (TextView) findViewById(R.id.Score);

        isClick = false;
        button = new ImageButton[3];

        int scale = 0;
        for (int i = 0; i < 3; i++) {
            button[i] = new ImageButton(this);
            button[i].setBackgroundResource(COLOR[i]);
            scale = DPFromPixel(button_size);
            params = new FrameLayout.LayoutParams(scale, scale);
            params.leftMargin = DPFromPixel(ButtonCenter.get(i).x - (button_size / 2));
            params.topMargin = DPFromPixel(ButtonCenter.get(i).y - (button_size / 2));
            game_layout.addView(button[i], params);
            button[i].setOnClickListener(this);
            button[i].setId(COLOR[i]);
        }

        bit = new ArrayList<>();
        temp_scale = DPFromPixel(min_size);
        bit_count = SquareInfo.size();
        for(int i = 0;i<SquareInfo.size();i++){
            test = new ImageView(this);
            test.setBackgroundColor(Color.BLACK);
            bit.add(test);
            temp_params = new FrameLayout.LayoutParams(temp_scale, temp_scale);
            temp_params.leftMargin = DPFromPixel(Center.get(i).x - min_size/2);
            temp_params.topMargin = DPFromPixel(Center.get(i).y - min_size/2);
            game_layout.addView(bit.get(i),temp_params);
        }

        temp_bit = (ArrayList<ImageView>)bit.clone();

        //game_layout.setRotation(225);

        menu = new AlertDialog.Builder(this);
        menu.setTitle("모드 선택").setPositiveButton("선택", null).setNegativeButton("취소", null).setItems(MODE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Game mode : " + MODE[which], Toast.LENGTH_SHORT).show();
                switch (which) {
                    case BREAK:
                        game = new Break();
                        break;
                    case MEMORIAL:
                        break;
                }
            }
        });
        menu.show();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private int DPFromPixel(int pixel) {
        float scale = this.getResources().getDisplayMetrics().density;
        return (int) (pixel * scale);
    }

    public void OnClickStart(View v){

        this.Change_Color();
        stopwatch.setBase(SystemClock.elapsedRealtime());
        stopwatch.start();


        Toast.makeText(getApplicationContext(), "Game Start", Toast.LENGTH_SHORT).show();

    }
    public void OnClickPause(View v){
        stopwatch.stop();
        this.Pause();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Game Page", // TODO: Define a title for the content shown.
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
                "Game Page", // TODO: Define a title for the content shown.
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
    public void onClick(View v) {
        Boolean combo = false;

        switch (v.getId()){
            case R.drawable.red:
                if(game.isRecentColor(0)){
                    combo = true;
                }
                else{
                    combo = false;
                    Toast.makeText(getApplicationContext(), "MISS", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.drawable.green:
                if(game.isRecentColor(1)){
                    combo = true;
                }
                else{
                    combo = false;
                    Toast.makeText(getApplicationContext(), "MISS", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.drawable.blue:
                if(game.isRecentColor(2)){
                    combo = true;
                }
                else{
                    combo = false;
                    Toast.makeText(getApplicationContext(), "MISS", Toast.LENGTH_SHORT).show();
                }

                break;
        }

        particle = new ParticleSystem(this, 40,R.drawable.star_pink,500);
        particle.setScaleRange(0.7f, 0.7f);
        particle.setSpeedRange(0.1f, 0.1f);
        particle.setRotationSpeedRange(90, 180);
        particle.setAcceleration(0.00013f, 90);
        particle.setFadeOut(200, new AccelerateInterpolator());
        particle.emit(bit.get(Square_number), 100, 500);


        game.Delete_Square(SquareInfo, Square_number);
        bit.get(Square_number).setBackgroundColor(Color.WHITE);
        game_layout.removeView(bit.get(Square_number));
        bit.remove(Square_number);

        Change_Color();
        game.isCombo(combo);
        set_score = game.Score();
        score.setText(String.valueOf(set_score));
    }
    void Change_Color(){

        if(SquareInfo.size() == 0){
            stopwatch.stop();
            QR_Insert_Update_Database(id,contents,String.valueOf(bit_count),score.getText().toString(),stopwatch.getText().toString());
            this.Pause();
            return;
        }
        Square_number =game.Suffle(SquareInfo.size());
        Color_number = game.Suffle(3);

        bit.get(Square_number).setBackgroundColor(COLOR_NUMBER[Color_number]);
        game.setRecentColor(Color_number);

    }
    private void QR_Insert_Update_Database(String id, String contents, String square_count, String score, String time){

        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(GameActivity.this, "Please Wait", null, true, true);
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

    public void Pause() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Game Over");

        alert.setPositiveButton("ReStart", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                for(int i = 0;i<SquareInfo.size();i++){
                    game_layout.removeView(bit.get(i));
                }

                score.setText(String.valueOf(0));
                SquareInfo = (ArrayList<Rect>)tempSquare.clone();
                bit = (ArrayList<ImageView>)temp_bit.clone();

                for(int i = 0;i<SquareInfo.size();i++){
                    bit.get(i).setBackgroundColor(Color.BLACK);
                    temp_params = new FrameLayout.LayoutParams(temp_scale, temp_scale);
                    temp_params.leftMargin = DPFromPixel(Center.get(i).x - min_size/2);
                    temp_params.topMargin = DPFromPixel(Center.get(i).y - min_size/2);
                    game_layout.addView(bit.get(i),temp_params);
                }
                stopwatch.setBase(SystemClock.elapsedRealtime());
                stopwatch.stop();
            }
        });
        alert.setNegativeButton("to Main", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                Intent intent = new Intent(GameActivity.this, MainActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        alert.show();


    }

}

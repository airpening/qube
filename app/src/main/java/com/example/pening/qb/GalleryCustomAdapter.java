package com.example.pening.qb;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by pening on 2016-06-13.
 */
public class GalleryCustomAdapter extends BaseAdapter {

    private ArrayList<Data> qr_list;
    public GalleryCustomAdapter(){
        qr_list = new ArrayList<Data>();
    }
    String contents;

    @Override
    public int getCount() {
        return qr_list.size();
    }

    @Override
    public Object getItem(int position) {
        return qr_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // 리스트가 길어지면서 현재 화면에 보이지 않는 아이템은 converView가 null인 상태로 들어 옴
        if (convertView == null) {
            // view가 null일 경우 커스텀 레이아웃을 얻어 옴
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gallery_listview, parent, false);

            // TextView에 현재 position의 문자열 추가
            final TextView gallery_contents = (TextView) convertView.findViewById(R.id.gallery_contents);
            TextView bitcount = (TextView) convertView.findViewById(R.id.gallery_bitcount);

            TextView score = (TextView) convertView.findViewById(R.id.gallery_bestscore);

            TextView time = (TextView) convertView.findViewById(R.id.gallery_besttime);
            TextView score_id = (TextView) convertView.findViewById(R.id.gallery_scoreid);
            TextView time_id = (TextView) convertView.findViewById(R.id.gallery_timeid);
            TextView use_count = (TextView) convertView.findViewById(R.id.gallery_usecount);


            gallery_contents.setText(String.valueOf(qr_list.get(position).getContents()));

            bitcount.setText(String.valueOf(qr_list.get(position).getBitcount()));

            score.setText(String.valueOf(qr_list.get(position).getScore()));
            time.setText(qr_list.get(position).getTime());
            score_id.setText(qr_list.get(position).getScore_id());
            time_id.setText(qr_list.get(position).getTime_id());
            use_count.setText(String.valueOf(qr_list.get(position).getUse_count()));

            // 버튼을 터치 했을 때 이벤트 발생
            Button challenge = (Button) convertView.findViewById(R.id.gallery_challenge);

            challenge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MainActivity.class);
                    contents = gallery_contents.getText().toString();
                    intent.putExtra("contents", contents);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    context.startActivity(intent);
                }
            });

        }
        return convertView;
    }
    public void add(Data data) {
        qr_list.add(data);
    }
    public void remove(int _position) {
        qr_list.remove(_position);
    }

}

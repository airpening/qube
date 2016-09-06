package com.example.pening.qb;


import android.graphics.*;
import android.graphics.Point;
import android.widget.FrameLayout;

import com.google.zxing.common.BitMatrix;


import java.util.ArrayList;


/**
 * Created by pening on 2016-05-23.
 */


public class Scan {

    static final int width = 330;
    static final int height = 330;
    private int Button_size = 0;

    Scan(){

    }
    public int GetButton_size(){
        if(Button_size >= 0) {
            return Button_size;
        }
        else{
            return -1;
        }
    }

    public void ButtonFind(BitMatrix map, int min_size, ArrayList<android.graphics.Point> ButtonCenter,ArrayList<android.graphics.Point> center ){

        int x = 0;
        int y = 0;
        for(int i = 0;i<center.size();i++){
            x = center.get(i).x;
            y = center.get(i).y;
            if(map.get(x, y)){
                if (map.get(x - min_size, y - min_size) && map.get(x - min_size, y) && map.get(x, y - min_size)
                 && map.get(x + min_size, y - min_size) && map.get(x + min_size, y) && map.get(x + min_size, y + min_size)
                 && map.get(x - min_size, y + min_size) && map.get(x, y + min_size)) {
                    ButtonCenter.add(new Point(x, y));

                }
            }
        }
        if(ButtonCenter.size() >= 4){
            this.Erase_ohter_button(min_size, ButtonCenter, center);
        }
    }
    public void EraseButtonSide(int min_size, ArrayList<android.graphics.Point> ButtonCenter,ArrayList<android.graphics.Point> center){
        int x = 0;
        int y = 0;
        boolean flag = false;
        int center_x;
        int center_y;
        int erase = min_size * 4;
        this.Button_size = min_size * 7;

        for(int i = 0;i<ButtonCenter.size();i++) {
            x = ButtonCenter.get(i).x;
            y = ButtonCenter.get(i).y;
            for (int j = 0; j < center.size(); j++) {
                if (flag) {
                    j--;
                    flag = false;
                }
                center_x = center.get(j).x;
                center_y = center.get(j).y;
                if (((x - erase <= center_x) && (center_x <= x + erase)) && ((y - erase <= center_y) && (center_y <= y + erase))) {
                    center.remove(new Point(center_x, center_y));
                    flag = true;
                }
            }
        }
    }
    public void Erase_ohter_button(int min_size, ArrayList<android.graphics.Point> ButtonCenter, ArrayList<android.graphics.Point> center){
        int x = 0;
        int y = 0;
        int rocate = 0;
        boolean flag = false;
        for(int i = 0;i<ButtonCenter.size();i++){
            x = ButtonCenter.get(i).x;
            y = ButtonCenter.get(i).y;
            if((x == ButtonCenter.get(i+1).x) || (y == ButtonCenter.get(i+1).y) || (x == ButtonCenter.get(i+1).y) || (y ==ButtonCenter.get(i+1).x)){
                center.remove(new Point(x - min_size, y - min_size));
                center.remove(new Point(x-min_size,y));
                center.remove(new Point(x,y-min_size));
                center.remove(new Point(x+min_size,y-min_size));
                center.remove(new Point(x+min_size,y));
                center.remove(new Point(x+min_size,y+min_size));
                center.remove(new Point(x-min_size,y+min_size));
                center.remove(new Point(x,y+min_size));
                center.remove(new Point(x, y));
                continue;
            }
            else{
                rocate = i+1;
                flag = true;
                break;
            }
        }
        if(flag){
            ButtonCenter.remove(rocate);
        }
    }
    public void Make_Square(int min_size, ArrayList<Rect> SquareInfo,  ArrayList<android.graphics.Point> center ){
        int left;
        int top;
        int right;
        int bottom;
        int x,y;

        for(int i = 0;i<center.size();i++){
            x = center.get(i).x;
            y = center.get(i).y;

            left = x-(min_size/2);
            top = y+(min_size/2);
            right = x+(min_size/2);
            bottom = y-(min_size/2);
            SquareInfo.add(new Rect(left,top,right,bottom));
        }
    }
    public void Make_Button(ArrayList<android.graphics.Point> ButtonCenter){

    }



}

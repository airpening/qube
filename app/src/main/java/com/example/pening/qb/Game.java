package com.example.pening.qb;

import android.graphics.Bitmap;
import android.graphics.Rect;

import java.util.ArrayList;

/**
 * Created by pening on 2016-06-07.
 */
abstract public class  Game {

    abstract int Suffle(int number);
    abstract void Timer();
    abstract int Score();
    abstract void isCombo(Boolean Combo);
    abstract void Delete_Square(ArrayList<Rect> SquareInfo,int Square_number);
    abstract Boolean isRecentColor(int Colornumber);
    abstract void setRecentColor(int Colornumber);
}

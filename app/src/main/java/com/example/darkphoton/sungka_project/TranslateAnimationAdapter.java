package com.example.darkphoton.sungka_project;

import android.view.View;
import android.view.animation.Animation;

/**
 * Created by darkphoton on 25/10/15.
 */
public class TranslateAnimationAdapter implements Animation.AnimationListener {
    private View _v;
    private float _x;
    private float _y;

    public TranslateAnimationAdapter(View v, float x, float y){
        _v = v;
        _x = x;
        _y = y;
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        _v.setX(_x);
        _v.setY(_y);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}

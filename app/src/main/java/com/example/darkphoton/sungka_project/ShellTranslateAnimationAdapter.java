package com.example.darkphoton.sungka_project;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.concurrent.CountDownLatch;

import game.Cup;

/**
 * Created by darkphoton on 25/10/15.
 */
public class ShellTranslateAnimationAdapter implements Animation.AnimationListener {
    private int[] _latch;
    private CupButton _btn;
    private View _v;
    private float _x;
    private float _y;

    public ShellTranslateAnimationAdapter(int[] latch, CupButton btn, View v, float x, float y){
        _latch = latch;
        _btn = btn;
        _v = v;
        _x = x;
        _y = y;
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
        animation.setDuration(1);
        _v.startAnimation(animation);

        _v.setX(_x);
        _v.setY(_y);

        _btn.updateText();
        --_latch[0];
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}

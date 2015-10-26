package com.example.darkphoton.sungka_project;

import android.content.Intent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import java.util.concurrent.CountDownLatch;

/**
 * Created by darkphoton on 25/10/15.
 */
public class ShellTranslation implements Animation.AnimationListener {
    private CupButton _btn;
    private View _view;
    private float[] _coords;
    private float[] _relativeCoords;
    private int _duration;

    public ShellTranslation(CupButton btn, View view, float[] coords, int duration){
        _btn = btn;
        _view = view;
        _coords = coords;
        _relativeCoords = new float[]{
                coords[0] - _view.getX(),
                coords[1] - _view.getY()};
        _duration = duration;
    }

    public void startAnimation() {
        TranslateAnimation anim = new TranslateAnimation(0, _relativeCoords[0], 0, _relativeCoords[1]);
        anim.setInterpolator(new AccelerateInterpolator(1.0f));
        anim.setDuration(_duration);
        anim.setAnimationListener(this);
        _view.startAnimation(anim);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
        animation.setDuration(1);
        _view.startAnimation(animation);

        _view.setX(_coords[0]);
        _view.setY(_coords[1]);

        _btn.updateText();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}

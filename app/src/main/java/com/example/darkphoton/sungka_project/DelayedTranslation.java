package com.example.darkphoton.sungka_project;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.TranslateAnimation;

/**
 * Created by darkphoton on 25/10/15.
 */
public class DelayedTranslation implements Runnable {
    private View _view;
    private float[] _coord;
    private int _duration;

    public DelayedTranslation(View view, float[] coord, int duration){
        _view = view;
        _coord = coord;
        _duration = duration;
    }

    @Override
    public void run() {
        TranslateAnimation anim = new TranslateAnimation(0, _coord[0] - _view.getX(), 0, _coord[1] - _view.getY());
        anim.setInterpolator(new AccelerateInterpolator(1.0f));
        anim.setDuration(_duration);
        anim.setAnimationListener(new TranslateAnimationAdapter(_view, _coord[0], _coord[1]));
        _view.startAnimation(anim);
    }
}

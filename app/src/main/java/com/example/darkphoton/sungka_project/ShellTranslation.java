package com.example.darkphoton.sungka_project;

import android.content.Intent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.TranslateAnimation;

import java.util.concurrent.CountDownLatch;

/**
 * Created by darkphoton on 25/10/15.
 */
public class ShellTranslation implements Runnable {
    private int[] _latch;
    private CupButton _btn;
    private View _view;
    private float[] _coord;
    private int _duration;

    public ShellTranslation(int[] latch, CupButton btn, View view, float[] coord, int duration){
        _latch = latch;
        _btn = btn;
        _view = view;
        _coord = coord;
        _duration = duration;
    }

    @Override
    public void run() {
        TranslateAnimation anim = new TranslateAnimation(0, _coord[0] - _view.getX(), 0, _coord[1] - _view.getY());
        anim.setInterpolator(new AccelerateInterpolator(1.0f));
        anim.setDuration(_duration);
        anim.setAnimationListener(new ShellTranslateAnimationAdapter(_latch, _btn, _view, _coord[0], _coord[1]));
        _view.startAnimation(anim);
    }
}

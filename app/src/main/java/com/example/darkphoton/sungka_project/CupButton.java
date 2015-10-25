package com.example.darkphoton.sungka_project;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import game.Cup;

public class CupButton extends Button {
    private PlayerType _player_type;
    private CupType _cup_type;

    private FrameLayout _layoutMaster;

    private ArrayList<View> _shells = new ArrayList<View>();
    private Cup _cup;
    private TextView _text;
    private CupMargins _sizes;
    private int _id;

    public CupButton(Context context, Cup cup, PlayerType pType, CupType cType, CupMargins sizes, int id) {
        super(context);

        _cup = cup;
        _player_type = pType;
        _cup_type = cType;
        _sizes = sizes;
        _id = id;

        _text = new TextView(context);
        _text.setText("" + _cup.getCount());
        _text.setTextSize(30 * sizes.scale);

        if(_cup_type == CupType.PLAYER){
            if (_player_type == PlayerType.A) {
                _text.setTextColor(Color.parseColor("#FFFFFF"));
                setBackgroundResource(R.drawable.player_bigcup);
            }
            else if (_player_type == PlayerType.B) {
                _text.setTextColor(Color.parseColor("#000000"));
                setBackgroundResource(R.drawable.opponent_bigcup);
            }
        }
        else if (_cup_type == CupType.SHELL){
            if (_player_type == PlayerType.A) {
                _text.setTextColor(Color.parseColor("#FFFFFF"));
                setBackgroundResource(R.drawable.opponent_smallcup);
            }
            else if (_player_type == PlayerType.B) {
                _text.setTextColor(Color.parseColor("#000000"));
                setBackgroundResource(R.drawable.player_smallcup);
            }
        }

        for (int i = 0; i < _cup.getCount(); i++) {
            ImageView shell = new ImageView(context);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);

            shell.setLayoutParams(params);

            shell.setImageDrawable(GameActivity.shells[i % 4]);
            shell.setScaleType(ImageView.ScaleType.MATRIX);
            shell.setPivotX(shell.getWidth() / 2);
            shell.setPivotY(shell.getHeight() / 2);

            _shells.add(shell);
        }
    }

    public void addToLayout(GridLayout layoutBase, int cupColumn, int cupRow){
        _layoutMaster = (FrameLayout)layoutBase.getParent();

        GridLayout.LayoutParams paramsButton = new GridLayout.LayoutParams();
        GridLayout.LayoutParams paramsText = new GridLayout.LayoutParams();

        paramsButton.columnSpec = GridLayout.spec(cupColumn);
        paramsButton.rowSpec = GridLayout.spec(cupRow);
        paramsButton.leftMargin = _sizes.spaceSmall;
        paramsButton.rightMargin = _sizes.spaceSmall;

        paramsText.width = GridLayout.LayoutParams.WRAP_CONTENT;
        paramsText.height = GridLayout.LayoutParams.WRAP_CONTENT;

        if(_cup_type == CupType.PLAYER){
            paramsButton.width = _sizes.store;
            paramsButton.height = _sizes.store;
            if (_player_type == PlayerType.A){
                paramsButton.rightMargin = _sizes.spaceSmall;
                paramsButton.leftMargin = _sizes.spaceLeft;
            }
            else if (_player_type == PlayerType.B){
                paramsButton.rightMargin = _sizes.spaceLeft;
                paramsButton.leftMargin = _sizes.spaceSmall;
            }
            paramsButton.topMargin = _sizes.spaceStoreTop;
            paramsButton.bottomMargin = _sizes.spaceStoreTop;
        }
        else if (_cup_type == CupType.SHELL){
            paramsButton.width = _sizes.cup;
            paramsButton.height = _sizes.cup;
        }

        paramsText.setGravity(Gravity.CENTER);
        setLayoutParams(paramsButton);
        layoutBase.addView(this);
        _text.setLayoutParams(paramsText);
        _layoutMaster.addView(_text);

        for (int i = 0; i < _shells.size(); i++) {
            _layoutMaster.addView(_shells.get(i));
        }

        layoutBase.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                float offsetX = ((GridLayout)getParent()).getX();
                float offsetY = ((GridLayout)getParent()).getY();

                _text.setX(offsetX + getX() + (getWidth() / 2) - (_text.getWidth() / 2));

                float text_y = offsetY + getY();
                if (_player_type == PlayerType.A)
                    _text.setY(text_y + getHeight());
                else if (_player_type == PlayerType.B)
                    _text.setY(text_y - _text.getHeight());

                Random r = new Random();
                for (int i = 0; i < _shells.size(); i++) {
                    float[] pos = randomPositionInCup(r, _shells.get(i));

                    _shells.get(i).setX(pos[0]);
                    _shells.get(i).setY(pos[1]);
                }
            }
        });
    }

    public float[] randomPositionInCup(Random r, View shell){
        float[] pos = new float[2];

        float offsetX = ((GridLayout)getParent()).getX();
        float offsetY = ((GridLayout)getParent()).getY();

        float angle = (float)r.nextDouble() * (float)Math.PI * 2;
        int radius = r.nextInt(getWidth()/3);

        pos[0] = offsetX + ((float)Math.cos(angle) * radius) + getX() + (getWidth() / 2) - (shell.getWidth() / 2);
        pos[1] = offsetY + ((float)Math.sin(angle) * radius) + getY() + (getHeight() / 2) - (shell.getHeight() / 2);

        return pos;
    }

    public ArrayList<View> getShells(){
        _text.setText("" + _cup.getCount());
        return _shells;
    }

    public void updateText(){
        _text.setText("" + _cup.getCount());
    }

    public void addShell(ImageView image){
        _shells.add(image);
    }
}

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

import game.Cup;

public class CupButton extends Button {
    private PlayerType _player_type;
    private CupType _cup_type;

    private FrameLayout _layoutMaster;

    private ArrayList<ImageView> _shells = new ArrayList<ImageView>();
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
        _text.setText(String.valueOf(_id));
        _text.setTextSize(30 * sizes.scale);

        for (int i = 0; i < _cup.getCount(); i++) {
            ImageView shell = new ImageView(context);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);

            shell.setLayoutParams(params);
            shell.setScaleType(ImageView.ScaleType.MATRIX);
            shell.setImageResource(R.drawable.shell1);

            _shells.add(shell);
        }

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
                _text.setX(getX() + (getWidth() / 2) - (_text.getWidth() / 2));

                float text_y = getY() + ((GridLayout)getParent()).getY();

                if (_player_type == PlayerType.A)
                    _text.setY(text_y + getHeight());
                else if (_player_type == PlayerType.B)
                    _text.setY(text_y - _text.getHeight());
            }
        });
    }
}

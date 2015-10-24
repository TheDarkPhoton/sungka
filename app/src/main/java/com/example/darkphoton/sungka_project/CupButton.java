package com.example.darkphoton.sungka_project;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import game.HandOfShells;

public class CupButton extends Button {
    private GameActivity.PlayerType _player_type;
    private GameActivity.CupType _cup_type;

    private TextView _text;
    CupMargins _sizes;
    private int _id;

    public CupButton(Context context, GameActivity.PlayerType pType, GameActivity.CupType cType, CupMargins sizes, int id) {
        super(context);

        _player_type = pType;
        _cup_type = cType;
        _sizes = sizes;
        _id = id;

        if(_cup_type == GameActivity.CupType.Player){
            if (_player_type == GameActivity.PlayerType.A)
                setBackgroundResource(R.drawable.player_bigcup);
            else if (_player_type == GameActivity.PlayerType.B)
                setBackgroundResource(R.drawable.opponent_bigcup);
        }
        else if (_cup_type == GameActivity.CupType.Shell){
            if (_player_type == GameActivity.PlayerType.A)
                setBackgroundResource(R.drawable.opponent_smallcup);
            else if (_player_type == GameActivity.PlayerType.B)
                setBackgroundResource(R.drawable.player_smallcup);
        }

        _text = new TextView(context);
        _text.setTextColor(Color.parseColor("#FFFFFF"));
        _text.setText(String.valueOf(_id));
        _text.setTextSize(30 * sizes.scale);
    }

    public void addToLayout(GridLayout layoutBase, int cupColumn, int cupRow, int textRow){
        GridLayout.LayoutParams paramsButton = new GridLayout.LayoutParams();
        GridLayout.LayoutParams paramsText = new GridLayout.LayoutParams();

        paramsButton.columnSpec = GridLayout.spec(cupColumn);
        paramsButton.rowSpec = GridLayout.spec(cupRow);
        paramsText.width = GridLayout.LayoutParams.WRAP_CONTENT;
        paramsText.height = GridLayout.LayoutParams.WRAP_CONTENT;
        paramsText.columnSpec = GridLayout.spec(cupColumn);
        paramsText.rowSpec = GridLayout.spec(textRow);
        paramsButton.leftMargin = _sizes.spaceSmall;
        paramsButton.rightMargin = _sizes.spaceSmall;

        if(_cup_type == GameActivity.CupType.Player){
            paramsButton.width = _sizes.store;
            paramsButton.height = _sizes.store;
            if (_player_type == GameActivity.PlayerType.A){
                paramsButton.rightMargin = _sizes.spaceLeft;
                paramsButton.leftMargin = _sizes.spaceSmall;
                paramsText.rightMargin = _sizes.spaceSmall;
            }
            else if (_player_type == GameActivity.PlayerType.B){
                paramsButton.rightMargin = _sizes.spaceSmall;
                paramsButton.leftMargin = _sizes.spaceLeft;
                paramsText.leftMargin = _sizes.spaceSmall;
            }
            paramsButton.topMargin = _sizes.spaceStoreTop;
            paramsButton.bottomMargin = _sizes.spaceStoreTop;
            paramsText.setGravity(Gravity.CENTER);
        }
        else if (_cup_type == GameActivity.CupType.Shell){
            paramsButton.width = _sizes.cup;
            paramsButton.height = _sizes.cup;
            if (_player_type == GameActivity.PlayerType.A){
                paramsText.topMargin = _sizes.spaceTop;
            }
            else if (_player_type == GameActivity.PlayerType.B){
                paramsText.bottomMargin = _sizes.spaceTop;
            }
            paramsText.setGravity(Gravity.CENTER);
        }

        setLayoutParams(paramsButton);
        layoutBase.addView(this);
        _text.setLayoutParams(paramsText);
        layoutBase.addView(_text);
    }

    public void handleButton(){
        this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.i(TAG, "Hole " + _id + " was pressed.");
//
//                if (game.isValidMove(_id)) {
//                    final HandOfShells hand = game.fetchHand(_id);
//
//                    animateCupTransfer(hand, cupShells.get(_id).size());
//                } else {
//                    Log.i(TAG, "Invalid move");
//                }
            }
        });
    }
}

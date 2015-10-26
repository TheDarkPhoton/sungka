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
    public static final int PLAYER_A = 0;
    public static final int PLAYER_B = 1;
    public static final int STORE = 0;
    public static final int CUP = 1;

    public static CupMargins sizes;
    public static class CupMargins {
        public final float scale;
        public final int store, cup, spaceTop, spaceLeft, spaceSmall, spaceStoreTop;

        CupMargins(int screenWidth, int screenHeight){
            store = (int) (screenWidth * 0.156);                                                        //Store cups are 15.6% of the screen width
            cup = (int) (screenWidth * 0.078);                                                          //Small cups are 7.8% of the screen width
            scale = cup / 199.0f;                                                                       // A scale factor for text sizes

            //Calculates spaces between cups
            spaceSmall = (int) (screenWidth * 0.005);
            spaceStoreTop = (int) (screenHeight * 0.05);
            spaceLeft = (screenWidth - (((store * 2) + (cup * 7) + (spaceSmall * 14)))) / 2;
            spaceTop = ((screenHeight - ((store + (cup * 2) + (spaceStoreTop * 2)))) / 2) - cup / 2;
        }
    };
    public static void generateSizes(int screenWidth, int screenHeight){
        sizes = new CupMargins(screenWidth, screenHeight);
    }

    private int _player_type;
    private int _cup_type;

    private FrameLayout _layoutMaster;

    private ArrayList<View> _shells = new ArrayList<View>();
    private Cup _cup;
    private TextView _text;
    private int _id;

    public CupButton(Context context, Cup cup, int pType, int cType, int id) {
        super(context);

        _cup = cup;
        _player_type = pType;
        _cup_type = cType;
        _id = id;

        _text = new TextView(context);
        _text.setText("" + _cup.getCount());
        _text.setTextSize(30 * sizes.scale);
        _text.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                updateTextLocation();
            }
        });

        if(_cup_type == STORE){
            if (_player_type == PLAYER_A) {
                _text.setTextColor(Color.parseColor("#FFFFFF"));
                setBackgroundResource(R.drawable.player_bigcup);
            }
            else if (_player_type == PLAYER_B) {
                _text.setTextColor(Color.parseColor("#000000"));
                setBackgroundResource(R.drawable.opponent_bigcup);
            }
        }
        else if (_cup_type == CUP){
            if (_player_type == PLAYER_A) {
                _text.setTextColor(Color.parseColor("#FFFFFF"));
                setBackgroundResource(R.drawable.opponent_smallcup);
            }
            else if (_player_type == PLAYER_B) {
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
        paramsButton.leftMargin = sizes.spaceSmall;
        paramsButton.rightMargin = sizes.spaceSmall;

        paramsText.width = GridLayout.LayoutParams.WRAP_CONTENT;
        paramsText.height = GridLayout.LayoutParams.WRAP_CONTENT;

        if(_cup_type == STORE){
            paramsButton.width = sizes.store;
            paramsButton.height = sizes.store;
            if (_player_type == PLAYER_A){
                paramsButton.rightMargin = sizes.spaceSmall;
                paramsButton.leftMargin = sizes.spaceLeft;
            }
            else if (_player_type == PLAYER_B){
                paramsButton.rightMargin = sizes.spaceLeft;
                paramsButton.leftMargin = sizes.spaceSmall;
            }
            paramsButton.topMargin = sizes.spaceStoreTop;
            paramsButton.bottomMargin = sizes.spaceStoreTop;
        }
        else if (_cup_type == CUP){
            paramsButton.width = sizes.cup;
            paramsButton.height = sizes.cup;
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
                updateTextLocation();
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
        updateText();

        ArrayList<View> shells = new ArrayList<>();
        while (_shells.size() > 0) {
            shells.add(_shells.remove(_shells.size() - 1));
        }

        return shells;
    }

    public void updateText(){
        _text.setText("" + _cup.getCount());
    }

    public void addShell(ImageView image){
        _shells.add(image);
    }

    public void initShellLocation(){
        Random r = new Random();
        for (int i = 0; i < _shells.size(); i++) {
            float[] pos = randomPositionInCup(r, _shells.get(i));

            _shells.get(i).setX(pos[0]);
            _shells.get(i).setY(pos[1]);
        }
    }

    private void updateTextLocation(){
        float offsetX = ((GridLayout)getParent()).getX();
        float offsetY = ((GridLayout)getParent()).getY();

        _text.setX(offsetX + getX() + (getWidth() / 2) - (_text.getWidth() / 2));

        float text_y = offsetY + getY();
        if (_player_type == PLAYER_A)
            _text.setY(text_y + getHeight());
        else if (_player_type == PLAYER_B)
            _text.setY(text_y - _text.getHeight());
    }
}

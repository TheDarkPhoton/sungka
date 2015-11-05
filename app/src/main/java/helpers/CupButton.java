package helpers;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.deathgull.sungka_project.GameActivity;
import com.example.deathgull.sungka_project.R;

import java.util.ArrayList;
import java.util.Random;

import game.board.Board;
import game.cup.Cup;
import game.player.Side;

public class CupButton extends Button implements View.OnTouchListener {
    public static final int PLAYER_A = 0;
    public static final int PLAYER_B = 1;
    public static final int STORE = 0;
    public static final int CUP = 1;

    public static CupMargins sizes;

    public static class CupMargins {
        public final float scale;
        public final int store, cup, spaceTop, spaceLeft, spaceSmall, spaceStoreTop;

        CupMargins(int screenWidth, int screenHeight) {
            store = (int) (screenWidth * 0.156);                                                        //Store cups are 15.6% of the screen width
            cup = (int) (screenWidth * 0.078);                                                          //Small cups are 7.8% of the screen width
            scale = cup / 199.0f;                                                                       // A scale factor for text sizes

            //Calculates spaces between cups
            spaceSmall = (int) (screenWidth * 0.005);
            spaceStoreTop = (int) (screenHeight * 0.05);
            spaceLeft = (screenWidth - (((store * 2) + (cup * 7) + (spaceSmall * 16)))) / 2;
            spaceTop = ((screenHeight - ((store + (cup * 2) + (spaceStoreTop * 2)))) / 2) - cup / 2;
        }
    }

    public static void generateSizes(int screenWidth, int screenHeight) {
        sizes = new CupMargins(screenWidth, screenHeight);
    }

    private int _player_type;
    private int _cup_type;

    private FrameLayout _layoutMaster;

    private ArrayList<View> _shells = new ArrayList<>();
    private TextView _text;
    private int _cup_index;
    private Cup _cup;

    /**
     * Initialises default variables of the cup button.
     * @param context The screen it belongs to.
     * @param board Reference to the board.
     * @param cup_index The cup it represents.
     * @param pType The player it represents.
     * @param cType Type of the button.
     */
    public CupButton(Context context, Board board, int cup_index, int pType, int cType) {
        super(context);

        _cup_index = cup_index;
        _cup = board.getCup(cup_index);

        _player_type = pType;
        _cup_type = cType;

        _text = new TextView(context);
        _text.setVisibility(View.GONE);
        _text.setTextSize(30 * sizes.scale);
        _text.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                updateTextLocation();
            }
        });

        if (_cup_type == STORE) {
            if (_player_type == PLAYER_A) {
                _text.setTextColor(Color.parseColor("#FFFFFF"));
                setBackgroundResource(R.drawable.player_bigcup);
            } else if (_player_type == PLAYER_B) {
                _text.setTextColor(Color.parseColor("#000000"));
                setBackgroundResource(R.drawable.opponent_bigcup);
            }
        } else if (_cup_type == CUP) {
            if (_player_type == PLAYER_A) {
                _text.setTextColor(Color.parseColor("#FFFFFF"));
                setBackgroundResource(R.drawable.opponent_smallcup);
            } else if (_player_type == PLAYER_B) {
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

            shell.setImageDrawable(GameActivity.shells[GameActivity.random.nextInt(4)]);
            shell.setScaleType(ImageView.ScaleType.MATRIX);
            shell.setPivotX(shell.getWidth() / 2);
            shell.setPivotY(shell.getHeight() / 2);

            _shells.add(shell);
        }
        _text.setText("" + _cup.getCount());
    }

    /**
     * The layout to which dependencies need to be added to.
     * @param layoutBase Grid Layout in question.
     * @param cupColumn  Column of the cup position.
     * @param cupRow     Row of the cup position.
     */
    public void addToLayout(GridLayout layoutBase, int cupColumn, int cupRow) {
        _layoutMaster = (FrameLayout) layoutBase.getParent();

        GridLayout.LayoutParams paramsButton = new GridLayout.LayoutParams();
        GridLayout.LayoutParams paramsText = new GridLayout.LayoutParams();

        paramsButton.columnSpec = GridLayout.spec(cupColumn);
        paramsButton.rowSpec = GridLayout.spec(cupRow);
        paramsButton.leftMargin = sizes.spaceSmall;
        paramsButton.rightMargin = sizes.spaceSmall;

        paramsText.width = GridLayout.LayoutParams.WRAP_CONTENT;
        paramsText.height = GridLayout.LayoutParams.WRAP_CONTENT;

        if (_cup_type == STORE) {
            paramsButton.width = sizes.store;
            paramsButton.height = sizes.store;
            if (_player_type == PLAYER_A) {
                paramsButton.rightMargin = sizes.spaceLeft;
                paramsButton.leftMargin = sizes.spaceSmall;
            } else if (_player_type == PLAYER_B) {
                paramsButton.rightMargin = sizes.spaceSmall;
                paramsButton.leftMargin = sizes.spaceLeft;
            }
            paramsButton.topMargin = sizes.spaceStoreTop;
            paramsButton.bottomMargin = sizes.spaceStoreTop;
        } else if (_cup_type == CUP) {
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

        //_text.setVisibility(View.VISIBLE);
    }

    /**
     * Generates a random position within a cup.
     * @param shell The shell to be moved.
     * @return x, y position in a form of array.
     */
    public float[] randomPositionInCup(View shell) {
        float[] pos = new float[2];

        float offsetX = ((GridLayout) getParent()).getX();
        float offsetY = ((GridLayout) getParent()).getY();

        float angle = (float) GameActivity.random.nextDouble() * (float) Math.PI * 2;
        int radius = GameActivity.random.nextInt(getWidth() / 3);

        pos[0] = offsetX + ((float) Math.cos(angle) * radius) + getX() + (getWidth() / 2) - (shell.getWidth() / 2);
        pos[1] = offsetY + ((float) Math.sin(angle) * radius) + getY() + (getHeight() / 2) - (shell.getHeight() / 2);

        return pos;
    }

    /**
     * Removes all shell images from the cup and returns them in another array.
     * @return array list of images removed.
     */
    public ArrayList<View> getShells() {
        ArrayList<View> shells = new ArrayList<>();
        while (_shells.size() > 0) {
            shells.add(_shells.remove(_shells.size() - 1));
        }
        updateText();

        return shells;
    }

    public int getCupIndex(){
        return _cup_index;
    }

    /**
     * Updates the content of the buttons text.
     */
    public void updateText() {
        _text.setText("" + _cup.getCount());
    }

    /**
     * Adds a shell image to the list of shells.
     * @param image Shell to be added.
     */
    public void addShell(ImageView image) {
        _shells.add(image);
        updateText();
    }

    /**
     * Adds a list of shell images to the shells list.
     * @param shells Shells to be added.
     */
    public void addShells(ArrayList<View> shells) {
        _shells.addAll(shells);
        updateText();
    }

    /**
     * Positions shells in the right location.
     */
    public void initShellLocation() {
        Random r = new Random();
        for (int i = 0; i < _shells.size(); i++) {
            float[] pos = randomPositionInCup(_shells.get(i));

            _shells.get(i).setX(pos[0]);
            _shells.get(i).setY(pos[1]);
        }
    }

    /**
     * Positions text view in the right location.
     */
    private void updateTextLocation() {
        float offsetX = ((GridLayout) getParent()).getX();
        float offsetY = ((GridLayout) getParent()).getY();

        _text.setX(offsetX + getX() + (getWidth() / 2) - (_text.getWidth() / 2));

        float text_y = offsetY + getY();
        if (_player_type == PLAYER_A)
            _text.setY(text_y + getHeight());
        else if (_player_type == PLAYER_B)
            _text.setY(text_y - _text.getHeight());
    }

    /**
     * Handle touch events
     *
     * @param v     the view that was touched
     * @param event Type of event (touch down, touch up, etc.)
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                scaleUp();
                break;
            case MotionEvent.ACTION_UP:
                scaleDown();
                break;
        }
        return false;
    }

    /**
     * Enlargens this button by 5%
     */
    private void scaleUp() {
        changeSize(1.0f, 1.05f);
    }

    /**
     * Scales down this button to 100%
     */
    private void scaleDown() {
        changeSize(1.05f, 1.0f);
    }

    /**
     * Scales this button
     * @param fromValue the initial scale factor
     * @param toValue
     */
    private void changeSize(float fromValue, float toValue) {
        float center = this.getWidth() / 2;

        Animation scaleAnimation = new ScaleAnimation(fromValue, toValue, fromValue, toValue, center, center);
        scaleAnimation.setDuration(50);
        scaleAnimation.setFillAfter(true);
        this.startAnimation(scaleAnimation);

    }

    /**
     * Highlights this button
     */
    public void highlight() {
        changeAlpha(1.0f);
    }

    /**
     * Removes the highlight on this button
     */
    public void dehighlight() {
        changeAlpha(0.7f);
    }

    /**
     * Animates the change in the alpha value of this button
     * @param toValue float of the alpha value (eg. 1.05)
     */
    public void changeAlpha(float toValue) {
        Animation alphaAnimation = new AlphaAnimation(this.getAlpha(), toValue);
        alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);
        startAnimation(alphaAnimation);
    }

    /**
     * Rotate the text view to face a side
     * @param side of the player that has the current move
     */
    public void rotateTowards(Side side) {
        float fromRotation = (side != Side.A) ? 0 : 180;
        float rotation = (side == Side.A) ? 0 : 180;

        System.out.println("Rotation " + fromRotation + " to " + rotation);

        if (rotation == fromRotation)
            return;

        float toRotation = rotation;

        float pivotX = _text.getX() + _text.getWidth() / 2;
        float pivotY = _text.getY() + _text.getHeight() / 2;

        Animation rotateAnimation = new RotateAnimation(fromRotation, toRotation, pivotX, pivotY);
        rotateAnimation.setDuration(500);
        rotateAnimation.setFillAfter(true);
        _text.startAnimation(rotateAnimation);

    }

    /**
     * Set the text visibility to visible
     */
    public void showText() {
        _text.setVisibility(View.VISIBLE);
        //System.out.println("Show text");
    }
}
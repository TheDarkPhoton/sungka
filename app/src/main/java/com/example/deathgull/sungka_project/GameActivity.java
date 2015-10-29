package com.example.deathgull.sungka_project;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Random;

import game.board.Board;
import game.Game;
import game.board.HandOfShells;
import game.board.BoardState;
import game.player.Player;
import game.player.PlayerActionAdapter;
import helpers.CupButton;
import helpers.PauseThreadWhile;
import helpers.ShellTranslation;

public class GameActivity extends Activity {
    private static final String TAG = "GameActivity";
    public static final Random random = new Random();                                               //Object for random number generation
    public static Drawable[] shells;

    private final Context _context = this;

    private FrameLayout _layoutMaster;                                                              //Master layout
    private GridLayout _layoutBase;                                                                 //Base layout
    private int _screenWidth, _screenHeight;                                                        //Screen size

    private CupButton[] _cupButtons;
    private Game _game;
    private Board _board;

    private PlayerActionAdapter _playerActionListener = new PlayerActionAdapter() {
        @Override
        public void onMoveStart(Player player) {
            Log.i(TAG, player.get_name() + " started his turn");
        }

        @Override
        public void onMove(final Player player, final int index) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    new PauseThreadWhile<>(PlayerActionAdapter.class, "isAnimationInProgress");

                    Handler h = new Handler(_context.getMainLooper());
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, player.get_name() + " performed an action on cup["+index+"]");

                            HandOfShells hand = _board.pickUpShells(index);
                            if (hand == null)
                                return;

                            setAnimationInProgress(true);
                            ArrayList<View> images = _cupButtons[index].getShells();
                            moveShellsRec(hand, images, 300);
                        }
                    });
                }
            });
            t.start();
        }

        @Override
        public void onMoveEnd(Player player) {
            Log.i(TAG, player.get_name() + " ended his turn");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(
                _layoutMaster = new FrameLayout(this),
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT));

        shells =new Drawable[]{
                ResourcesCompat.getDrawable(getResources(), R.drawable.shell1, null),
                ResourcesCompat.getDrawable(getResources(), R.drawable.shell2, null),
                ResourcesCompat.getDrawable(getResources(), R.drawable.shell3, null),
                ResourcesCompat.getDrawable(getResources(), R.drawable.shell4, null),
        };

        _game = new Game(_playerActionListener);
        _board = _game.getBoard();

        hideNav();                                                  //Hide navigation bar and system bar
        setScreenSize();                                            //Set screen size
        initLayouts();                                              //Setup layouts
        initView();                                                 //Programmatically create and lay out elements
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) { return true; }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        _game.start();
    }

    /**
     * Hide navigation bar and system bar for all API levels
     */
    private void hideNav() {
        //Hide the navigation bar on all API levels
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        int flags =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            flags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        getWindow().getDecorView().setSystemUiVisibility(flags);
    }

    /**
     *
     * @return - parameters for master layout
     */
    private void initLayouts() {
        // Set root layout background
        _layoutMaster.setBackgroundResource(R.drawable.background);

        //Create base layout and parameters
        _layoutBase = new GridLayout(this);
        _layoutBase.setLayoutParams(new GridLayout.LayoutParams());
        _layoutBase.setColumnCount(9);
        _layoutBase.setRowCount(3);

        //Create top layout
        _layoutBase.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        //Add layouts to master
        _layoutMaster.addView(_layoutBase);

        _layoutMaster.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            boolean once = false;

            @Override
            public void onGlobalLayout() {
                _layoutBase.setY((_layoutMaster.getHeight() / 2) - (_layoutBase.getHeight() / 2));

                if (once)
                    return;
                once = true;

                for (CupButton btn : _cupButtons) {
                    btn.initShellLocation();
                }
                _board.getCurrentPlayer().moveStart();
            }
        });
    }

    /**
     * Gets the size of the device screen
     */
    private void setScreenSize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        _screenWidth = displayMetrics.widthPixels;
        _screenHeight = displayMetrics.heightPixels;

        //Calculate sizes of store cups and small cups
        CupButton.generateSizes(_screenWidth, _screenHeight);
    }

    /**
     * Initialises all cup buttons in the right location on the screen.
     */
    private void initView() {
        for (int i = 0; i < shells.length; i++) {
            Bitmap b = ((BitmapDrawable)shells[i]).getBitmap();
            Bitmap bitmapResized = Bitmap.createScaledBitmap(b, (int)(CupButton.sizes.cup * 0.2), (int)(CupButton.sizes.cup * 0.2), false);
            shells[i] = new BitmapDrawable(getResources(), bitmapResized);
        }

        //Initialise button array
        _cupButtons = new CupButton[16];

        int topColumnIndex = 7;
        int bottomColumnIndex = 1;
        for(int i = 0; i < 7; i++) {
            //PLAYER A shell cup
            CupButton btn = new CupButton(this, _board, i, CupButton.PLAYER_A, CupButton.CUP);
            btn.addToLayout(_layoutBase, bottomColumnIndex++, 2);
            _cupButtons[i] = btn;

            final int indexA = i;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _board.getCurrentPlayer().move(indexA);
                }
            });

            //PlayerB shell cup
            btn = new CupButton(this, _board, i + 8, CupButton.PLAYER_B, CupButton.CUP);
            btn.addToLayout(_layoutBase, topColumnIndex--, 0);
            _cupButtons[i+8] = btn;

            final int indexB = i+8;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _board.getCurrentPlayer().move(indexB);
                }
            });
        }
        //PLAYER A store
        CupButton btnPlayerA = new CupButton(this, _board, 7, CupButton.PLAYER_A, CupButton.STORE);
        btnPlayerA.addToLayout(_layoutBase, 8, 1);
        _cupButtons[7] = btnPlayerA;

        //PLAYER B store
        CupButton btnPlayerB = new CupButton(this, _board, 15, CupButton.PLAYER_B, CupButton.STORE);
        btnPlayerB.addToLayout(_layoutBase, 0, 1);
        _cupButtons[15] = btnPlayerB;
    }

    /**
     * Recursive animation method, performs single animation from cup a to cup b.
     * @param hand Used to control the state of the backend.
     * @param images Images to be moved from the clicked cup.
     * @param duration Duration of the animation.
     */
    private void moveShellsRec(final HandOfShells hand, final ArrayList<View> images, final int duration) {
        int index = hand.getNextCup();
        final CupButton b = _cupButtons[index];

        final ArrayList<ShellTranslation> animations = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            animations.add(new ShellTranslation(images.get(i), b, duration));
            animations.get(i).startAnimation();
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                new PauseThreadWhile<>(animations, "hasEnded", false);

                Handler h = new Handler(_context.getMainLooper());
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        final HandOfShells robersHand = hand.dropShell();
                        b.addShell((ImageView) images.remove(images.size() - 1));

                        if (images.size() > 0) {
                            moveShellsRec(hand, images, duration);
                        } else if (robersHand != null) {
                            moveRobOpponent(robersHand, duration * 2);
                            processBoardMessages();
                        } else {
                            processEndOfAnimation();
                        }
                    }
                });
            }
        });
        t.start();
    }

    /**
     * Method that handles the case where the opponents cup is robbed because shell
     * landed in the current players cup.
     * @param robbersHand The index of the opponents cup to be robbed.
     * @param duration Duration of the animation.
     */
    private void moveRobOpponent(final HandOfShells robbersHand, final int duration){
        boolean handBelongsToPlayerA = _board.isPlayerA(robbersHand.belongsToPlayer());

        final CupButton playersCup = handBelongsToPlayerA ? _cupButtons[15] : _cupButtons[7];
        CupButton robbedCup = _cupButtons[robbersHand.currentCupIndex()];
        robbersHand.setNextCup(playersCup.getCupIndex());

        final ArrayList<View> images = robbedCup.getShells();
        final ArrayList<ShellTranslation> animations = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            animations.add(new ShellTranslation(images.get(i), playersCup, duration));
            animations.get(i).startAnimation();
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                new PauseThreadWhile<>(animations, "hasEnded", false);

                Handler h = new Handler(_context.getMainLooper());
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        robbersHand.dropAllShells();
                        playersCup.addShells(images);

                        processEndOfAnimation();
                    }
                });
            }
        });
        t.start();
    }

    private void processEndOfAnimation(){
        _playerActionListener.setAnimationInProgress(false);
        processBoardMessages();
    }

    /**
     * Displays messages that explains what happened during animation. TODO Display info messages on the screen.
     */
    private void processBoardMessages(){
        ArrayList<BoardState> msgs = Board.getMessages();
        for (BoardState state : msgs) {
            switch (state){
                case PLAYER_A_TURN:
                    Log.i(TAG, _board.getPlayerA().get_name() + "'s turn.");
                    break;
                case PLAYER_B_TURN:
                    Log.i(TAG, _board.getPlayerB().get_name() + "'s turn.");
                    break;
                case PLAYER_A_HAS_NO_VALID_MOVE:
                    Log.i(TAG, _board.getPlayerA().get_name() + " has no valid move");
                    break;
                case PLAYER_B_HAS_NO_VALID_MOVE:
                    Log.i(TAG, _board.getPlayerB().get_name() + " has no valid move");
                    break;
                case PLAYER_A_GETS_ANOTHER_TURN:
                    Log.i(TAG, _board.getPlayerA().get_name() + " gets another turn.");
                    break;
                case PLAYER_B_GETS_ANOTHER_TURN:
                    Log.i(TAG, _board.getPlayerB().get_name() + " gets another turn.");
                    break;
                case PLAYER_A_WAS_ROBBED:
                    Log.i(TAG, _board.getPlayerA().get_name() + " was robbed.");
                    break;
                case PLAYER_B_WAS_ROBBED:
                    Log.i(TAG, _board.getPlayerB().get_name() + " was robbed.");
                    break;
                case PLAYER_A_WAS_ROBBED_OF_HIS_FINAL_MOVE:
                    Log.i(TAG, _board.getPlayerA().get_name() + " was robbed of his final move... " + _board.getPlayerB().get_name() + " gets another turn.");
                    break;
                case PLAYER_B_WAS_ROBBED_OF_HIS_FINAL_MOVE:
                    Log.i(TAG, _board.getPlayerB().get_name() + " was robbed of his final move... " + _board.getPlayerA().get_name() + " gets another turn.");
                    break;
                case GAME_OVER:
                    Log.i(TAG, "Game Over!!!");
                    for (Pair<Player, Integer> move: _board.getMoves()) {
                        Log.i(TAG, move.first.get_name() + ": " + move.second);
                    }
                    break;
            }
        }

        msgs.clear();
    }
}
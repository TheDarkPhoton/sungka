package com.example.deathgull.sungka_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.content.res.ResourcesCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import game.Game;
import game.board.Board;
import game.board.BoardState;
import game.board.HandOfShells;
import game.connection.SungkaClient;
import game.connection.SungkaConnection;
import game.connection.SungkaProtocol;
import game.connection.SungkaServer;
import game.player.AI;
import game.player.Human;
import game.player.Player;
import game.player.PlayerActionAdapter;
import game.player.PlayerStatistic;
import game.player.RemoteHuman;
import game.tutorial.TutExtraMoves;
import game.tutorial.TutFirstMove;
import game.tutorial.TutRobbing;
import game.tutorial.Tutorial;
import helpers.backend.PauseThreadFor;
import helpers.backend.PauseThreadWhile;
import helpers.frontend.CupButton;
import helpers.frontend.MessageManager;
import helpers.frontend.MusicService;
import helpers.frontend.PlayerNameTextView;
import helpers.frontend.ShellTranslation;

public class GameActivity extends Activity {
    public static final String PLAYER_ONE = "playerOneName";
    public static final String PLAYER_TWO = "playerTwoName";
    public static final String IS_ONLINE = "is_online_game";
    public static final String AI_DIFF = "ai_difficulty";
    public static final String IS_TUTORIAL = "is_tutorial";

    public static boolean IS_TEST;

    private static final String TAG = "GameActivity";
    private static final String fileName = "player_statistics";
    public static final Random random = new Random();                                               //Object for random number generation
    public static Drawable[] shells;
    public static Vibrator vb;

    private final Context _context = this;

    private FrameLayout _layoutMaster;                                                              //Master layout
    private GridLayout _layoutBase;                                                                 //Base layout

    private CupButton[] _cupButtons;
    private Board _board;

    private MessageManager _messageManager;

    private static SungkaConnection usersConnection = null;

    private float _animationDurationFactor = 1.0f;

    private boolean _countdownMode = true;
    private boolean _tutorialMode = false;
    private boolean _clearOpponentsText = false;

    private boolean isPlayerAReady = false;
    private boolean isPlayerBReady = false;
    private LinearLayout readyAreaView;

    private SoundPool _soundPool;
    private int _soundShell, _soundStore;
    private boolean _soundsLoaded;

    private int _sWidth, _sHeight;

    private PlayerActionAdapter _playerActionListener = new PlayerActionAdapter() {
        @Override
        public void onMoveStart(Player player) {
            Log.i(TAG, player.getName() + " started his turn");
            _messageManager.onMoveStart(player);

            if (_board.getCurrentPlayer() != null && player instanceof Human) {
                setupMove(player);
            }

            if (_board.getCurrentPlayer() != null && _board instanceof TutFirstMove) {
                _board.addStateMessage(BoardState.TUTORIAL_FIRSTMOVE_END);
            }

            if (_board.getCurrentPlayer() != null && _tutorialMode){
                Tutorial t = ((Tutorial) _board);
                if (t.isMessagePressent())
                    makeDialog(t.getCurrentMessage(), Gravity.LEFT, 15, true);
            }
        }

        @Override
        public void onMove(final Player player, final int index) {
            _messageManager.onMoveEnd(player);

            if (_board.getCurrentPlayer() != null)
                dehighlightAllCups();

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (_board.getCurrentPlayer() == null)
                        new PauseThreadWhile<>(player, "isAnimationInProgress");
                    else
                        new PauseThreadWhile<>(PlayerActionAdapter.class, "isAnimationInProgress");

                    Log.i(TAG, player.getName() + " performed an action on cup["+index+"]");

                    Handler h = new Handler(_context.getMainLooper());
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            HandOfShells hand = _board.pickUpShells(index);
                            if (hand == null) {
                                player.setPlayerCannotPerformAction(false);
                                return;
                            }


                            if (_board.getCurrentPlayer() == null)
                                player.setAnimationInProgress(true);
                            else
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
            _messageManager.onMoveEnd(player);
            Log.i(TAG, player.getName() + " ended his turn");

            if (_board.getCurrentPlayer() != null && _tutorialMode){
                Tutorial t = ((Tutorial) _board);
                t.nextStep();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        vb  = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        super.onCreate(savedInstanceState);

        PlayerActionAdapter.setAnimationInProgress(false);

        setContentView(
                _layoutMaster = new FrameLayout(this),
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT));


        shells = new Drawable[] {
                ResourcesCompat.getDrawable(getResources(), R.drawable.shell1, null),
                ResourcesCompat.getDrawable(getResources(), R.drawable.shell2, null),
                ResourcesCompat.getDrawable(getResources(), R.drawable.shell3, null),
                ResourcesCompat.getDrawable(getResources(), R.drawable.shell4, null),
        };

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes _attributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();

            _soundPool = new SoundPool.Builder()
                    .setMaxStreams(25)
                    .setAudioAttributes(_attributes)
                    .build();
        } else {
            //noinspection deprecation
            _soundPool = new SoundPool(25, AudioManager.STREAM_MUSIC, 0);
        }

        _soundsLoaded = false;
        _soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                _soundsLoaded = true;
            }
        });

        _soundShell = _soundPool.load(this, R.raw.shell, 1);
        _soundStore = _soundPool.load(this, R.raw.cash, 1);

        getScreenSize();

        Bundle bundle = getIntent().getExtras();
        if (bundle.getString(IS_TUTORIAL) != null){
            if (bundle.getString(IS_TUTORIAL).equals("FirstMove")) {
                Game game = new Game(new TutFirstMove(this), _playerActionListener);
                _board = game.getBoard();
                _tutorialMode = true;
                makeDialog(R.string.msg_TutorialFirstMove, Gravity.LEFT, 15, true);
            } else if (bundle.getString(IS_TUTORIAL).equals("ExtraMoves")){
                Game game = new Game(new TutExtraMoves(this), _playerActionListener);
                _board = game.getBoard();
                _tutorialMode = true;
                _clearOpponentsText = true;
            } else if (bundle.getString(IS_TUTORIAL).equals("Robbing")){
                Game game = new Game(new TutRobbing(this), _playerActionListener);
                _board = game.getBoard();
                _tutorialMode = true;
                _clearOpponentsText = true;
            }
        } else {
            String firstName = bundle.getString(PLAYER_ONE);
            String secondName = bundle.getString(PLAYER_TWO);
            int aiDiff = bundle.getInt(AI_DIFF, 0);
            boolean isOnlineGame = bundle.getBoolean(IS_ONLINE, false);
            IS_TEST = bundle.getBoolean("is_test", false);
            Log.v(TAG, "Its an online game? " + isOnlineGame);

            if(isOnlineGame){
                usersConnection.setActivity(this);
            }

            Game game = new Game(_playerActionListener, isOnlineGame, firstName, secondName, aiDiff);
            _board = game.getBoard();
        }

        hideNav();                                                  //Hide navigation bar and system bar
        setScreenSize();                                            //Set screen size
        initLayouts();                                              //Setup layouts
        initView();                                                 //Programmatically create and lay out elements

        doBindService();
    }

    private void getScreenSize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        _sWidth = displayMetrics.widthPixels;
        _sHeight = displayMetrics.heightPixels;
    }

    private void makeDialog(int message, int gravity, float textSize, boolean changeSize) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this).setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                vb.vibrate(25);
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.show();
        Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        button.setBackgroundResource(R.drawable.roundedbuttonblack);
        button.setTextColor(Color.WHITE);
        if(changeSize) {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.copyFrom(dialog.getWindow().getAttributes());
            params.width = (int)(_sWidth/1.1);
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(params);
        }
        TextView msg = (TextView) dialog.findViewById(android.R.id.message);
        msg.setGravity(gravity); msg.setTextColor(Color.BLACK); msg.setTextSize(textSize);
    }

    /**
     * The counter that is started before the Players can start their first move
     */
    public void startCounter() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Handler h = new Handler(_context.getMainLooper());

                for (int i = 3; i >= 0; --i) {
                    final int counter = i;
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "Count down: " + counter);
                            _messageManager.countdown(counter);
                        }
                    });
                    new PauseThreadFor(1000);
                }

                h.post(new Runnable() {
                    @Override
                    public void run() {
                        _countdownMode = false;
                        _board.getPlayerA().moveStart();
                        _board.getPlayerB().moveStart();
                    }
                });
            }
        });
        t.start();
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
            @Override
            public void onGlobalLayout() {
                _layoutBase.setY((_layoutMaster.getHeight() / 2) - (_layoutBase.getHeight() / 2));

                for (CupButton btn : _cupButtons) {
                    btn.initShellLocation();
                }

                initReturnButtonLocation();

                if (IS_TEST || (_tutorialMode && !(_board instanceof TutFirstMove))) {
                    _countdownMode = false;
                    _board.getPlayerA().moveStart();
                } else {
                    setupReadyScreen();
                }

                _layoutMaster.getViewTreeObserver().removeOnGlobalLayoutListener(this);
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

        //Calculate sizes of store cups and small cups
        CupButton.generateSizes(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }

    private void initCupButtonSmall(String name_id, int index, int row, int column, int player) {
        initCupButton(name_id, index, row, column, player, CupButton.CUP);
    }

    private void initCupButton(String name_id, int index, int row, int column, int player, int type){
        CupButton btn = new CupButton(this, _board, index, player, type);
        btn.addToLayout(_layoutBase, column, row);
        _cupButtons[index] = btn;

        if (type == CupButton.CUP){
            final int finalIndex = index;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Player player = _board.getPlayerA();
                    if (finalIndex > 7 && finalIndex < 15){
                        player = _board.getPlayerB();
                    }

                    if (player instanceof AI || player instanceof RemoteHuman || _countdownMode || !_board.isValid(finalIndex, false))
                        return;

                    player.move(finalIndex);
                }
            });
            btn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (!_board.isValid(finalIndex, false))
                        return false;

                    _cupButtons[finalIndex].onTouch(v, event);
                    return false;
                }
            });
        }

        // fetch and apply a unique identifier to the Player A cup
        int id = getResources().getIdentifier(name_id, "id", getPackageName());
        btn.setId(id);
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
            //PLAYER shell cups
            initCupButtonSmall("cup1_" + (i + 1), i, 2, bottomColumnIndex++, CupButton.PLAYER_A);
            initCupButtonSmall("cup2_" + (i + 1), i + 8, 0, topColumnIndex--, CupButton.PLAYER_B);
        }

        //PLAYER stores
        initCupButton("cup1_store", 7, 1, 8, CupButton.PLAYER_A, CupButton.STORE);
        initCupButton("cup2_store", 15, 1, 0, CupButton.PLAYER_B, CupButton.STORE);

        initReturnButton();

        _messageManager = new MessageManager(this, _layoutMaster);
        if (_clearOpponentsText) {
            _messageManager.clearOpponent();
        }

        // Setup Player views
        PlayerNameTextView[] _playerTextViews = new PlayerNameTextView[2];

        _playerTextViews[0] = new PlayerNameTextView(this, _board.getPlayerA());
        _playerTextViews[1] = new PlayerNameTextView(this, _board.getPlayerB());
        _layoutMaster.addView(_playerTextViews[0]);
        _layoutMaster.addView(_playerTextViews[1]);

        // Setup animation speed listener
        _layoutMaster.getRootView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    _animationDurationFactor = 0.5f;
                } else {
                    _animationDurationFactor = 1.0f;
                }
                return false;
            }
        });
    }

    /**
     * Put a button which wil appear at the end of the game into the layout.
     */
    private void initReturnButton() {
        Button btnReturn = new Button(this);

        // set up layout
        FrameLayout.LayoutParams paramsButton = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);

        btnReturn.setBackgroundResource(R.drawable.roundedbuttonwhite);
        btnReturn.setTextColor(Color.BLACK);
        btnReturn.setTextSize(TypedValue.COMPLEX_UNIT_PX, 25);
        btnReturn.setText(getResources().getString(R.string.msg_ReturnToMenu));
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnReturn.setLayoutParams(paramsButton);
        _layoutMaster.addView(btnReturn);

        int id = getResources().getIdentifier("btn_ReturnToMenu", "id", getPackageName());
        btnReturn.setId(id);

    }

    /**
     * Set the position of the "return to game" button.
     */
    private void initReturnButtonLocation() {
        Button btnReturn = (Button) findViewById(R.id.btn_ReturnToMenu);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);

        btnReturn.setX((displayMetrics.widthPixels - btnReturn.getWidth()) / 2.0f);
        btnReturn.setY(displayMetrics.heightPixels / 2.0f);
        btnReturn.setVisibility(View.GONE);
    }

    /**
     * Recursive animation method, performs single animation from cup a to cup b.
     * @param hand Used to control the state of the backend.
     * @param images Images to be moved from the clicked cup.
     * @param duration Duration of the animation.
     */
    private void moveShellsRec(final HandOfShells hand, final ArrayList<View> images, final int duration) {
        final int index = hand.getNextCup();
        final CupButton b = _cupButtons[index];

        final ArrayList<ShellTranslation> animations = new ArrayList<>();
        final int finalDuration = (int) (_animationDurationFactor * duration);

        for (int i = 0; i < images.size(); i++) {
            animations.add(new ShellTranslation(images.get(i), b, finalDuration));
            animations.get(i).startAnimation();
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                new PauseThreadWhile<>(animations, "hasEnded", false);

                if (index == 7 || index == 15) {
                    playSound(_soundStore);
                } else {
                    playSound(_soundShell);
                }

                Handler h = new Handler(_context.getMainLooper());
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        final HandOfShells robbersHand = hand.dropShell();

                        if (images.size() > 0)
                            b.addShell((ImageView) images.remove(images.size() - 1));

                        if (images.size() > 0) {
                            moveShellsRec(hand, images, duration);
                        } else if (robbersHand != null) {
                            moveRobOpponent(_board.pickUpShells(hand.currentCupIndex()), robbersHand, duration * 2);
                            //processBoardMessages();
                        } else {
                            processEndOfAnimation(hand.belongsToPlayer());
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
     * @param activatorHand The hand object of the cup that initiated the rob move.
     * @param robbersHand The hand object of the cup that was robbed.
     * @param duration Duration of the animation.
     */
    private void moveRobOpponent(final HandOfShells activatorHand, final HandOfShells robbersHand, final int duration){
        boolean handBelongsToPlayerA = _board.isPlayerA(robbersHand.belongsToPlayer());

        final CupButton playersCup = handBelongsToPlayerA ? _cupButtons[7] : _cupButtons[15];
        CupButton activatorCup = _cupButtons[activatorHand.currentCupIndex()];
        CupButton robbedCup = _cupButtons[robbersHand.currentCupIndex()];
        robbersHand.setNextCup(playersCup.getCupIndex());
        activatorHand.setNextCup(playersCup.getCupIndex());

        final ArrayList<View> images = robbedCup.getShells();
        images.addAll(activatorCup.getShells());

        playSound(_soundStore);

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
                        activatorHand.dropAllShells();
                        robbersHand.dropAllShells();
                        playersCup.addShells(images);

                        _board.nextPlayersMove();
                        processEndOfAnimation(activatorHand.belongsToPlayer());
                    }
                });
            }
        });
        t.start();
    }

    private void processEndOfAnimation(Player player){
        if (_board.getCurrentPlayer() == null) {
            player.setPlayerCannotPerformAction(false);
            player.setAnimationInProgress(false);
            if (_board.getPlayerA().isFirstMovesExhausted() && _board.getPlayerB().isFirstMovesExhausted()){
                if (_board.isPlayerA(player))
                    _board.setCurrentPlayerB();
                else
                    _board.setCurrentPlayerA();

                _board.getCurrentPlayer().moveStart();
            }

            if (!player.isFirstMovesExhausted())
                player.moveStart();
        }
        else
            PlayerActionAdapter.setAnimationInProgress(false);

        processBoardMessages();
    }

    /**
     * A check to see whether shell animations are running.
     * @return true if all shell animations have finished.
     */
    public boolean animationFinished(boolean isFirstTurn) {
        if (isFirstTurn) {
            return _board.getPlayerA().isFirstMovesExhausted() &&
                    _board.getPlayerB().isFirstMovesExhausted();
        } else {
            return !PlayerActionAdapter.isAnimationInProgress();
        }
    }

    /**
     * Displays messages that explains what happened during animation. TODO Display info messages on the screen.
     */
    private void processBoardMessages(){
        ArrayList<BoardState> msgs = _board.getMessages();
        for (BoardState state : msgs) {
            switch (state){
                case PLAYER_A_TURN:
                    Log.i(TAG, _board.getPlayerA().getName() + "'s turn.");
                    break;
                case PLAYER_B_TURN:
                    Log.i(TAG, _board.getPlayerB().getName() + "'s turn.");
                    break;
                case PLAYER_A_HAS_NO_VALID_MOVE:
                    Log.i(TAG, _board.getPlayerA().getName() + " has no valid move");
                    break;
                case PLAYER_B_HAS_NO_VALID_MOVE:
                    Log.i(TAG, _board.getPlayerB().getName() + " has no valid move");
                    break;
                case PLAYER_A_GETS_ANOTHER_TURN:
                    Log.i(TAG, _board.getPlayerA().getName() + " gets another turn.");
                    _board.getPlayerA().addMove();//they get another move
                    _messageManager.playerGetsAnotherTurn(_board.getPlayerA());
                    break;
                case PLAYER_B_GETS_ANOTHER_TURN:
                    Log.i(TAG, _board.getPlayerB().getName() + " gets another turn.");
                    _board.getPlayerB().addMove();//they get another move
                    _messageManager.playerGetsAnotherTurn(_board.getPlayerB());
                    break;
                case PLAYER_A_WAS_ROBBED:
                    Log.i(TAG, _board.getPlayerA().getName() + " was robbed.");
                    _messageManager.playerGotRobbed(_board.getPlayerA());
                    break;
                case PLAYER_B_WAS_ROBBED:
                    Log.i(TAG, _board.getPlayerB().getName() + " was robbed.");
                    _messageManager.playerGotRobbed(_board.getPlayerB());
                    break;
                case PLAYER_A_WAS_ROBBED_OF_HIS_FINAL_MOVE:
                    _messageManager.playerGotRobbedOfHisFinalMove(_board.getPlayerA());
                    Log.i(TAG, _board.getPlayerA().getName() + " was robbed of his final move... " + _board.getPlayerB().getName() + " gets another turn.");
                    checkGameOver();
                    break;
                case PLAYER_B_WAS_ROBBED_OF_HIS_FINAL_MOVE:
                    _messageManager.playerGotRobbedOfHisFinalMove(_board.getPlayerB());
                    Log.i(TAG, _board.getPlayerB().getName() + " was robbed of his final move... " + _board.getPlayerA().getName() + " gets another turn.");
                    checkGameOver();
                    break;
                case GAME_OVER:
                    doGameOver();
                    break;
                case TUTORIAL_FIRSTMOVE_END:
                    makeDialog(R.string.msg_TutorialFirstMoveEnd, Gravity.LEFT, 15, true);
                    ((Button) findViewById(R.id.btn_ReturnToMenu)).setVisibility(View.VISIBLE);
                    ((Tutorial) _board).endTutorial();
                    break;
            }
        }

        msgs.clear();
    }

    private void checkGameOver() {
        if (!(_board.getPlayerA().hasValidMove() || _board.getPlayerB().hasValidMove())) {
            doGameOver();
        }
    }

    private void doGameOver() {
        _messageManager.gameOver(_board.isDraw(), _board.getWinningPlayer());
        Log.i(TAG, "Game Over!!!");
        if(usersConnection != null){
            usersConnection.stopPings();

        }

        ArrayList<PlayerStatistic> playerStatistics = readStats(getApplicationContext(), null);//read the stats
        //store the leaderboard data for non online games
        if(!(_board.getPlayerA() instanceof RemoteHuman)){//if the first player isnt a remote human than store data for them
            updateList(_board.getPlayerA(), playerStatistics);
        }

        if(!(_board.getPlayerB() instanceof RemoteHuman)){//if the second player isnt a remote human than store data for them
            updateList(_board.getPlayerB(), playerStatistics);
        }

        try {
            storeStats(playerStatistics);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ((Button) findViewById(R.id.btn_ReturnToMenu)).setVisibility(View.VISIBLE);
    }



    /**
     * Highlight all the buttons that need highlighting
     */
    public void setupMove(Player player) {
        for (int i = 0; i < _cupButtons.length; i++) {
            _cupButtons[i].rotateTowards(player);

            if (_board.getCurrentPlayer() != null && (_board.isOpponentStore(i) || _board.isCurrentPlayersStore(i)))
                continue;

            if (_board.isValid(i, false)) {
                _cupButtons[i].highlight();
            } else {
                _cupButtons[i].dehighlight();
            }
        }
    }

    public void dehighlightAllCups() {
        for (int i = 0; i < _cupButtons.length; i++) {
            if (_board.getCurrentPlayer() != null && (_board.isOpponentStore(i) || _board.isCurrentPlayersStore(i)))
                continue;

            _cupButtons[i].dehighlight();
        }
    }

    /**
     * Store the SungkaConnection, that has previously been set up, that the user will use for the the multiplayer
     * @param sungkaConnection the SungkaConnection that corresponds to the user on this device
     */
    public static void setConnection(SungkaConnection sungkaConnection){
        usersConnection = sungkaConnection;

    }

    /**
     * Get the SungkaConnection that has previously been set up for a multiplayer game
     * @return the SungkaConnection used to send messages between the devices
     */
    public static SungkaConnection getUsersConnection(){
        return usersConnection;
    }

    /**
     * Set up the host connection
     * @param menuActivity the activity that calls this method, to use in the onPreExecute method in the AsyncTask, when establishing
     *                     the connection.
     * @param playerName the name of the current Player trying to connect to the other Player
     */
    public static void setUpHostConnection(MenuActivity menuActivity,String playerName){
        SungkaServer sungkaServer = new SungkaServer(4000,menuActivity,playerName);
        usersConnection = sungkaServer;
        sungkaServer.execute();

    }

    /**
     * Set up a connection where you join a host
     * @param menuActivity the activity that calls this method, to use in the onPreExecute method in the AsyncTask, when establishing
     *                     the connection.
     * @param ip the ip to which you connect to.
     * @param playerName the name of the current Player trying to connect to the other Player
     */
    public static void setUpJoinConnection(MenuActivity menuActivity,String ip,String playerName){
        // In the case of a client connecting to the server, the server needs to be set up before
        SungkaClient sungkaClient = new SungkaClient(ip,4000,playerName,menuActivity);//server ip and port need to be inserted by the user
        usersConnection = sungkaClient;
        sungkaClient.execute();                 //this is a test one

    }

    /**
     * Displays a message saying other person disconnected and brings them to the main menu.
     */
    public void otherPlayerDidDisconnect() {
        DialogInterface.OnClickListener goToMainMenuListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Go to main menu
                finish();
            }
        };

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(_context);
        alertBuilder
                .setTitle(R.string.str_playerDisconnectedTitle)
                .setMessage(R.string.str_playerDisconnected)
                .setPositiveButton("OK", goToMainMenuListener);

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    /**
     * Update the list of PlayerStatistic objects by updating data for the Player object provided, or add a new
     * PlayerStatistic object for that Player, if there was no data for them before
     * @param player the Player object that we are updating the data for
     * @param playerStatistics the list of PlayerStatistic object that we are updating data on
     */
    private void updateList(Player player,ArrayList<PlayerStatistic> playerStatistics){
        boolean updatedStats = false;
        for(PlayerStatistic playerStatistic: playerStatistics){
            if(player.getName().equals(playerStatistic.getPlayerName())){
                Log.v(TAG,"found the player");
                updateStats(player,playerStatistic);
                Log.v(TAG,playerStatistic.toString());
                updatedStats = true;
                break;
            }
        }
        if(!updatedStats){//if the player doesnt exist in the file, because they havent played before
            addNewPlayerStats(player,playerStatistics);
        }
    }

    /**
     * Creates fake data and stores them on the device
     * Call only once per device!
     */
    private static void createDummyData(Context context) {
        String path = "player_statistics_dummy";

        if (!(new File(path).exists())) {
            String line1 = "Han Solo,3,0,1,1,5062.428571428572,26,12\n";
            String line2 = "Chewie,3,1,0,1,4832.684210526316,34,3\n";
            String data = line1 + line2;

            try {
                FileOutputStream fileOutputStream = context.openFileOutput(path, Context.MODE_PRIVATE);
                fileOutputStream.write(data.getBytes());
                fileOutputStream.close();
                Log.i(TAG, "Created dummy data");
            } catch (IOException e) {
                Log.w(TAG, "Could not create dummy data!");
            }
        }
    }

    private static void createEmptyData(Context context) {
        String path = "player_statistics_empty";
        File file = new File(path);
        if (!file.exists()) {
            String data = "";

            try {
                FileOutputStream fileOutputStream = context.openFileOutput(path, Context.MODE_PRIVATE);
                fileOutputStream.write(data.getBytes());
                fileOutputStream.close();
                Log.i(TAG, "Created empty data");
            } catch (IOException e) {
                Log.w(TAG, "Could  not create empty data!");
            }
        }
    }

    /**
     * Store the list of PlayerStatistic objects in the data file "player_statistics"
     * @param playerStatistics the list of PlayerStatistic objects we are storing
     * @throws IOException if there is an error when opening the file output or writing to the file
     */
    private void storeStats(ArrayList<PlayerStatistic> playerStatistics) throws IOException {
        //store the stats
        String data = "";
        for(PlayerStatistic playerStatistic: playerStatistics){
            data += playerStatistic.toString();
        }
        FileOutputStream fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
        fileOutputStream.write(data.getBytes());
        Log.v(TAG, "Stored the new stats");
        Log.v(TAG, "Stats:");
        Log.v(TAG, data);
        fileOutputStream.close();
        //replaced the old file with a new one that has the updated data
        Log.v(TAG, "About ot read stats");
        ArrayList<PlayerStatistic> stats = readStats(getApplicationContext(), null);
        for(PlayerStatistic player: stats){
            Log.v(TAG,player.toString());
        }
        Log.v(TAG, "Read stats");
    }

    /**
     * Add a new Player to the list of PlayerStatistics, if they are not in the list
     * @param player the Player for which we are storing data for
     * @param playerStatistics the list of PlayerStatistic objects, that we need to add the new PlayerStatistic object too
     */
    private void addNewPlayerStats(Player player,ArrayList<PlayerStatistic> playerStatistics){
        PlayerStatistic playerStatistic = new PlayerStatistic(player.getName());//make a new PlayerStatistic object
        playerStatistic.increaseGamesPlayed();      //that holds the data for a Player that has just finished there first game
        if(_board.isDraw()){
            playerStatistic.increaseGamesDraw();
        }else if(_board.getPlayerWon() == player){
            playerStatistic.increaseGamesWon();
        }else{
            playerStatistic.increaseGamesLost();
        }
        playerStatistic.setAverageMoveTimeInMillis(player.getAverageTurnTime());
        playerStatistic.setMaxNumShellsCollected(player.getMaxNumberShellsCollected());
        playerStatistic.setMaxConsecutiveMoves(player.getMaxConsecutiveMoves());
        playerStatistics.add(playerStatistic);
        Log.v(TAG, "Made a player Statistic: " + playerStatistic.toString());
    }

    /**
     * Updata the statistics in the PlayerStatistic object of a Player
     * @param player the Player for which we are updating there data for
     * @param playerStatistic the PlayerStatistic which we are updating data for
     */
    private void updateStats(Player player,PlayerStatistic playerStatistic){
        playerStatistic.increaseGamesPlayed();
        if(_board.isDraw()){
            playerStatistic.increaseGamesDraw();
        }else if(_board.getPlayerWon() == player){
            playerStatistic.increaseGamesWon();
        }else{
            playerStatistic.increaseGamesLost();
        }
        playerStatistic.updateAverageMoveTimeInMillis(player.getAverageTurnTime());
        if(playerStatistic.getMaxNumShellsCollected() < player.getMaxNumberShellsCollected()){
            playerStatistic.setMaxNumShellsCollected(player.getMaxNumberShellsCollected());
        }
    }

    /**
     * Read the stats stored in the data file "player_statistics", or some other file if parameter
     * `file` is not null.
     * @param context the context the application is currently in (to open the file input)
     * @param file the name of the data file to be read from. Can be null.
     * @return an ArrayList of PlayerStatistic objects (can be empty if there is no data)
     */
    public static ArrayList<PlayerStatistic> readStats(Context context, String file) {
        if (file == null) {
            file = fileName;
        } else {
            createDummyData(context);
            createEmptyData(context);
        }

        FileInputStream fileInputStream;
        ArrayList<PlayerStatistic> playerStatistics = new ArrayList<PlayerStatistic>();
        String textInFile = "";
        int n;
        try {
            fileInputStream = context.openFileInput(file);
            while ((n = fileInputStream.read()) != -1) {
                textInFile += Character.toString((char) n);
            }
            Log.v(TAG,textInFile);
            if(!textInFile.equals("")) {
                String[] players = textInFile.split("\n");
                for (String player : players) {
                    Log.v(TAG, player);
                    String[] info = player.split(",");//to get the individual data of a PlayerStatistic
                    String playerName = info[0];
                    String gamesPlayed = info[1];
                    String gamesWon = info[2];
                    String gamesLost = info[3];
                    String gamesDrawn = info[4];
                    String avgTimeInMillis = info[5];
                    String maxShellCollected = info[6];
                    String maxConsecutiveMoves = info[7];
                    PlayerStatistic playerStatistic = new PlayerStatistic(playerName);//make a PlayerStatistic object with the
                    playerStatistic.setGamesPlayed(Integer.valueOf(gamesPlayed));   //data we just obtained
                    playerStatistic.setGamesWon(Integer.valueOf(gamesWon));
                    playerStatistic.setGamesLost(Integer.valueOf(gamesLost));
                    playerStatistic.setGamesDrawn(Integer.valueOf(gamesDrawn));
                    playerStatistic.setAverageMoveTimeInMillis(Double.valueOf(avgTimeInMillis));
                    playerStatistic.setMaxNumShellsCollected(Integer.valueOf(maxShellCollected));
                    playerStatistic.setMaxConsecutiveMoves(Integer.valueOf(maxConsecutiveMoves));
                    playerStatistics.add(playerStatistic);
                    Log.v(TAG, playerStatistic.toString());
                }
                Log.v(TAG,"Read in the file");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return playerStatistics;
    }

    private void setupReadyScreen() {
        readyAreaView = new LinearLayout(this);
        readyAreaView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        readyAreaView.setOrientation(LinearLayout.VERTICAL);
        _layoutMaster.addView(readyAreaView);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.weight = 0.5f;


        View topView = new View(this);
        topView.setLayoutParams(params);
        readyAreaView.addView(topView);

        View bottomView = new View(this);
        bottomView.setLayoutParams(params);
        readyAreaView.addView(bottomView);

        if (_board.getPlayerA() instanceof Human) {
            Log.i(TAG, "Player A is " + _board.getPlayerA());
            bottomView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(usersConnection != null){//online game
                        usersConnection.sendMessage(SungkaProtocol.STARTCOUNTER);
                    }
                    isPlayerAReady = true;
                    trySetupCountdown();
                }
            });
        } else {
            isPlayerAReady = true;
            trySetupCountdown();
        }

        if (_board.getPlayerB() instanceof Human) {
            Log.i(TAG, "Player B is " + _board.getPlayerB());
            topView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isPlayerBReady = true;
                    trySetupCountdown();
                }
            });
        }else if(_board.getPlayerB() instanceof RemoteHuman){
            Log.i(TAG,"Player B is "+_board.getPlayerB().getName());
            isPlayerBReady = false;
        }
        else {
            isPlayerBReady = true;
            trySetupCountdown();
        }

        int idTop = getResources().getIdentifier("readyTopView", "id", getPackageName());
        int idBot = getResources().getIdentifier("readyBottomView", "id", getPackageName());
        topView.setId(idTop);
        bottomView.setId(idBot);
    }

    private void trySetupCountdown() {
        if (isPlayerAReady && isPlayerBReady) {
            readyAreaView.removeAllViews();
            _layoutMaster.removeView(readyAreaView);
            readyAreaView = null;
            startCounter();

        } else if (isPlayerAReady) {
            _messageManager.waitingForOtherPlayer(
                    _board.getPlayerA(),
                    _board.getPlayerB()
            );
        } else if (isPlayerBReady) {
            _messageManager.waitingForOtherPlayer(
                    _board.getPlayerB(),
                    _board.getPlayerA()
            );
        }
    }

    /**
     * Plays a given sound
     * @param sound - sound id to play
     */
    private void playSound(int sound) {
        if(_soundsLoaded) {
            _soundPool.play(sound, 1.0f, 1.0f, 1, 0, 1);
        }
    }

    /**
     * closes multiplayer connections when activity is closed
     */
    public void onDestroy(){
        super.onDestroy();
        if(usersConnection != null){
            try {
                usersConnection.closeConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        doUnbindService();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void setPlayerBReady(){
        isPlayerBReady = true;
        trySetupCountdown();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(musicService != null) {
            musicService.resumeMusic();
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        if(musicService != null) {
            musicService.pauseMusic();
        }
    }

    private boolean isBound = false;
    private MusicService musicService;
    private ServiceConnection serviceConnection = new ServiceConnection(){
        public void onServiceConnected(ComponentName name, IBinder binder) {
            musicService = ((MusicService.ServiceBinder)binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
        }
    };

    void doBindService(){
        bindService(new Intent(this, MusicService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        isBound = true;
    }

    void doUnbindService() {
        if(isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }
}
package com.example.deathgull.sungka_project;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Random;

import java.util.concurrent.ExecutionException;

import game.connection.SungkaClient;
import game.connection.SungkaConnection;
import game.connection.SungkaServer;
import game.board.Board;
import game.Game;
import game.board.HandOfShells;
import game.board.BoardState;
import game.player.AI;
import game.player.Player;
import game.player.PlayerActionAdapter;
import game.player.RemoteHuman;
import helpers.backend.PauseThreadFor;
import helpers.frontend.CupButton;
import helpers.backend.PauseThreadWhile;
import helpers.frontend.ShellTranslation;
import helpers.frontend.YourMoveTextView;

public class GameActivity extends Activity {
    private static final String TAG = "GameActivity";
    public static final Random random = new Random();                                               //Object for random number generation
    public static Drawable[] shells;
    private static SungkaConnection usersConnection = null;

    private final Context _context = this;

    private FrameLayout _layoutMaster;                                                              //Master layout
    private GridLayout _layoutBase;                                                                 //Base layout

    private CupButton[] _cupButtons;
    private Board _board;
    private YourMoveTextView _yourMoveTop;
    private YourMoveTextView _yourMoveBottom;

    private float _animationDurationFactor = 1.0f;

    private boolean _countdownMode = true;

    private PlayerActionAdapter _playerActionListener = new PlayerActionAdapter() {
        @Override
        public void onMoveStart(Player player) {
            Log.i(TAG, player.getName() + " started his turn");

            if (_board.isPlayerA(player))
                _yourMoveBottom.show();
            else
                _yourMoveTop.show();

            setupMove(player);
        }

        @Override
        public void onMove(final Player player, final int index) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (_board.getCurrentPlayer() == null)
                        new PauseThreadWhile<>(player, "isAnimationInProgress");
                    else
                        new PauseThreadWhile<>(PlayerActionAdapter.class, "isAnimationInProgress");

                    Handler h = new Handler(_context.getMainLooper());
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            HandOfShells hand = _board.pickUpShells(index);
                            if (hand == null) {
                                player.setPlayerCannotPerformAction(false);
                                return;
                            }

                            Log.i(TAG, player.getName() + " performed an action on cup["+index+"]");

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
            Log.i(TAG, player.getName() + " ended his turn");

            if (_board.isPlayerA(player))
                _yourMoveBottom.hide();
            else
                _yourMoveTop.hide();
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

        getIp();

        shells =new Drawable[]{
                ResourcesCompat.getDrawable(getResources(), R.drawable.shell1, null),
                ResourcesCompat.getDrawable(getResources(), R.drawable.shell2, null),
                ResourcesCompat.getDrawable(getResources(), R.drawable.shell3, null),
                ResourcesCompat.getDrawable(getResources(), R.drawable.shell4, null),
        };
        
        Game game = new Game(_playerActionListener,this);
        _board = game.getBoard();

        hideNav();                                                  //Hide navigation bar and system bar
        setScreenSize();                                            //Set screen size
        initLayouts();                                              //Setup layouts
        initView();                                                 //Programmatically create and lay out elements
    }

    private void startCounter() {
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

    @Override
    protected void onResume() {
        super.onResume();
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

                startCounter();

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

                    if (player instanceof AI || player instanceof RemoteHuman || _countdownMode)
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

        // get package name for applying IDs
        String packageName = getPackageName();

        int topColumnIndex = 7;
        int bottomColumnIndex = 1;
        for(int i = 0; i < 7; i++) {
            //PLAYER shell cups
            initCupButtonSmall("cup1_" + (i + 1), i, 2, bottomColumnIndex++, CupButton.PLAYER_A);
            initCupButtonSmall("cup2_" + (i + 9), i + 8, 0, topColumnIndex--, CupButton.PLAYER_B);
        }

        //PLAYER stores
        initCupButton("cup1_store", 7, 1, 8, CupButton.PLAYER_A, CupButton.STORE);
        initCupButton("cup2_store", 15, 1, 0, CupButton.PLAYER_B, CupButton.STORE);

        // PLAYER A your move label
        _yourMoveBottom = new YourMoveTextView(this, true);
        _layoutMaster.addView(_yourMoveBottom);

        // PLAYER B your move label
        _yourMoveTop = new YourMoveTextView(this, false);
        _layoutMaster.addView(_yourMoveTop);

        // Setup animation speed listener
        _layoutMaster.getRootView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    _animationDurationFactor = 5f;
                } else {
                    _animationDurationFactor = 1.0f;
                }
                return false;
            }
        });
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
        final int finalDuration = (int) (_animationDurationFactor * duration);

        for (int i = 0; i < images.size(); i++) {
            animations.add(new ShellTranslation(images.get(i), b, finalDuration));
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
                        final HandOfShells robbersHand = hand.dropShell();
                        b.addShell((ImageView) images.remove(images.size() - 1));

                        if (images.size() > 0) {
                            moveShellsRec(hand, images, duration);
                        } else if (robbersHand != null) {
                            moveRobOpponent(_board.pickUpShells(hand.currentCupIndex()), robbersHand, duration * 2);
                            processBoardMessages();
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
                    break;
                case PLAYER_B_GETS_ANOTHER_TURN:
                    Log.i(TAG, _board.getPlayerB().getName() + " gets another turn.");
                    break;
                case PLAYER_A_WAS_ROBBED:
                    Log.i(TAG, _board.getPlayerA().getName() + " was robbed.");
                    break;
                case PLAYER_B_WAS_ROBBED:
                    Log.i(TAG, _board.getPlayerB().getName() + " was robbed.");
                    break;
                case PLAYER_A_WAS_ROBBED_OF_HIS_FINAL_MOVE:
                    Log.i(TAG, _board.getPlayerA().getName() + " was robbed of his final move... " + _board.getPlayerB().getName() + " gets another turn.");
                    break;
                case PLAYER_B_WAS_ROBBED_OF_HIS_FINAL_MOVE:
                    Log.i(TAG, _board.getPlayerB().getName() + " was robbed of his final move... " + _board.getPlayerA().getName() + " gets another turn.");
                    break;
                case GAME_OVER:
                    Log.i(TAG, "Game Over!!!");
                    for (int i = 0; i < _board.getMoves().size(); i++) {
                        Pair<Player, Integer> move = _board.getMoves().get(i);
                        Log.i(TAG, i + ": " + move.second + " -> " + move.first.getName());
                    }
                    break;
            }
        }

        msgs.clear();
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
     * To set up the connection
     * @param connectionType the type of connection we want to create, a Server (Host) connection or a Client (Join) connection
     */
    private void setUpConnection(String connectionType){
        if(connectionType.equals(SungkaConnection.HOST_CONNECTION)){//uses SungkaServer, sets up the game
            SungkaServer sungkaServer = new SungkaServer(4000);
            sungkaServer.execute();
            try {
                sungkaServer.get();//wait for the connection to be established; returns true if it is set up,else false
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            setConnection(sungkaServer);
        }else if(connectionType.equals(SungkaConnection.JOIN_CONNECTION)){//uses SungkaClient, joins the game
            // In the case of a client connecting to the server, the server needs to be set up before
            SungkaClient sungkaClient = new SungkaClient("10.230.211.57",4000);//server ip and port need to be inserted by the user
            sungkaClient.execute();                 //this is a test one
            try {
                sungkaClient.get();//wait for the connection to be established; returns true if it is set up, else false
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            setConnection(sungkaClient);
        }
    }

    /**
     * Get the IP of the current device
     * @return the IPv4 of the device
     */
    private String getIp(){
        //To get the ip
        //TODO: find another method to get the IP address
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        Log.v(TAG, "ip: " + ip);
        return ip;
    }
}
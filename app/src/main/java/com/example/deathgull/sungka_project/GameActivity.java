package com.example.deathgull.sungka_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import game.player.Human;
import game.player.Player;
import game.player.PlayerActionAdapter;
import game.player.RemoteHuman;
import helpers.MessageManager;
import helpers.frontend.CupButton;
import helpers.backend.PauseThreadWhile;
import helpers.frontend.PlayerNameTextView;
import helpers.frontend.ShellTranslation;
import game.player.Side;

public class GameActivity extends Activity {
    private static final String TAG = "GameActivity";
    private static final String fileName = "player_statistics";
    public static final Random random = new Random();                                               //Object for random number generation
    public static Drawable[] shells;

    private final Context _context = this;

    private FrameLayout _layoutMaster;                                                              //Master layout
    private GridLayout _layoutBase;                                                                 //Base layout

    private CupButton[] _cupButtons;
    private Game _game;
    private Board _board;
    private MessageManager _messageManager;
    private PlayerNameTextView[] _playerTextViews;



    private float _animationDurationFactor = 1.0f;

    private static SungkaConnection usersConnection = null;

    private PlayerActionAdapter _playerActionListener = new PlayerActionAdapter() {
        @Override
        public void onMoveStart(Player player) {
            Log.i(TAG, player.getName() + " started his turn");
            _messageManager.onMoveStart(player);

            if (player instanceof Human) {
                setupMove(player.getSide());
            }
        }

        @Override
        public void onMove(final Player player, final int index) {
            dehighlightAllCups();

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
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


        //setUpHostConnection();
        //setUpJoinConnection("10.230.155.205");
        shells =new Drawable[]{
                ResourcesCompat.getDrawable(getResources(), R.drawable.shell1, null),
                ResourcesCompat.getDrawable(getResources(), R.drawable.shell2, null),
                ResourcesCompat.getDrawable(getResources(), R.drawable.shell3, null),
                ResourcesCompat.getDrawable(getResources(), R.drawable.shell4, null),
        };

        
        _game = new Game(_playerActionListener,this);
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
        //_game.start();
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

        //Calculate sizes of store cups and small cups
        CupButton.generateSizes(displayMetrics.widthPixels, displayMetrics.heightPixels);
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
            //PLAYER A shell cup
            CupButton btn = new CupButton(this, _board, i, CupButton.PLAYER_A, CupButton.CUP);
            btn.addToLayout(_layoutBase, bottomColumnIndex++, 2);
            _cupButtons[i] = btn;

            final int indexA = i;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (_board.getCurrentPlayer() instanceof AI || _board.getCurrentPlayer() instanceof RemoteHuman)
                        return;
                    _board.getCurrentPlayer().move(indexA);
                }
            });
            btn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (!_board.isValid(indexA, false))
                        return false;

                    _cupButtons[indexA].onTouch(v, event);
                    return false;
                }
            });

            // fetch and apply a unique identifier to the Player A cup
            int id = getResources().getIdentifier("cup1_" + (i+1), "id", packageName);
            btn.setId(id);

            //PlayerB shell cup
            btn = new CupButton(this, _board, i + 8, CupButton.PLAYER_B, CupButton.CUP);
            btn.addToLayout(_layoutBase, topColumnIndex--, 0);
            _cupButtons[i+8] = btn;

            final int indexB = i+8;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (_board.getCurrentPlayer() instanceof AI || _board.getCurrentPlayer() instanceof RemoteHuman)
                        return;
                    _board.getCurrentPlayer().move(indexB);
                }
            });
            btn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (!_board.isValid(indexB, false))
                        return false;

                    _cupButtons[indexB].onTouch(v, event);
                    return false;
                }
            });

            // fetch and apply a unique identifier to the Player B cup
            id = getResources().getIdentifier("cup2_" + (i+1), "id", packageName);
            btn.setId(id);
        }
        //PLAYER A store
        CupButton btnPlayerA = new CupButton(this, _board, 7, CupButton.PLAYER_A, CupButton.STORE);
        btnPlayerA.addToLayout(_layoutBase, 8, 1);
        _cupButtons[7] = btnPlayerA;

        //PLAYER B store
        CupButton btnPlayerB = new CupButton(this, _board, 15, CupButton.PLAYER_B, CupButton.STORE);
        btnPlayerB.addToLayout(_layoutBase, 0, 1);
        _cupButtons[15] = btnPlayerB;

        // set IDs to stores
        int id = getResources().getIdentifier("cup1_store", "id", packageName);
        btnPlayerA.setId(id);
        id = getResources().getIdentifier("cup2_store", "id", packageName);
        btnPlayerB.setId(id);

        // Setup message manager
        _messageManager = new MessageManager(this, _layoutMaster);

        // Setup Player views
        _playerTextViews = new PlayerNameTextView[2];

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
     * @param activatorHand The hand object of the cup that initiated the rob move.
     * @param robbersHand The hand object of the cup that was robbed.
     * @param duration Duration of the animation.
     */
    private void moveRobOpponent(final HandOfShells activatorHand, final HandOfShells robbersHand, final int duration){
        boolean handBelongsToPlayerA = _board.isPlayerA(robbersHand.belongsToPlayer());

        final CupButton playersCup = handBelongsToPlayerA ? _cupButtons[15] : _cupButtons[7];
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
                        processEndOfAnimation();
                    }
                });
            }
        });
        t.start();
    }

    private void processEndOfAnimation(){
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
                    break;
                case PLAYER_B_WAS_ROBBED_OF_HIS_FINAL_MOVE:
                    _messageManager.playerGotRobbedOfHisFinalMove(_board.getPlayerB());
                    Log.i(TAG, _board.getPlayerB().getName() + " was robbed of his final move... " + _board.getPlayerA().getName() + " gets another turn.");
                    break;
                case GAME_OVER:
                    _messageManager.gameOver(_board.isDraw(), _board.getWinningPlayer());
                    Log.i(TAG, "Game Over!!!");
                    if(usersConnection != null){
                        usersConnection.stopPings();
                        usersConnection.stopPings();
                    }
                    for (int i = 0; i < _board.getMoves().size(); i++) {
                        Pair<Player, Integer> move = _board.getMoves().get(i);
                        Log.i(TAG, i + ": " + move.second + " -> " + move.first.getName());
                    }
                    ArrayList<PlayerStatistic> playerStatistics = readStats(getApplicationContext());//read the stats
                    //store the leaderboard data for non online games
                    if(!(_board.getPlayerA() instanceof RemoteHuman)){//if the first player isnt a remote human than store data for them
                        updateList(_board.getPlayerA(),playerStatistics);
                    }
                    if(!(_board.getPlayerB() instanceof RemoteHuman)){//if the second player isnt a remote human than store data for them
                        updateList(_board.getPlayerB(),playerStatistics);
                    }
                    try {
                        storeStats(playerStatistics);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Return to main menu after 5 seconds
                    Log.v(TAG,"Finish the game");
                    Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 5000);
                    break;
            }
        }

        msgs.clear();
    }

    /**
     * Highlight all the buttons that need highlighting
     */
    public void setupMove(Side side) {
        for (int i = 0; i < _cupButtons.length; i++) {
            _cupButtons[i].rotateTowards(side);

            if (_board.isOpponentStore(i) || _board.isCurrentPlayersStore(i))
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
            if (_board.isOpponentStore(i) || _board.isCurrentPlayersStore(i))
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
     * @return the name of the other user.
     */
    public static String setUpHostConnection(MenuActivity menuActivity){
        SungkaServer sungkaServer = new SungkaServer(4000,menuActivity);
        sungkaServer.execute();
        try {
            sungkaServer.get();//wait for the connection to be established; returns true if it is set up,else false
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        setConnection(sungkaServer);
        String otherName = "";
        try {
            otherName = sungkaServer.connectToSendNames("Oliver Host");//the name of the current player
            Log.v(TAG,"Other name: "+otherName);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return otherName;
    }

    /**
     * Set up a connection where you join a host
     * @param ip the ip to which you connect to.
     * @return the name of the other Player you are connecting to
     */
    public static String setUpJoinConnection(String ip){
        // In the case of a client connecting to the server, the server needs to be set up before
        SungkaClient sungkaClient = new SungkaClient(ip,4000);//server ip and port need to be inserted by the user
        sungkaClient.execute();                 //this is a test one
        try {
            sungkaClient.get();//wait for the connection to be established; returns true if it is set up, else false
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        setConnection(sungkaClient);
        String otherName = "";
        try {
            otherName = sungkaClient.connectToSendNames("Oliver Join");//the name of the current player
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.v(TAG,"Other Name: "+otherName);
        return otherName;
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
        FileOutputStream fileOutputStream = openFileOutput(fileName,Context.MODE_PRIVATE);
        fileOutputStream.write(data.getBytes());
        Log.v(TAG, "Stored the new stats");
        Log.v(TAG, "Stats:");
        Log.v(TAG, data);
        fileOutputStream.close();
        //replaced the old file with a new one that has the updated data
        Log.v(TAG,"About ot read stats");
        ArrayList<PlayerStatistic> stats = readStats(getApplicationContext());
        for(PlayerStatistic player: stats){
            Log.v(TAG,player.toString());
        }
        Log.v(TAG,"Read stats");
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
        Log.v(TAG,"Made a player Statistic: "+playerStatistic.toString());
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
     * Read the stats stored in the data file "player_statistics"
     * @param context the context the application is currently in (to open the file input)
     * @return an ArrayList of PlayerStatistic objects (can be empty if there is no data)
     */
    public static ArrayList<PlayerStatistic> readStats(Context context) {
        FileInputStream fileInputStream;
        ArrayList<PlayerStatistic> playerStatistics = new ArrayList<PlayerStatistic>();
        String textInFile = "";
        int n;
        try {
            fileInputStream = context.openFileInput(fileName);
            while ((n = fileInputStream.read()) != -1) {
                textInFile += Character.toString((char) n);
            }
            Log.v(TAG,textInFile);
            if(!textInFile.equals("")) {
                String[] players = textInFile.split("\n");
                for (int i = 0; i < players.length; i++) {
                    Log.v(TAG,players[i]);
                    String[] info = players[i].split(",");//to get the individual data of a PlayerStatistic
                    String playerName = info[0];
                    String gamesPlayed = info[1];
                    String gamesWon = info[2];
                    String gamesLost = info[3];
                    String gamesDrawn = info[4];
                    String avgTimeInMillis = info[5];
                    String maxShellCollected = info[6];
                    String maxConsecutiveMoves = info[7];
                    PlayerStatistic playerStatistic = new PlayerStatistic(playerName);//make a PlayerStatistic object with the
                    playerStatistic.setGamesPlayed(new Integer(gamesPlayed));   //data we just obtained
                    playerStatistic.setGamesWon(new Integer(gamesWon));
                    playerStatistic.setGamesLost(new Integer(gamesLost));
                    playerStatistic.setGamesDrawn(new Integer(gamesDrawn));
                    playerStatistic.setAverageMoveTimeInMillis(new Double(avgTimeInMillis));
                    playerStatistic.setMaxNumShellsCollected(new Double(maxShellCollected));
                    playerStatistic.setMaxConsecutiveMoves(new Double(maxConsecutiveMoves));
                    playerStatistics.add(playerStatistic);
                    Log.v(TAG,playerStatistic.toString());
                }
                Log.v(TAG,"Read in the file");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return playerStatistics;
    }




}
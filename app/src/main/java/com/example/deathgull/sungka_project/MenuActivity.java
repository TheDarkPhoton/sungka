package com.example.deathgull.sungka_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.content.res.ResourcesCompat;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import helpers.frontend.CupButton;
import helpers.frontend.MusicService;

public class MenuActivity extends Activity {
    private static final String TAG = "MenuActivity";
    private Bundle bundle;
    private boolean _switchingActivities;
    public static Vibrator vb;
    private Drawable[] _muteIcons;
    private int _muteIndex;
    
    //Main menu elements
    private RelativeLayout _mainMenu;
    private Button _play, _leaderboard, _mute, _help;

    //Sub menu elements
    private RelativeLayout _alpha;
    private RelativeLayout _subMenu;
    private Button _previous;
    private EditText _player1Name;
    private TextView _VS;
    private LinearLayout _layoutBase, _layoutPlayer, _layoutAi, _layoutRemote, _layoutHost, _layoutJoin;

    //Base elements
    private Button _btnPlayer, _btnAi, _btnRemote;

    //Player elements
    private EditText _player2Name;
    private Button _btnPlayerPlay;

    //Ai elements
    private SeekBar _aiDiff;
    private TextView _aiDiffText;
    private Button _btnAiPlay;

    //Remote elements
    private Button _btnHost, _btnJoin;

    //Host elements
    private TextView _ipAddress, _waiting;

    //Join elements
    private TextView _ipAddressToJoin;
    private Button _btnJoinIpAddress;

    //Other variables
    private int _index, _prevIndex;

    private int _sWidth, _sHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        vb  = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        _muteIcons = new Drawable[] {
                ResourcesCompat.getDrawable(getResources(), R.drawable.mute, null),
                ResourcesCompat.getDrawable(getResources(), R.drawable.unmute, null)
        };
        _muteIndex = 1;

        getScreenSize();
        getElements();
        scale();
        initialise();

        _switchingActivities = false;

        startMusic();
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * retrieves all the menu elements for use
     */
    private void getElements() {
        _mainMenu = (RelativeLayout) findViewById(R.id.mainMenu);
        _play = (Button) findViewById(R.id.btnPlay);
        _leaderboard = (Button) findViewById(R.id.btnLeaderboard);
        _mute = (Button) findViewById(R.id.btnMute);
        _help = (Button) findViewById(R.id.btnHelp);

        _alpha = (RelativeLayout) findViewById(R.id.alphaLayer);
        _subMenu = (RelativeLayout) findViewById(R.id.playSub);
        _previous = (Button) findViewById(R.id.btnPrevious);
        _player1Name = (EditText) findViewById(R.id.txtPlayerName);
        _VS = (TextView) findViewById(R.id.txtVS);

        _layoutBase = (LinearLayout) findViewById(R.id.layoutBase);
        _layoutPlayer = (LinearLayout) findViewById(R.id.layoutPlayer);
        _layoutAi = (LinearLayout) findViewById(R.id.layoutAi);
        _layoutRemote = (LinearLayout) findViewById(R.id.layoutRemote);
        _layoutHost = (LinearLayout) findViewById(R.id.layoutHost);
        _layoutJoin = (LinearLayout) findViewById(R.id.layoutJoin);

        _btnPlayer = (Button) findViewById(R.id.btnPlayer);
        _btnAi = (Button) findViewById(R.id.btnAi);
        _btnRemote = (Button) findViewById(R.id.btnRemote);

        _player2Name = (EditText) findViewById(R.id.txtPlayerName2);
        _btnPlayerPlay = (Button) findViewById(R.id.btnPlayerPlay);

        _aiDiff = (SeekBar) findViewById(R.id.seekAiDiff);
        _aiDiffText = (TextView) findViewById(R.id.txtAiDiffText);
        _btnAiPlay = (Button) findViewById(R.id.btnAiPlay);

        _btnHost = (Button) findViewById(R.id.btnHost);
        _btnJoin = (Button) findViewById(R.id.btnJoin);

        _ipAddress = (TextView) findViewById(R.id.txtIPAddress);
        _waiting = (TextView) findViewById(R.id.txtWaiting);

        _ipAddressToJoin = (EditText) findViewById(R.id.txtIpEntry);
        _btnJoinIpAddress = (Button) findViewById(R.id.btnRemoteJoin);
    }

    /**
     * scale elements to better fit smaller screens
     */
    private void scale() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int _width = displayMetrics.widthPixels;

        if(_width - 300 < 550) {
            _VS.setTextSize(20);
        }
    }

    /**
     * sets up all listeners for menu button presses
     */
    private void initialise() {
        _index = 0;
        _prevIndex = 0;

        final String placeHolder = getResources().getString(R.string.str_NameHolder);

        _leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                _switchingActivities = true;
                vb.vibrate(25);
                Intent intent = new Intent(MenuActivity.this, StatisticsActivity.class);
                String file = null;
                intent.putExtra(StatisticsActivity.DATA_FILE, file);
                startActivity(intent);
            }
        });

        _play.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                _index = 1;
                _prevIndex = 0;
                vb.vibrate(25);
                updateView();
            }
        });

        _btnPlayer.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Log.d(TAG, "_btnPlayer");
                _index = 2;
                _prevIndex = 1;
                vb.vibrate(25);
                updateView();
            }
        });

        _btnAi.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Log.d(TAG, "_btnAi");
                _index = 3;
                _prevIndex = 1;
                vb.vibrate(25);
                updateView();
            }
        });

        _btnRemote.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Log.d(TAG, "_btnRemote");
                _index = 4;
                _prevIndex = 1;
                vb.vibrate(25);
                updateView();
            }
        });

        _btnHost.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                _index = 5;
                _prevIndex = 4;
                vb.vibrate(25);
                updateView();

                //handle mutliplayer hosting methods.
                //use _ipAddress to show user ip address
                _ipAddress.setText(getIp());
                
                //to pass to the game activity that it will be a online game
                String firstPlayerName = _player1Name.getText().toString();
                bundle.putBoolean(GameActivity.IS_ONLINE, true);
                bundle.putString(GameActivity.PLAYER_ONE, firstPlayerName);
                
                Log.v(TAG, "Setting up Host Connection");
                GameActivity.setUpHostConnection(MenuActivity.this, firstPlayerName);
                
                //use _waiting to show when waiting, and update when connection established
               /* String otherUserName = GameActivity.setUpHostConnection(MenuActivity.this,firstPlayerName);
                //could maybe have a timeout if no connection is established
                //passing the names of the players to the main activity

                bundle.putString("secondName",otherUserName);
                //send them to the GameActivity
                Intent intent = new Intent(MenuActivity.this,GameActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);*/
            }
        });

        _btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                _index = 6;
                _prevIndex = 4;
                vb.vibrate(25);
                updateView();
            }
        });

        _previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _index = _prevIndex;
                vb.vibrate(25);
                updateView();
                if (_index == 1) {
                    _prevIndex = 0;
                }
                if (_index == 4) {
                    _prevIndex = 1;
                }
                //if its hosting
                if (GameActivity.getUsersConnection() != null) {
                    GameActivity.getUsersConnection().cancel(true);//if its hosting a game and you press back, cancel it
                    _waiting.setText(R.string.str_Waiting);//set the text back to its original state
                }
            }
        });

        _aiDiff.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                _aiDiffText.setText(String.valueOf(progress + 50));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        _player1Name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String holder = getResources().getString(R.string.str_NameHolder);
                if (hasFocus && _player1Name.getText().toString().equals(holder)) {
                    _player1Name.setText("");
                }
            }
        });

        _player2Name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String holder = getResources().getString(R.string.str_NameHolder);
                if (hasFocus && _player2Name.getText().toString().equals(holder)) {
                    _player2Name.setText("");
                }
            }
        });

        // starts a human vs human game
        _btnPlayerPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vb.vibrate(25);
                String player1Name = _player1Name.getText().toString();
                String player2Name = _player2Name.getText().toString();

                if (player1Name.equals(placeHolder) || player1Name.equals("")) {
                    makeDialog(R.string.msg_Player1Name, Gravity.CENTER, 25, false);
                } else if(player2Name.equals(placeHolder) || player2Name.equals("")) {
                    makeDialog(R.string.msg_Player2Name, Gravity.CENTER, 25, false);
                } else {
                    //do 2 player play method
                    //use _player1Name as player 1's name, and _player2Name as player 2's name
                    Intent intent = new Intent(v.getContext(), GameActivity.class);
                    bundle.putString(GameActivity.PLAYER_ONE, player1Name);
                    bundle.putString(GameActivity.PLAYER_TWO, player2Name);
                    intent.putExtras(bundle);

                    System.out.println("2 player play");
                    _switchingActivities = true;

                    startActivity(intent);
                }
            }
        });

        // starts a human vs AI game
        _btnAiPlay.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                vb.vibrate(25);
                int difficulty = _aiDiff.getProgress() + 50;
                String player1Name = _player1Name.getText().toString();
                String player2Name = getAiName(difficulty);

                if (player1Name.equals(placeHolder) || player1Name.equals("")) {
                    makeDialog(R.string.msg_PlayerName, Gravity.CENTER, 25, false);
                } else {
                    //do ai play method
                    //use _player1Name as player's name
                    //use _difficulty as ai difficulty
                    System.out.println("ai play, difficulty: " + difficulty);

                    Intent intent = new Intent(v.getContext(), GameActivity.class);
                    bundle.putString(GameActivity.PLAYER_ONE, player1Name);
                    bundle.putString(GameActivity.PLAYER_TWO, player2Name);
                    bundle.putInt(GameActivity.AI_DIFF, difficulty);
                    intent.putExtras(bundle);
                    _switchingActivities = true;
                    startActivity(intent);
                }
            }
        });

        _btnJoinIpAddress.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                vb.vibrate(25);
                String firstPlayerName = _player1Name.getText().toString();
                String ipAddress = _ipAddressToJoin.getText().toString();
                if (firstPlayerName.equals(placeHolder) || firstPlayerName.equals("")) {
                    makeDialog(R.string.msg_PlayerName, Gravity.CENTER, 25, false);
                } else if(ipAddress.equals(placeHolder) || ipAddress.equals("")) {
                    makeDialog(R.string.msg_IPAddress, Gravity.CENTER, 25, false);
                } else {
                    //do remote play method
                    //use _player1Name as player 1's name
                    //use ipAddresToJoin as the IP address to try to connect to
                    bundle.putString(GameActivity.PLAYER_ONE, firstPlayerName);
                    bundle.putBoolean(GameActivity.IS_ONLINE, true);

                    GameActivity.setUpJoinConnection(MenuActivity.this, ipAddress, firstPlayerName);
                    System.out.println("remote play (join)");
                }
            }
        });

        _mute.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                vb.vibrate(25);
                if(_muteIndex == 1) {
                    _muteIndex = 0;
                } else {
                    _muteIndex = 1;
                }
                _mute.setBackground(_muteIcons[_muteIndex]);

                if(musicService.isMuted()) {
                    musicService.unmuteMusic();
                } else {
                    musicService.muteMusic();
                }
            }
        });

        _help.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                vb.vibrate(25);
                makeDialog(R.string.msg_Help, Gravity.LEFT, 15, true);
            }
        });
    }

    private void getScreenSize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        _sWidth = displayMetrics.widthPixels;
        _sHeight = displayMetrics.heightPixels;
    }

    private void makeDialog(int message, int gravity, float textSize, boolean changeSize) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this).setMessage(message);
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
        msg.setGravity(gravity); msg.setTextColor(Color.BLACK); msg.setTextSize(textSize);    }

    /**
     * updates the menu to show and hide elements
     */
    private void updateView() {
        switch(_index) {
            case 0:
                _mainMenu.setVisibility(View.VISIBLE);
                _alpha.setVisibility(View.GONE);
                _subMenu.setVisibility(View.GONE);
                _layoutBase.setVisibility(View.GONE);
                _layoutPlayer.setVisibility(View.GONE);
                _layoutAi.setVisibility(View.GONE);
                _layoutRemote.setVisibility(View.GONE);
                _layoutHost.setVisibility(View.GONE);
                _layoutJoin.setVisibility(View.GONE);
                break;
            case 1:
                _mainMenu.setVisibility(View.GONE);
                _alpha.setVisibility(View.VISIBLE);
                _subMenu.setVisibility(View.VISIBLE);
                _layoutBase.setVisibility(View.VISIBLE);
                _layoutPlayer.setVisibility(View.GONE);
                _layoutAi.setVisibility(View.GONE);
                _layoutRemote.setVisibility(View.GONE);
                _layoutHost.setVisibility(View.GONE);
                _layoutJoin.setVisibility(View.GONE);
                break;
            case 2:
                _mainMenu.setVisibility(View.GONE);
                _alpha.setVisibility(View.VISIBLE);
                _subMenu.setVisibility(View.VISIBLE);
                _layoutBase.setVisibility(View.GONE);
                _layoutPlayer.setVisibility(View.VISIBLE);
                _layoutAi.setVisibility(View.GONE);
                _layoutRemote.setVisibility(View.GONE);
                _layoutHost.setVisibility(View.GONE);
                _layoutJoin.setVisibility(View.GONE);
                break;
            case 3:
                _mainMenu.setVisibility(View.GONE);
                _alpha.setVisibility(View.VISIBLE);
                _subMenu.setVisibility(View.VISIBLE);
                _layoutBase.setVisibility(View.GONE);
                _layoutPlayer.setVisibility(View.GONE);
                _layoutAi.setVisibility(View.VISIBLE);
                _layoutRemote.setVisibility(View.GONE);
                _layoutHost.setVisibility(View.GONE);
                _layoutJoin.setVisibility(View.GONE);
                break;
            case 4:
                _mainMenu.setVisibility(View.GONE);
                _alpha.setVisibility(View.VISIBLE);
                _subMenu.setVisibility(View.VISIBLE);
                _layoutBase.setVisibility(View.GONE);
                _layoutPlayer.setVisibility(View.GONE);
                _layoutAi.setVisibility(View.GONE);
                _layoutRemote.setVisibility(View.VISIBLE);
                _layoutHost.setVisibility(View.GONE);
                _layoutJoin.setVisibility(View.GONE);
                break;
            case 5:
                _mainMenu.setVisibility(View.GONE);
                _alpha.setVisibility(View.VISIBLE);
                _subMenu.setVisibility(View.VISIBLE);
                _layoutBase.setVisibility(View.GONE);
                _layoutPlayer.setVisibility(View.GONE);
                _layoutAi.setVisibility(View.GONE);
                _layoutRemote.setVisibility(View.GONE);
                _layoutHost.setVisibility(View.VISIBLE);
                _layoutJoin.setVisibility(View.GONE);
                break;
            case 6:
                _mainMenu.setVisibility(View.GONE);
                _alpha.setVisibility(View.VISIBLE);
                _subMenu.setVisibility(View.VISIBLE);
                _layoutBase.setVisibility(View.GONE);
                _layoutPlayer.setVisibility(View.GONE);
                _layoutAi.setVisibility(View.GONE);
                _layoutRemote.setVisibility(View.GONE);
                _layoutHost.setVisibility(View.GONE);
                _layoutJoin.setVisibility(View.VISIBLE);
                break;
        }
    }

    private String getAiName(int difficulty) {
        String[] aiNames = getResources().getStringArray(R.array.ai_names);

        if (difficulty < 70) {
            return aiNames[0];
        } else if (difficulty < 90) {
            return  aiNames[1];
        } else {
            return aiNames[2];
        }
    }

    public void connectionHasEstablished(){
        _waiting.setText(R.string.str_ConnectionEst);
    }

    /**
     * Get the IP of the current device
     * @return the IPv4 of the device
     */
    public String getIp(){
        //To get the ip
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        //noinspection deprecation
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        Log.v(TAG, "ip: " + ip);
        return ip;
    }

    public void setSecondPlayerName(String secondPlayerName) {
        Log.v(TAG,"Got the second player name "+secondPlayerName);
        bundle.putString(GameActivity.PLAYER_TWO, secondPlayerName);
    }

    public void startGameActivity(){
        _switchingActivities = true;
        Log.v(TAG,"Starting the GameActivity");
        Intent intent = new Intent(MenuActivity.this,GameActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * binds to music service and starts playing
     */
    private void startMusic() {
        doBindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        startService(music);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(musicService != null) {
            musicService.resumeMusic();
        }
        bundle = new Bundle();
        _waiting.setText(R.string.str_Waiting);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(!_switchingActivities) {
            if(musicService != null) {
                musicService.pauseMusic();
            }
        }
        _switchingActivities = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        doUnbindService();
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

    /**
     * Used to show a toast in the menu activity, with the given message
     * @param message the message that you want to show in the Toast
     */
    public void showToast(String message){
        Toast toast = Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT);
        toast.show();
    }
}

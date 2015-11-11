package com.example.deathgull.sungka_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class MenuActivity extends Activity {

    //Main menu elements
    private RelativeLayout _mainMenu;
    private ImageButton _play, _leaderboard;

    //Sub menu elements
    private RelativeLayout _alpha;
    private RelativeLayout _subMenu;
    private Button _previous;
    private TextView _player1Name;
    private LinearLayout _layoutBase, _layoutPlayer, _layoutAi, _layoutRemote, _layoutHost, _layoutJoin;

    //Base elements
    private Button _btnPlayer, _btnAi, _btnRemote;

    //Player elements
    private TextView _player2Name;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        getElements();
        initialise();
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

    private void getElements() {
        _mainMenu = (RelativeLayout) findViewById(R.id.mainMenu);
        _play = (ImageButton) findViewById(R.id.btnPlay);
        _leaderboard = (ImageButton) findViewById(R.id.btnLeaderboard);

        _subMenu = (RelativeLayout) findViewById(R.id.playSub);
        _alpha = (RelativeLayout) findViewById(R.id.alphaLayer);
        _previous = (Button) findViewById(R.id.btnPrevious);
        _player1Name = (TextView) findViewById(R.id.txtPlayerName);

        _layoutBase = (LinearLayout) findViewById(R.id.layoutBase);
        _layoutPlayer = (LinearLayout) findViewById(R.id.layoutPlayer);
        _layoutAi = (LinearLayout) findViewById(R.id.layoutAi);
        _layoutRemote = (LinearLayout) findViewById(R.id.layoutRemote);
        _layoutHost = (LinearLayout) findViewById(R.id.layoutHost);
        _layoutJoin = (LinearLayout) findViewById(R.id.layoutJoin);

        _btnPlayer = (Button) findViewById(R.id.btnPlayer);
        _btnAi = (Button) findViewById(R.id.btnAi);
        _btnRemote = (Button) findViewById(R.id.btnRemote);

        _player2Name = (TextView) findViewById(R.id.txtPlayerName2);
        _btnPlayerPlay = (Button) findViewById(R.id.btnPlayerPlay);

        _aiDiff = (SeekBar) findViewById(R.id.seekAiDiff);
        _aiDiffText = (TextView) findViewById(R.id.txtAiDiffText);
        _btnAiPlay = (Button) findViewById(R.id.btnAiPlay);

        _btnHost = (Button) findViewById(R.id.btnHost);
        _btnJoin = (Button) findViewById(R.id.btnJoin);

        _ipAddress = (TextView) findViewById(R.id.txtIPAddress);
        _waiting = (TextView) findViewById(R.id.txtWaiting);

        _ipAddressToJoin = (TextView) findViewById(R.id.txtIpEntry);
        _btnJoinIpAddress = (Button) findViewById(R.id.btnRemoteJoin);
    }

    private void initialise() {
        _index = 0;
        _prevIndex = 0;

        _leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, StatisticsActivity.class);
                startActivity(intent);
            }
        });

        _play.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                _index = 1;
                _prevIndex = 0;
                updateView();
            }
        });

        _btnPlayer.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                _index = 2;
                _prevIndex = 1;
                updateView();
            }
        });

        _btnAi.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                _index = 3;
                _prevIndex = 1;
                updateView();
            }
        });

        _btnRemote.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                _index = 4;
                _prevIndex = 1;
                updateView();
            }
        });

        _btnHost.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                _index = 5;
                _prevIndex = 4;
                updateView();
            }
        });

        _btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                _index = 6;
                _prevIndex = 4;
                updateView();
            }
        });

        _previous.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                _index = _prevIndex;
                updateView();
                if(_index == 1) {
                    _prevIndex = 0;
                }
                if(_index == 4) {
                    _prevIndex = 1;
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

        _btnPlayerPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_player1Name.getText().toString().equals("...") || _player1Name.getText().toString().equals("")) {
                    new AlertDialog.Builder(MenuActivity.this).setMessage(R.string.msgPlayer1Name).show();
                } else if(_player2Name.getText().toString().equals("...") || _player2Name.getText().toString().equals("")) {
                    new AlertDialog.Builder(MenuActivity.this).setMessage(R.string.msgPlayer2Name).show();
                } else {
                    //do 2 player play method
                    System.out.println("2 player play");
                }
            }
        });

        _btnAiPlay.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if(_player1Name.getText().toString().equals("...") || _player1Name.getText().toString().equals("")) {
                    new AlertDialog.Builder(MenuActivity.this).setMessage(R.string.msgPlayerName).show();
                } else {
                    //do ai play method
                    int _difficulty = _aiDiff.getProgress() + 50;
                    System.out.println("ai play, difficulty: " + _difficulty);
                }
            }
        });

        _btnJoinIpAddress.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if(_player1Name.getText().toString().equals("...") || _player1Name.getText().toString().equals("")) {
                    new AlertDialog.Builder(MenuActivity.this).setMessage(R.string.msgPlayerName).show();
                } else if(_ipAddressToJoin.getText().toString().equals("...") || _ipAddressToJoin.getText().toString().equals("")) {
                    new AlertDialog.Builder(MenuActivity.this).setMessage(R.string.msgIPAddress).show();
                } else {
                    //do remote play method
                    System.out.println("remote play (join)");
                }
            }
        });
    }

    private void updateView() {
        System.out.println(_index + ", prev: " + _prevIndex);
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
}

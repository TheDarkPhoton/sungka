package game.board;

import java.util.Comparator;

import game.player.Player;

public class State {
    private Integer _index;
    private Integer _value;
    private Integer[] _state;
    private Player _player;

    private boolean _extraTurn = false;
    private boolean _deadEnd = false;
    private boolean _goal = false;

    public State(Integer index){
        _index = index;
    }

    public void setPlayer(Player player){
        _player = player;
    }

    public Player getPlayer(){
        return _player;
    }

    public Integer getIndex(){
        return _index;
    }

    public void setValue(int value){
        _value = value;
    }

    public Integer getValue(){
        return _value;
    }

    public void setState(Integer[] state){
        _state = state;
    }

    public Integer[] getState(){
        return _state;
    }

    public void setDeadEnd(){
        _deadEnd = true;
    }

    public boolean isDeadEnd(){
        return _deadEnd;
    }

    public void setExtraTurn(){
        _extraTurn = true;
    }

    public boolean leadsToExtraTurn(){
        return _extraTurn;
    }

    public void setGoal(){
        _goal = true;
    }

    public boolean isGoal(){
        return _goal;
    }

    @Override
    public String toString() {
        return "index:" + _index + " value:" + _value;
    }
}

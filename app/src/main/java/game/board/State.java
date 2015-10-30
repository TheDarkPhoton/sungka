package game.board;

import java.util.Comparator;

import game.player.Player;

/**
 * Created by darkphoton on 30/10/15.
 */
public class State implements Comparator<State> {
    private Integer _index;
    private Integer _value;
    private Integer[] _state;
    private Player _player;

    public State(Player player, Integer index, Integer value, Integer[] state){
        _player = player;
        _index = index;
        _value = value;
        _state = state;
    }

    public Player getPlayer(){
        return _player;
    }

    public Integer getIndex(){
        return _index;
    }

    public Integer getValue(){
        return _value;
    }

    public Integer[] getState(){
        return _state;
    }

    @Override
    public int compare(State lhs, State rhs) {
        return rhs.getValue().compareTo(lhs.getValue());
    }

    @Override
    public String toString() {
        return "index:" + _index + " value:" + _value;
    }
}

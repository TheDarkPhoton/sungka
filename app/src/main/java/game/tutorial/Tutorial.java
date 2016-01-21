package game.tutorial;

import android.util.Pair;

import java.util.ArrayList;

import game.board.Board;
import game.player.AI;
import game.player.Human;

public abstract class Tutorial extends Board {
    protected ArrayList<Pair<Integer, String>> _moves = new ArrayList<>();
    protected int _current_step = 0;

    /**
     * Constructs board with default attributes.
     */
    public Tutorial() {
        super(new Human("Student"), new AI(100,100, "SenseiSato"));
    }

    public boolean isValid(int index, boolean robber){
        if (isActionPresent()){
            return index == getCurrentAction();
        } else {
            return superIsValid(index, robber);
        }
    }

    public boolean isActionPresent(){
        return _current_step < _moves.size() && _moves.get(_current_step).first != null;
    }

    public Integer getCurrentAction(){
        return _moves.get(_current_step).first;
    }

    public boolean isMessagePressent(){
        return _current_step < _moves.size() && _moves.get(_current_step).second != null;
    }

    public String getCurrentMessage(){
        return _moves.get(_current_step).second;
    }

    public void nextStep(){
        ++_current_step;
    }

    protected void setState(Integer[] board){
        for (int i = 0; i < board.length; i++) {
            _cups[i].setShells(board[i]);
        }
    }
}

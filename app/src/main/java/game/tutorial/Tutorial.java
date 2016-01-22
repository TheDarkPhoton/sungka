package game.tutorial;

import android.content.Context;
import android.util.Pair;

import java.util.ArrayList;

import game.board.Board;
import game.player.AI;
import game.player.Human;

public abstract class Tutorial extends Board {
    protected ArrayList<Pair<Integer, Integer>> _moves = new ArrayList<>();
    protected int _current_step = 0;
    protected boolean _tutorial_finished = false;
    protected Context _context;

    /**
     * Constructs board with default attributes.
     */
    public Tutorial(Context context) {
        super(new Human("Student"), new AI(100,100, "SenseiSato"));
        _context = context;
    }

    public boolean isValid(int index, boolean robber){
        if (_tutorial_finished)
            return false;

        if (robber)
            nextStep();

        if (getCurrentPlayer() != null && isActionPresent() && !robber){
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

    public int getCurrentMessage(){
        return _moves.get(_current_step).second;
    }

    public void endTutorial(){
        _tutorial_finished = true;
    }

    public void nextStep(){
        ++_current_step;
    }

    protected void setState(Integer[] board){
        for (int i = 0; i < board.length; i++) {
            _cups[i].setShells(board[i]);
        }
    }

    protected int getStringResourceId(String name){
        String packageName = _context.getPackageName();
        return _context.getResources().getIdentifier(name, "string", packageName);
    }
}

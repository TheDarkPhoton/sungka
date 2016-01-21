package game.tutorial;

import android.util.Pair;

import game.board.Board;

public class ExtraMoves extends Tutorial {
    private final static Integer[] _state = {
            7, 5, 17, 6, 1, 1, 0, 0,
            7, 7, 7, 7, 7, 7, 7, 0
    };

    public ExtraMoves(){
        setCurrentPlayerA();
        setState(_state);

        _moves.add(new Pair<Integer, String>(0, "This tutorial will cover the extra move mechanic. The player receives an extra move if the last shell lands in that players cup. Press on the leftmost cup and observe what happens."));
    }
}

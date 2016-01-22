package game.tutorial;

import android.content.Context;
import android.util.Pair;

public class TutRobbing extends Tutorial {

    public TutRobbing(Context context){
        super(context);

        Integer[] _state = {
                14, 0, 18, 2, 1, 0, 10, 0,
                4, 7, 7, 7, 0, 7, 7, 0
        };

        setCurrentPlayerA();
        setState(_state);

        _moves.add(new Pair<Integer, Integer>(3, getStringResourceId("msg_TutorialRobbing1")));
        _moves.add(new Pair<Integer, Integer>(5, null));
        _moves.add(new Pair<Integer, Integer>(8, null));
        _moves.add(new Pair<Integer, Integer>(12, null));
        _moves.add(new Pair<Integer, Integer>(6, getStringResourceId("msg_TutorialRobbing2")));
        _moves.add(new Pair<Integer, Integer>(1, null));
        _moves.add(new Pair<Integer, Integer>(8, null));
        _moves.add(new Pair<Integer, Integer>(0, getStringResourceId("msg_TutorialRobbing3")));
        _moves.add(new Pair<Integer, Integer>(0, null));
        _moves.add(new Pair<Integer, Integer>(null, null));
        _moves.add(new Pair<Integer, Integer>(null, getStringResourceId("msg_TutorialEnd")));
    }
}

package game.tutorial;

import android.content.Context;
import android.util.Pair;

public class TutRobbing extends Tutorial {
    private final static Integer[] _state = {
            7, 5, 18, 6, 1, 1, 0, 0,
            7, 7, 7, 7, 7, 7, 7, 0
    };

    public TutRobbing(Context context){
        super(context);

        setCurrentPlayerA();
        setState(_state);

        _moves.add(new Pair<Integer, Integer>(0, getStringResourceId("msg_TutorialExtraMoves1")));
    }
}

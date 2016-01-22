package game.tutorial;

import android.content.Context;
import android.util.Pair;

public class TutExtraMoves extends Tutorial {
    private final static Integer[] _state = {
        7, 5, 18, 6, 1, 1, 0, 0,
        7, 7, 7, 7, 7, 7, 7, 0
    };

    public TutExtraMoves(Context context) {
        super(context);

        setCurrentPlayerA();
        setState(_state);

        _moves.add(new Pair<Integer, Integer>(0, getStringResourceId("msg_TutorialExtraMoves1")));
        _moves.add(new Pair<Integer, Integer>(5, getStringResourceId("msg_TutorialExtraMoves2")));
        _moves.add(new Pair<Integer, Integer>(1, getStringResourceId("msg_TutorialExtraMoves3")));
        _moves.add(new Pair<Integer, Integer>(4, null));
        _moves.add(new Pair<Integer, Integer>(5, null));
        _moves.add(new Pair<Integer, Integer>(2, getStringResourceId("msg_TutorialExtraMoves4")));
        _moves.add(new Pair<Integer, Integer>(null, getStringResourceId("msg_TutorialEnd")));
    }
}

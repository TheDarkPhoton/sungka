package helpers.frontend;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import game.player.Player;

/**
 * Created by martinkubat on 10/11/15.
 */
public class PlayerNameTextView extends TextView {

    private Player _player;
    private FrameLayout.LayoutParams _layoutParams;

    public PlayerNameTextView(Context context, Player player) {
        super(context);

        _player = player;

        _layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(_layoutParams);

        setText(player.getName());
        setTextSize(20.0f);
        setPadding(20, 20, 20, 20);

        if (_player.getBoard().isPlayerA(_player)) {
            _layoutParams.gravity = Gravity.BOTTOM;
            setTextColor(Color.WHITE);
            setGravity(Gravity.RIGHT);
        } else {
            _layoutParams.gravity = Gravity.TOP;
            setTextColor(Color.BLACK);
            setGravity(Gravity.LEFT);
        }

    }

}

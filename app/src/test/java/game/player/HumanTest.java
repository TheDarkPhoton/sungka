package game.player;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.util.Log;

import org.junit.Before;
import org.junit.Test;

import game.board.Board;
import game.cup.Cup;
import game.cup.PlayerCup;

/**
 * Test the Human class
 */
public class HumanTest {
    private static final String TAG = "HumanTest";

    private Board board;
    private Player playerA;
    private PlayerActionListener playerActionListener;
    private Cup cup;

    @Before
    public void setup() {
        playerActionListener = mock(PlayerActionAdapter.class);
        cup = mock(PlayerCup.class);

        playerA = new Human("Automated Test Human");
        playerA.setPlayerActionListener(playerActionListener);
    }

    /**
     * Make sure that calling moveStart results in a call to method onMoveStart of the player's
     * PlayerActionAdapter.
     */
    @Test
    public void testMoveStart() {
        playerA.moveStart();
        verify(playerActionListener).onMoveStart(playerA);
    }

    /**
     * Make sure that the player cannot perform a move without moveStart being called first.
     */
    @Test
    public void testCannotMove() {
        playerA.move(0);
        verifyZeroInteractions(playerActionListener);
    }

    /**
     * Make sure that calling moveStart then move results in a call to method onMove of the player's
     * PlayerActionAdapter.
     */
    @Test
    public void testProperMoveOrder() {
        int index = 0;
        playerA.moveStart();
        playerA.move(index);
        verify(playerActionListener).onMove(playerA, index);
    }

    /**
     * Make sure that the completion of a move results in a MoveInfo being created for that move
     */
    @Test
    public void testMoveInfoStored() {
        int shellCount = 5;
        when(cup.getCount()).thenReturn(shellCount);

        playerA.bindStore(cup);
        playerA.moveStart();

        try {
            Thread.sleep(1000, 0);
        } catch (InterruptedException e) {
            Log.w(TAG, "testMoveInfoStored: thread was interrupted");
        }

        playerA.moveEnd();

        assertTrue(playerA.get_moveInfos().size() == 1);

        MoveInfo m = playerA.get_moveInfos().get(0);
        String[] moveTimes = m.getDurationOfMove().split(":");

        int minutes = Integer.valueOf(moveTimes[0]);
        int seconds = Integer.valueOf(moveTimes[1]);

        assertEquals(0, minutes);
        assertTrue(seconds >= 1);
        assertEquals(shellCount, m.getNumOfShellsCollected());
    }
}

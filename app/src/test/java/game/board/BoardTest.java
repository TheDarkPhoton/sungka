package game.board;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import game.player.Player;
import game.cup.Cup;

/**
 * Test the Board class.
 */
public class BoardTest {

    private Board board;
    private Player playerA;
    private Player playerB;

    @Before
    public void setup() {
        playerA = mock(Player.class);
        playerB = mock(Player.class);
        board = new Board(playerA, playerB);
        board.setCurrentPlayerA();
    }

    /**
     * When PlayerA moves, and the last shell doesn't fall in his Store, then
     * change current player to PlayerB.
     */
    @Test
    public void testChangePlayer() {
        when(playerA.hasValidMove()).thenReturn(true);
        when(playerB.hasValidMove()).thenReturn(true);

        Player p = board.nextPlayersMove(playerA, 0);

        assertEquals(playerB, p);
    }

    /**
     * When PlayerA moves, and the last shell falls in his store, then don't
     * change player.
     */
    @Test
    public void testNotChangePlayer() {
        when(playerA.hasValidMove()).thenReturn(true);
        when(playerB.hasValidMove()).thenReturn(true);
        when(playerA.isPlayersCup(any(Cup.class), eq(true))).thenReturn(true);

        Player p = board.nextPlayersMove(playerA, 0);

        assertEquals(playerA, p);
    }

    /**
     * When all the regular cups are empty, even if the last shell fell in the player's store,
     * ensure that the Board prepares itself for game-over.
     */
    @Test
    public void testNoMovesGameEnds1() {
        when(playerA.hasValidMove()).thenReturn(false);
        when(playerB.hasValidMove()).thenReturn(false);
        when(playerA.isPlayersCup(any(Cup.class), eq(true))).thenReturn(true);

        Player p = board.nextPlayersMove(playerA, 0);

        assertNull(p);
        assertFalse(board.hasValidMoves());
    }

    /**
     * When all the regular cups are empty, ensure that the Board prepares itself for game-over.
     */
    @Test
    public void testNoMovesGameEnds2() {
        when(playerA.hasValidMove()).thenReturn(false);
        when(playerB.hasValidMove()).thenReturn(false);
        when(playerA.isPlayersCup(any(Cup.class), eq(false))).thenReturn(false);

        Player p = board.nextPlayersMove(playerA, 0);

        assertNull(p);
        assertFalse(board.hasValidMoves());
    }

    /**
     * Make sure that Board.nextPlayersMove() changes the current player.
     */
    @Test
    public void testNextPlayersMove() {
        board.nextPlayersMove();

        assertEquals(playerB, board.getCurrentPlayer());
    }

    /**
     * Make sure that a null HandOfShells is returned when the specified cup is a Store.
     */
    @Test
    public void testCantPickUpFromStore() {
        when(playerA.isPlayersCup(any(Cup.class))).thenReturn(true);

        assertNull(board.pickUpShells(anyInt()));
    }
}

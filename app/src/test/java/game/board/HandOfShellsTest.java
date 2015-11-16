package game.board;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;

import game.player.Player;
import game.cup.Cup;

/**
 * Test the HandOfShells class.
 */
public class HandOfShellsTest {

    private HandOfShells hand;
    private Player playerA;
    private Player playerB;
    private Board board;

    private ArrayList<BoardState> states;

    @Before
    public void setup() {
        playerA = mock(Player.class);
        playerB = mock(Player.class);
        board = mock(Board.class);

        states = new ArrayList<>();
    }

    @Test
    public void testGetNextCup() {
        hand = new HandOfShells(playerA, 0, 7);
        hand.bindBoard(board);

        when(board.getOpponent()).thenReturn(playerA);
        when(playerA.isPlayersCup(any(Cup.class), eq(true)))
                .thenReturn(false)
                .thenReturn(false)
                .thenReturn(true);

        // first test normal (non-edge) use
        hand.setNextCup(3);
        assertEquals(4, hand.getNextCup());

        // pass over end of array
        hand.setNextCup(15);
        assertEquals(0, hand.getNextCup());

        // pass over opponent store and end of array
        hand.setNextCup(14);
        assertEquals(0, hand.getNextCup());
    }

    /**
     * Make sure that a Hand that drops its last shell robs the opponent.
     */
    @Test
    public void testDropShellRob() {
        // hand with only one shell
        hand = new HandOfShells(playerA, 0, 1);
        hand.bindBoard(board);

        Cup cup = mock(Cup.class);

        when(cup.getCount()).thenReturn(1);
        when(board.getCup(anyInt())).thenReturn(cup);
        when(board.isPlayerA(any(Player.class))).thenReturn(true);
        when(board.pickUpShells(anyInt(), eq(true))).thenReturn(new HandOfShells(playerB, 14, 7));
        when(board.getPlayerB()).thenReturn(playerB);
        when(playerA.isPlayersCup(any(Cup.class), eq(true))).thenReturn(false);
        when(playerA.isPlayersCup(any(Cup.class))).thenReturn(true);
        when(playerA.hasValidMove()).thenReturn(true);
        when(playerB.hasValidMove()).thenReturn(true);

        setupGrabStateMessages();

        HandOfShells newHand = hand.dropShell();

        assertNotNull(newHand);
        assertTrue(states.contains(BoardState.PLAYER_B_WAS_ROBBED));
        assertTrue(states.contains(BoardState.PLAYER_B_TURN));
    }

    /**
     * Make sure that the player gets another turn when the Hand drops is last shell
     * into that player's store.
     */
    @Test
    public void testDropShellStore() {
        // hand with only one shell
        hand = new HandOfShells(playerA, 7, 1);
        hand.bindBoard(board);

        Cup cup = mock(Cup.class);

        when(cup.getCount()).thenReturn(1);
        when(board.getCup(anyInt())).thenReturn(cup);
        when(board.isPlayerA(any(Player.class))).thenReturn(true);
        when(board.getPlayerB()).thenReturn(playerB);
        when(board.getOpponent()).thenReturn(playerB);
        when(board.hasValidMoves()).thenReturn(true);
        when(board.nextPlayersMove(any(Player.class), anyInt())).thenReturn(playerA);
        when(playerA.isPlayersCup(any(Cup.class), eq(true))).thenReturn(true);
        when(playerA.isPlayersCup(any(Cup.class))).thenReturn(true);
        when(playerA.hasValidMove()).thenReturn(true);
        when(playerB.hasValidMove()).thenReturn(true);

        setupGrabStateMessages();

        HandOfShells newHand = hand.dropShell();

        assertNull(newHand);
        assertTrue(states.contains(BoardState.PLAYER_A_GETS_ANOTHER_TURN));
    }

    public void setupGrabStateMessages() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                states.add((BoardState) args[0]);
                return null;
            }
        }).when(board).addStateMessage(any(BoardState.class));
    }
}

package game.cup;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import game.player.Player;

/**
 * Test the Cup, PlayerCup, and ShellCup classes.
 */
public class CupTest {

    private Player player;
    private ShellCup sCup;
    private PlayerCup pCup;

    @Before
    public void setup() {
        player = mock(Player.class);
        sCup = new ShellCup(7);
        pCup = new PlayerCup(player);
    }

    /**
     * Make sure that the cup is empty after its shells have been picked up.
     */
    @Test
    public void testPickUpShells() {
        int shells = sCup.pickUpShells();

        assertEquals(7, shells);
        assertTrue(sCup.isEmpty());
    }

    /**
     * Make sure that isNotPlayerCup hasn't been confused between classes.
     */
    @Test
    public void testIsPlayerCup() {
        assertTrue(sCup.isNotPlayerCup());
        assertFalse(pCup.isNotPlayerCup());
    }

    /**
     * Make sure that clone() produces a ShellCup with identical state.
     */
    @Test
    public void testShellCupClone() {
        ShellCup sCupClone;
        try {
            sCupClone = (ShellCup) sCup.clone();
        } catch (CloneNotSupportedException e) {
            sCupClone = null;
        }

        assertNotNull(sCupClone);
        assertEquals(sCup.getCount(), sCupClone.getCount());
    }

    /**
     * Make sure that clone() produces a PlayerCup with identical state.
     */
    @Test
    public void testPlayerCupClone() {
        pCup.addShells(10);

        PlayerCup pCupClone;
        try {
            pCupClone = (PlayerCup) pCup.clone();
        } catch (CloneNotSupportedException e) {
            pCupClone = null;
        }

        assertNotNull(pCupClone);
        assertEquals(pCup.getCount(), pCupClone.getCount());
        assertEquals(pCup.getPlayer(), pCupClone.getPlayer());
    }

}

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.*;


public class TestingTest {
    @Test
    public void myTest1() {
        int x = 3 * 4;
        Board board = mock(Board.class);
        when(board.isOpponentStore()).thenReturn(false);

        assertTrue(x == 12);
    }

    @Test
    public void myTest2() {
        int y = 4 * 5;
        assertFalse(y == 10);
    }
}

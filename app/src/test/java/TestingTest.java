import static org.junit.Assert.*;

import org.junit.*;

public class TestingTest {
    @Test
    public void myTest1() {
        int x = 3 * 4;
        assertTrue(x == 12);
    }

    @Test
    public void myTest2() {
        int y = 4 * 5;
        assertFalse(y == 10);
    }
}

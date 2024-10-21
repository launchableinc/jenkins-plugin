package test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {

    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @Test
    public void failingTest() {
        System.err.println("stderr output");
        assertTrue(false);
    }

    @Test
    public void errorTest() {
        System.out.println("stdout output");
        throw new IllegalArgumentException("Some unexpected error");
    }

    @Disabled
    public void ignoredTest() {}
}

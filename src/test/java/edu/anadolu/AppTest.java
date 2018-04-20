package edu.anadolu;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static edu.anadolu.App.analyze;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() throws IOException {
        String input = "addiu $sp,$sp,-32\n" +
                "sw $fp,28($sp)\n" +
                "move $fp,$sp\n" +
                "sw $4,32($fp)\n" +
                "sw $5,36($fp)\n" +
                "sw $0,8($fp)\n" +
                "$L3:\n" +
                "lw $2,8($fp)\n" +
                "nop\n" +
                "sltu $2,$2,4\n" +
                "beq $2,$0,$L2\n" +
                "nop\n";

        analyze(Indexer.shingle(), input);

        assertTrue(true);

        int code = (int) '|';
        System.out.println(code);

        int c = Character.codePointAt(new char[]{'|'}, 0);
        System.out.println(c);
        assertEquals(code, c);

    }
}

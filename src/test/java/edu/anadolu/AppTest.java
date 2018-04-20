package edu.anadolu;

import org.junit.Test;

import java.io.IOException;

import static edu.anadolu.App.analyze;
import static org.junit.Assert.assertEquals;

/**
 * Unit test for simple App.
 */
public class AppTest {
    @Test
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

        int code = (int) '|';
        System.out.println(code);

        int c = Character.codePointAt(new char[]{'|'}, 0);
        System.out.println(c);
        assertEquals(code, c);

    }
}

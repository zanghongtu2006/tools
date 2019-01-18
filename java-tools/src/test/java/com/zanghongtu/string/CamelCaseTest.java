package com.zanghongtu.string;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author : Hongtu Zang
 * @date : Created in 上午9:06 19-1-17
 */
public class CamelCaseTest {
    @Test
    public void testToCamelCase() {
        String orig0 = "hello_world";
        String orig1 = "helloWorld";
        String orig2 = "hello-world";
        String orig3 = "hello-world-2";
        String orig4 = "_hello-world-2";

        assertEquals("Not the same", "helloWorld", CamelCase.toCamelCase(orig0));
        assertEquals("Not the same", "helloWorld", CamelCase.toCamelCase(orig1));
        assertEquals("Not the same", "helloWorld", CamelCase.toCamelCase(orig2));
        assertEquals("Not the same", "helloWorld2", CamelCase.toCamelCase(orig3));
        assertEquals("Not the same", "helloWorld2", CamelCase.toCamelCase(orig4));
    }
}

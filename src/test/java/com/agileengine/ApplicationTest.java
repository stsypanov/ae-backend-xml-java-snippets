package com.agileengine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationTest {

    @Test
    void diff1() {
        final String path = new Application().getPathToButton("original.html", "diff1.html");
        assertEquals("html > body > div > div > div > div > div > div > a", path);
    }

    @Test
    void diff2() {
        final String path = new Application().getPathToButton("original.html", "diff2.html");
        assertEquals("html > body > div > div > div > div > div > div > div > a", path);
    }

    @Test
    void diff3() {
        final String path = new Application().getPathToButton("original.html", "diff3.html");
        assertEquals("html > body > div > div > div > div > div > div > a", path);
    }

    @Test
    void diff4() {
        final String path = new Application().getPathToButton("original.html", "diff4.html");
        assertEquals("html > body > div > div > div > div > div > div > a", path);
    }

}
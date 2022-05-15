// $Id: TestTextUI.java 348 2010-09-22 17:30:40Z charpov $
package test;

import guess.*;
import java.util.Set;
import java.io.StringWriter;
import java.io.StringReader;
import java.io.IOException;
import java.nio.CharBuffer;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestTextUI {

    Guesser<String> g;
    StringWriter w;
	static double score;

    @Before
    public void BEFORE() {
        w = new StringWriter(4096);
    }

    @After
    public void AFTER() {
        g = null;
    }

    @BeforeClass
    public static void BEFORECLASS() {
		score = 0;
    }

    @AfterClass
    public static void AFTERCLASS() {
        System.out.println("\nScore: " + score);
    }

    @Test(timeout=1000) //small test: 10 answers
        public void testUI1 () {
        g = new SillyGuesser(10);
        StringBuilder b = new StringBuilder();
        while (b.length() < 21){
            b.append("n\ny\n");
        }
        int n = new TextUI(g, new StringReader(b.toString()), w).play();
        assertEquals(n, 1);
        assertTrue(w.toString().contains("YES:5 NO:5"));
		score += 5;
    }

    @Test(timeout=1000) //small test: 10 answers, 2 games
        public void testUI2 () {
        g = new SillyGuesser(10);
        StringBuilder b = new StringBuilder();
        while (b.length() < 43){
            b.append("y\nn\n");
        }
        int n = new TextUI(g, new StringReader(b.toString()), w).play();
        assertEquals(n, 2);
        assertTrue(w.toString().contains("YES:5 NO:5"));
		score += 5;
    }

    @Test(timeout=60000) //large test: 1000000 answers
        public void testUI3 () {
        g = new SillyGuesser(1000000);
        StringBuilder b = new StringBuilder();
        while (b.length() < 2000001){
            b.append("y\nn\nn\n");
        }
        int n = new TextUI(g, new StringReader(b.toString()), w).play();
        assertEquals(n, 1);
        assertTrue(w.toString().contains("YES:333334 NO:666666"));
		score += 5;
    }

    @Test(timeout=60000) //large test: 1000 answers, 1000 games
        public void testUI4 () {
        g = new SillyGuesser(1000);
        StringBuilder b = new StringBuilder();
        int i = 0;
        while (b.length() < 2001995){
            b.append("y\n");
        }
        b.append("n\n");
        b.append("n\n");
        int n = new TextUI(g, new StringReader(b.toString()), w).play();
        assertEquals(n, 1000);
        assertTrue(w.toString().contains("YES:999 NO:1"));
		score += 5;
    }

    @Test(timeout=1000) //game solved with no questions
        public void testUI5 () {
        g = new SillyGuesser(0);
        int n = new TextUI(g, new StringReader("n\n"), w).play();
        assertEquals(n, 1);
        assertTrue(w.toString().contains("YES:0 NO:0"));
		score += 5;
    }

    @Test(timeout=1000) //small test w/ garbage answers
        public void testUI6 () {
        g = new SillyGuesser(10);
        StringBuilder b = new StringBuilder();
        while (b.length() < 80){
            b.append("n\ne\nw\nn\ny\nt\ny\n");
        }
        int n = new TextUI(g, new StringReader(b.toString()), w).play();
        assertEquals(n, 2);
        assertTrue(w.toString().contains("YES:5 NO:5"));
		score += 5;
    }
}

//class SillyGuesser implements Guesser<String> {
//
//    private int yes, no, count;
//    private boolean q;
//
//    public SillyGuesser (int n) {
//        count = n;
//        yes = -1;
//    }
//
//    public String initialize () {
//        yes = no = 0;
//        q = true;
//        return "Let's go!";
//    }
//
//    public boolean hasSolved () {
//        if (yes < 0)
//            throw new IllegalStateException();
//        return yes + no == count;
//    }
//
//    public String getSecret () {
//        if (yes < 0 || !hasSolved())
//            throw new IllegalStateException();
//        return "YES:" + yes + " NO:" + no;
//    }
//
//    public void yes () {
//        if (yes < 0 || q)
//            throw new IllegalStateException();
//        yes++;
//        q = true;
//    }
//
//    public void no () {
//        if (yes < 0 || q)
//            throw new IllegalStateException();
//        no++;
//        q = true;
//    }
//
//    public String makeQuestion () {
//        if (yes < 0 || hasSolved() || !q)
//            throw new IllegalStateException();
//        q = false;
//        return "yes or no?";
//    }
//
//    public double progress () {
//        if (yes < 0)
//            throw new IllegalStateException();
//        if (hasSolved())
//            return 1;
//        return (double)(yes + no) / count;
//    }
// }

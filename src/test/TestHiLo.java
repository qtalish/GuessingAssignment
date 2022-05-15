// $Id: TestHiLo.java 372 2010-10-13 16:57:37Z charpov $
package test;

import guess.HiLo;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class TestHiLo {

    HiLo hilo;
	static double score;

	
    public TestHiLo() {
		super();
	}

	@Before
    public void BEFORE() {
        hilo = new HiLo(0, 999);
    }

    @After
    public void AFTER() {
        hilo = null;
    }

    @BeforeClass
    public static void BEFORECLASS() {
		score = 0;
    }

    @AfterClass
    public static void AFTERCLASS() {
        System.out.println("\nScore: " + score);
    }

    @Test() //low > high in constructor
        public void consParam1 () {
        try{
            new HiLo(5, 4);
        }
        catch(IllegalArgumentException ex){
			score += 1;
            return;
        }
        throw new AssertionError("expected IllegalArgumentException");
    }

    @Test() //valid args in constructor
        public void consParam2 () {
        new HiLo(3, 3);
        new HiLo(-5, 0);
        new HiLo(Integer.MIN_VALUE, Integer.MAX_VALUE);
		score += 1;
    }

    @Test() //makeQuestion() called before initialize()
        public void state1 () {
        try{
            hilo.makeQuestion();
        }
        catch(IllegalStateException ex){
			score += 1;
            return;
        }
        throw new AssertionError("expected IllegalStateException");
    }

    @Test() //yes() called before makeQuestion()
        public void state2 () {
        try{
            hilo.initialize();
            hilo.yes();
        }
        catch(IllegalStateException ex){
			score += 1;
            return;
        }
        throw new AssertionError("expected IllegalStateException");
    }

    @Test() //makeQuestion() called twice in succession
        public void state3 () {
        try{
            hilo.initialize();
            hilo.makeQuestion();
            hilo.makeQuestion();
        }
        catch(IllegalStateException ex){
			score += 1;
            return;
        }
        throw new AssertionError("expected IllegalStateException");
    }

    @Test() //no() called right after yes()
        public void state4 () {
        try{
            hilo.initialize();
            hilo.makeQuestion();
            hilo.yes();
            hilo.no();
        }
        catch(IllegalStateException ex){
			score += 1;
            return;
        }
        throw new AssertionError("expected IllegalStateException");
    }

    @Test() //getSecret() called before initialize()
        public void state5 () {
        try{
            hilo.getSecret();
        }
        catch(IllegalStateException ex){
			score += 1;
            return;
        }
        throw new AssertionError("expected IllegalStateException");
    }

    @Test() //progress() called before initialize() (1 pt)
        public void state6 () {
        try{
            hilo.progress();
        }
        catch(IllegalStateException ex){
			score += 1;
            return;
        }
        throw new AssertionError("expected IllegalStateException");
    }

    @Test() //hasSolved called before initialize()
        public void state7 () {
        try{
            hilo.hasSolved();
        }
        catch(IllegalStateException ex){
			score += 1;
            return;
        }
        throw new AssertionError("expected IllegalStateException");
    }

    @Test(timeout=1000) //makeQuestion() called after game is solved
        public void state8 () {
        try{
            while (!hilo.hasSolved()) {
                hilo.makeQuestion();
                hilo.yes();
            }
            hilo.makeQuestion();
        }
        catch(IllegalStateException ex){
            return;
        }
        throw new AssertionError("expected IllegalStateException");
    }

    @Test() //yes() called before initialize()
        public void state9 () {
        try{
            hilo.yes();
        }
        catch(IllegalStateException ex){
			score += 1;
            return;
        }
        throw new AssertionError("expected IllegalStateException");
    }

    @Test(timeout=5000) //find secret 421 in [0..999]
        public void getSecret1 () throws Exception {
        getSecret(421);
		score += 1;
    }

    @Test(timeout=5000) //find secret 0 in [0..999]
        public void getSecret2 () throws Exception {
        getSecret(0);
		score += 1;
    }

    @Test(timeout=5000) //find secret 999 in [0..999]
        public void getSecret3 () throws Exception {
        getSecret(999);
		score += 1;
    }

    @Test(timeout=5000) //find secret 123456789 in [0..2000000000]
        public void getSecret4 () throws Exception {
        hilo = new HiLo(0, 2000000000);
        getSecret(123456789);
		score += 2;
    }

    @Test(timeout=5000)
    //find secret 0 in all of type int (OK if args rejected by constructor) 
        public void getSecret5 () throws Exception {
        try {
            hilo = new HiLo(Integer.MIN_VALUE, Integer.MAX_VALUE);
        } catch (IllegalArgumentException e) {
			score += 1;
            return;
        }
        getSecret(0);
    }

    @Test(timeout=1000)
    //find secret 42 in [42..42] without asking any question
        public void getSecret6 () throws Exception {
        hilo = new HiLo(42, 42);
        hilo.initialize();
        assertTrue(hilo.hasSolved());
        assertEquals(42, hilo.getSecret().intValue());
		score += 1;
    }

    static Pattern num = Pattern.compile("-?\\d+");
    static Pattern larger =
        Pattern.compile("(?i:larger)|(?i:higher)|(?i:more)|(?i:greater)");
    static Pattern smaller =
        Pattern.compile("(?i:smaller)|(?i:lower)|(?i:less)");

    void getSecret (int target) throws NumberFormatException {
        hilo.initialize();
            String q = hilo.makeQuestion();
            Matcher m = num.matcher(q);
            if (!m.find()) {
                fail("no number in question");
                return;
            }
            boolean ans;
            int n = Integer.parseInt(m.group());
            if (larger.matcher(q).find())
                ans = target > n;
            else if (smaller.matcher(q).find())
                ans = target < n;
            else {
                fail("cannot understand question; contact instructor");
                return;
            }
            if (ans)
                hilo.yes();
            else
                hilo.no();
        assertEquals(target, hilo.getSecret().intValue());
    }

    @Test() //progress() is 0 at the beginning
        public void progress1 () {
        hilo.initialize();
        double d = hilo.progress();
        assertEquals(0, d, 1e-5);
        assertTrue("close to 0 but not equal", d == 0);
		score += 1;
    }

    @Test() 
    //progress() is 1 at the beginning if no question is needed
        public void progress4 () {
        hilo = new HiLo(100,100);
        hilo.initialize();
        double d = hilo.progress();
        assertEquals(1, d, 1e-5);
        assertTrue("close to 1 but not equal", d == 1);
		score += 1;
    }

    @Test(timeout=1000) //progress() is 1 at the end
        public void progress2 () {
        hilo.initialize();
        while (!hilo.hasSolved()) {
            hilo.makeQuestion();
            hilo.yes();
        }
        double d = hilo.progress();
        assertEquals(1, d, 1e-5);
        assertTrue("close to 1 but not equal", d == 1);
		score += 1;
    }

    @Test(timeout=1000) //progress() always increases
        public void progress3 () {
        hilo.initialize();
        double d = 0;
        while (!hilo.hasSolved()) {
            hilo.makeQuestion();
            hilo.yes();
            assertTrue("progress() has not increased", d < (d = hilo.progress()));
        }
		score += 1;
    }
}

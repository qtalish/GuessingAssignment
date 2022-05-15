// $Id: TestLiar.java 348 2010-09-22 17:30:40Z charpov $
package test;

import guess.*;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestLiar {

    Set<String> composers;
    Set<Number> numbers;
    Liar<String> b, b1;
    Liar<Number> b2;
	static double score;

    @Before
    public void BEFORE() {
        composers = new java.util.TreeSet<String>();
        composers.add("Albeniz");
        composers.add("Borodin");
        composers.add("Chopin");
        composers.add("Debussy");
        composers.add("Enesco");
        composers.add("Franck");
        composers.add("Beethoven");
        composers.add("Berlioz");
        composers.add("Brahms");
        composers.add("Bruckner");
        b = new Liar<String>(composers,5,"composer");
        Set<String> s = new java.util.HashSet<String>();
        s.add("foo");
        b1 = new Liar<String>(s,100,"object");
//        initB2(100,100);
    }

//    void initB2 (int n, int l) {
//        numbers = new java.util.HashSet<Number>();
//        for (int i=0; i<n; i++)
//            numbers.add(Integer.valueOf(i));
//        b2 = new Liar<Number>(numbers,l,"number");
//    }

    @After
    public void AFTER() {
        b = b1 = null;
        b2 = null;
    }

    @BeforeClass
    public static void BEFORECLASS() {
		score = 0;
    }

    @AfterClass
    public static void AFTERCLASS() {
        System.out.println("\nScore: " + score);
    }

//    @Test() //field 'name' is correctly initialized
//        public void testFields1 () {
//        assertEquals("composer",b.name);
//		score += 0.5;
//    }

    @Test() //field 'maxLies' is correctly initialized
        public void testFields2 () {
        assertEquals(5,b.maxLies);
		score += 0.5;
    }

    @Test() //hasSolved() is false initially (non singleton)
        public void testHasSolved1 () {
        b.initialize();
        assertFalse(b.hasSolved());
		score += 1;
    }

    @Test() //hasSolved() is true initially (singleton)
        public void testHasSolved2 () {
        b1.initialize();
        assertTrue(b1.hasSolved());
		score += 1;
    }

    @Test(timeout=1000)
    //find secret in set of 10 strings, always answer yes
        public void testGetAnswer1 () {
        b.initialize();
        while (!b.hasSolved()) {
            b.makeQuestion();
            b.yes();
        }
        Liar.Secret<String> s = b.getSecret();
        assertTrue(composers.contains(s.getSecret()));
        assertTrue(s.getLies() <= 5);
		score += 2;
    }

    @Test(timeout=1000) //find secret in set of 10 strings, never lie
        public void testGetAnswer2 () {
        b.initialize();
        while (!b.hasSolved()) {
            if (b.makeQuestion().contains("Borodin"))
                b.yes();
            else
                b.no();
        } 
        Liar.Secret<String> s = b.getSecret();
        assertEquals("Borodin",s.getSecret());
        assertEquals(0,s.getLies());
		score += 2;
    }

    @Test(timeout=1000) //find secret with no question on a singleton
        public void testGetAnswer3 () {
        b1.initialize();
        Liar.Secret<String> s = b1.getSecret();
        assertTrue(b1.hasSolved());
        assertEquals("foo",s.getSecret());
        assertEquals(0,s.getLies());
		score += 1;
    }

    @Test(timeout=1000) //find secret 'Borodin' in set of 10 strings, some lies
        public void testGetAnswer4 () {
        b.initialize();
        getAnswer(b, "Borodin", .6);
		score += 2;
    }

    @Test(timeout=1000) //find secret 'Borodin' in set of 10 strings, few lies
        public void testGetAnswer5 () {
        b.initialize();
        getAnswer(b, "Borodin", .9);
		score += 2;
    }

    @Test(timeout=1000) //find secret 'Borodin' in set of 10 strings, many lies
        public void testGetAnswer6 () {
        b.initialize();
        getAnswer(b, "Borodin", .1);
		score += 2;
    }

    <T> void getAnswer (Liar<T> b, T target, double d) {
        int lies = b.maxLies;
        while (!b.hasSolved()) {
            boolean lie = lies > 0 && Math.random() > d;
            if (b.makeQuestion().contains(String.valueOf(target))) {
                if (lie) {
                    lies--;
                    b.no();
                } else {
                    b.yes();
                }
            } else {
                if (lie) {
                    lies--;
                    b.yes();
                } else {
                    b.no();
                }
            }
        }
        Liar.Secret<T> s = b.getSecret();
        assertEquals(target,s.getSecret());
        assertEquals(b.maxLies-lies,s.getLies());
    }

    @Test(timeout=1000) //find secret in [0..99], always answer no
        public void testGetAnswer7 () {
        b2.initialize();
        while (!b2.hasSolved()) {
            b2.makeQuestion();
            b2.no();
        }
        Liar.Secret<Number> s = b2.getSecret();
        int n = s.getSecret().intValue();
        assertTrue(0 <= n && n < 100);
        assertTrue(s.getLies() <= 100);
		score += 2;
    }

    @Test(timeout=5000) //find secret 42 in [0..99], some lies
        public void testGetAnswer8 () {
        getAnswer8to10(.6);
		score += 2;
    }

    @Test(timeout=5000) //find secret 42 in [0..99], few lies
        public void testGetAnswer9 () {
        getAnswer8to10(.9);
		score += 2;
    }

    @Test(timeout=5000) //find secret 42 in [0..99], many lies
        public void testGetAnswer10 () {
        getAnswer8to10(.1);
		score += 2;
    }

    void getAnswer8to10 (double d) {
        for (int i=0; i<100; i++) {
            int lies = 100;
            b2.initialize();
            Integer target = Integer.valueOf(42);
            getAnswer(b2, target, d);
        }
    }

    @Test() //progress() is 0 initially (non singleton) 
        public void testProgress1 () {
        b.initialize();
        double d = b.progress();
        assertEquals(0, d, 1e-5);
        assertTrue("close to 0 but not equal", d == 0);
		score += 2;
    }

    @Test() //progress() is 1 initially (singleton) 
        public void testProgress2 () {
        b1.initialize();
        double d = b1.progress();
        assertEquals(1, d, 1e-5);
        assertTrue("close to 1 but not equal", d == 1);
		score += 2;
    }

    @Test() //progress() is > 0 after one round 
        public void testProgress3 () {
        b.initialize();
        b.makeQuestion();
        b.yes();
        assertTrue(b.progress() > 0);
		score += 1;
    }

    @Test(timeout=1000) //progress() is 1 at the end (secret string) 
        public void testProgress4 () {
        b.initialize();
        progress3to4(b);
		score += 2;
    }

    @Test(timeout=1000) //progress() is 1 at the end (secret number) 
        public void testProgress5 () {
        b2.initialize();
        progress3to4(b2);
		score += 2;
    }

    @Test(timeout=1000) //progress() always increases (secret string) 
        public void testProgress6 () {
        b.initialize();
        progress5to6(b);
		score += 2;
    }

    @Test(timeout=1000) //progress() always increases (secret number) 
        public void testProgress7 () {
        b2.initialize();
        progress5to6(b2);
		score += 2;
    }

    void progress3to4 (Liar<?> b) {
        double p = 0;
        while (!b.hasSolved()) {
            b.makeQuestion();
            b.yes();
        }
        double d = b.progress();
        assertEquals(1, d, 1e-5);
        assertTrue("close to 1 but not equal", d == 1);
    }

    void progress5to6 (Liar<?> b) {
        double p = 0;
        while (!b.hasSolved()) {
            b.makeQuestion();
            b.yes();
            assertTrue("progress has not increased", p < (p = b.progress()));
        }
    }

    @Test(timeout=1000)
    //makeQuestion() called before initialize() (strings)
        public void testState1 () {
        try{
            b1.makeQuestion();
        }
        catch(IllegalStateException ex){
			score += 1;
            return;
        }
        throw new AssertionError("expected IllegalStateException");
    }

    @Test() //makeQuestion() called before initialize() (numbers)
        public void testState2 () {
        try{
            b2.makeQuestion();
        }
        catch(IllegalStateException ex){
			score += 1;
            return;
        }
        throw new AssertionError("expected IllegalStateException");
    }

    @Test() //yes() called before makeQuestion()
        public void testState3 () {
        try{
            b.initialize();
            b.yes();
        }
        catch(IllegalStateException ex){
			score += 1;
            return;
        }
        throw new AssertionError("expected IllegalStateException");
    }

    @Test() //getSecret() called before hasSolved() is true
        public void testState4 () {
        try{
            b.initialize();
            b.getSecret();
        }
        catch(IllegalStateException ex){
			score += 1;
            return;
        }
        throw new AssertionError("expected IllegalStateException");
    }

    @Test() //makeQuestion() called twice in a row
        public void testState6 () {
        try{
            b.initialize();
            b.makeQuestion();
            b.makeQuestion();
        }
        catch(IllegalStateException ex){
			score += 1;
            return;
        }
        throw new AssertionError("expected IllegalStateException");
    }

    @Test() //progress() called before initialize()
        public void testState7 () {
        try{
            b2.progress();
        }
        catch(IllegalStateException ex){
			score += 1;
            return;
        }
        throw new AssertionError("expected IllegalStateException");
    }

    @Test() //hasSolved() called before initialize()
        public void testState8 () {
        try{
            b2.hasSolved();
        }
        catch(IllegalStateException ex){
			score += 1;
            return;
        }
        throw new AssertionError("expected IllegalStateException");
    }

    @Test() //makeQuestion() called after problem is solved
        public void testState9 () {
        try{
            b.initialize();
            while (!b.hasSolved()) {
                b.makeQuestion();
                b.yes();
            }
            b.makeQuestion();
        }
        catch(IllegalStateException ex){
			score += 1;
            return;
        }
        throw new AssertionError("expected IllegalStateException");
    }

    @Test() //no() called right after yes()
        public void testState10 () {
        try{
            b.initialize();
            b.makeQuestion();
            b.yes();
            b.no();
        }
        catch(IllegalStateException ex){
			score += 1;
            return;
        }
        throw new AssertionError("expected IllegalStateException");
    }

    // Big tests

    @Test(timeout=60000) //find secret 242 in [0..999], many lies 
        public void testBig1 () {
        testBig(242, 1000, 1000, .1);
		score += 1;
    }

    @Test(timeout=60000) //find secret 242 in [0..999], some lies 
        public void testBig2 () {
        testBig(242, 1000, 1000, .6);
		score += 1;
    }

    @Test(timeout=60000) //find secret 242 in [0..999], few lies 
        public void testBig3 () {
        testBig(242, 1000, 1000, .9);
		score += 1;
    }

    @Test(timeout=60000) //find secret 4242 in [0..4999], many lies 
        public void testBig4 () {
        testBig(4242, 5000, 1000, .1);
		score += 1;
    }

    @Test(timeout=60000) //find secret 4242 in [0..9999], many lies 
        public void testBig5 () {
        testBig(4242, 10000, 1000, .1);
		score += 1;
    }

    @Test(timeout=120000) //find secret 4242 in [0..9999], many, many lies 
        public void testBig6 () {
        testBig(4242, 10000, 5000, .1);
		score += 1;
    }

    void testBig (int n, int s, int l, double d) {
        Integer target = Integer.valueOf(n);
//        initB2(s,l);
        b2.initialize();
        getAnswer(b2, target, d);
    }
}

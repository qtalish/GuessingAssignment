// $Id: SampleTests.java 326 2010-09-01 20:14:00Z charpov $

package test;

import guess.*;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Set;
import java.io.StringWriter;
import java.io.StringReader;

public class SampleTests {

  Guesser<String> g;
  StringWriter w;
  HiLo hilo;
  Set<String> composers;
  Liar<String> b;

  @Before
  public void BEFORE() {
  }

  @After
  public void AFTER() {
  }

  @Test() //valid args in constructor
  public void sample1 () {
    new HiLo(3, 3);
    new HiLo(-5, 0);
    new HiLo(Integer.MIN_VALUE, Integer.MAX_VALUE);
  }

  @Test() //makeQuestion() called before initialize()
  public void sample2 () {
    try{
	hilo = new HiLo(0, 999);
	hilo.makeQuestion();
    }
    catch (IllegalStateException ex){
	return;
    }
    // if there was no exception to catch, the test fails
    throw new AssertionError("expected IllegalStateException");
  }

  @Test(timeout=1000) //progress() is 1 at the end
  public void sample3 () {
    hilo = new HiLo(0, 999);
    hilo.initialize();
    while (!hilo.hasSolved()) {
      hilo.makeQuestion();
      hilo.yes();
    }
    assertTrue(hilo.progress() == 1);
  }

//  @Test() //field 'name' is correctly initialized
//  public void sample4 () {
//    Set<String> composers = new java.util.TreeSet<String>();
//    composers.add("Enesco");
//    composers.add("Beethoven");
//    b = new Liar<String>(composers,5,"composer");
//    assertEquals("composer",b.name);
//  }

  @Test(timeout=1000) //find secret in set of 10 strings, never lie
  public void sample5 () {
    Set<String> composers = new java.util.TreeSet<String>();
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
    b.initialize();
    while (!b.hasSolved()) {
      if (b.makeQuestion().contains("Borodin"))
        b.yes();
      else
        b.no();
    } 
    Liar.Secret<String> s = b.getSecret();
    // secret should have been Borodin
    assertEquals("Borodin",s.getSecret());
    // told 0 lies
    assertEquals(0,s.getLies());
  }

  @Test(timeout=1000) //small test w/ garbage answers
  public void sample6 () throws java.io.IOException{
    w = new StringWriter(4096);
    //our guesser will ask exactly 10 questions
    g = new SillyGuesser(10);
    // a fake user is answering: n, e, w, n, y, t, y, ...
    // repeats the above sequence
    StringBuilder b = new StringBuilder();
    while (b.length() < 80){
        b.append("n\ne\nw\nn\ny\nt\ny\n");
    }
    int n = new TextUI(g, new StringReader(b.toString()), w).play();
    //System.out.println(w.toString()); //print the game's output
    // should have played 2 whole games before quitting
    assertEquals(n, 2);
    // last game should have had 5 yes, 5 no
    assertTrue(w.toString().contains("YES:5 NO:5"));
  }
}

/** A guesser that asks an exact number of questions and counts the
 * number of yes and no answers.
 */
class SillyGuesser implements Guesser<String> {

  private int yes, no, count;
  private boolean q;

  public SillyGuesser (int n) {
    count = n;
    yes = -1;
  }

  public String initialize () {
    yes = no = 0;
    q = true;
    return "Let's go!";
  }

  public boolean hasSolved () {
    if (yes < 0)
      throw new IllegalStateException();
    return yes + no == count;
  }

  public String getSecret () {
    if (yes < 0 || !hasSolved())
      throw new IllegalStateException();
    return "YES:" + yes + " NO:" + no;
  }

  public void yes () {
    if (yes < 0 || q)
      throw new IllegalStateException();
    yes++;
    q = true;
  }

  public void no () {
    if (yes < 0 || q)
      throw new IllegalStateException();
    no++;
    q = true;
  }

  public String makeQuestion () {
    if (yes < 0 || hasSolved() || !q)
      throw new IllegalStateException();
    q = false;
    return "yes or no?";
  }

  public double progress () {
    if (yes < 0)
      throw new IllegalStateException();
    if (hasSolved())
      return 1;
    return (double)(yes + no) / count;
  }
}

// $Id: TextUI.java 638 2012-01-10 15:37:57Z charpov $

package guess;

import java.util.regex.Pattern;

import guess.Liar.Secret;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.io.PrintWriter;
import java.util.Set;

/** A simple, text-based user interface for guessing games.  This
 * interface is constructed from a <code>Guesser</code> object and
 * offers a <code>play</code> method.  This method is blocking (i.e.,
 * it does not return until the user is done playing) and returns the
 * number of games played.
 * 
 * <p>This class also implements a command-line program to start a
 * <code>HiLo</code> or a <code>Liar</code> guessing session.
 *
 * @author  Michel Charpentier
 * @version 2.2, 08/31/10
 * @see Guesser
 * @see guess.HiLo
 * @see guess.Liar
 * @see <a href="TextUI.java"><tt>TextUI.java</tt></a>
 */
public class TextUI {

  private final Guesser<?> guesser;
  private final Scanner in;
  private final PrintWriter out;

  /** Builds a user interface for the given guesser.  Questions are
   *  displayed on <code>output</code> and user input is read from
   *  <code>input</code>.
   */
   public TextUI (Guesser<?> g, java.io.Reader input, java.io.Writer output) {
    guesser = g;
    in = new Scanner(input);
    out = new PrintWriter(output, true);
  }

  /** Builds a user interface for the given guesser.  Questions are
   *  displayed on <code>System.out</code> and user input is read from
   *  <code>System.in</code>.
   */
   public TextUI (Guesser<?> g) {
     this(g,
          new java.io.InputStreamReader(System.in),
          new java.io.OutputStreamWriter(System.out));
  }

  private boolean getReply () {
    while (true) {
      String reply = in.nextLine().trim().toUpperCase();
      if (reply.equals("YES") || reply.equals("Y"))
        return true;
      if (reply.equals("NO") || reply.equals("N"))
        return false;
      out.printf("Please answer YES or NO: ");
    }
  }

  /** Starts the interaction with the user. This method will only return when the
   * user is done playing.
   *
   * <p> {@code 'y'} (followed by a newline) is accepted as a valid
   * input for 'yes'; similarly, {@code 'n'} means 'no'.  This
   * implementation accepts other answers such as 'Y', 'N', 'yes',
   * 'no', etc.  Invalid answers from the user do <em>not</em> end the
   * interaction.  Instead, the method keeps asking until it gets a yes
   * or no.
   *
   * <p>Upon termination, this method flushes the output writer but
   * does <em>not</em> close it, nor does it close the input reader.
   *
   * @return the number of games played
   */
  public int play () {
    int nbGames = 0;
    try {
      do {
        nbGames++;
        out.println();
        out.println(guesser.initialize());
        while (!guesser.hasSolved()) {
          out.printf("%s ", guesser.makeQuestion());
          if (getReply())
            guesser.yes();
          else
            guesser.no();
//          int progress = (int)(guesser.progress()*100);
          out.printf("I'm %.0f%% finished%n", guesser.progress()*100);
        }
        out.printf("The secret is: %s%n%n", guesser.getSecret());
        out.printf("Play again? ");
      } while (getReply());
    } catch (java.util.NoSuchElementException e) { // thrown by getReply()
      System.err.println("\nEOF on input stream; interaction terminated.");
    } finally {
      out.flush();
    }
    return nbGames;
  }

  // This method should be modified if the bonus question is not implemented
  private static void usage () {
    System.err.println
      ("Usage: TextUI -hilo min max\n"+
       "   or: TextUI -liar #lies name1 [name2 ...]\n"+
       "   or: TextUI -liar #lies -file filename #names");
  }

  /** Starts a command-line program.  This program can be started in 3
   * different ways:
   *<pre>
   * Usage: TextUI -hilo min max
   *    or: TextUI -liar #lies name1 [name2 ...]
   *    or: TextUI -liar #lies -file filename #names
   *</pre>
   *
   * The last form takes the names of the secret objects from a file
   * and the last parameter specifies how many of these are actually used in
   * the game.
   *
   * @param args command-line parameters
   * @see Liar#selectCandidates
   * @throws java.io.IOException if the file of names cannot be opened and read
   */
  public static void main(String[] args) throws java.io.IOException {
    if (args.length < 3) {
      usage();
      return;
    }
    Guesser<?> guesser = null;
    if (args[0].equals("-hilo")) 
      guesser = makeHiLoGuesser(args);
    else if (args[0].equals("-liar")) 
      guesser = makeLiarGuesser(args); // args.length > 2
    if (guesser == null) {
      usage();
      return;
    }
    int n = new TextUI(guesser).play();
    System.out.printf("(%d game", n);
    if (n > 1)
      System.out.printf("s");
    System.out.println(" played)");
  }

  private static Guesser<Integer> makeHiLoGuesser (String[] args) {
    if (args.length != 3)
      return null;
    try {
      int min = Integer.parseInt(args[1]);
      int max = Integer.parseInt(args[2]);
      return new HiLo(min, max);
    } catch (NumberFormatException e) {
      System.err.println("Cannot parse parameters as numbers");
      return null;
    }
  }

  private static Guesser<Liar.Secret<String>> makeLiarGuesser (String[] args) {
	  try {
	      int lie = Integer.parseInt(args[1]);
	      Set<String> colors = new HashSet<>();
	      for(int i=2; i<args.length; i++) {
	    	 colors.add(args[i]);
	      }
	      return new Liar<String>(colors, lie, "");
	    } catch (NumberFormatException e) {
	      System.err.println("Cannot parse parameters as numbers");
	      return null;
	    }
  }
}

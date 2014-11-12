package taojava.expt;

import java.io.PrintWriter;
import java.util.Iterator;

import taojava.util.SkipList;

/**
 * An experiment with SkipLists.
 * 
 * @author Samuel A. Rebelsky
 */
public class SkipListExpt
{
  public static void main(String[] args)
    throws Exception
  {
    
    PrintWriter pen = new PrintWriter(System.out, true);
    
    SortedListExpt.stringExperiment(pen, new SkipList<String>());
    pen.flush();
    
    SkipList<Integer> intskip = new SkipList<Integer>();
    intskip.remove(1);
    intskip.add(1);
    intskip.add(5);
    intskip.add(8);
    intskip.add(2);
    intskip.add(3);
    intskip.add(23);
    Iterator<Integer> intiter = intskip.iterator();
    pen.println(intiter.hasNext());
    pen.println(intiter.next());
    pen.println(intiter.next());
    pen.println(intiter.next());
    pen.println(intiter.next());
    pen.println(intiter.next());
    pen.println(intiter.next());
    pen.println(intiter.hasNext());
    /*
    pen.println(Boolean.toString(intskip.contains(5)));
    pen.println(Boolean.toString(intskip.contains(4)));
    */
  } // main(String[])
} // class SkipListExpt


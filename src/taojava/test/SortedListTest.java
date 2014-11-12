package taojava.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import static org.junit.Assert.*;

import org.junit.Test;

import taojava.util.SortedList;

/**
 * Generic tests of sorted lists.
 *
 * To test a particular implementation of sorted lists, subclass this
 * class and add an appropriate @Before clause to fill in strings and
 * ints.
 * 
 * @author Samuel A. Rebelsky
 */
public class SortedListTest
{
  // +--------+----------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * A sorted list of strings for tests.  (Gets set by the subclasses.)
   */
  SortedList<String> strings;

  /**
   * A sorted list of integers for tests.  (Gets set by the subclasses.)
   */
  SortedList<Integer> ints;

  /**
   * A random number generator for the randomized tests.
   */
  Random random = new Random();

  // +---------+---------------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Dump a SortedList to stderr.
   */
  static <T extends Comparable<T>> void dump(SortedList<T> slist)
  {
    System.err.print("[");
    for (T val : slist)
      {
        System.err.print(val + " ");
      } // for
    System.err.println("]");
  } // dump

  /**
   * Determine if an iterator only returns values in non-decreasing
   * order.
   */
  static <T extends Comparable<T>> boolean inOrder(Iterator<T> it)
  {
    // Simple case: The empty iterator is in order.
    if (!it.hasNext())
      return true;
    // Otherwise, we need to compare neighboring elements, so
    // grab the first element.
    T current = it.next();
    // Step through the remaining elements
    while (it.hasNext())
      {
        // Get the next element
        T next = it.next();
        // Verify that the current node <= next
        if (current.compareTo(next) > 0)
          {
            return false;
          } // if (current > next)
        // Update the current node
        current = next;
      } // while
    // If we've made it this far, everything is in order
    return true;
  } // inOrder(Iterator<T> it)

  // +-------------+-----------------------------------------------------
  // | Basic Tests |
  // +-------------+

  /**
   * A really simple test.  Add an element and make sure that it's there.
   */
  @Test
  public void simpleTest()
  {
    strings.add("hello");
    assertTrue(strings.contains("hello"));
    assertFalse(strings.contains("goodbye"));
  } // simpleTest()

  /**
   * Another simple test.  The list should not contain anything when
   * we start out.
   */
  @Test
  public void emptyTest()
  {
    assertFalse(strings.contains("hello"));
  } // emptyTest()

  // +-----------------+-------------------------------------------------
  // | RandomizedTests |
  // +-----------------+

  /**
   * Verify that a randomly created list is sorted.
   */
  @Test
  public void testOrdered()
  {
    // For reporting errors: an array of operations
    ArrayList<String> operations = new ArrayList<String>();
    // Add a bunch of values
    for (int i = 0; i < 100; i++)
      {
        int rand = random.nextInt(1000);
        ints.add(rand);
        operations.add("ints.add(" + rand + ")");
      } // for
    if (!inOrder(ints.iterator()))
      {
        System.err.println("inOrder() failed");
        for (String op : operations)
          System.err.println(op + ";");
        dump(ints);
        fail("The instructions did not produce a sorted list.");
      } // if the elements are not in order.
  } // testOrdered()

  /**
   * Verify that a randomly created list contains all the values
   * we added to the list.
   */
  @Test
  public void testContainsOnlyAdd()
  {
    ArrayList<String> operations = new ArrayList<String>();
    ArrayList<Integer> vals = new ArrayList<Integer>();
    // Add a bunch of values
    for (int i = 0; i < 100; i++)
      {
        int rand = random.nextInt(200);
        vals.add(rand);
        operations.add("ints.add(" + rand + ")");
        ints.add(rand);
      } // for i
    // Make sure that they are all there.
    for (Integer val : vals)
      {
        if (!ints.contains(val))
          {
            System.err.println("contains(" + val + ") failed");
            for (String op : operations)
              System.err.println(op + ";");
            dump(ints);
            fail(val + " is not in the sortedlist");
          } // if (!ints.contains(val))
      } // for val
  } // testContainsOnlyAdd()

  /**
   * An extensive randomized test.
   */
  @Test
  public void randomTest()
  {
    // Set up a list of all the operations we performed.  (That way,
    // we can replay a failed test.)
    ArrayList<String> operations = new ArrayList<String>();
    // Keep track of the values that are currently in the sorted list.
    ArrayList<Integer> vals = new ArrayList<Integer>();

    // Add a bunch of values
    for (int i = 0; i < 1000; i++)
      {
        boolean ok = true;
        int rand = random.nextInt(2000);
        // Half the time we add
        if (random.nextBoolean())
          {
            if (!ints.contains(rand))
              vals.add(rand);
            operations.add("ints.add(" + rand + ")");
            ints.add(rand);
            if (!ints.contains(rand))
              {
                System.err.println("After adding " + rand + " contains fails");
                ok = false;
              } // if (!ints.contains(rand))
          } // if we add
        // Half the time we remove
        else
          {
            operations.add("ints.remove(" + rand + ")");
            ints.remove(rand);
            vals.remove((Integer) rand);
            if (ints.contains(rand))
              {
                System.err.println("After removing " + rand
                                   + " contains succeeds");
                ok = false;
              } // if ints.contains(rand)
          } // if we remove
        // See if all of the appropriate elements are still there
        for (Integer val : vals)
          {
            if (!ints.contains(val))
              {
                System.err.println("ints no longer contains " + val);
                ok = false;
                break;
              } // if the value is no longer contained
          } // for each value
        // Dump the instructions if we've encountered an error
        if (!ok)
          {
            for (String op : operations)
              System.err.println(op + ";");
            dump(ints);
            fail("Operations failed");
          } // if (!ok)
      } // for i
  } // randomTest()

  @Test
  public void containsTest()
  {
    int size = 10000;
    Integer[] rand = new Integer[size];
    // Create an array of random ints.
    // add the int to the sorted list
    for (int i = 0; i < size; i++)
      {
        rand[i] = random.nextInt();
        ints.add(rand[i]);
      } // for
    for (int i = 0; i < size; i++)
      {
        if (!ints.contains(rand[i]))
          {
            System.err.println("ints doesn't contain" + rand[i]);
            dump(ints);
            fail("Contains failed");
          } // if
      } // for    
  } // containsTest()

  /**
   * Test to see if add doesn't add repeat values
   */
  @Test
  public void addTest()
  {
    strings.add("Yes");
    strings.add("No");
    strings.add("Yes");
    assertTrue(strings.contains("Yes"));
    assertTrue(strings.contains("No"));
    strings.remove("Yes");
    assertFalse(strings.contains("Yes"));
  } // addTest()

  /**
   * A test to see if modifying an iterator invalidates a different iterator.
   */
  @Test
  public void invalidIteratorTest()
  {
    for (int i = 0; i < 100; i++)
      {
        ints.add(i);
      } // for
    Iterator<Integer> iterator1 = ints.iterator();
    Iterator<Integer> iterator2 = ints.iterator();

    iterator1.next();
    iterator1.remove();

    try
      {
        iterator2.hasNext();
      }
    catch (Exception e)
      {
        return;
      }
    fail("Iterator is still valid after changing ints with a different iterator");

  } // invalidIteratorTest()

  /**
   * Test to see if the order values are added to the list matters
   */
  @Test
  public void orderAddedTest()
  {
    int size = 200;
    Integer[] rand = new Integer[size];
    SortedList<Integer> ints2 = ints;
    for (int i = 0; i < size; i++)
      {
        rand[i] = random.nextInt();
        ints.add(rand[i]);
      } // for
    for (int i = size - 1; i >= 0; i--)
      {
        ints2.add(rand[i]);
      } // for
    Iterator<Integer> it1 = ints.iterator();
    Iterator<Integer> it2 = ints2.iterator();
    for (int i = 0; i < 200; i++)
      {
        assertTrue(it1.next().equals(it2.next()));
      } // for
  } // orderAddedTest()
} // class SortedListTest

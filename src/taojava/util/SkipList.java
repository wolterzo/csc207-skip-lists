package taojava.util;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * A randomized implementation of sorted lists.  
 * 
 * @author Samuel A. Rebelsky
 * @author Zoe Wolter
 * 
 * Looked at 
 * http://www.mathcs.duq.edu/drozdek/DSinJava/SkipListNode.java
 * and 
 * http://www.mathcs.duq.edu/drozdek/DSinJava/SkipList.java
 * 
 */
public class SkipList<T extends Comparable<T>>
    implements SortedList<T>
{
  // +--------+----------------------------------------------------------
  // | Fields |
  // +--------+
  /**
   * Maximum allowed levels in the list
   */
  int maxLevel;

  /**
   * Total of current levels in the list.
   */
  int levels;
  /**
   * Array of the first nodes in each level.
   */
  Node<T> first;
  /**
   * Array of last elements in the list.
   */
  Node<T> last;
  /**
   * Number of modifications made to the SkipList
   */
  int mods = 0;

  /**
   * Random number generator
   */
  Random random = new Random();

  /**
   * Probability used in determining random level.
   * (Now set to 1/2)
   */
  double probability = 0.5;

  // +------------------+------------------------------------------------
  // | Internal Classes |
  // +------------------+

  /**
   * Nodes for skip lists.
   */
  public class Node<T>
  {
    // +--------+--------------------------------------------------------
    // | Fields |
    // +--------+

    /**
     * The value stored in the node.
     */
    T val;
    /**
     * Array of forward pointers, size determined by level of node.
     */
    Node<T>[] forward;

    @SuppressWarnings("unchecked")
    public Node(T val, int level)
    {
      this.val = val;
      this.forward = new Node[level + 1];
      for (int i = 0; i < level; i++)
        {
          forward[i] = null;
        } // for
    } // Node(int)
  } // class Node

  // +--------------+----------------------------------------------------
  // | Constructors |
  // +--------------+
  /**
   * Creates a SkipList with a specified maxLevel
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public SkipList(int max)
  {
    this.maxLevel = max;
    this.levels = 1;
    this.first = new Node(null, maxLevel);
    this.last = new Node(null, maxLevel);
    for (int i = 1; i < maxLevel; i++)
      {
        // set all nodes in last to null
        this.last.forward[i] = null;
        // point all nodes in first to the corresponding node in last
        this.first.forward[i] = last;
      } // for
  }// SkipList(int)

  /**
   * Creates a SkipList with a maxLevel of 20.
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public SkipList()
  {
    this.maxLevel = 20;
    this.levels = 1;
    this.first = new Node(null, maxLevel);
    this.last = new Node(null, maxLevel);
    for (int i = 1; i < maxLevel; i++)
      {
        // set all nodes in last to null
        this.last.forward[i] = null;
        // point all nodes in first to the corresponding node in last
        this.first.forward[i] = last;
      } // for
  } // SkipList()

  // +-------------------------+-----------------------------------------
  // | Internal Helper Methods |
  // +-------------------------+
  private int randomLevel()
  {
    int newLevel = 1;
    while (this.random.nextDouble() < this.probability)
      {
        newLevel = newLevel + 1;
      } // while
    return Math.min(newLevel, this.maxLevel);
  } // randomLevel()

  /**
   * Determine if the given node has a next element at the given level.
   * @param node
   * @return
   */
  @SuppressWarnings("unused")
  private boolean hasNext(Node<T> node, int level)
  {
    return node.forward[level].val != null;
  } // hasNext(Node<T>, int)

  // +-----------------------+-------------------------------------------
  // | Methods from Iterable |
  // +-----------------------+

  /**
   * Return a read-only iterator (one that does not implement the remove
   * method) that iterates the values of the list from smallest to
   * largest.
   */
  public Iterator<T> iterator()
  {
    return new Iterator<T>()
      {
        /**
         * The node that immediately precedes the value to be returned by
         * next.
         */
        Node<T> cursor = SkipList.this.first;
        /**
         * The number of modifications at the time this iterator was
         * created or last updated.
         */
        int mods = SkipList.this.mods;

        // +---------+-----------------------------------------------------
        // | Helpers |
        // +---------+

        /**
         * Determine if the list has been updated since this iterator
         * was created or modified.
         */
        void failFast()
        {
          if (this.mods != SkipList.this.mods)
            throw new ConcurrentModificationException();
        } // failFast

        // +---------+-----------------------------------------------------
        // | Methods |
        // +---------+
        @Override
        public boolean hasNext()
        {
          failFast();
          return cursor.forward[1] != null;
        } // hasNext()

        @Override
        public T next()
        {
          failFast();
          if (!this.hasNext())
            throw new NoSuchElementException();
          // Advance to the next node.
          this.cursor = this.cursor.forward[1];
          // Return the data in the now current node.
          return this.cursor.val;
        } // next()
      };
  } // iterator()

  // +------------------------+------------------------------------------
  // | Methods from SimpleSet |
  // +------------------------+

  /**
   * Add a value to the set.
   *
   * @post contains(val)
   * @post For all lav != val, if contains(lav) held before the call
   *   to add, contains(lav) continues to hold.
   */
  public void add(T val)
  {
    int newLevel = randomLevel();
    @SuppressWarnings("unchecked")
    Node<T>[] update = new Node[this.maxLevel];
    Node<T> newNode = new Node<T>(val, newLevel);
    Node<T> tmp = this.first;
    for (int i = this.levels; i >= 1; i--)
      {
        while (hasNext(tmp, i) && tmp.forward[i].val.compareTo(val) < 0)
          {
            tmp = tmp.forward[i];
          } // while
        update[i] = tmp;
      } // for
    // if the newLevel is bigger than levels
    if (newLevel > this.levels)
      {
        for (int i = this.levels + 1; i <= newLevel; i++)
          {
            update[i] = this.first;
          } // for
        this.levels = newLevel;
      } // if 
    for (int i = 1; i <= newLevel; i++)
      {
        newNode.forward[i] = update[i].forward[i];
        update[i].forward[i] = newNode;
      } // for
  } // add(T val)

  /**
   * Determine if the set contains a particular value.
   */
  @SuppressWarnings("unused")
  public boolean contains(T val)
  {
    Node<T> tmp = this.first;
    for (int i = this.levels; i > 0; i--)
      {
        while (hasNext(tmp, i) && tmp.forward[i].val.compareTo(val) < 0)
          {
            tmp = tmp.forward[i];
          } // while
        tmp = tmp.forward[1];
        if (tmp.val.compareTo(val) == 0)
          return true;
        else
          return false;
      } // for
    return false;
  } // contains(T)

  /**
   * Remove an element from the set.
   *
   * @post !contains(val)
   * @post For all lav != val, if contains(lav) held before the call
   *   to remove, contains(lav) continues to hold.
   */
  @SuppressWarnings({ "unused", "unchecked" })
  public void remove(T val)
  {
    Node<T>[] update = new Node[maxLevel];
    Node<T> tmp = this.first;
    for (int i = this.levels; i > 0; i--)
      {
        while (hasNext(tmp, i) && tmp.forward[i].val.compareTo(val) < 0)
          {
            tmp = tmp.forward[i];
          } // while
        update[i] = tmp;
      } // for
    tmp = tmp.forward[1];
    if (tmp.val.compareTo(val) == 0)
      {
        for (int i = 1; i <= this.levels; i++)
          {
            if (!update[i].forward[i].equals(tmp))
              break;
            update[i].forward[i] = tmp.forward[i];
          } // for
        while (this.levels > 1
               && this.first.forward[this.levels].val == null)
          {
            this.levels--;
          } // while
      } // if
  } // remove(T)

  // +--------------------------+----------------------------------------
  // | Methods from SemiIndexed |
  // +--------------------------+

  /**
   * Get the element at index i.
   *
   * @throws IndexOutOfBoundsException
   *   if the index is out of range (index < 0 || index >= length)
   */
  public T get(int i)
  {
    // STUB
    return null;
  } // get(int)

  /**
   * Determine the number of elements in the collection.
   */
  public int length()
  {
    // STUB
    return 0;
  } // length()

} // class SkipList<T>

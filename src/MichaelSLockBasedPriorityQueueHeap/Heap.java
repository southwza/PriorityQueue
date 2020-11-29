package MichaelSLockBasedPriorityQueueHeap;

import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

public class Heap<T extends Comparable<T>>
{
   private ReentrantLock heapLock;
   private BitReversedCounter counter;
   private HeapNode<T> items[];
   // TODO: This can impact performance since there will
   // be wasted cycles growing and initializing the array.
   // Increasing number slows down constructor.
   private final int initialSize = 1024 * 1000;
   private int rootIndex = 1;

   Heap() {
      heapLock = new ReentrantLock();
      counter = new BitReversedCounter();
      items = new HeapNode[initialSize];
      for (int i = 0; i < items.length; i++) {
         items[i] = new HeapNode<>();
      }
   }

   private void lock(int nodeIndex) {
      items[nodeIndex].lock.lock();
   }

   private void unlock(int nodeIndex) {
      items[nodeIndex].lock.unlock();
   }

   private int getTag(int nodeIndex) {
      return items[nodeIndex].tag;
   }

   private T getPriority(int nodeIndex) {
      return items[nodeIndex].priority;
   }

   private void setTag(int nodeIndex, int tag) {
      items[nodeIndex].tag = tag;
   }

   private void setPriority(int nodeIndex, T priority) {
      items[nodeIndex].priority = priority;
   }

   private void clear(int nodeIndex) {
      items[nodeIndex].clear();
   }

   private void swap(int first, int second) {
      items[first].swap(items[second]);
   }

   private boolean hasLeftChild(int i)
   {
      return ((2 * i) < items.length);
   }

   private boolean hasRightChild(int i)
   {
      return ((2 * i + 1) < items.length);
   }

   private void growHeapArray() {
      // System.out.println("Grow heap was called!");
      final int oldSize = items.length;
      final int newSize = oldSize * 2;
      items = Arrays.copyOf(items, newSize);
      // Initialize new part of array
      for (int i = oldSize; i < items.length; i++) {
         items[i] = new HeapNode<>();
      }
   }

   public int size() {
      return counter.getCounter();
   }

   public void clear() {
      heapLock = new ReentrantLock();
      counter = new BitReversedCounter();
      items = new HeapNode[initialSize];
      for (int i = 0; i < items.length; i++) {
         items[i] = new HeapNode<>();
      }
   }

   public void concurrentInsert(T priority) {
      final int currPid = (int) Thread.currentThread().getId();
      // Lock heap
      heapLock.lock();
      int child = counter.bit_reversed_increment();

      if (child >= items.length - 1) {
         growHeapArray();
      }

      lock(child);

      heapLock.unlock();

      setPriority(child, priority);
      setTag(child, currPid);
      unlock(child);

      while (child > rootIndex) {
         final int parent = child / 2;
         lock(parent);
         lock(child);
         final int old_child = child;
         // Parent is available and that current child is still
         // owned by thread.
         if (getTag(parent) == HeapNodeTag.AVAILABLE &&
             getTag(child) == currPid) {
            // compareTo return positive if greater, zero if equal, and negative if smaller.
            if (getPriority(child).compareTo(getPriority(parent)) < 0) {
               swap(child, parent);
               child = parent;
            } else {
               setTag(child, HeapNodeTag.AVAILABLE);
               child = 0;
            }
         } else if (getTag(parent) == HeapNodeTag.EMPTY) {
            child = 0;
         } else if (getTag(child) != currPid) {
            child = parent;
         }

         unlock(old_child);
         unlock(parent);
      }

      if (child == rootIndex) {
         lock(child);
         if (getTag(child) == currPid) {
            setTag(child, HeapNodeTag.AVAILABLE);
         }
         unlock(child);
      }
   }

   public T concurrentDelete() {
      heapLock.lock();

      if (counter.getCounter() == 0) {
         heapLock.unlock();
         return null;
      }

      int bottom = counter.getReversed();
      counter.bit_reversed_decrement();

      lock(rootIndex);
      lock(bottom);

      heapLock.unlock();

      // Root has the lowest element
      T priority = getPriority(rootIndex);

      setTag(rootIndex, HeapNodeTag.EMPTY);
      // Swap root with bottom node
      swap(rootIndex, bottom);
      unlock(bottom);

      if (getTag(rootIndex) == HeapNodeTag.EMPTY) {
         unlock(rootIndex);
         return priority;
      }

      setTag(rootIndex, HeapNodeTag.AVAILABLE);
      int child = 0;
      int parent = rootIndex;

      while (parent < items.length / 2) {
         int left = parent * 2;
         lock(left);
         int right = -1;

         if (hasRightChild(parent)) {
            right = (parent * 2) + 1;
            lock(right);
         }

         if (getTag(left) == HeapNodeTag.EMPTY) {
            unlock(left);
            if (hasRightChild(parent)) {
               unlock(right);
            }
            break;
         } else if (!hasRightChild(parent)) {
            child = left;
         } else if (getTag(right) == HeapNodeTag.EMPTY ||
                    getPriority(left).compareTo(getPriority(right)) < 0) {
            unlock(right);
            child = left;
         } else {
            unlock(left);
            child = right;
         }

         if (getPriority(child).compareTo(getPriority(parent)) < 0 &&
             getTag(child) != HeapNodeTag.EMPTY) {

            swap(parent, child);
            unlock(parent);
            parent = child;
         } else {
            unlock(child);
            break;
         }
      }

      unlock(parent);

      return priority;
   }

   @Override
   public String toString() {
      String result = "";
      int depth = 1;
      int size = this.counter.getCounter();
      while (size > 1) {
         size = size >>> 1;
         depth++;
      }
      for (int d = 0; d < depth; d++) {
         String spacer = " ".repeat((depth - d));
         for (int i = (1 << d); i < (1 << (d+1)); i++) {
            if (i > items.length) {
               break;
            }
            result += spacer + String.format("%2d", items[i].priority);
         }
         result += System.lineSeparator();
      }
      return result;
   }
}

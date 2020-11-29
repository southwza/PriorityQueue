package MichaelSLockBasedPriorityQueueHeap;

import java.util.concurrent.locks.ReentrantLock;

public class HeapNode<T>
{
   int tag;
   T priority;
   ReentrantLock lock;

   HeapNode() {
      lock = new ReentrantLock();
      tag = HeapNodeTag.EMPTY;
      priority = (T) Integer.valueOf(0);
   }

   HeapNode(HeapNode<T> node) {
      this.lock = node.lock;
      this.tag = node.tag;
      this.priority = node.priority;
   }

   public void setTag(int tag) {
      this.tag = tag;
   }

   public void setPriority(T priority) {
      this.priority = priority;
   }

   public void set(HeapNode<T> node) {
      this.lock = node.lock;
      this.tag = node.tag;
      this.priority = node.priority;
   }

   public void clear() {
      // Note: no need to clear the lock.
      // lock = new ReentrantLock();
      tag = HeapNodeTag.EMPTY;
      priority = (T) Integer.valueOf(0);
   }

   public void swap(HeapNode<T> other) {
      final HeapNode<T> tmp = new HeapNode<>(this);
      this.tag = other.tag;
      this.priority = other.priority;

      other.tag = tmp.tag;
      other.priority = tmp.priority;
   }
}

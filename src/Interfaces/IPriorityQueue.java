package Interfaces;

public interface IPriorityQueue<T>
{
   boolean enqueue(T e);
   T dequeue();
   int size();
   void clear();
   String getImplementationName();
}

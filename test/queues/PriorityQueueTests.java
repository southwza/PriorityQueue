package queues;

import Interfaces.IPriorityQueue;
import org.apache.commons.math3.exception.NullArgumentException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import queues.JavaLibPriorityQueue;
import queues.ASPriorityQueue;

@RunWith(Parameterized.class)
public class PriorityQueueTests
{
   private IPriorityQueue<Integer> mQueueUnderTest;

   @Parameterized.Parameters()
   public static Collection<IPriorityQueue<Integer>> data() {
      IPriorityQueue<Integer>[] implementations = new IPriorityQueue[]{
            new JavaLibPriorityQueue<Integer>(),
            new ASPriorityQueue<Integer>(),
            new LockFreePriorityQueue<Integer>(),
            // TODO: add other implementations here
      };

      return Arrays.asList(implementations);
   }

   @Before
   public void runBeforeTestMethod() {
      System.out.println("Implementation: " + mQueueUnderTest.getImplementationName());
      mQueueUnderTest.clear();
   }

   public PriorityQueueTests(IPriorityQueue implementation) {
      mQueueUnderTest = implementation;
   }

   @Test(expected = IndexOutOfBoundsException.class)
   public void testEmptyQueue() {
      final Integer value = mQueueUnderTest.dequeue();
      Assert.assertNull(value);
   }

   @Test(expected = NullArgumentException.class)
   public void testNullInput() {
      mQueueUnderTest.enqueue(null);
   }

   @Test
   public void testCorrectness() {
      final Integer testDataSize = 100;
      for (Integer i = 0; i < testDataSize; i++) {
         mQueueUnderTest.enqueue(i);
      }

      for (Integer i = 0; i < testDataSize; i++) {
         Assert.assertEquals(i, mQueueUnderTest.dequeue());
      }
   }

   @Test
   public void testAddingAndRemoving() {
      final int numOfThreadsPerOperation = 50;
      final int numOfThreads = numOfThreadsPerOperation * 2;
      Random rand = new Random(); //instance of random class
      final ExecutorService executorService = Executors.newFixedThreadPool(numOfThreads);
      final ArrayList<Future> listOfFutures = new ArrayList<>();
      for (int i = 0; i < numOfThreadsPerOperation; i++) {
         final Future future = executorService.submit(new Runnable()
         {
            @Override
            public void run() {
               mQueueUnderTest.enqueue(rand.nextInt());
            }
         });
         listOfFutures.add(future);
      }

      // Wait for enqueue operations
      for (Future<Integer> future: listOfFutures)
      {
         try
         {
            future.get();
         } catch (InterruptedException e)
         {
            System.out.println("Exception was thrown." + e.toString());
            e.printStackTrace();
            assert (false);
         } catch (ExecutionException e)
         {
            System.out.println("Exception was thrown." + e.toString());
            e.printStackTrace();
            assert (false);
         }
      }
      // Clear enqueue futures.
      listOfFutures.clear();

      Assert.assertEquals(numOfThreadsPerOperation, mQueueUnderTest.size());

      for (int i = 0; i < numOfThreadsPerOperation; i++) {
         final Future future = executorService.submit(new Runnable()
         {
            @Override
            public void run() {
               final Integer value = mQueueUnderTest.dequeue();
               assertNotNull(value);
            }
         });
         listOfFutures.add(future);
      }

      // Wait for dequeue operations
      for (Future<Integer> future: listOfFutures)
      {
         try
         {
            future.get();
         } catch (InterruptedException e)
         {
            System.out.println("Exception was thrown." + e.toString());
            e.printStackTrace();
            assert (false);
         } catch (ExecutionException e)
         {
            System.out.println("Exception was thrown." + e.toString());
            e.printStackTrace();
            assert (false);
         }
      }

      Assert.assertEquals(0, mQueueUnderTest.size());
   }

   @Test
   public void testAddingAndRemovingSimultaneously() {
      final int numOfInitialAddOperation = 4000;
      final int numOfThreads = 500;
      Random rand = new Random(); //instance of random class
      final ExecutorService executorService = Executors.newFixedThreadPool(numOfThreads);
      final ArrayList<Future> listOfFutures = new ArrayList<>();
      for (int i = 0; i < numOfInitialAddOperation; i++) {
         final Future future = executorService.submit(new Runnable()
         {
            @Override
            public void run() {
               mQueueUnderTest.enqueue(rand.nextInt());
            }
         });
         listOfFutures.add(future);
      }

      // Wait for Add operations
      for (Future<Integer> future: listOfFutures)
      {
         try
         {
            future.get();
         } catch (InterruptedException e)
         {
            System.out.println("Exception was thrown." + e.toString());
            e.printStackTrace();
            assert (false);
         } catch (ExecutionException e)
         {
            System.out.println("Exception was thrown." + e.toString());
            e.printStackTrace();
            assert (false);
         }
      }
      // Clear enqueue futures.
      listOfFutures.clear();

      Assert.assertEquals(numOfInitialAddOperation, mQueueUnderTest.size());

      final int numOfRemoveOperations = 2000;
      final int numOfAddOperations = 2000;

      for (int i = 0; i < numOfRemoveOperations; i++) {
         final Future future = executorService.submit(new Runnable()
         {
            @Override
            public void run() {
               final Integer value = mQueueUnderTest.dequeue();
               assertNotNull(value);
            }
         });
         listOfFutures.add(future);
      }

      for (int i = 0; i < numOfAddOperations; i++) {
         final Future future = executorService.submit(new Runnable()
         {
            @Override
            public void run() {
               mQueueUnderTest.enqueue(rand.nextInt());
            }
         });
         listOfFutures.add(future);
      }

      // Wait for operations
      for (Future<Integer> future: listOfFutures)
      {
         try
         {
            future.get();
         } catch (InterruptedException e)
         {
            System.out.println("Exception was thrown." + e.toString());
            e.printStackTrace();
            assert (false);
         } catch (ExecutionException e)
         {
            System.out.println("Exception was thrown." + e.toString());
            e.printStackTrace();
            assert (false);
         }
      }

      Assert.assertEquals(numOfInitialAddOperation, mQueueUnderTest.size());
   }

   @Test
   public void testLockFreeQueue1() {
      assertEquals(0, mQueueUnderTest.size(), "queue is empty");
      assertEquals(0, mQueueUnderTest.size(), "queue is empty");

      mQueueUnderTest.enqueue(10);
      mQueueUnderTest.enqueue(20);
      assertEquals(2, mQueueUnderTest.size());
      assertEquals(10, mQueueUnderTest.dequeue());
      assertEquals(20, mQueueUnderTest.dequeue());
      assertEquals(0, mQueueUnderTest.size(), "queue is empty");

   }

   @Test
   public void testLockFreeQueue2() {
      ArrayList<Thread> threads = new ArrayList<Thread>();
      for (int tid = 0; tid < 8; tid++) {
         Thread t = new Thread() {
            public void run() {
               for (int i = 0; i < 1000; i++) {
                  mQueueUnderTest.enqueue(i);
               }
            }
         };
         threads.add(t);
         t.start();
      }

      //Wait for all the threads
      threads.forEach(t -> {
         try {
            t.join();
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      });

      assertEquals(8000, mQueueUnderTest.size(), "queue size is good");

      threads = new ArrayList<Thread>();
      for (int tid = 0; tid < 8; tid++) {
         Thread t = new Thread() {
            public void run() {
               for (int i = 0; i < 1000; i++) {
                  mQueueUnderTest.dequeue();
               }
            }
         };
         threads.add(t);
         t.start();
      }

      //Wait for all the threads
      threads.forEach(t -> {
         try {
            t.join();
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      });

      assertEquals(0, mQueueUnderTest.size(), "queue is empty");
   }
}

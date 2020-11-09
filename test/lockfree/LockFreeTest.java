package lockfree;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

class LockFreeTest {

    @Test
    void test() {
        BinomialHeap<Integer> lockFree = new BinomialHeap<>();

        final ExecutorService executorService = Executors.newFixedThreadPool(8);
        final ArrayList<Future<?>> listOfFutures = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            final Future<?> future = executorService.submit(() -> {
                for (int j = 0; j < 100; j++) {
                    try {
                        lockFree.insert(j);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
            listOfFutures.add(future);
        }

        // Wait for enqueue operations
        for (Future<?> future: listOfFutures)
        {
           try
           {
              future.get();
           } catch (InterruptedException | ExecutionException e)
           {
              System.out.println("Exception was thrown." + e.toString());
              e.printStackTrace();
              assert (false);
           }
        }
        // Clear enqueue futures.
        listOfFutures.clear();


        assertEquals(0, lockFree.minimum());

    }

    @Test
    void testDeleteMin() throws Exception {
        BinomialHeap<Integer> lockFree = new BinomialHeap<>();
        Integer val = lockFree.deleteMin();
        assertNull(val);
        lockFree.insert(3);
        lockFree.insert(6);
        lockFree.insert(0);
        lockFree.insert(2);
        lockFree.insert(4);
        assertEquals(0, lockFree.deleteMin());
        assertEquals(2, lockFree.deleteMin());
        assertEquals(3, lockFree.deleteMin());
        assertEquals(4, lockFree.deleteMin());
        assertEquals(6, lockFree.deleteMin());
        assertNull(lockFree.deleteMin());
    }

    @Test
    void testDeleteMin2() {
        BinomialHeap<Integer> lockFree = new BinomialHeap<>();

        final ExecutorService executorService = Executors.newFixedThreadPool(8);
        ArrayList<Future<?>> listOfFutures = new ArrayList<>();

        //Thread #1
        Future<?> future = executorService.submit(() -> {
            for (int j = 0; j < 1000; j++) {
                try {
                    lockFree.insert(j);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        listOfFutures.add(future);

        //Thread #2
        future = executorService.submit(() -> {
            for (int j = 1000; j < 2000; j++) {
                try {
                    lockFree.insert(j);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        listOfFutures.add(future);

        // Wait for enqueue operations
        for (Future<?> f: listOfFutures)
        {
           try
           {
              future.get();
           } catch (InterruptedException | ExecutionException e)
           {
              System.out.println("Exception was thrown." + e.toString());
              e.printStackTrace();
              assert (false);
           }
        }

        assertEquals(2000, lockFree.size());
        assertFalse(lockFree.isEmpty());

        for (int i = 0; i < 2000; i++) {
            assertEquals(i, lockFree.deleteMin());
        }

        assertEquals(0, lockFree.size());
        assertTrue(lockFree.isEmpty());

        assertNull(lockFree.deleteMin());
    }

}

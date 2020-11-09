package unsynchronized;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import unsynchronized.ASPriorityQueue;

public class TestASPriorityQueue {

    @Test
    public void testAddElements() {
        List<Integer> list = Arrays.asList( 12,10,15,14,11,2,6,2,18 );
        offerAndPoll(list);

        list = Arrays.asList( 8,12,8,6,7,2,1,4,6,12,23,4,4,9 );
        offerAndPoll(list);
    }

    private void offerAndPoll(List<Integer> list) {
        ASPriorityQueue<Integer> queue = new ASPriorityQueue<>();
        List<Integer> results = new ArrayList<Integer>();
        for (Integer i : list) {
            queue.offer(i);
        }
        while (queue.size() > 0) {
            results.add(queue.poll());
        }

        Collections.sort(list);
        System.out.println("list:    " + list);
        System.out.println("results: " + results);
        assertTrue(list.equals(results));
    }
}

package lowe_lockfree_queue;

import java.util.Random;

public class Backoff {
    int limit;
    int minDelay;
    int maxDelay;
    float ratio;

    //Values in nanos
    public Backoff(int minDelay, int maxDelay, float ratio) {
        this.minDelay = minDelay;
        this.limit = minDelay;
        this.maxDelay = maxDelay;
        this.ratio = ratio;
    }

    //https://stackoverflow.com/questions/11498585/how-to-suspend-a-java-thread-for-a-small-period-of-time-like-100-nanoseconds
    public void backoff() {
        Random random = new Random();
        int delay = 1 + random.nextInt(limit);
        limit = Math.min((int) (ratio * limit), maxDelay);
        //since the delay is in nanonsecs, let's do a busy wait instead of a Thread.sleep
        long elapsed;
        final long start = System.nanoTime();
        do {
            elapsed = System.nanoTime() - start;
        } while (elapsed < delay);
    }

    public void reset() {
        limit = minDelay;
    }
}
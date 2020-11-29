package MichaelSLockBasedPriorityQueueHeap;

public class BitReversedCounter {
   int counter;
   int reversed;
   int highBit;

   BitReversedCounter() {
      counter = 0;
      reversed = 0;
      highBit = -1;
   }

   public void clear() {
      counter = 0;
      reversed = 0;
      highBit = -1;
   }

   public int getCounter() {
      return counter;
   }

   public int getReversed() {
      return reversed;
   }

   private int xor(int i, int bit) {
      int mask = 1 << (bit - 1);
      return i ^ mask;
   }

   private boolean andBit(int i, int bit) {
      int mask = 1 << (bit - 1);
      return (i & mask) != 0;
   }

   public int bit_reversed_increment() {
      counter++;

      int bit = highBit - 1;
      while (bit > 0) {
         reversed = xor(reversed, bit);
         if (andBit(reversed, bit)) {
            break;
         }
         bit--;
      }

      if (bit <= 0) {
         reversed = counter;
         highBit++;
      }

      return reversed;
   }

   public int bit_reversed_decrement()
   {
      counter--;

      int bit = highBit - 1;
      while (bit > 0)
      {
         reversed = xor(reversed, bit);
         if (!andBit(reversed, bit))
         {
            break;
         }
         bit--;
      }

      if (bit <= 0)
      {
         reversed = counter;
         highBit--;
      }

      return reversed;
   }

   public static void main(String[] args) {
      final BitReversedCounter bitReversedCounter = new BitReversedCounter();
      for (int i = 0; i < 50; i++) {
         System.out.println("Current Location: " + bitReversedCounter.bit_reversed_increment());
      }
   }
}

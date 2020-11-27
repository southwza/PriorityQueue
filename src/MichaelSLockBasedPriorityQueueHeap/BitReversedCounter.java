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

   public int xor(int i, int bit) {
      int mask = 1 << (bit - 1);
      return i ^ mask;
   }

   public boolean andBit(int i, int bit) {
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
}

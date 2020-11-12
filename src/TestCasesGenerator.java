import java.util.Random;

public class TestCasesGenerator
{
   // NOTE: Assumes lower value is higher priority
   enum TestCaseMode
   {
      kBestCase,
      kWorstCase,
      kRandom
   }

   // Usage: java TestCasesGenerator size TestCaseMode OutputFile.txt
   // Example: java TestCasesGenerator 10 0 test1.txt
   public static void main(String args[])
   {
      final int kExpectedNumberOfArguments = 3;
      final boolean kDebug = false;

      String outputStr;

      if (kExpectedNumberOfArguments == args.length)
      {
         final int kDataSetSize = Integer.parseInt(args[0]);
         final int kMode = Integer.parseInt(args[1]);
         final String kFileName = args[2];

         TestCaseMode mode = TestCaseMode.kBestCase;

         switch(kMode)
         {
            case 0:
               mode = TestCaseMode.kBestCase;
               break;
            case 1:
               mode = TestCaseMode.kWorstCase;
               break;
            case 2:
               mode = TestCaseMode.kRandom;
               break;
            default:
               System.out.println("Invalid Mode!");
               System.out.println("Valid Modes: BestCase:0 WorstCase:1 Random:2");
               System.exit(-1);
         }

         outputStr = getTestCaseOutputStr(mode, kDataSetSize);
         Utils.writeToFile(outputStr, kFileName);
      } else if (kDebug) {
         outputStr = getTestCaseOutputStr(TestCaseMode.kBestCase, 10);
         Utils.writeToFile(outputStr, "debug_output.txt");
      }
      else
      {
         System.out.println("Invalid Number of Arguments");
         System.out.println("Usage: java TestCasesGenerator NumberOfPreferences Mode outputFile.txt");
         System.out.println("Example: java TestCasesGenerator 100 0 test1.txt");
         System.out.println("Valid Modes: BestCase:0 WorstCase:1 Random:2");
         System.exit(-1);
      }
   }

   private static String getTestCaseOutputStr(TestCaseMode mode, int size) {
      String outputStr = "";

      switch (mode) {
         case kBestCase:
            outputStr = getBestCaseOutputStr(size);
            break;
         case kWorstCase:
            outputStr = getWorstCaseOutputStr(size);
            break;
         case kRandom:
            outputStr = getRandomCaseOutputStr(size);
            break;
         default:
            System.out.println("Invalid Mode!");
            System.out.println("Valid Modes: BestCase:0 WorstCase:1 Random:2");
            System.exit(-1);
      }

      return outputStr;
   }

   private static int[] getBestCaseOutput(int size) {
      int[] array = new int[size];
      for (int i = 0; i < size; i++) {
         array[i] = i;
      }

      return array;
   }

   private static String getBestCaseOutputStr(int size) {
      final int[] array = getBestCaseOutput(size);
      return arrayToOutputString(array);
   }

   private static int[] getWorstCaseOutput(int size) {
      int[] array = new int[size];
      for (int i = size - 1, j = 0; i >= 0; i--, j++) {
         array[j] = i;
      }

      return array;
   }

   private static String getWorstCaseOutputStr(int size) {
      final int[] array = getWorstCaseOutput(size);
      return arrayToOutputString(array);
   }

   private static int[] getRandomCaseOutput(int size) {
      Random random = new Random();
      int[] array = new int[size];
      for (int i = 0; i < size; i++) {
         array[i] = random.nextInt();
      }

      return array;
   }

   private static String getRandomCaseOutputStr(int size) {
      final int[] array = getRandomCaseOutput(size);
      return arrayToOutputString(array);
   }

   private static String arrayToOutputString(int[] array) {
      StringBuilder outStr = new StringBuilder();
      final int size = array.length;
      for (int i = 0; i < size; i++)
      {
         outStr.append(array[i]);
         if (i+1 != size) {
            outStr.append('\n');
         }
      }

      return outStr.toString();
   }
}

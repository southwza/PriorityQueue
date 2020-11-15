package Utils;

import java.util.Random;

public class TestCasesGenerator
{
   // NOTE: Assumes lower value is higher priority
   enum TestCaseMode
   {
      AscendingOrder,
      DescendingOrder,
      RandomOrder
   }

   private static String kUsageHelpMessage =
               "Usage: java Utils.TestCasesGenerator DataSize Mode \n" +
               "Valid Modes: AscendingOrder:0 DescendingOrder:1 Random:2 \n" +
               "Example: java Utils.TestCasesGenerator 100 0 \n";

   // Usage: java Utils.TestCasesGenerator size TestCaseMode OutputFile.txt
   // Example: java Utils.TestCasesGenerator 10 0 test1.txt
   public static void main(String args[])
   {
      final int kExpectedNumberOfArguments = 2;
      final boolean kDebug = false;

      String outputStr;

      if (kExpectedNumberOfArguments == args.length)
      {
         final int kDataSetSize = Integer.parseInt(args[0]);
         final int kMode = Integer.parseInt(args[1]);

         TestCaseMode mode = TestCaseMode.AscendingOrder;

         switch(kMode)
         {
            case 0:
               mode = TestCaseMode.AscendingOrder;
               break;
            case 1:
               mode = TestCaseMode.DescendingOrder;
               break;
            case 2:
               mode = TestCaseMode.RandomOrder;
               break;
            default:
               System.out.println("Invalid Mode!");
               System.out.println(kUsageHelpMessage);
               System.exit(-1);
         }

         outputStr = getTestCaseOutputStr(mode, kDataSetSize);
         final String kOutputFileName = getOutputFileName(mode, kDataSetSize);
         UtilityFunctions.writeToFile(outputStr, kOutputFileName);
      } else if (kDebug) {
         final int kDataSetSize = 10;
         final TestCaseMode mode = TestCaseMode.AscendingOrder;
         outputStr = getTestCaseOutputStr(mode, kDataSetSize);
         final String kOutputFileName = getOutputFileName(mode, kDataSetSize);;
         UtilityFunctions.writeToFile(outputStr, "debug_output.txt");
      }
      else
      {
         System.out.println("Invalid Number of Arguments");
         System.out.println(kUsageHelpMessage);
         System.exit(-1);
      }
   }

   private static String getOutputFileName(TestCaseMode mode, int dataSize) {
      return mode.toString() + "_" + dataSize + ".txt";
   }

   private static String getTestCaseOutputStr(TestCaseMode mode, int size) {
      String outputStr = "";

      switch (mode) {
         case AscendingOrder:
            outputStr = getBestCaseOutputStr(size);
            break;
         case DescendingOrder:
            outputStr = getWorstCaseOutputStr(size);
            break;
         case RandomOrder:
            outputStr = getRandomCaseOutputStr(size);
            break;
         default:
            System.out.println("Invalid Mode!");
            System.out.println(kUsageHelpMessage);
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

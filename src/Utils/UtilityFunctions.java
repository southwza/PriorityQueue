package Utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UtilityFunctions
{
   public static void writeToFile(String outputString, String outputFileName)
   {
      try
      {
         BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));
         writer.write(outputString);
         writer.close();
      }
      catch (Exception e)
      {
         System.out.println("Exception While Writing To File: " + e.toString());
         System.exit(-1);
      }
   }

   public static int[] readFromFile(final String fileName)
   {
      if (fileName == null || fileName.isEmpty()) {
         throw new IllegalArgumentException();
      }

      try (Stream<String> stream = Files.lines(Paths.get(fileName)))
      {
         final List<String> fileLines = stream.collect(Collectors.toList());
         final int size = fileLines.size();
         final int[] array = new int[size];

         for (int lineIndex = 0; lineIndex < size; ++lineIndex)
         {
            array[lineIndex] = Integer.parseInt(fileLines.get(lineIndex));
         }

         return array;
      }
      catch (Exception e)
      {
         System.out.println("Exception While Reading File: " + e.toString());
         System.exit(-1);
         return null;
      }
   }
}

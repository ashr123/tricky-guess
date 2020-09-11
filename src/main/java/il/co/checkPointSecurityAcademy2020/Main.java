package il.co.checkPointSecurityAcademy2020;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class Main
{
	public static void main(String... args) throws IOException
	{
		long startTime = System.nanoTime();
		final var ref = new Object()
		{
			List<String> wordList = Files.readAllLines(Path.of(Objects.requireNonNull(Main.class.getClassLoader().getResource("words.txt")).getPath()));
		};
		System.out.println("Read file in " + ((System.nanoTime() - startTime) / 1e9) + " seconds");

		startTime = System.nanoTime();
		final boolean collect = isUniqueCharsInCollection(ref.wordList);
		System.out.println("Checked list in " + ((System.nanoTime() - startTime) / 1e9) + " seconds");
		System.out.println(collect + "\n");


		try (Scanner scanner = new Scanner(System.in))
		{
			for (int i = 0, num = 3; i < 15; i++)
			{
				if (ref.wordList.size() == 0)
					return;
				System.out.print("Chosen word: " + ref.wordList.get(0) +
				                 "\nEnter number of characters you were correct with: ");
				num = scanner.nextByte();
				if (num == -1)
					return;
				startTime = System.nanoTime();
				final int finalNum = num;
				ref.wordList = ref.wordList.parallelStream()
						.filter(word -> word.chars()
								                .filter(ch -> ref.wordList.get(0).indexOf(ch) != -1)
								                .count() == finalNum)
						.collect(Collectors.toList());
				System.out.println("getPerms() computed in " + ((System.nanoTime() - startTime) / 1e9) + " seconds." +
				                   "\nList size: " + ref.wordList.size());
			}
		}


//		Process childProc = Runtime.getRuntime().exec(new String[]{"nc", "tricky-guess.csa-challenge.com", "2222", "-v"});
//
//		childProc.onExit().thenApply(process ->
//		{
//			System.exit(process.exitValue());
//			return process.exitValue();
//		});
//		final var ref = new Object()
//		{
//			transient String outputString;
//		};
//
//		new Thread(() ->
//		{
//			try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream())
//			{
//				while (true)
//				{
//					childProc.getInputStream().transferTo(byteArrayOutputStream);
//					System.out.println(ref.outputString = new String(byteArrayOutputStream.toByteArray()));
//					byteArrayOutputStream.flush();
//				}
//			}
//			catch (IOException e)
//			{
//				e.printStackTrace();
//			}
//		}).start();
//
////		Scanner scanner = new Scanner(System.in);
////		System.out.println("Started!!!");
//		Thread.sleep(4000);
//		try (OutputStreamWriter outputStream = new OutputStreamWriter(childProc.getOutputStream()))
//		{
//			for (int i = 0; i < 15; i++)
//			{
//				System.out.println("Sending word: " + wordList.get(0));
//				outputStream.write(wordList.get(0));
//				outputStream.flush();
//				while (ref.outputString == null)
//					Thread.sleep(500);
//				System.out.println("The answer is: " + ref.outputString);
//				wordList = getPerms(wordList, wordList.get(0), Integer.parseInt(ref.outputString));
//			}
//		}
	}

	private static boolean isUniqueCharsInCollection(Collection<String> stringCollection)
	{
		return stringCollection.parallelStream()
				.map(word -> word.chars()
						.mapToObj(c -> (char) c)
						.collect(groupingBy(Function.identity(), counting())))
				.flatMap(characterLongMap -> characterLongMap.values().parallelStream())
				.allMatch(num -> num == 1);
	}
}

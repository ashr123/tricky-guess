package il.co.checkPointSecurityAcademy2020;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class Main
{
	public static void main(String... args) throws IOException, InterruptedException
	{
//		File file = new File(Objects.requireNonNull(Main.class.getClassLoader().getResource("words.txt")).getPath());
//		System.out.println("File Found : " + file.exists());
		long startTime = System.nanoTime();
		List<String> wordList = Files.readAllLines(Path.of(Objects.requireNonNull(Main.class.getClassLoader().getResource("words.txt")).getPath()));
		System.out.println("Read file in " + ((System.nanoTime() - startTime) / 1e9) + " seconds");

		startTime = System.nanoTime();
		final boolean collect = isUniqueCharsInCollection(wordList);
		System.out.println("Mapped list in " + ((System.nanoTime() - startTime) / 1e9) + " seconds");
		System.out.println(collect);
		Process childProc = Runtime.getRuntime().exec(new String[]{"nc", "tricky-guess.csa-challenge.com", "2222", "-v"});

		childProc.onExit().thenApply(process ->
		{
			System.exit(process.exitValue());
			return process.exitValue();
		});

		final var ref = new Object()
		{
			String outputString;
		};

		new Thread(() ->
		{
			try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream())
			{
				while (true)
				{
					childProc.getInputStream().transferTo(byteArrayOutputStream);
					System.out.println(ref.outputString = new String(byteArrayOutputStream.toByteArray()));
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}).start();

//      krzvctoxdnya
//		Scanner scanner = new Scanner(System.in);
//		System.out.println("Started!!!");
		Thread.sleep(4000);
		try (OutputStreamWriter outputStream = new OutputStreamWriter(childProc.getOutputStream()))
		{
			for (int i = 0; i < 15; i++)
			{
				System.out.println(wordList.get(0));
				outputStream.write(wordList.get(0));
				outputStream.flush();
				Thread.sleep(500);
				wordList = getPerms(wordList, wordList.get(0), Integer.parseInt(ref.outputString));
			}
		}
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

	private static List<String> getPerms(Collection<String> stringCollection, String prevChosenWord, int commonChars)
	{
		return stringCollection.parallelStream()
				.filter(word -> word.chars()
						                .mapToObj(ch -> String.valueOf((char) ch))
						                .filter(prevChosenWord::contains)
						                .count() == commonChars)
				.collect(Collectors.toList());
	}
}

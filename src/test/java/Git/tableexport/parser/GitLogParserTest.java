package Git.tableexport.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import Git.tableexport.model.Author;
import Git.tableexport.model.ChangedFile;
import Git.tableexport.model.Commit;
import Git.tableexport.parser.GitLogParser;

@DisplayName("gitLogParserTest")
public class GitLogParserTest {

	final GitLogParser parser = new GitLogParser();

	@Test
	public void parseCommitLogTest() {
		List<String> commitData = readData("TestFiles/gitLogTestFile.txt");
		List<Commit> commits = parser.parseCommitLog(commitData);

		assertFalse(commits.isEmpty());
		assertTrue(commits.size() == 2);
	}

	@Test
	public void parseSingleCommitTest() {
		List<String> singleCommitMessage = readData("TestFiles/singleCommit.txt");
		Commit singleCommit = parser.parseSingleCommit(singleCommitMessage);
		assertNotNull(singleCommit);
		assertNotNull(singleCommit.getAuthor());
		assertNotNull(singleCommit.getDate());
		assertFalse(singleCommit.getFiles().isEmpty());
		assertFalse(singleCommit.getCommitId().isEmpty());
	}

	@Test
	public void parseSingleCommitLeadingWhiteSpaceTest() {
		List<String> singleCommitMessage = new ArrayList<>();
		singleCommitMessage.add("");
		singleCommitMessage.addAll(readData("TestFiles/singleCommit.txt"));
		Commit singleCommit = parser.parseSingleCommit(singleCommitMessage);
		assertNotNull(singleCommit);
		assertNotNull(singleCommit.getAuthor());
		assertNotNull(singleCommit.getDate());
		assertFalse(singleCommit.getFiles().isEmpty());
		assertFalse(singleCommit.getCommitId().isEmpty());
	}

	private List<String> readData(String filename) {
		List<String> data = new ArrayList<>();
		try (Stream<String> stream = Files.lines(Paths.get(filename))) {
			data = stream.collect(Collectors.toList());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return data;
	}

	@Test
	public void extractCommitTest() {
		Optional<Commit> extractedCommit = parser.extractCommit("commit 123456789");
		assertTrue(extractedCommit.isPresent());
		assertTrue(extractedCommit.get().getCommitId().equals("123456789"));
		assertNull(extractedCommit.get().getAuthor());
		assertNull(extractedCommit.get().getDate());
		assertTrue(extractedCommit.get().getFiles().isEmpty());
	}

	@Test
	public void extractCommitWithInvalidStringTest() {
		Optional<Commit> extractedCommit = parser.extractCommit("commit ");
		assertFalse(extractedCommit.isPresent());
		extractCommitWithInvalidInitialStringTest();
	}

	@Test
	public void extractCommitWithInvalidInitialStringTest() {
		Optional<Commit> extractedCommit = parser.extractCommit("ommit");
		assertFalse(extractedCommit.isPresent());
	}

	@Test
	public void extractAuthorTest() {
		Optional<Author> extractAuthor = parser.extractAuthor("Author: Max Mustermann <maxmustermann@musterhost.de>");
		assertTrue(extractAuthor.isPresent());
		assertTrue(extractAuthor.get().getName().equals("Max Mustermann"));
		assertTrue(extractAuthor.get().getEmail().equals("maxmustermann@musterhost.de"));

	}

	@Test
	public void extractAuthorWithEmptyFieldTest() {
		Optional<Author> extractAuthor = parser.extractAuthor("Author: ");
		assertFalse(extractAuthor.isPresent());
	}

	@Test
	public void extractAuthorWithEmptyStringTest() {
		Optional<Author> extractAuthor = parser.extractAuthor("");
		assertFalse(extractAuthor.isPresent());
	}

	@Test
	public void extractAuthorWithNoNameStringTest() {
		Optional<Author> extractAuthor = parser.extractAuthor("Author: <maxmustermann@musterhost.de>");
		assertFalse(extractAuthor.isPresent());
	}

	@Test
	public void extractAuthorWithNoEMailStringTest() {
		Optional<Author> extractAuthor = parser.extractAuthor("Author: Max Mustermann");
		assertFalse(extractAuthor.isPresent());
	}

	@Test
	public void extractAuthorWithInvalidMailTest() {
		Optional<Author> extractAuthor = parser.extractAuthor("Author: Max Mustermann <maxmustermannmusterhost.de>");
		assertFalse(extractAuthor.isPresent());
	}

	@Test
	public void extractAuthorWithGreaterNameTest() {
		Optional<Author> extractAuthor = parser
				.extractAuthor("Author: Violeta Georgieva Georgieva <violetagg@apache.org>");
		assertTrue(extractAuthor.isPresent());
	}

	@Test
	public void extractDateTest() {
		Optional<LocalDateTime> extractedDate = parser.extractDate("Date:   Tue Nov 6 13:06:58 2018 +0000");
		assertTrue(extractedDate.isPresent());
		LocalDateTime ldt = extractedDate.get();
		assertTrue(ldt.getDayOfWeek() == DayOfWeek.TUESDAY);
		assertTrue(ldt.getDayOfMonth() == 6);
		assertTrue(ldt.getMonth() == Month.NOVEMBER);
		assertTrue(ldt.getYear() == 2018);
		assertTrue(ldt.getHour() == 13);
		assertTrue(ldt.getMinute() == 6);
		assertTrue(ldt.getSecond() == 58);
	}

	@Test
	public void extractChangedFileNumstatTest() {
		Optional<ChangedFile> extractChangedFile = parser
				.extractChangedFile("11	12	test/org/apache/catalina/startup/LoggingBaseTest.java");
		assertTrue(extractChangedFile.isPresent());
		assertTrue(extractChangedFile.get().getAddedLines() == 11);
		assertTrue(extractChangedFile.get().getDeletedLines() == 12);
		assertTrue(extractChangedFile.get().getPath().toString()
				.equals("test/org/apache/catalina/startup/LoggingBaseTest.java"));
		assertTrue(extractChangedFile.get().getPath().getFileName().toString().equals("LoggingBaseTest.java"));
	}

	@Test
	public void extractChangedFileNameStatusTest() {
		List<String> testList = Arrays.asList("A	core/src/test/java/com/graphhopper/storage/GraphHopperStorageCHTest.java",
				"C	core/src/test/java/com/graphhopper/storage/GraphHopperStorageCHTest.java",
				"D	core/src/test/java/com/graphhopper/storage/GraphHopperStorageCHTest.java",
				"M	core/src/test/java/com/graphhopper/storage/GraphHopperStorageCHTest.java",
				"R	core/src/test/java/com/graphhopper/storage/GraphHopperStorageCHTest.java",
				"T	core/src/test/java/com/graphhopper/storage/GraphHopperStorageCHTest.java",
				"U	core/src/test/java/com/graphhopper/storage/GraphHopperStorageCHTest.java",
				"X	core/src/test/java/com/graphhopper/storage/GraphHopperStorageCHTest.java",
				"B	core/src/test/java/com/graphhopper/storage/GraphHopperStorageCHTest.java");
		for (String string : testList) {
			Optional<ChangedFile> extractChangedFile = parser
					.extractChangedFile(string);
			assertTrue(extractChangedFile.isPresent());
			assertTrue(extractChangedFile.get().getAddedLines() == 0);
			assertTrue(extractChangedFile.get().getDeletedLines() == 0);
			assertTrue(extractChangedFile.get().getStatus() == string.charAt(0));
			assertTrue(extractChangedFile.get().getPath().toString()
					.equals("core/src/test/java/com/graphhopper/storage/GraphHopperStorageCHTest.java"));
			assertTrue(extractChangedFile.get().getPath().getFileName().toString().equals("GraphHopperStorageCHTest.java"));
		}
		
	}

}

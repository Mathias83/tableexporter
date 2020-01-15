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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import Git.tableexport.model.Author;
import Git.tableexport.model.ChangedFile;
import Git.tableexport.model.Commit;

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
		Commit extractedCommit = parser.extractCommit("commit 123456789");
		assertTrue(extractedCommit.getCommitId().equals("123456789"));
		assertNull(extractedCommit.getAuthor());
		assertNull(extractedCommit.getDate());
		assertTrue(extractedCommit.getFiles().isEmpty());
	}

	@Disabled
	@Test
	public void extractAuthorTest() {
		Author extractAuthor = parser.extractAuthor("Author: Max Mustermann <maxmustermann@musterhost.de>");
		assertTrue(extractAuthor.getName().equals("Max Mustermann"));
		assertTrue(extractAuthor.getEmail().equals("maxmustermann@musterhost.de"));

	}

	@Disabled
	@Test
	public void extractAuthorWithEmptyFieldTest() {
		Author extractAuthor = parser.extractAuthor("Author: ");
		assertTrue(extractAuthor.getName().equals("Nobody"));
		assertTrue(extractAuthor.getEmail().equals("nobody@neverland.de"));
	}

	@Test
	public void extractAuthorWithEmptyStringTest() {
		Author extractAuthor = parser.extractAuthor("");
		assertTrue(extractAuthor.getName().equals("Nobody"));
		assertTrue(extractAuthor.getEmail().equals("nobody@neverland.de"));
	}

	@Disabled
	@Test
	public void extractAuthorWithNoNameStringTest() {
		Author extractAuthor = parser.extractAuthor("Author: <maxmustermann@musterhost.de>");
		assertTrue(extractAuthor.getName().equals("NoName"));
		assertTrue(extractAuthor.getEmail().equals("maxmustermann@musterhost.de"));
	}

	@Disabled
	@Test
	public void extractAuthorWithNoEMailStringTest() {
		Author extractAuthor = parser.extractAuthor("Author: Max Mustermann");
		assertTrue(extractAuthor.getName().equals("Nobody"));
		assertTrue(extractAuthor.getEmail().equals("nobody@neverland.de"));
	}

	@Disabled
	@Test
	public void extractAuthorWithInvalidMailTest() {
		Author extractAuthor = parser.extractAuthor("Author: Max Mustermann <maxmustermannmusterhost.de>");
		assertTrue(extractAuthor.getName().equals("Nobody"));
		assertTrue(extractAuthor.getEmail().equals("nobody@neverland.de"));
	}

	@Disabled
	@Test
	public void extractAuthorWithGreaterNameTest() {
		Author extractAuthor = parser.extractAuthor("Author: Violeta Georgieva Georgieva <violetagg@apache.org>");
		assertTrue(extractAuthor.getName().equals("Violeta Georgieva Georgieva"));
		assertTrue(extractAuthor.getEmail().equals("violetagg@apache.org"));
	}

	@Test
	public void extractDateTest() {
		LocalDateTime extractedDate = parser.extractDate("Date:   Tue Nov 6 13:06:58 2018 +0000");
		assertTrue(extractedDate.getDayOfWeek() == DayOfWeek.TUESDAY);
		assertTrue(extractedDate.getDayOfMonth() == 6);
		assertTrue(extractedDate.getMonth() == Month.NOVEMBER);
		assertTrue(extractedDate.getYear() == 2018);
		assertTrue(extractedDate.getHour() == 13);
		assertTrue(extractedDate.getMinute() == 6);
		assertTrue(extractedDate.getSecond() == 58);
	}

	@Test
	public void extractChangedFileNumstatTest() {
		ChangedFile extractChangedFile = parser
				.extractChangedFile("11	12	test/org/apache/catalina/startup/LoggingBaseTest.java");
		assertTrue(extractChangedFile.getAddedLines() == 11);
		assertTrue(extractChangedFile.getDeletedLines() == 12);
		assertTrue(extractChangedFile.getPath().toString()
				.equals("test/org/apache/catalina/startup/LoggingBaseTest.java"));
		assertTrue(extractChangedFile.getPath().getFileName().toString().equals("LoggingBaseTest.java"));
	}

	@Test
	public void extractChangedFileNameStatusTest() {
		List<String> testList = Arrays.asList(
				"A	core/src/test/java/com/graphhopper/storage/GraphHopperStorageCHTest.java",
				"C	core/src/test/java/com/graphhopper/storage/GraphHopperStorageCHTest.java",
				"D	core/src/test/java/com/graphhopper/storage/GraphHopperStorageCHTest.java",
				"M	core/src/test/java/com/graphhopper/storage/GraphHopperStorageCHTest.java",
				"R	core/src/test/java/com/graphhopper/storage/GraphHopperStorageCHTest.java",
				"T	core/src/test/java/com/graphhopper/storage/GraphHopperStorageCHTest.java",
				"U	core/src/test/java/com/graphhopper/storage/GraphHopperStorageCHTest.java",
				"X	core/src/test/java/com/graphhopper/storage/GraphHopperStorageCHTest.java",
				"B	core/src/test/java/com/graphhopper/storage/GraphHopperStorageCHTest.java");
		for (String string : testList) {
			ChangedFile extractChangedFile = parser.extractChangedFile(string);
			assertTrue(extractChangedFile.getAddedLines() == 0);
			assertTrue(extractChangedFile.getDeletedLines() == 0);
			assertTrue(extractChangedFile.getStatus() == string.charAt(0));
			assertTrue(extractChangedFile.getPath().toString()
					.equals("core/src/test/java/com/graphhopper/storage/GraphHopperStorageCHTest.java"));
			assertTrue(extractChangedFile.getPath().getFileName().toString().equals("GraphHopperStorageCHTest.java"));
		}

	}

	@Test
	public void parseBinaryFilesTest() {
		List<String> commitData = readData("TestFiles/gitLogChangedFilesTest.txt");
		List<Commit> commits = parser.parseCommitLog(commitData);
		assertFalse(commits.isEmpty());
		assertTrue(commits.size() == 2);
		assertTrue(commits.get(0).getFiles().size() == 3);
		assertTrue(commits.get(1).getFiles().size() == 2);

	}

}

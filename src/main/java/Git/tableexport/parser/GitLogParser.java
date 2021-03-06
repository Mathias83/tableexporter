package Git.tableexport.parser;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.util.StringUtils;

import Git.tableexport.model.Author;
import Git.tableexport.model.ChangedFile;
import Git.tableexport.model.Commit;

public class GitLogParser {
	List<String> fileExtensions = new ArrayList<>();
	protected static Logger logger = LogManager.getLogger(GitLogParser.class);
	List<String> doubleID = new ArrayList<>();
	int length = 0;

	public GitLogParser() {
		super();

	}

	public List<Commit> parseCommitLog(List<String> commitData) {

		List<String> singleCommitMessage = new ArrayList<>();
		List<Commit> extractedCommits = new ArrayList<>();

		for (int i = 0; i < commitData.size(); i++) {
			if (singleCommitMessage.isEmpty() && !commitData.get(i).startsWith("commit"))
				continue;
			if (!singleCommitMessage.isEmpty() && commitData.get(i).startsWith("commit")) {
				extractedCommits.add(parseSingleCommit(singleCommitMessage));
				singleCommitMessage = new ArrayList<>();
			}
			singleCommitMessage.add(commitData.get(i));
		}
		extractedCommits.add(parseSingleCommit(singleCommitMessage));

		return extractedCommits;
	}

	protected Commit parseSingleCommit(List<String> singleCommitMessage) {

		Commit commit = new Commit("empty");
		List<ChangedFile> changedFiles = new ArrayList<>();

		try {
			for (Iterator<String> iterator = singleCommitMessage.iterator(); iterator.hasNext();) {
				String string = (String) iterator.next();
				if (string.isEmpty() || string.isBlank())
					continue;
				else if (isCommitLine(string))
					commit = extractCommit(string);
				else if (isAuthorLine(string))
					commit.setAuthor(extractAuthor(string));
				else if (isDateLine(string))
					commit.setDate(extractDate(string));
				else if (isChangedFileLine(string))
					changedFiles.add(extractChangedFile(string));
				else if (!string.isEmpty())
					commit.appendDescription(string);
				else 
					logger.warn("No case: " + string);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		commit.setFiles(changedFiles);
		
		return commit;
	}

	protected ChangedFile extractChangedFile(String string) {
		String[] split = string.split("\t");
		String extension = extractLanguage(split[split.length - 1]);
		if (!fileExtensions.contains(extension))
			fileExtensions.add(extension);
		if (isNumstatOption(split))
			return new ChangedFile(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Paths.get(split[2]),
					extension);
		if (isBinaryFile(split))
			return new ChangedFile(0, 0, Paths.get(split[2]),
					extension);
		return new ChangedFile(split[0].charAt(0), Paths.get(split[1]), extension);
	}

	private boolean isBinaryFile(String[] split) {		
		return (split[0].equals("-") && split[1].equals("-"));
	}

	private String extractLanguage(String string) {
		if (!string.contains("."))
			return string.substring(string.lastIndexOf("/") + 1);
		return string.substring(string.lastIndexOf(".") + 1);
	}

	private boolean isNumstatOption(String[] split) {
		return (split.length > 2&& !split[0].equals("-")&&!split[1].equals("-"));
	}

	protected LocalDateTime extractDate(String string) {
		String substring = string.substring(8);
		LocalDateTime parsedTime;

		if (substring.length() > 11) {
			parsedTime = LocalDateTime.parse(substring,
					DateTimeFormatter.ofPattern("EEE MMM d HH:mm:ss yyyy Z").localizedBy(Locale.US));
		} else if (substring.length() == 10) {
			parsedTime = LocalDateTime.of(
					LocalDate.parse(substring, DateTimeFormatter.ofPattern("yyyy-MM-dd").localizedBy(Locale.US)),
					LocalTime.MIDNIGHT);
		} else {
			logger.warn("No Valid Date: " + string);
			parsedTime = LocalDateTime.of(1970, Month.JANUARY, 1, 1, 1, 1);
		}
		return parsedTime;
	}

	protected Author extractAuthor(String string) {

		if (string.isEmpty() || !string.startsWith("Author: ")) {
			Author a = new Author("Nobody", "nobody@neverland.de");
			logger.warn("No Valid Author: " + string + " replaced with: " + a.toString());
			return a;
		}
		String[] splitAuthor = string.split(": ");		
		return new Author(splitAuthor[1]);
		/*
		String[] splitAuthor = string.split(": ");
		String[] splitNameAndEmail = splitAuthor[1].split("<");
		if (splitNameAndEmail[0].isEmpty() && !splitNameAndEmail[1].contains("@")) {
			Author a = new Author("NoName", splitNameAndEmail[1]);
			logger.warn("No Valid Author: " + string+ " replaced with: " + a.toString());
			return a;
		}
		if (splitNameAndEmail[0].isEmpty() && splitNameAndEmail[1].contains("@")) {
			return new Author("NoName", splitNameAndEmail[1].substring(0, splitNameAndEmail[1].length() - 1));
		}
		if (!splitNameAndEmail[0].isEmpty() && !splitNameAndEmail[1].contains("@")) {
			return new Author(splitNameAndEmail[0].substring(0, splitNameAndEmail[0].length() - 1), "NoEmail");
		}
		if (splitNameAndEmail[0].isEmpty() && !splitNameAndEmail[1].contains("@")) {
			logger.warn("No Valid Author: " + string);
			return new Author("NoName", splitNameAndEmail[1]);
		}
		return new Author(splitNameAndEmail[0].substring(0, splitNameAndEmail[0].length() - 1),
				splitNameAndEmail[1].substring(0, splitNameAndEmail[1].length() - 1));*/
	}

	protected Commit extractCommit(String string) {
		if (string.isEmpty() || !string.startsWith("commit ")) {
			logger.warn("Commitstring is not Valid: " + string);
			return new Commit("999999zzzzzzImpossibleID");
		}
		String[] split = string.split(" ");
		if (split.length != 2) {
			logger.warn("Commitstring is not Valid: " + string);
			return new Commit("999999zzzzzzImpossibleID");
		}
		if (length<10)
			length = split[1].length();
		if (doubleID.contains(split[1]))
			logger.warn("Double ID: " + split[1]);
		else
			doubleID.add(split[1]);
		if(length!=split[1].length())
			logger.warn("Länger unterschiedlich: " + length + " " + split[1].length());
		return new Commit(split[1]);
	}

	protected boolean isChangedFileLine(String string) {
		return string.matches("^((\\d+\\s+)+|[ACDMRTUXB]{1}\\s+|-\\s+-\\s+)(.+.|.\\s=>\\s\\w+})+");
	}

	protected boolean isDateLine(String string) {
		return string.startsWith("Date:");
	}

	protected boolean isAuthorLine(String string) {
		return string.startsWith("Author:");
	}

	protected boolean isCommitLine(String string) {
		return string.startsWith("commit");
	}

	public List<String> getLanguages() {
		return fileExtensions;
	}

}

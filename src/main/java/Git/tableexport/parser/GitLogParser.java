package Git.tableexport.parser;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import Git.tableexport.model.Author;
import Git.tableexport.model.ChangedFile;
import Git.tableexport.model.Commit;


public class GitLogParser {
	List<String> fileExtensions = new ArrayList<>();

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

				if (isCommitLine(string))
					commit = extractCommit(string).orElseThrow();
				else if (isAuthorLine(string))
					commit.setAuthor(extractAuthor(string).orElseThrow());
				else if (isDateLine(string))
					commit.setDate(extractDate(string).orElseThrow());
				else if (isChangedFileLine(string))
					changedFiles.add(extractChangedFile(string).orElseThrow());
				else if (!string.isEmpty())
					commit.appendDescription(string);

			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		commit.setFiles(changedFiles);

		return commit;
	}

	protected Optional<ChangedFile> extractChangedFile(String string) {
		String[] split = string.split("\t");
		String extension = extractLanguage(split[split.length - 1]);
		if (!fileExtensions.contains(extension))
			fileExtensions.add(extension);
		if (isNumstatOption(split))
			return Optional
					.of(new ChangedFile(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Paths.get(split[2]),extension));
		return Optional.of(new ChangedFile(split[0].charAt(0), Paths.get(split[1]),extension));
	}

	private String extractLanguage(String string) {
		if (!string.contains("."))
			return string.substring(string.lastIndexOf("/") + 1);
		return string.substring(string.lastIndexOf(".") + 1);
	}

	private boolean isNumstatOption(String[] split) {
		return split.length > 2;
	}

	protected Optional<LocalDateTime> extractDate(String string) {
		String substring = string.substring(8);
		LocalDateTime parsedTime;

		if (substring.length() > 11) {
			parsedTime = LocalDateTime.parse(substring,
					DateTimeFormatter.ofPattern("EEE MMM d HH:mm:ss yyyy Z").localizedBy(Locale.US));
		} else {
			parsedTime = LocalDateTime.of(
					LocalDate.parse(substring, DateTimeFormatter.ofPattern("yyyy-MM-dd").localizedBy(Locale.US)),
					LocalTime.MIDNIGHT);
		}
		return Optional.of(parsedTime);
	}

	protected Optional<Author> extractAuthor(String string) {
		
		if (string.isEmpty() || !string.startsWith("Author: ") || !string.contains("@"))
			return Optional.empty();
		String[] splitAuthor = string.split(": ");
		String[] splitNameAndEmail = splitAuthor[1].split("<");
		if (splitNameAndEmail[0].isEmpty() || !splitNameAndEmail[1].contains("@"))
			return Optional.empty();
		return Optional.of(new Author(splitNameAndEmail[0].substring(0, splitNameAndEmail[0].length() - 1),
				splitNameAndEmail[1].substring(0, splitNameAndEmail[1].length() - 1)));
	}

	protected Optional<Commit> extractCommit(String string) {
		if (string.isEmpty() || !string.startsWith("commit "))
			return Optional.empty();
		String[] split = string.split(" ");

		if (split.length != 2)
			return Optional.empty();
		return Optional.of(new Commit(split[1]));
	}

	protected boolean isChangedFileLine(String string) {
		// if (string.endsWith("java"))
		return string.matches("^(\\d+.\\d+.|[ACDMRTUXB]{1}\\s+)([\\d\\w]+.)+");
		// return false;
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

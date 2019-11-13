package Git.tableexport;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import Git.tableexport.exporter.CommitLogExporter;
import Git.tableexport.model.Commit;
import Git.tableexport.parser.GitHubExporter;
import Git.tableexport.parser.GitLogParser;

public class App {
	private static Logger logger = LogManager.getLogger();

	public static void main(String[] args) {
		logger.info("Start fetching ... ");
		CommandLine cmd = InitCommandLineParser(args);
		List<String> listOfRepositorys = readFrom(cmd.getOptionValue("input", "URLList.txt"));
		for (String url : listOfRepositorys) {
			logger.info("Cloning Repository: " + url);
			try (Git clonedRepository = GitHubExporter.cloneRepository(url, extractRepoName(url))) {
				List<String> gitLogRepository = GitHubExporter.gitLogRepository(clonedRepository);
				GitLogParser parser = new GitLogParser();
				logger.info("Parsing Repository ...");
				List<Commit> parsedCommitLog = parser.parseCommitLog(gitLogRepository);
				logger.info("SizeOfCommitLog: " + parsedCommitLog.size());
				CommitLogExporter exporter = new CommitLogExporter();
				logger.info("Export Repository ...");
				List<String> export = exporter.exportCommitLog(parsedCommitLog);
				logger.info("SizeOfExport: " + export.size());
				write(extractRepoName(url) + ".csv", export);
			} catch (GitAPIException | InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private static String extractRepoName(String url) {
		String[] split = url.split("/");
		return split[split.length - 2] + "_" + split[split.length - 1];
	}

	private static void write(String fileName, List<String> export) {
		try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
			for (String string : export) {
				outputStream.write(string.getBytes());
				outputStream.write("\n".getBytes());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static List<String> readFrom(String file) {

		List<String> commitData = new ArrayList<>();

		try (Stream<String> stream = Files.lines(Paths.get(file))) {
			commitData = stream.collect(Collectors.toList());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return commitData;
	}

	/**
	 * <h1>Konfigurations-Methode CLI</h1> Initialisiert den
	 * <b>CommandLineParser</b> mit seinen Argumenten
	 * 
	 * @param args Übergabeparameter aus main
	 * @return Commandline mit extrahierten Parametern
	 */
	private static CommandLine InitCommandLineParser(String[] args) {
		Options options = new Options();

		Option input = new Option("i", "input", true, "input file path");
		input.setRequired(true);
		options.addOption(input);

		Option output = new Option("o", "output", true, "output file");
		output.setRequired(false);
		options.addOption(output);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("utility-name", options);

			System.exit(1);
		}

		return cmd;
	}
}

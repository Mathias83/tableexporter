package Git.tableexport;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Git.tableexport.exporter.CommitLogExporter;
import Git.tableexport.model.Commit;
import Git.tableexport.parser.GitLogParser;

/**
 * Hello world!
 *
 */
public class App {
	private static Logger logger = LogManager.getLogger();

	public static void main(String[] args) {
		logger.info("------ Application Start LogLevel=" + logger.getLevel() + " ------");
		GitLogParser parser = new GitLogParser();
		List<Commit> parsedCommitLog = parser.parseCommitLog(readFrom("graphlog.txt"));
		logger.info("SizeOfCommitLog: " + parsedCommitLog.size());
		CommitLogExporter exporter = new CommitLogExporter();
		List<String> export = exporter.exportCommitLog(parsedCommitLog);
		logger.info("SizeOfExport: " + export.size());
		write("graphlog.csv", export);

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
}

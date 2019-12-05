package Git.tableexport.exporter;

import java.io.File;
import java.io.FileWriter;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Git.tableexport.model.ChangedFile;
import Git.tableexport.model.Commit;

public class CommitLogExporter {
	protected static Logger logger = LogManager.getLogger(CommitLogExporter.class);
	private static final String COLUMNHEAD = "name;commit;authorName;authorEmail;timestamp;file;change";
	FileWriter fr;

	public void exportCommitLog(List<Commit> parsedCommitLog, String name) throws Exception {
		File fileName = new File(name + ".csv");
		fr = new FileWriter(fileName, true);
		fr.write(COLUMNHEAD);
		List<String> export = new LinkedList<String>();
		export.add(createColumnHead());
		logger.info(parsedCommitLog.size());
		for (Commit commit : parsedCommitLog) {
			if (commit.getFiles().isEmpty())
				logger.warn("empty: " + commit.getCommitId() + " Description: " + commit.getDescription());
			for (ChangedFile file : commit.getFiles()) {				
					createLine2(commit, file, name);
					

			}
		}
		fr.close();

	}

	private void createLine2(Commit commit, ChangedFile file, String name) throws Exception {

		fr.write("\n" + name + ";");
		fr.write(commit.getCommitId() + ";");
		fr.write(commit.getAuthor().getName() + ";");
		fr.write(commit.getAuthor().getEmail() + ";");
		fr.write(commit.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).toString() + ";");
		// tableLine.append(commit.getDescription()+";");
		fr.write(file.getPath().toString() + ";");
		fr.write(extractedChange(file));
		fr.flush();

	}

	private String extractedChange(ChangedFile file) {
		return Integer.toString(file.getAddedLines()-file.getDeletedLines());
	}

	private String createColumnHead() {

		return COLUMNHEAD;
	}

}

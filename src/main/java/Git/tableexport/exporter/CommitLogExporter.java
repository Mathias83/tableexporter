package Git.tableexport.exporter;

import java.util.LinkedList;
import java.util.List;

import Git.tableexport.model.ChangedFile;
import Git.tableexport.model.Commit;

public class CommitLogExporter {

	private static final String COLUMNHEAD = "name;commit;authorName;authorEmail;timestamp;file;change";

	public List<String> exportCommitLog(List<Commit> parsedCommitLog, String name) {
		List<String> export = new LinkedList<String>();
		export.add(createColumnHead());
		for (Commit commit : parsedCommitLog) {
			for (ChangedFile file : commit.getFiles()) {
				export.add(createLine(commit,file,name));
				
			}
		}

		return export;
	}

	private String createLine(Commit commit, ChangedFile file,String name) {
		StringBuilder tableLine = new StringBuilder();
		tableLine.append(name+";");
		tableLine.append(commit.getCommitId()+";");
		tableLine.append(commit.getAuthor().getName()+";");
		tableLine.append(commit.getAuthor().getEmail()+";");
		tableLine.append(commit.getDate().toString()+";");
		//tableLine.append(commit.getDescription()+";");
		tableLine.append(file.getPath().toString()+";");
		tableLine.append(extractedChange(file));
		return tableLine.toString();
	}

	private String extractedChange(ChangedFile file) {
		return file.getAddedLines()>0?"+" + file.getAddedLines():"-"+file.getDeletedLines();
	}

	private String createColumnHead() {
		
		return COLUMNHEAD;
	}

}

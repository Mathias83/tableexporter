package Git.tableexport.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Commit implements Comparable<Commit>{
	String commitId;
	Author author;
	LocalDateTime date;
	String description = "";
	List<ChangedFile> files = new ArrayList<>();

	public Commit(String commitId, Author author, LocalDateTime date, String description, List<ChangedFile> files) {
		super();
		this.commitId = commitId;
		this.author = author;
		this.date = date;
		this.description = description;
		this.files = files;
	}
	
	public Commit(Commit commit) {
		super();
		this.commitId = commit.commitId;
		this.author = new Author(commit.author);
		this.date = commit.date;
		this.description = commit.description;
		this.files = new ArrayList<ChangedFile>(commit.files);
	}
	
	public Commit(String commitId) {
		this.commitId = commitId;	
		
	}

	public String getCommitId() {
		return commitId;
	}

	public void setCommitId(String commitId) {
		this.commitId = commitId;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public void appendDescription(String additionalDesc) {
		StringBuilder descString = new StringBuilder(this.description);
		descString.append("\n");
		descString.append(additionalDesc);
		setDescription(descString.toString());
	}

	public List<ChangedFile> getFiles() {
		return Collections.unmodifiableList(files);
	}

	public void setFiles(List<ChangedFile> files) {
		this.files = files;
	}

	@Override
	public String toString() {
		return "\nCommit [commitId=" + commitId + ", \nauthor=" + author + ", \ndate=" + date + ", \ndescription=" + description
				+ ", \nfiles=" + files.toString() + "]\n";
	}

	@Override
	public int compareTo(Commit o) {
		return date.compareTo(o.date);
	}

	
}

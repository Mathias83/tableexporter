package Git.tableexport.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Evaluation {
	List<Commit> commits = new ArrayList<>();
	LocalDateTime generated;

	public Evaluation(List<Commit> commits, LocalDateTime generated) {
		super();
		this.commits = commits;
		this.generated = generated;
	}

	public List<Commit> getCommits() {
		return commits;
	}

	public void setCommits(List<Commit> commits) {
		this.commits = commits;
	}

	public LocalDateTime getGenerated() {
		return generated;
	}

	public void setGenerated(LocalDateTime generated) {
		this.generated = generated;
	}

	@Override
	public String toString() {
		return "Evaluation [commits=" + commits + ", getCommits()=" + getCommits() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}

}

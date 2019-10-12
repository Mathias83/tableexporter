package Git.tableexport.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Developer {
	String name;
	String email;
	List<ChangedFile> changedFiles = new ArrayList<>();
	Map<Path, Integer> paths = new TreeMap<Path,Integer>(Path::compareTo);

	public Developer(Commit commit) {
		name = commit.getAuthor().getName();
		email = commit.getAuthor().getEmail();
		commit.getFiles().forEach(f -> addChangedFile(f));		
	}

	public Map<Path, Integer> getPaths() {
		return paths;
	}

	public void setPaths(Map<Path, Integer> paths) {
		this.paths = paths;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean addChangedFile(ChangedFile changedFile) {
		paths.compute(changedFile.getPath(),Developer::incrementValue);
		return changedFiles.add(changedFile);
	}
	
	private static Integer incrementValue(Path key, Integer value) {
		return (value == null) ? Integer.valueOf(1) : Integer.valueOf(value.intValue() + 1);
	}

	public List<ChangedFile> getChangedFiles() {
		return changedFiles;
	}

	public void setChangedFiles(List<ChangedFile> changedFiles) {
		this.changedFiles = changedFiles;
	}

	public void append(Developer developer) {
		developer.getChangedFiles().stream().forEach(file -> addChangedFile(file));		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Developer other = (Developer) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Developer [name=" + name + ", email=" + email + ", changedFiles=" + changedFiles + ", paths=" + paths
				+ "]";
	}

}

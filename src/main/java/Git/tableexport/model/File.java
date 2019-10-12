package Git.tableexport.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class File {
	Path pathOfFile;
	int linesAdded;
	int linesDeleted;
	List<ChangedFile> changeEntrys = new ArrayList<>();

	public File(Path path) {
		this.pathOfFile = path;
	}

	public File(Path path, int linesAdded, int linesDeleted) {
		this(path);
		this.linesAdded = linesAdded;
		this.linesDeleted = linesDeleted;
	}

	public File(ChangedFile changedFile) {
		this(changedFile.getPath(),changedFile.getAddedLines(),changedFile.getDeletedLines());
	}

	public Path getPathOfFile() {
		return pathOfFile;
	}

	public void setPathOfFile(Path pathOfFile) {
		this.pathOfFile = pathOfFile;
	}

	public int getLinesAdded() {
		return linesAdded;
	}

	public void setLinesAdded(int linesAdded) {
		this.linesAdded = linesAdded;
	}

	public int getLinesDeleted() {
		return linesDeleted;
	}

	public void setLinesDeleted(int linesDeleted) {
		this.linesDeleted = linesDeleted;
	}

	public List<ChangedFile> getChangeEntrys() {
		return changeEntrys;
	}

	public void setChangeEntrys(List<ChangedFile> changeEntrys) {
		this.changeEntrys = changeEntrys;
	}

	public void addChangeEntry(ChangedFile fileEntry) {
		linesAdded += fileEntry.getAddedLines();
		linesDeleted += fileEntry.getDeletedLines();
		changeEntrys.add(fileEntry);
	}

	@Override
	public String toString() {
		return "File [pathOfFile=" + pathOfFile + ", linesAdded=" + linesAdded + ", linesDeleted=" + linesDeleted
				+ ", changeEntrys=" + changeEntrys + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pathOfFile == null) ? 0 : pathOfFile.hashCode());
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
		File other = (File) obj;
		if (pathOfFile == null) {
			if (other.pathOfFile != null)
				return false;
		} else if (!pathOfFile.equals(other.pathOfFile))
			return false;
		return true;
	}

}

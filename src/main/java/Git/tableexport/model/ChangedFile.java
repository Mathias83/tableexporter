package Git.tableexport.model;

import java.nio.file.Path;

public class ChangedFile {
	int addedLines;
	int deletedLines;
	char status;
	Path path;
	String extension;
	public ChangedFile(int addedLines, int deletedLines, Path path, String extension) {
		super();
		this.addedLines = addedLines;
		this.deletedLines = deletedLines;
		this.path = path;
		this.extension = extension;
	}
	public ChangedFile(char status,  Path path, String extension) {
		super();
		this.status = status;
		this.path = path;
		this.extension = extension;
	}
	
	public ChangedFile(char status,  Path path) {
		super();
		this.status = status;
		this.path = path;
		this.extension = "";
	}
	
	public ChangedFile(ChangedFile file) {
		super();
		this.addedLines = file.addedLines;
		this.deletedLines = file.deletedLines;
		this.status = file.status;
		this.path = file.path;
		this.extension = file.extension;
	}
	
	public int getAddedLines() {
		return addedLines;
	}
	public void setAddedLines(int addedLines) {
		this.addedLines = addedLines;
	}
	public int getDeletedLines() {
		return deletedLines;
	}
	public void setDeletedLines(int deletedLines) {
		this.deletedLines = deletedLines;
	}
	public Path getPath() {
		return path;
	}
	public void setPath(Path path) {
		this.path = path;
	}
	
	
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	/*@Override
	public String toString() {
		return "ChangedFile [addedLines=" + addedLines + ", deletedLines=" + deletedLines + ", path=" + path + "]";
	}*/
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
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
		ChangedFile other = (ChangedFile) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}
	public char getStatus() {
		return status;
	}
	
	

}

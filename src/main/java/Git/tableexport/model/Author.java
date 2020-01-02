package Git.tableexport.model;

public class Author {
	

	String name = "";

	String email = "";

	public Author(String name, String email) {
		super();
		this.name = name;
		this.email = email;
	}
	public Author(String nameAndEmail) {
		super();
		this.name = nameAndEmail;
	}

	public Author(Author author) {
		super();
		if (author==null)
			author = new Author("Max Mustermann","Mustermann@Musterstadt.de");
		this.name = author.name;
		this.email = author.email;
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

	@Override
	public String toString() {
		return "Author [name=" + name + ", email=" + email + "]";
	}
	
	public static Author emptyAuthor(){
		return new Author("noname","no@e.mail");
	}
}

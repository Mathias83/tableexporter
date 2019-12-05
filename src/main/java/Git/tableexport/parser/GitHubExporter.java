package Git.tableexport.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;

public class GitHubExporter {
	private static Logger logger = LogManager.getLogger();
	public static Git cloneRepository(String URL, String repoName)
			throws InvalidRemoteException, TransportException, GitAPIException {
		try {
			return Git.open(new File("temp/" + repoName));
		} catch (RepositoryNotFoundException e) {
			logger.warn("Repo doesnt exist");
			return Git.cloneRepository().setURI(URL).setDirectory(new File("temp/" + repoName)).call();
		} catch (IOException e) {
			logger.error("IOException: " + e.getMessage());
			e.printStackTrace();
		}
		logger.error("Return Null!");
		return null;
	}

	public static List<String> gitLogRepository(Git git) throws InterruptedException, IOException {
		ProcessBuilder processBuilder = new ProcessBuilder();
		String path = git.getRepository().getWorkTree().getAbsolutePath();
		processBuilder.command("bash", "-c", "(cd " + path + " && git log --numstat)");
		Process process = processBuilder.start();

		StringBuilder output = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

		List<String> lines = new ArrayList<String>();
		String line;
		while ((line = reader.readLine()) != null) {
			lines.add(line);
		}

		int exitVal = process.waitFor();
		if (exitVal == 0) {
			return lines;
		} else {
			return new ArrayList<String>();
		}

	}
}

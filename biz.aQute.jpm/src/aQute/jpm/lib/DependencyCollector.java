package aQute.jpm.lib;

import java.io.*;
import java.util.*;

class DependencyCollector {
	private final List<ArtifactData>		list	= new ArrayList<ArtifactData>();
	private final JustAnotherPackageManager	jpm;
	private boolean							synced;

	public DependencyCollector(JustAnotherPackageManager jpm) {
		this.jpm = jpm;
	}

	/**
	 * Add a revision to this collector and start the download
	 * 
	 * @param key
	 * @throws Exception
	 */
	public void add(String coordinate) throws Exception {
		list.add(jpm.getCandidateAsync(coordinate));
	}

	void sync() throws InterruptedException {
		if (!synced) {
			for (ArtifactData artifact : list) {
				artifact.sync();
				if (artifact.error != null)
					jpm.reporter.error("Download error %s for %s", artifact.error, artifact.url);
				File file = new File(artifact.file);
				if ( !file.isFile())
					jpm.reporter.error("No file found for %s", artifact.url);
			}
			synced = true;
		}
	}

	public Collection< ? extends String> getPaths() throws InterruptedException {
		sync();
		List<String> paths = new ArrayList<String>();
		for (ArtifactData artifact : list) {
			if ( artifact.error == null) {
				paths.add( artifact.file);
			}
		}
		
		return paths;
	}

}

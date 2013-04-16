package no.ntnu.tdt4215.group7.indexer;

import org.apache.lucene.store.Directory;

public class OwlIndexer implements Indexer {
	
	private final String filename;

	public OwlIndexer(String filename) {
		super();
		this.filename = filename;
	}


	@Override
	public Directory call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}

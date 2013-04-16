package no.ntnu.tdt4215.group7.lookup;

import java.util.List;

import org.apache.lucene.store.Directory;

public interface QueryEngine {
	
	List<String> lookup(String query, Directory index);

}

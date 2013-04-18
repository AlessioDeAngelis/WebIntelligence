package no.ntnu.tdt4215.group7.lookup;

import java.util.Set;

import org.apache.lucene.store.Directory;

public interface QueryEngine {
	
	Set<String> lookup(String query, Directory index);

}

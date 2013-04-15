package no.ntnu.tdt4215.group7.indexer;

import java.util.concurrent.Callable;

import org.apache.lucene.store.Directory;

public interface Indexer extends Callable<Directory> {

}

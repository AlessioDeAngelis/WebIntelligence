package no.ntnu.tdt4215.group7.test;

import java.io.IOException;
import java.util.List;

import no.ntnu.tdt4215.group7.App;
import no.ntnu.tdt4215.group7.entity.ATC;
import no.ntnu.tdt4215.group7.entity.ICD;
import no.ntnu.tdt4215.group7.indexer.ATCIndexer;
import no.ntnu.tdt4215.group7.indexer.ICDIndexer;
import no.ntnu.tdt4215.group7.lookup.ATCQueryEngine;
import no.ntnu.tdt4215.group7.lookup.ICDQueryEngine;
import no.ntnu.tdt4215.group7.parser.ATCParser;
import no.ntnu.tdt4215.group7.parser.ICDParser;
import no.ntnu.tdt4215.group7.utils.Paths;

import org.apache.lucene.store.Directory;

public class IndexingTest {

    public static final String ICD10_FILE = Paths.ICD10_FILE;
    public static final String ATC_FILE = Paths.ATC_FILE;
    public static final String LMHB_DIR = Paths.LMHB_DIR;

    public static void main(String[] args) throws IOException {
        String queryString = "diabetes insulin";
        /**
         * ATC
         * **/
        ATCParser atcParser = new ATCParser();
        ATCQueryEngine atcQueryEngine = new ATCQueryEngine();
        List<ATC> atcs = atcParser.parseATC(Paths.ATC_FILE);
        ATCIndexer atcIndexer = new ATCIndexer(Paths.ATC_INDEX_DIRECTORY, atcs);
        // index must be created only once or you will have duplicates
        System.out.println("ATC Indexing start");
        long start = System.currentTimeMillis();
        Directory index = atcIndexer.createIndex();
        System.out.println("ATC Indexing done in " + (System.currentTimeMillis() - start) + " msec");
        List<String> result = atcQueryEngine.lookup(queryString, index);
        for (String s : result) {
            System.out.println(s);
        }

        /***
         * ICD
         * */
        ICDParser icdParser = new ICDParser();
        ICDQueryEngine icdQueryEngine = new ICDQueryEngine();
        List<ICD> icds = icdParser.parseICD(Paths.ICD10_FILE);
        ICDIndexer icdIndexer = new ICDIndexer(Paths.ICD10_INDEX_DIRECTORY, icds);
        System.out.println("ICD Indexing start");
        start = System.currentTimeMillis();
        index = icdIndexer.createIndex();
        System.out.println("ICD Indexing done in " + (System.currentTimeMillis() - start) + " msec");
        result = icdQueryEngine.lookup(queryString, index);
        for (String s : result) {
            System.out.println(s);
        }        
    }
}

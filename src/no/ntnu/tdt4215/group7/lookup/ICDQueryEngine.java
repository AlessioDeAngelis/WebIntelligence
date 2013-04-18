package no.ntnu.tdt4215.group7.lookup;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

public class ICDQueryEngine implements QueryEngine {

	static Logger log = Logger.getLogger("ql");
	static Logger problematic = Logger.getLogger("pql");

	/**
	 * Lookup on ICD Lucene index
	 * **/
	@Override
	public Set<String> lookup(String query, Directory index) {
		Set<String> resultList = new HashSet<String>();
		try {
			resultList = query(query, "extra", index);
		} catch (IOException e) {
			log.info(e.getStackTrace());
		}
		return resultList;
	}

	private Set<String> query(String queryString, String fieldToQuery, Directory index) throws IOException {
    	Set<String> resultList = new HashSet<String>();
        Query q = null;
        try {
            q = new QueryParser(Version.LUCENE_40, fieldToQuery, new NorwegianAnalyzer(Version.LUCENE_40))
                            .parse(queryString);
        } catch (org.apache.lucene.queryparser.classic.ParseException e) {
        	problematic.error(queryString);
            log.info(e.getStackTrace());
        }

        // 3. search
        int hitsPerPage = 100;
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);

        searcher.search(q, collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;

        // 4. display results
        log.info("Found " + hits.length + " hits.");
        
        for (int i = 0; i < hits.length; i++) {
            int docId = hits[i].doc;
            float score = hits[i].score;
            Document d = searcher.doc(docId);
           log.debug((score) + ". " + "CODE COMPACTED: " + d.get("code_compacted") + "\t" + "LABEL: "
                            + d.get("label") + d.get("extra"));
            resultList.add(d.get("code_compacted"));
        }
        return resultList;
    }
}

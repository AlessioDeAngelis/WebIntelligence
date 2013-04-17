package no.ntnu.tdt4215.group7.lookup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

public class ATCQueryEngine implements QueryEngine{

    /**
     * Lookup on ATC index
     * */
    @Override
    public List<String> lookup(String query, Directory index) {
        List<String> resultList = new ArrayList<String>();
        try {
            resultList = query(query,"label",index);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultList;
    }
    
    private List<String> query(String queryString, String fieldToQuery, Directory index) throws IOException {
        Query q = null;
        List<String> resultList = new ArrayList<String>();
        try {
            q = new QueryParser(Version.LUCENE_40, fieldToQuery, new NorwegianAnalyzer(Version.LUCENE_40))
                            .parse(queryString);
        } catch (org.apache.lucene.queryparser.classic.ParseException e) {
            e.printStackTrace();
        }

        // 3. search
        int hitsPerPage = 100;
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);

        searcher.search(q, collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;

        // 4. display results
        System.out.println("Found " + hits.length + " hits.");
        for (int i = 0; i < hits.length; i++) {
            int docId = hits[i].doc;
            float score = hits[i].score;
            Document d = searcher.doc(docId);
            resultList.add(d.get("code"));
//            System.out.println((score) + "."+ "Code: " + d.get("code")+"\t Label: "+d.get("label"));
        }
        return resultList;
    }

}

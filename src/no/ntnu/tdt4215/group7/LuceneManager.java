package no.ntnu.tdt4215.group7;

import java.io.File;
import java.io.IOException;
import java.util.List;


import no.ntnu.tdt4215.group7.entity.ICD;

import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public class LuceneManager {
    public void indexContact(IndexWriter w, ICD icd) throws IOException {
        NorwegianAnalyzer analyzer = new NorwegianAnalyzer(Version.LUCENE_40);
        // 1. create the index
//        Directory index = new RAMDirectory();
        Directory index =  FSDirectory.open(new File("data/index"));  // disk index storage
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, analyzer);
        IndexWriter w2 = new IndexWriter(index, config);
//        this.setIndexWriter(w2);
        
        Document doc = new Document();
        System.out.println(icd.getCode_compacted());
        if(icd.getCode_compacted()!=null)
        doc.add(new StringField("code_compacted", icd.getCode_compacted(), Field.Store.YES));
       if(icd.getLabel()!=null)
        doc.add(new TextField("label", icd.getLabel(), Field.Store.YES));
        w2.addDocument(doc);
        w2.close();
//        System.out.println(w.numDocs());
    }

    public void query(String queryString, Directory index2) throws IOException {
        
        NorwegianAnalyzer analyzer = new NorwegianAnalyzer(Version.LUCENE_40);
        // 1. create the index
//        Directory index = new RAMDirectory();
        Directory index =  FSDirectory.open(new File("data/index"));  // disk index storage
   
        Query q = null;
        try {
            q = new QueryParser(Version.LUCENE_40, "label", new NorwegianAnalyzer(Version.LUCENE_40))
                            .parse(queryString);
        } catch (org.apache.lucene.queryparser.classic.ParseException e) {
            // TODO Auto-generated catch block
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
            System.out.println((score) + ". " + "CODE COMPACTED: " + d.get("code_compacted") + "\t" + "LABEL: "
                            + d.get("label"));
        }
//
//        int docId = hits[0].doc;
//        Document d = searcher.doc(docId);
        // System.out.println((1) + ". " + "CODE COMPACTED: " +
        // d.get("code_compacted") + "\t" + "LABEL: " + d.get("label"));

    }

}

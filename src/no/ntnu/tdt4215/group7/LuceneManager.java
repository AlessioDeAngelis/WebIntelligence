package no.ntnu.tdt4215.group7;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public class LuceneManager {

    public void createIndex(List<Resource> resources) throws IOException {
        NorwegianAnalyzer analyzer = new NorwegianAnalyzer(Version.LUCENE_40);
        Directory index = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, analyzer);
        IndexWriter w = new IndexWriter(index, config);
        // add docs
        for (Resource res : resources) {
          Statement code_compacted = res.getProperty(this.mapOntProperties.get("code_compacted"));
//          Statement 

        }
        w.close();
    }

    public void addDoc(IndexWriter w, String code_compacted, String label, String extraInformation) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("label", label, Field.Store.YES));
        // use a string field for because we don't want it tokenized
        doc.add(new StringField("code_compacted", code_compacted, Field.Store.YES));
        if (extraInformation != null || !extraInformation.equals("")) {
            doc.add(new TextField("extra", label, Field.Store.YES));
        }
        w.addDocument(doc);
    }
}

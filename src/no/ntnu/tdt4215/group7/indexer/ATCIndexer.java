package no.ntnu.tdt4215.group7.indexer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import no.ntnu.tdt4215.group7.entity.ATC;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * Index atc files on lucene
 * **/
public class ATCIndexer implements Indexer{
	
	static Logger log = Logger.getLogger("ATCIndexer");

    /*
     * the output directory of the index
     */
    private String filePath;
    /*
     * The list of atc codes to be indexed *
     */
    private List<ATC> atcs;

    public ATCIndexer(String filePath, List<ATC> atcs) {
        super();
        this.filePath = filePath;
        this.atcs = atcs;
    }

    public Directory createIndex() throws IOException {
        NorwegianAnalyzer analyzer = new NorwegianAnalyzer(Version.LUCENE_40);
        // Directory index = new RAMDirectory();
        Directory index = FSDirectory.open(new File(filePath)); // disk index
                                                                // storage

        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, analyzer);
        IndexWriter w = new IndexWriter(index, config);
        for (ATC atc : this.atcs) {
            this.addATCDoc(w, atc);
        }
        
        log.info("Index contains: " + atcs.size() + " items.");
        
        w.close();
        return index;
    }

    private void addATCDoc(IndexWriter w, ATC atc) throws IOException {

        String code = atc.getCode();
        String label = atc.getLabel();

        Document doc = new Document();
        FieldType type = new FieldType();
        type.setIndexed(true);
        type.setStored(true);
        type.setStoreTermVectors(true);
        type.setTokenized(true);
        Field fieldLabel = new Field("label", label, type);

        doc.add(fieldLabel);

        // use a string field for because we don't want it tokenized
        doc.add(new StringField("code", code, Field.Store.YES));
        w.addDocument(doc);
    }

    @Override
    public Directory call() throws Exception {
        return createIndex();
    }

}

package no.ntnu.tdt4215.group7;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDFS;

public class OwlParser {

    private OntModel model;
    private Map<String, OntProperty> mapOntProperties;

    public OwlParser() {
        this.model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        this.setMapOntProperties(new HashMap<String, OntProperty>());
    }

    public OntModel parse(String pathFile) {
        return model;
    }

    public Map<String, OntProperty> mapOntProperties(String pathFile) {
        model.read(pathFile);
        Map<String, OntProperty> map = new HashMap<String, OntProperty>();
        ExtendedIterator<OntProperty> i = model.listAllOntProperties();

        while (i.hasNext()) {
            OntProperty prop = (OntProperty) i.next();
            String propString = prop.toString();
            String[] splits = propString.split("#");
            System.out.println("Found prop: " + splits[1]);
            map.put(splits[1], prop);
        }
        this.setMapOntProperties(map);
        return map;
    }

    public List<Resource> listResourcesWithProperty(Property property) {
        // select all the resources with a VCARD.FN property
        ResIterator iter = model.listResourcesWithProperty(property);
        List<Resource> resources = new ArrayList<Resource>();
        if (iter.hasNext()) {
            while (iter.hasNext()) {
                Resource res = iter.nextResource();
                // Statement stat =
                // iter.nextResource().getProperty(this.mapOntProperties.get("code_compacted"));
                resources.add(res);
                // if (stat !=null) {
                // String stringa = stat.getString();
                // System.out.println("  " + stringa);
                // }
            }
        } else {
            System.out.println("No vcards were found in the database");
        }

        return resources;
    }

    public Map<String, OntProperty> getMapOntProperties() {
        return mapOntProperties;
    }

    public void setMapOntProperties(Map<String, OntProperty> mapOntProperties) {
        this.mapOntProperties = mapOntProperties;
    }

    public Directory createIndex(List<Resource> resources) throws IOException {
        NorwegianAnalyzer analyzer = new NorwegianAnalyzer(Version.LUCENE_40);
        Directory index = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, analyzer);
        IndexWriter w = new IndexWriter(index, config);
        // add docs
        for (Resource res : resources) {
            Statement code_compacted = res.getProperty(this.mapOntProperties.get("code_compacted"));
            Statement label = res.getProperty(this.mapOntProperties.get(RDFS.label));
            Statement underterm = res.getProperty(this.mapOntProperties.get("underterm"));
            // there may be more synonyms
            // Object underterm = null;
            StmtIterator iter = res.listProperties(this.mapOntProperties.get("synonym"));
            String extraInformation = "";
            if (underterm != null) {
                extraInformation += underterm.getString() + " ";
            }
            while (iter.hasNext()) {
                extraInformation += iter.nextStatement().getObject().toString() + " ";
            }
            String codeS = "";
            if (code_compacted != null) {
                code_compacted.getString();
            }

            /**
             * There is more than one hit
             * */
            String labelS = "";

            iter = res.listProperties(this.mapOntProperties.get(RDFS.label));

            while (iter.hasNext()) {
                String tmp = iter.nextStatement().getObject().toString();
                String[] splits = tmp.split("\\^\\^");
                if (!splits[0].matches("http.*")) {
                    labelS += splits[0] + "\t";
                }
            }

            this.addDoc(w, codeS, labelS, extraInformation);
        }
        w.close();
        return index;
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

    public void query(String queryString, Directory index) throws IOException {
        Query q = null;
        try {
            q = new QueryParser(Version.LUCENE_40, "label", new NorwegianAnalyzer(Version.LUCENE_40))
                            .parse(queryString);
        } catch (org.apache.lucene.queryparser.classic.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // 3. search
        int hitsPerPage = 10;
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
        searcher.search(q, collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;

        // 4. display results
        System.out.println("Found " + hits.length + " hits.");
        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println((i + 1) + ". " + d.get("code_compacted") + "\t" + d.get("label"));
        }

    }
}

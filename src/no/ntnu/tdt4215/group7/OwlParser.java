package no.ntnu.tdt4215.group7;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.ntnu.tdt4215.group7.entity.ICD;

import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
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
    private List<ICD> icds;

    public OwlParser() {
        this.model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        this.setMapOntProperties(new HashMap<String, OntProperty>());
        this.icds = new ArrayList<ICD>();
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

            // System.out.println("Found prop: " +prop.getLocalName());
            System.out.println("private String " + prop.getLocalName() + ";");

            map.put(prop.getLocalName(), prop);
        }
        this.setMapOntProperties(map);
        return map;
    }

    public List<Resource> listResourcesWithProperty(Property property) {
        // select all the resources with a VCARD.FN property

        ResIterator iter = model.listResourcesWithProperty(this.mapOntProperties.get("code_compacted"));
        List<Resource> resources = new ArrayList<Resource>();
        if (iter.hasNext()) {
            while (iter.hasNext()) {
                Resource res = iter.nextResource();
                // Statement stat =
                // iter.nextResource().getProperty(this.mapOntProperties.get("code_compacted"));
                resources.add(res);
                // System.out.println(res.getLocalName() + "," + "," +
                // res.getNameSpace());

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

    /*
     * From the resource to ICD object
     */
    public void createICDObjects(List<Resource> resources) throws IOException {
        NorwegianAnalyzer analyzer = new NorwegianAnalyzer(Version.LUCENE_40);
        Directory index = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, analyzer);
        IndexWriter w = new IndexWriter(index, config);
        // add ICD codes
        for (Resource res : resources) {
            String codeString = res.getLocalName();
            String undertermString = "";
            String labelString = "";
            String subclassString = "";
            Set<String> synonyms = new HashSet<String>();

            Statement underterm = res.getProperty(this.mapOntProperties.get("underterm"));
            // Object underterm = null;
            if (underterm != null) {
                undertermString = underterm.getObject().toString();
            }

            /*
             * there may be more synonyms
             */
            StmtIterator iter;
            iter = res.listProperties(this.mapOntProperties.get("synonym"));
            while (iter.hasNext()) {
                // extraInformation +=
                String synonym = iter.nextStatement().getObject().toString().split("\\^\\^")[0] + " ";
                synonyms.add(synonym);
            }

            /**
             * There is more than one hit for label
             * */
            iter = res.listProperties(RDFS.label);

            while (iter.hasNext()) {
                String tmp = iter.nextStatement().getObject().toString();
                String[] splits = tmp.split("\\^\\^");
                if (!splits[0].matches("http.*")) {
                    labelString += splits[0] + " ";
                }
            }
            
            /*
             * Subclass
             * */
            Statement subclass = res.getProperty(this.mapOntProperties.get(RDFS.subClassOf));
            if(subclass != null){
                subclassString = subclass.getObject().toString().split("#")[1];
            }
            ICD icd = new ICD();
            icd.setCode_compacted(codeString);
            icd.setLabel(labelString);
            icd.setSynonyms(synonyms);
            icd.setUnderterm(undertermString);
            icd.setSubclass(subclassString);
            this.icds.add(icd);
        }
    }
    
    /*
     * Index ICD objects on Lucene
     */
    public Directory indexIcdObjects() throws IOException {
        NorwegianAnalyzer analyzer = new NorwegianAnalyzer(Version.LUCENE_40);
        Directory index = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, analyzer);
        IndexWriter w = new IndexWriter(index, config);
        // add ICD codes
        for(ICD icd : this.icds){
             this.addICDDoc(w, icd);
        }
         w.close();
        return index;
    }

    /**
     *In the Lucene doc relative to the icd file we index and stem the label together with the extra information like synonyms
     *The extra information is used for query expansion
     **/
    public void addICDDoc(IndexWriter w, ICD icd) throws IOException {
        String codecompacted = icd.getCode_compacted();
        String label = icd.getLabel();
        //at the momemnt the extra information is given by the underterm and by the synonyms
        String extraInformation = icd.getUnderterm();
        for(String syn : icd.getSynonyms()){
            extraInformation += " " + syn;
        }
        
        Document doc = new Document();
        FieldType type = new FieldType();
        type.setIndexed(true);
        type.setStored(true);
        type.setStoreTermVectors(true);
        type.setTokenized(true);
        Field fieldLabel = new Field("label", label, type);
        Field fieldExtra = new Field("extra", label+" "+extraInformation, type);

        doc.add(fieldLabel);

        // use a string field for because we don't want it tokenized
        doc.add(new StringField("code_compacted", codecompacted, Field.Store.YES));
        
        if (extraInformation != null || !extraInformation.equals("")) {
            doc.add(fieldExtra);
        }
        w.addDocument(doc);
    }

    public void query(String queryString, String fieldToQuery, Directory index) throws IOException {
        Query q = null;
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
            System.out.println((score) + ". " + "CODE COMPACTED: " + d.get("code_compacted") + "\t" + "LABEL: "
                            + d.get("label") + d.get("extra"));
        }
    }
}

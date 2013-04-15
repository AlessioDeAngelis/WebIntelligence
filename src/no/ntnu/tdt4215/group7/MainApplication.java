package no.ntnu.tdt4215.group7;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.lucene.store.Directory;

import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Resource;

public class MainApplication {
    public static void main(String[] args) throws IOException {
        String pathFile = "data/icd10no.owl";
        OwlParser owlParser = new OwlParser();
        Map<String,OntProperty> map = owlParser.mapOntProperties(pathFile);
        List<Resource> resources = owlParser.listResourcesWithProperty(map.get("code_compacted"));
        owlParser.createICDObjects(resources);
        Directory index = null;
        try {
           index = owlParser.indexIcdObjects();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String queryString = "Hun har en bror som også har diabetes og som har brukt insulin i flere år";
        try {
            owlParser.query(queryString,"extra",index);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

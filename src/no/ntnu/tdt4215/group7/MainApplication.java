package no.ntnu.tdt4215.group7;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.lucene.store.Directory;

import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Resource;

public class MainApplication {
    public static void main(String[] args) {
        String pathFile = "data/icd10no.owl";
        OwlParser owlParser = new OwlParser();
        Map<String,OntProperty> map = owlParser.mapOntProperties(pathFile);
        List<Resource> resources = owlParser.listResourcesWithProperty(map.get(1));
        Directory index = null;
        try {
           index = owlParser.createIndex(resources);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String queryString = "Eva Andersen er en skoleelev som har hatt insulinkrevende diabetes mellitus i 3 år.";
        try {
            owlParser.query(queryString,index);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

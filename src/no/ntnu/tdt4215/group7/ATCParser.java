package no.ntnu.tdt4215.group7;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.riot.RDFDataMgr;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ATCParser {
    private OntModel model;
    private Model mod;
    private Map<String, OntProperty> mapOntProperties;
    private List<ATC> atcs;

    public ATCParser() {
        this.model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        this.mapOntProperties = new HashMap<String, OntProperty>();
        this.atcs = new ArrayList<ATC>();
    }

    public OntModel getModel() {
        return model;
    }

    public void setModel(OntModel model) {
        this.model = model;
    }

    public Map<String, OntProperty> getMapOntProperties() {
        return mapOntProperties;
    }

    public void setMapOntProperties(Map<String, OntProperty> mapOntProperties) {
        this.mapOntProperties = mapOntProperties;
    }

    public void parse(String pathFile) {
        model.read(pathFile);
    }

    /**
     * Parses with Jena the atc file and create atc object
     * */
    public List<ATC> parseATC(String pathFile) {
        // model.read(pathFile);
        this.mod = RDFDataMgr.loadModel(pathFile);
        // each ATC code has the rdfs:label property, so we are sure to retrieve
        // all code
        Set<Resource> resources = mod.listResourcesWithProperty(RDFS.label).toSet();
        // we have all the resources, each is defined by the code
        for (Resource resource : resources) {
            ATC atc = new ATC();
            // the code is given by the local name of the resource
            String atcCode = resource.getLocalName();
            // rdfs:label value
            String label = "";
            // the atc codes of rdfs:subClassOf
            List<String> subClassOf = new ArrayList<String>();
            // we take all the statement triples related to the resource
            List<Statement> resourceStatements = resource.listProperties().toList();
            for (Statement statement : resourceStatements) {
                // the resource is always the object of these triples
                Node predicate = statement.asTriple().getPredicate();
                Node object = statement.asTriple().getObject();
                String predicateString = predicate.getLocalName();
                if (predicateString != null && predicateString.equals("label")) {
                    // some resources have more than one label
                    label += object.toString() + " ";
                }
                if (predicateString != null && predicateString.equals("subClassOf")) {
                    subClassOf.add(object.getLocalName());
                }
            }
            System.out.println(label);
            atc.setCode(atcCode);
            // according to the label we prefer the translation in norwegian
            atc.setLabel(parseTheLabel(label));
            atc.setSubClassOf(subClassOf);
            this.atcs.add(atc);
        }
        return this.atcs;
    }

    private String parseTheLabel(String label) {
        String result = "";
//        result = label.split(regex)
        return result;
    } 
}

package no.ntnu.tdt4215.group7.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import no.ntnu.tdt4215.group7.entity.ATC;

import org.apache.jena.riot.RDFDataMgr;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ATCParser {
    private Model model;
    private List<ATC> atcs;

    public ATCParser() {
        this.atcs = new ArrayList<ATC>();
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    /**
     * Parses with Jena the atc file and create atc object
     * */
    public List<ATC> parseATC(String pathFile) {
         this.model = RDFDataMgr.loadModel(pathFile);
        // each ATC code has the rdfs:label property, so we are sure to retrieve
        // all codes
        Set<Resource> resources = this.model.listResourcesWithProperty(RDFS.label).toSet();
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
            Set<String> labelTerms = new HashSet<String>(); // we use the set
                                                            // for not having
                                                            // duplicated terms
            for (Statement statement : resourceStatements) {
                // the resource is always the object of these triples
                Node predicate = statement.asTriple().getPredicate();
                Node object = statement.asTriple().getObject();
                String predicateString = predicate.getLocalName();
                if (predicateString != null && predicateString.equals("label")) {
                    // some resources have more than one label
                    labelTerms.add(parseTheLabel(object.toString()));
                }
                if (predicateString != null && predicateString.equals("subClassOf")) {
                    subClassOf.add(object.getLocalName());
                }
            }
            for (String term : labelTerms) {
                label += term + " ";
            }
            atc.setCode(atcCode);
            atc.setLabel(label);
            atc.setSubClassOf(subClassOf);
            this.atcs.add(atc);
        }
        return this.atcs;
    }

    /**
     * Extract
     * */
    private String parseTheLabel(String label) {
        String result = "";
        result = label.replaceAll("\\^\\^http://.*|(\\@no)", "");// to remove
                                                                 // http and @no
                                                                 // annotations
        result = result.substring(1, result.length() - 1);// to remove the first
                                                          // and last "
        result = result.toLowerCase();
        return result;
    }

    public List<ATC> getAtcs() {
        return atcs;
    }

    public void setAtcs(List<ATC> atcs) {
        this.atcs = atcs;
    }  
}

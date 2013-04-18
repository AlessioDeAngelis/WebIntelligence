package no.ntnu.tdt4215.group7.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.ntnu.tdt4215.group7.entity.ICD;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ICDParser {

    private OntModel model;
    private Map<String, OntProperty> mapOntProperties;
    private List<ICD> icds;

    public ICDParser() {
        this.model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        this.setMapOntProperties(new HashMap<String, OntProperty>());
        this.icds = new ArrayList<ICD>();
    }

    public List<ICD> parseICD(String pathFile) throws IOException {
        Map<String, OntProperty> mapOntProperties = mapOntProperties(pathFile);
        this.icds = createICDObjects();
        return this.icds;
    }

    private Map<String, OntProperty> mapOntProperties(String pathFile) {
        model.read(pathFile);
        Map<String, OntProperty> map = new HashMap<String, OntProperty>();
        ExtendedIterator<OntProperty> i = model.listAllOntProperties();

        while (i.hasNext()) {
            OntProperty prop = (OntProperty) i.next();
            map.put(prop.getLocalName(), prop);
        }
        this.setMapOntProperties(map);
        return map;
    }

    private List<Resource> listResourcesWithProperty() {
        // select all the resources with a VCARD.FN property

        ResIterator iter = model.listResourcesWithProperty(this.mapOntProperties.get("code_compacted"));
        List<Resource> resources = new ArrayList<Resource>();
        if (iter.hasNext()) {
            while (iter.hasNext()) {
                Resource res = iter.nextResource();
                resources.add(res);
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
    private List<ICD> createICDObjects() throws IOException {
        List<Resource> resources = new ArrayList<Resource>();
        // each resource has the property rdfs:label, so we are sure that we
        // list
        // all the resources
        resources = this.model.listResourcesWithProperty(RDFS.label).toList();
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
             */
            Statement subclass = res.getProperty(this.mapOntProperties.get(RDFS.subClassOf));
            if (subclass != null) {
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
        return this.icds;
    }
}

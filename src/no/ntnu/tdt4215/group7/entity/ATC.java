package no.ntnu.tdt4215.group7.entity;

import java.util.ArrayList;
import java.util.List;

public class ATC {
    private String code;
    private String label;
    private List<String> subClassOf;
    
    public ATC() {
        this.code = "";
        this.label = "";
        this.subClassOf = new ArrayList<String>();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<String> getSubClassOf() {
        return subClassOf;
    }

    public void setSubClassOf(List<String> subClassOf) {
        this.subClassOf = subClassOf;
    }
}

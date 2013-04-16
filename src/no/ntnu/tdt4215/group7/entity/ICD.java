package no.ntnu.tdt4215.group7.entity;

import java.util.HashSet;
import java.util.Set;

public class ICD {
    private String label;
    private String subclass;
    private String code_compacted;
    private String icpc2_label;
    private String umls_tui;
    private String umls_atomId;
    private String icpc2_code;

    private String inclusion;
    private String underterm;
    private String umls_semanticType;
    private String synonym;
    private String code_formatted;
    private String umls_conceptId;
    private String exclusion;
    private Set<String> synonyms;
    
    public ICD(){
        this.synonyms = new HashSet<String>();
    }

    public String getCode_compacted() {
        return code_compacted;
    }

    public void setCode_compacted(String code_compacted) {
        this.code_compacted = code_compacted;
    }

    public String getIcpc2_label() {
        return icpc2_label;
    }

    public void setIcpc2_label(String icpc2_label) {
        this.icpc2_label = icpc2_label;
    }

    public String getUmls_tui() {
        return umls_tui;
    }

    public void setUmls_tui(String umls_tui) {
        this.umls_tui = umls_tui;
    }

    public String getUmls_atomId() {
        return umls_atomId;
    }

    public void setUmls_atomId(String umls_atomId) {
        this.umls_atomId = umls_atomId;
    }

    public String getIcpc2_code() {
        return icpc2_code;
    }

    public void setIcpc2_code(String icpc2_code) {
        this.icpc2_code = icpc2_code;
    }

    public String getInclusion() {
        return inclusion;
    }

    public void setInclusion(String inclusion) {
        this.inclusion = inclusion;
    }

    public String getUnderterm() {
        return underterm;
    }

    public void setUnderterm(String underterm) {
        this.underterm = underterm;
    }

    public String getUmls_semanticType() {
        return umls_semanticType;
    }

    public void setUmls_semanticType(String umls_semanticType) {
        this.umls_semanticType = umls_semanticType;
    }

    public String getSynonym() {
        return synonym;
    }

    public void setSynonym(String synonym) {
        this.synonym = synonym;
    }

    public String getCode_formatted() {
        return code_formatted;
    }

    public void setCode_formatted(String code_formatted) {
        this.code_formatted = code_formatted;
    }

    public String getUmls_conceptId() {
        return umls_conceptId;
    }

    public void setUmls_conceptId(String umls_conceptId) {
        this.umls_conceptId = umls_conceptId;
    }

    public String getExclusion() {
        return exclusion;
    }

    public void setExclusion(String exclusion) {
        this.exclusion = exclusion;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getSubclass() {
        return subclass;
    }

    public void setSubclass(String subclass) {
        this.subclass = subclass;
    }
    
    public void addSynonym(String synonym){
        if(this.synonyms == null ){
            this.synonyms = new HashSet<String>();
            this.synonyms.add(synonym);
        }else{
            this.synonyms.add(synonym);
        }
    }

    public Set<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(Set<String> synonyms) {
        this.synonyms = synonyms;
    }
}

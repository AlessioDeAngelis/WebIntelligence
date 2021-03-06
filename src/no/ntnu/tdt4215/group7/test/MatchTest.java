package no.ntnu.tdt4215.group7.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import no.ntnu.tdt4215.group7.entity.CodeType;
import no.ntnu.tdt4215.group7.entity.MedDocument;
import no.ntnu.tdt4215.group7.entity.Sentence;
import no.ntnu.tdt4215.group7.service.MatchingService;
import no.ntnu.tdt4215.group7.service.MatchingServiceImpl;

import org.junit.Test;

public class MatchTest extends TestCase {

    @Test
    public void testFakeMatch() {
        MedDocument patientCase1 = new MedDocument(CodeType.CLINICAL_NOTE);
        patientCase1.setId("case 1");
        patientCase1.addSentence("Eva Andersen er en skoleelev som har hatt insulinkrevende diabetes mellitus i 3 år");
        patientCase1.getSentences().get(0).addCode(CodeType.ICD10, "E10");
        patientCase1.getSentences().get(0).addCode(CodeType.ICD10, "E14");

        patientCase1.addSentence("Hun har en bror som også har diabetes og som har brukt insulin i flere år");
        patientCase1.getSentences().get(1).addCode(CodeType.ICD10, "E14");
        patientCase1.getSentences().get(1).addCode(CodeType.ICD10, "E11");

        patientCase1.addSentence("Hun er blitt delvis uklar, og vurderer henvisning til sykehus");
        patientCase1.getSentences().get(2).addCode(CodeType.ICD10, "C22");

        MedDocument lmhbChapter1 = new MedDocument(CodeType.LMHB);
        lmhbChapter1.setId("legemiddelhåndboka 1");
        lmhbChapter1.addSentence("diabetes je vazna nemoc");
        lmhbChapter1.getSentences().get(0).addCode(CodeType.ICD10, "E10");

        lmhbChapter1.addSentence("musite si pichat inzulin");
        lmhbChapter1.getSentences().get(1).addCode(CodeType.ICD10, "E14");

        lmhbChapter1.addSentence("muzou vam unohat rizu");
        lmhbChapter1.getSentences().get(2).addCode(CodeType.ICD10, "R22");

        MedDocument lmhbChapter2 = new MedDocument(CodeType.LMHB);
        lmhbChapter2.setId("legemiddelhåndboka 2");
        lmhbChapter2.addSentence("plane nestovice jsou hracka");
        lmhbChapter2.getSentences().get(0).addCode(CodeType.ICD10, "Q10");

        lmhbChapter2.addSentence("vyskacou vam pupinky");
        lmhbChapter2.getSentences().get(1).addCode(CodeType.ICD10, "Q14");

        lmhbChapter2.addSentence("mazete se mastickou");
        lmhbChapter2.getSentences().get(2).addCode(CodeType.ICD10, "C22");

        List<MedDocument> book = new ArrayList<MedDocument>();
        book.add(lmhbChapter1);
        book.add(lmhbChapter2);

        MatchingService service = new MatchingServiceImpl(book);

        List<MedDocument> result = service.findRelevantDocuments(patientCase1);
        
        assertTrue(result.size() > 0);

        System.out.println("done");

        for (MedDocument doc : result) {
        	
        	assertNotNull(doc);
        	assertTrue(doc.getSentences().size() > 0);
        	
            StringBuffer sb = new StringBuffer();

            sb.append("Chapter: ").append(doc.getId()).append(" -- ");
            for (Sentence sentence : doc.getSentences()) {
            	assertNotNull(sentence);
            	assertNotNull(sentence.getText());
                sb.append(sentence.getText()).append(", ");
            }

            System.out.println(sb.toString());
        }
    }
}

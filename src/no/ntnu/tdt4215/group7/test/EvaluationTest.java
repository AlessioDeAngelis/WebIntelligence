package no.ntnu.tdt4215.group7.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import no.ntnu.tdt4215.group7.entity.CodeType;
import no.ntnu.tdt4215.group7.entity.EvaluationResult;
import no.ntnu.tdt4215.group7.entity.MedDocument;
import no.ntnu.tdt4215.group7.parser.GoldStandardParser;
import no.ntnu.tdt4215.group7.service.EvaluationService;
import no.ntnu.tdt4215.group7.service.FileService;
import no.ntnu.tdt4215.group7.service.FileServiceImpl;
import no.ntnu.tdt4215.group7.utils.Paths;
import junit.framework.TestCase;

public class EvaluationTest extends TestCase {

	FileService fileService;

	@Before
	public void setUp() {
		fileService = new FileServiceImpl();
	}

	@Test
	public void testEval() {
		List<MedDocument> goldStandardCol = new GoldStandardParser(Paths.GOLD_STANDARD_FILE).call();

		List<MedDocument> cases = new ArrayList<MedDocument>();

		for (int i = 0; i < 8; i++) {

			MedDocument patientX = new MedDocument(CodeType.CLINICAL_NOTE);

			patientX.setId(String.valueOf(i + 1));

			patientX.addRelevantDocId("T3.1 Diabetes mellitus");
			patientX.addRelevantDocId("T1.3 Mononukleose");
			patientX.addRelevantDocId("T4.1 Anemier");

			cases.add(patientX);

		}

		EvaluationService evService = new EvaluationService(cases, goldStandardCol);

		List<EvaluationResult> results = evService.call();

		assertTrue(results.size() == cases.size());
		assertTrue(results.size() == goldStandardCol.size());

		try {
			fileService.writeEval(results);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

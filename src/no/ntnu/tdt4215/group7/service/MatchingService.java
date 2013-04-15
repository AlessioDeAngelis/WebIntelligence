package no.ntnu.tdt4215.group7.service;

import java.util.List;

import no.ntnu.tdt4215.group7.entity.MedDocument;

public interface MatchingService {

	public List<MedDocument> findRelevantDocument(MedDocument input);

}
package no.ntnu.tdt4215.group7;

import java.util.List;
import java.util.concurrent.Callable;

import no.ntnu.tdt4215.group7.entity.MedDocument;

public interface DocumentParser extends Callable<List<MedDocument>>{

}

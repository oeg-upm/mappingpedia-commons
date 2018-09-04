package es.upm.fi.dia.oeg.mappingpedia.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import es.upm.fi.dia.oeg.mappingpedia.model.MpcTriple;
import es.upm.fi.dia.oeg.mappingpedia.model.result.ListResult;
import es.upm.fi.dia.oeg.mappingpedia.utility.MpcVirtuosoUtility;
import scala.Tuple2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TestVirtuosoUtility {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	MpcVirtuosoUtility virtuosoUtility = MpcVirtuosoUtility.apply();

	@Test
	void testGetInstanceDetails() {
		String instanceUri = "http://mappingpedia.linkeddata.es/instance/mappingdocument/mappingExecutionResult-3664aab7-74b9-4cd3-89a2-382bd468b3e3";
		ListResult<MpcTriple> result = virtuosoUtility.getInstanceDetails(instanceUri);
		logger.info("instanceUri = " + instanceUri);
		
		assertTrue(true);
	}

}

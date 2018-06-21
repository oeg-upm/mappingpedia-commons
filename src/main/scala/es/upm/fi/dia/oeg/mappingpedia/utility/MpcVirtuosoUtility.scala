package es.upm.fi.dia.oeg.mappingpedia.utility


import java.io.{ByteArrayInputStream, File, InputStream}
import java.util.Properties

import es.upm.fi.dia.oeg.mappingpedia.MappingPediaConstant
import org.apache.jena.graph.Triple
import org.apache.jena.rdf.model.{Model, ModelFactory}
import org.apache.jena.util.FileManager
import org.slf4j.{Logger, LoggerFactory}
import virtuoso.jena.driver.{VirtGraph, VirtModel, VirtuosoQueryExecutionFactory}

import scala.collection.JavaConversions._

object MpcVirtuosoUtility {
  val logger: Logger = LoggerFactory.getLogger(this.getClass);

  def apply(properties:Properties): MpcVirtuosoUtility  = {
    new MpcVirtuosoUtility(
      properties.getProperty(MappingPediaConstant.VIRTUOSO_JDBC)
      , properties.getProperty(MappingPediaConstant.VIRTUOSO_USER)
      , properties.getProperty(MappingPediaConstant.VIRTUOSO_PWD)
      , properties.getProperty(MappingPediaConstant.GRAPH_NAME)
    );
  }

  def apply(): MpcVirtuosoUtility = {
    val propertiesFilePath = "/" + MappingPediaConstant.DEFAULT_CONFIGURATION_FILENAME;

    val in = getClass.getResourceAsStream(propertiesFilePath)
    val properties = new Properties();
    properties.load(in)
    logger.debug(s"properties.keySet = ${properties.keySet()}")

    MpcVirtuosoUtility(properties)
  }
}

class MpcVirtuosoUtility(val virtuosoJDBC:String, val virtuosoUser:String, val virtuosoPwd:String
                         , val virtuosoGraphName:String
                        ) {
  val logger: Logger = LoggerFactory.getLogger(this.getClass);

  val virtGraph:VirtGraph = {
    logger.info("Connecting to Virtuoso Graph...");
    val graph = try {
      new VirtGraph (virtuosoGraphName, virtuosoJDBC, virtuosoUser, virtuosoPwd);
    } catch {
      case e:Exception => {
        e.printStackTrace();
        null
      }
    }
    graph

  }

  val databaseModel = VirtModel.openDatabaseModel(virtuosoGraphName, virtuosoJDBC, virtuosoUser, virtuosoPwd);
  //val defaultModel = VirtModel.openDefaultModel(virtuosoJDBC, virtuosoUser, virtuosoPwd)

  def createQueryExecution(queryString:String) = {
    VirtuosoQueryExecutionFactory.create(queryString, this.databaseModel)
  }

  def storeFromFile(file:File) : Unit = {
    val filePath = file.getPath;
    this.storeFromFilePath(filePath)
  }

  def storeFromFilePath(filePath:String) : Unit = {
    val model = this.readModelFromFile(filePath);
    val triples = MappingPediaUtility.toTriples(model);
    this.storeFromTriples(triples);
  }

  def storeFromString(modelText:String) : Unit = {
    val model = this.readModelFromString(modelText);
    val triples = MappingPediaUtility.toTriples(model);
    this.storeFromTriples(triples);
  }

  def storeFromTriples(pTriples:List[Triple]) : Unit = {
    this.store(pTriples, false, null);
  }

  def store(pTriples:List[Triple], skolemizeBlankNode:Boolean, baseURI:String) : Unit = {
    val initialGraphSize = this.virtGraph.getCount();
    logger.debug("initialGraphSize = " + initialGraphSize);

    val newTriples = if(skolemizeBlankNode) { MappingPediaUtility.skolemizeTriples(pTriples, baseURI)} else { pTriples }

    val triplesIterator = newTriples.iterator;
    while(triplesIterator.hasNext) {
      val triple = triplesIterator.next();
      this.virtGraph.add(triple);
    }

    val finalGraphSize = this.virtGraph.getCount();
    logger.debug("finalGraphSize = " + finalGraphSize);

    val addedTriplesSize = finalGraphSize - initialGraphSize;
    logger.info("No of added triples = " + addedTriplesSize);
  }

  def readModelFromFile(filePath:String) : Model = {
    this.readModelFromFile(filePath, "TURTLE");
  }

  def readModelFromFile(filePath:String, lang:String) : Model = {
    val inputStream = FileManager.get().open(filePath);
    val model = this.readModelFromInputStream(inputStream, lang);
    model;
  }

  def readModelFromString(modelText:String) : Model = {
    this.readModelFromString(modelText, "TURTLE");
  }

  def readModelFromString(modelText:String, lang:String) : Model = {
    val inputStream = new ByteArrayInputStream(modelText.getBytes());
    val model = this.readModelFromInputStream(inputStream, lang);
    model;
  }


  def readModelFromInputStream(inputStream:InputStream, lang:String) : Model = {
    val model = ModelFactory.createDefaultModel();

    model.read(inputStream, null, lang);
    model;
  }

}


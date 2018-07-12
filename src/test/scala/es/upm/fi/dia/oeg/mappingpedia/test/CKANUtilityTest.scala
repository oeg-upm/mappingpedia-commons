package es.upm.fi.dia.oeg.mappingpedia.test

import java.io.FileInputStream
import java.util.Properties

import es.upm.fi.dia.oeg.mappingpedia.MappingPediaProperties
import es.upm.fi.dia.oeg.mappingpedia.test.TestProperties.logger
import es.upm.fi.dia.oeg.mappingpedia.utility.MpcCkanUtility
import org.slf4j.{Logger, LoggerFactory}

object CKANUtilityTest {
  val logger: Logger = LoggerFactory.getLogger(this.getClass);
  val configurationFilename = "config.properties"

  def main(args: Array[String])= {
    val ckanURL = "";
    val ckanKey = "";

    logger.info("Loading configuration file ...")

    val is = new FileInputStream("src/main/resources/" + configurationFilename);
    val properties = new Properties();
    properties.load(is);
    val propertiesKeySet = properties.keySet()
    logger.info(s"propertiesKeySet = ${propertiesKeySet}")

    val mappingpediaProperties = MappingPediaProperties();
    logger.info(s"mappingpediaProperties.keySet() = ${mappingpediaProperties.keySet()}")


    val ckanUtility = new MpcCkanUtility(mappingpediaProperties.ckanURL, mappingpediaProperties.ckanKey)
    //val responseSuccess = ckanUtility.findPackageByPackageName("zaragoza_spain", "zaragoza-farmacias")
    //val responseFailed = ckanUtility.findPackageByPackageName("zaragoza_spain", "zaragoza-farmacias111")
    val ckanVersion = ckanUtility.ckanVersion;
    logger.info(s"ckanVersion = ${ckanVersion}")
    
    println("Bye")
  }

}

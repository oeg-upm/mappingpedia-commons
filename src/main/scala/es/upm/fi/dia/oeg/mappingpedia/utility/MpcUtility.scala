package es.upm.fi.dia.oeg.mappingpedia.utility

import java.io._
import java.util.UUID

import es.upm.fi.dia.oeg.mappingpedia.MappingPediaConstant
import es.upm.fi.dia.oeg.mappingpedia.model.result.ListResult
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.web.multipart.MultipartFile

object MpcUtility {
  val logger: Logger = LoggerFactory.getLogger(this.getClass);

  def calculateHash(downloadURL:String) : String = {
    this.calculateHash(downloadURL, null);
  }

  def calculateHash(downloadURL:String, pEncoding:String) : String = {
    logger.debug(s"calculating hash value of ${downloadURL}");
    val encoding = if(pEncoding == null) { "UTF-8" } else { pEncoding }

    val hashValue = try {
      if (downloadURL != null) {
        val downloadURLContent = scala.io.Source.fromURL(downloadURL, encoding).mkString
        //logger.info(s"downloadURLContent ${downloadURLContent}");

        //val downloadURLContentBase64 = GitHubUtility.encodeToBase64(downloadURLContent);
        //logger.info(s"downloadURLContentBase64 ${downloadURLContentBase64}");

        //MessageDigest.getInstance("SHA").digest(downloadURLContentBase64.getBytes).toString

        downloadURLContent.hashCode.toString;

      } else {
        null
      }
    } catch {
      case e:Exception => {
        e.printStackTrace()
        null
      }
    }

    logger.debug(s"hash value of of ${downloadURL} = ${hashValue}");
    hashValue
  }
  
  def detectMappingLanguage(mappingDocumentFile:File) : String = {
    if(mappingDocumentFile == null) {
      "r2rml"
    } else {
      val absolutePath = mappingDocumentFile.getAbsolutePath;
      this.detectMappingLanguage(absolutePath);
    }
  }

  def detectMappingLanguage(mappingDocumentDownloadURL:String) : String = {

    val mappingLanguage = if (mappingDocumentDownloadURL != null) {
      val splitedMappingLanguage = mappingDocumentDownloadURL.split("\\.")

      if (splitedMappingLanguage.length >= 3) {
        val extension1 = splitedMappingLanguage(splitedMappingLanguage.length-2);
        val extension2 = splitedMappingLanguage(splitedMappingLanguage.length-1);

        if("rml".equalsIgnoreCase(extension1) && "ttl".equalsIgnoreCase(extension2)) {
          "rml"
        } else {
          "r2rml"
        }
      } else {
        "r2rml"
      }
    } else {
      "r2rml"
    }
    mappingLanguage
  }

  def multipartFileToFile(fileRef:MultipartFile) : File = {
    val file = if(fileRef != null) {
      // Path where the uploaded files will be stored.
      val uuid = UUID.randomUUID().toString();

      this.multipartFileToFile(fileRef, uuid);
    } else {
      null
    }
    file
  }

  def multipartFileToFile(fileRef:MultipartFile, uuid:String) : File = {
    val file = if(fileRef != null) {
      // Create the input stream to uploaded files to read data from it.
      val fis:FileInputStream = try {
        if(fileRef != null) {
          val inputStreamAux = fileRef.getInputStream().asInstanceOf[FileInputStream];
          inputStreamAux;
        } else {
          val errorMessage = "can't process the uploaded file, fileRef is null";
          throw new Exception(errorMessage);
        }
      } catch {
        case e:Exception => {
          e.printStackTrace();
          throw e;
        }
      }

      // Get the name of uploaded files.
      val fileName = fileRef.getOriginalFilename();

      MappingPediaUtility.materializeFileInputStream(fis, uuid, fileName);
    } else {
      null
    }
    file


  }

  def generateStringFromTemplateFiles(map: Map[String, String]
                                      , templateFilesPaths:Iterable[String]) : String = {
    try {

      val templateLines:String = templateFilesPaths.map(templateFilePath => {
        val templateStream: InputStream = getClass.getResourceAsStream(
          "/" + templateFilePath);
        val templateLinesAux:String = scala.io.Source.fromInputStream(templateStream)
          .getLines.mkString("\n");
        templateLinesAux
      }).mkString("\n\n")

      val generatedLines = map.foldLeft(templateLines)( (acc, kv) => {
        val mapValue:String = map.get(kv._1).getOrElse("");
        if(mapValue ==null){
          logger.debug("the input value for " + kv._1 + " is null");
          acc.replaceAllLiterally(kv._1, "")
        } else {
          acc.replaceAllLiterally(kv._1, mapValue)
        }
      });

      generatedLines;
    } catch {
      case e:Exception => {
        logger.error("error generating file from template: " + templateFilesPaths);
        e.printStackTrace();
        throw e
      }
    }
  }

  def generateStringFromTemplateFile(map: Map[String, String]
                                     , templateFilePath:String) : String = {
    //logger.info(s"Generating string from template file: $templateFilePath ...")
    try {

      //var lines: String = Source.fromResource(templateFilePath).getLines.mkString("\n");
      val templateStream: InputStream = getClass.getResourceAsStream("/" + templateFilePath)
      val templateLines = scala.io.Source.fromInputStream(templateStream).getLines.mkString("\n");

      val generatedLines = map.foldLeft(templateLines)( (acc, kv) => {
        val mapValue:String = map.get(kv._1).getOrElse("");
        if(mapValue ==null){
          logger.debug("the input value for " + kv._1 + " is null");
          acc.replaceAllLiterally(kv._1, "")
        } else {
          acc.replaceAllLiterally(kv._1, mapValue)
        }
        //logger.info("replacing " + kv._1 + " with " + mapValue);

      });


      /*
      var lines3 = lines;
      map.keys.foreach(key => {
        lines3 = lines3.replaceAllLiterally(key, map(key));
      })
      logger.info("lines3 = " + lines3)
      */

      //logger.info(s"String from template file $templateFilePath generated.")
      generatedLines;
    } catch {
      case e:Exception => {
        logger.error("error generating file from template: " + templateFilePath);
        e.printStackTrace();
        throw e
      }
    }
  }


  def generateManifestString(map: Map[String, String], templateFiles:List[String]) : String = {
    val manifestString = templateFiles.foldLeft("") { (z, i) => {
      //logger.info("templateFiles.foldLeft" + (z, i))
      z + this.generateStringFromTemplateFile(map, i) + "\n\n" ;
    } }

    manifestString;
  }

  def generateManifestFile(map: Map[String, String], templateFiles:List[String], filename:String, datasetID:String) : File = {
    val manifestString = this.generateManifestString(map, templateFiles);
    this.generateManifestFile(manifestString, filename, datasetID);
  }

  def generateManifestFile(manifestString:String, filename:String, datasetID:String) : File = {
    try {
      //def mappingDocumentLines = this.generateManifestLines(map, "templates/metadata-mappingdocument-template.ttl");
      //logger.debug("mappingDocumentLines = " + mappingDocumentLines)

      //def datasetLines = this.generateManifestLines(map, "templates/metadata-dataset-template.ttl");
      //logger.debug("datasetLines = " + datasetLines)

      val uploadDirectoryPath: String = MappingPediaConstant.DEFAULT_UPLOAD_DIRECTORY;
      val outputDirectory: File = new File(uploadDirectoryPath)
      if (!outputDirectory.exists) {
        outputDirectory.mkdirs
      }
      val uuidDirectoryPath: String = uploadDirectoryPath + "/" + datasetID
      //logger.info("upload directory path = " + uuidDirectoryPath)
      val uuidDirectory: File = new File(uuidDirectoryPath)
      if (!uuidDirectory.exists) {
        uuidDirectory.mkdirs
      }

      val file = new File(uuidDirectory + "/" + filename)
      val bw = new BufferedWriter(new FileWriter(file))
      //logger.info(s"manifestTriples = $manifestTriples")
      bw.write(manifestString)
      bw.close()
      file
    } catch {
      case e:Exception => {
        logger.error("error generating manifest file: " + e.getMessage);
        e.printStackTrace();
        null

      }
    }
  }

  def stringToBoolean(inputString:String) : Boolean = {
    try {
      val listOfTrueString = List("true", "yes");

      val result = if(inputString == null) {
        false
      } else {
        if(listOfTrueString.contains(inputString)) {
          true
        } else {
          false
        }
      }

      result
    } catch {
      case e:Exception => {
        e.printStackTrace()
        false
      }
    }

  }

}

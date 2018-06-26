package es.upm.fi.dia.oeg.mappingpedia.model.result
import scala.collection.JavaConverters._

class SingleResult [A] (val result:A) {
  def getResult = this.result
}

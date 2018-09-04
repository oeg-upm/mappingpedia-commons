package es.upm.fi.dia.oeg.mappingpedia.model.result

import scala.collection.JavaConverters._

/**
  * Created by fpriyatna on 04/04/2017.
  */
class ListResult [A] (val count:Integer, val results:Iterable[A]) {
  var statusCode = 200;
  var statusMessage = "OK";

  def this(results:Iterable[A]) = {
    this(results.size, results)
  }

  def this(results:java.util.Collection[A]) = {
    this(results.size, results.asScala)
  }

  def this() = {
    this(Nil)
  }

  def getCount() = this.count;

  def getResults() = this.results.asJava;

  override def toString = s"ListResult($getCount, $getResults)"

  def getStatus_code = this.statusCode
  def getStatusMessage = this.statusMessage
}

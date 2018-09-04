package es.upm.fi.dia.oeg.mappingpedia.model

class MpcTriple(val sub:String, val pre:String, val obj:String) {
  def getSubject = this.sub;
  def getPredicate = this.pre;
  def getObject = this.obj;


}

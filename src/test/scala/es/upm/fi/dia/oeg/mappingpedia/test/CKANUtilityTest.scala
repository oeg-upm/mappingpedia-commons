package es.upm.fi.dia.oeg.mappingpedia.test

import es.upm.fi.dia.oeg.mappingpedia.utility.MappingpediaCommonCKANClient

object CKANUtilityTest {
  def main(args: Array[String])= {
    val ckanURL = "";
    val ckanKey = "";

    val ckanUtility = new MappingpediaCommonCKANClient(ckanURL, ckanKey)
    val responseSuccess = ckanUtility.findPackageByPackageName("zaragoza_spain", "zaragoza-farmacias")
    val responseFailed = ckanUtility.findPackageByPackageName("zaragoza_spain", "zaragoza-farmacias111")

    println("Bye")
  }

}

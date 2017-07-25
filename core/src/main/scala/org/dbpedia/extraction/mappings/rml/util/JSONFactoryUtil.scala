package org.dbpedia.extraction.mappings.rml.util

import com.fasterxml.jackson.databind.JsonNode
import org.dbpedia.extraction.ontology.datatypes.Datatype
import org.dbpedia.extraction.ontology.{Ontology, OntologyProperty, RdfNamespace}

/**
  * Created by wmaroy on 25.07.17.
  */
object JSONFactoryUtil {

  def parameters(key : String, templateNode: JsonNode) : String = {
    val text = templateNode.get("parameters").get(key).asText()
    if(text.equals("null")) null else text
  }

  def getOntologyProperty(templateNode: JsonNode, ontology: Ontology) : OntologyProperty = {
    val context = ContextCreator.createOntologyContext(ontology)
    getOntologyProperty(templateNode, context)
  }

  def getUnit(templateNode: JsonNode, ontology: Ontology) : Datatype = {
    val context = ContextCreator.createOntologyContext(ontology)
    getUnit(templateNode, context)
  }

  def getUnit(templateNode: JsonNode, context : {def ontology: Ontology}) : Datatype = {
    val unitName = JSONFactoryUtil.parameters("unit", templateNode)
    RMLOntologyUtil.loadOntologyDataType(unitName, context)
  }

  def getOntologyProperty(templateNode: JsonNode, context : {def ontology: Ontology}) : OntologyProperty = {
    val ontologyPropertyParameter = JSONFactoryUtil.parameters("ontologyProperty", templateNode)
    val prefix = extractPrefix(ontologyPropertyParameter)
    val localName = extractLocalName(ontologyPropertyParameter)

    if(RdfNamespace.prefixMap.contains(prefix)) {
      val ontologyPropertyIRI = RdfNamespace.prefixMap(prefix).namespace + localName
      RMLOntologyUtil.loadOntologyPropertyFromIRI(ontologyPropertyIRI, context)
    } else {
      RMLOntologyUtil.loadOntologyProperty(ontologyPropertyParameter, context)
    }

  }

  private def extractPrefix(s: String) : String = {
    val prefixPattern = "^[^:]*".r
    prefixPattern.findFirstIn(s).orNull
  }

  private def extractLocalName(s: String) : String = {
    val localNamePattern = "[^:]*$".r
    localNamePattern.findFirstIn(s).orNull
  }

}
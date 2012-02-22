package fi.finlit.edith.util

import java.io.StringReader
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamConstants
import javax.xml.stream.XMLStreamException
import javax.xml.stream.XMLStreamReader
import fi.finlit.edith.sql.domain.LinkElement
import fi.finlit.edith.sql.domain.Paragraph
import fi.finlit.edith.sql.domain.StringElement
import fi.finlit.edith.sql.domain.UrlElement
//remove if not needed
import scala.collection.JavaConversions._

object ParagraphParser {

  def parseSafe(s: String): Paragraph = {
    if (s != null) {
      try {
        return parseParagraph(s)
      } catch {
        case e: XMLStreamException => throw new RuntimeException(e)
      }
    }
    null
  }

  private def parseParagraph(s: String): Paragraph = {
    var document = new StringBuilder("<root>").append(s).append("</root>").toString
    var paragraph = new Paragraph()
    var factory = XMLInputFactory.newInstance()
    var reader = factory.createXMLStreamReader(new StringReader(document))
    var inBib = false
    var inA = false
    var reference: String = null
    while (true) {
      var event = reader.next()
      if (event == XMLStreamConstants.START_ELEMENT) {
        if (reader.getLocalName == "bibliograph") {
          inBib = true
          if (reader.getAttributeCount > 0) {
            reference = reader.getAttributeValue(0)
          }
        } else if (reader.getLocalName == "a") {
          inA = true
          if (reader.getAttributeCount > 0) {
            reference = reader.getAttributeValue(0)
          }
        }
      } else if (event == XMLStreamConstants.CHARACTERS) {
        if (inBib) {
          var element = new LinkElement(reader.getText)
          if (reference != null) {
            element.setReference(reference)
          }
          paragraph.addElement(element)
        } else if (inA) {
          var element = new UrlElement(reader.getText)
          if (reference != null) {
            element.setUrl(reference)
          }
          paragraph.addElement(element)
        } else {
          paragraph.addElement(new StringElement(reader.getText))
        }
      } else if (event == XMLStreamConstants.END_ELEMENT) {
        inBib = false
        inA = false
        reference = null
      } else if (event == XMLStreamConstants.END_DOCUMENT) {
        reader.close()
        //break
      }
    }
    paragraph
  }
}

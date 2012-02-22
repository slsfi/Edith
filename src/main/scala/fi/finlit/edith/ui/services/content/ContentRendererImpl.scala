package fi.finlit.edith.ui.services.content

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.ArrayList
import java.util.Arrays
import java.util.HashMap
import java.util.HashSet
import java.util.Iterator
import java.util.List
import java.util.Map
import java.util.Set
import java.util.regex.Pattern
import javax.xml.namespace.QName
import javax.xml.stream.XMLEventFactory
import javax.xml.stream.XMLEventReader
import javax.xml.stream.XMLEventWriter
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLOutputFactory
import javax.xml.stream.XMLStreamConstants
import javax.xml.stream.XMLStreamException
import javax.xml.stream.XMLStreamReader
import javax.xml.stream.events.Attribute
import javax.xml.stream.events.EndElement
import javax.xml.stream.events.StartElement
import javax.xml.stream.events.XMLEvent
import org.apache.commons.lang.StringEscapeUtils
import org.apache.commons.lang.mutable.MutableBoolean
import org.apache.tapestry5.MarkupWriter
import org.apache.tapestry5.ioc.annotations.Inject
import org.apache.tapestry5.ioc.annotations.Symbol
import fi.finlit.edith.EDITH
import fi.finlit.edith.sql.domain._
import fi.finlit.edith.ui.services.DocumentDao
import fi.finlit.edith.util.ElementContext
import fi.finlit.edith.util.ParagraphParser
import ContentRendererImpl._
//remove if not needed
import scala.collection.JavaConversions._

object ContentRendererImpl {

  private val XML_NS = "http://www.w3.org/XML/1998/namespace"

  private val EMPTY_ELEMENTS = new HashSet[String](Arrays.asList("anchor", "lb", "pb"))

  private val UL_ELEMENTS = new HashSet[String](Arrays.asList("castGroup", "castList", "listPerson"))

  private val LI_ELEMENTS = new HashSet[String](Arrays.asList("castItem", "person"))

  private val TYPE = "type"

  private val ANCHOR = "anchor"

  private val END = "end"

  private val SPAN = "span"

  private val WHITESPACE = Pattern.compile("\\s+")

  private val CLASS = "class"

  private val DIV = "div"
}

class ContentRendererImpl(@Inject documentDao: DocumentDao, @Inject @Symbol(EDITH.BIBLIOGRAPH_URL) bibliographUrl: String) extends ContentRenderer {

  private val inFactory = XMLInputFactory.newInstance()

  private val outFactory = XMLOutputFactory.newInstance()

  private val eventFactory = XMLEventFactory.newInstance()

  private def writeSpan(writer: MarkupWriter, attr: String) {
    writer.element("SPAN", CLASS, attr)
  }

  private def writeNote(writer: MarkupWriter, note: Note) {
    if (note.getLemmaMeaning != null || note.getSubtextSources != null) {
      writeSpan(writer, "lemmaMeaningAndSubtextSources")
      if (note.getLemmaMeaning != null) {
        writer.write("'" + note.getLemmaMeaning + "'")
      }
      if (note.getLemmaMeaning != null && note.getSubtextSources != null) {
        writer.write(", ")
      }
      if (note.getSubtextSources != null) {
        writer.write("Vrt. ")
        writeParagraph(writer, ParagraphParser.parseSafe(note.getSubtextSources))
      }
      writer.end()
    }
  }

  private def writePerson(writer: MarkupWriter, note: Note) {
    var person = note.getPerson
    if (person != null) {
      writeSpan(writer, "personName")
      writer.write(person.getNormalized.getName)
      var timeOfBirth = person.getTimeOfBirth
      var timeOfDeath = person.getTimeOfDeath
      if (timeOfBirth != null || timeOfDeath != null) {
        writer.write(",")
        writer.end()
        var builder = new StringBuilder()
        if (timeOfBirth != null) {
          builder.append(timeOfBirth.asString())
        }
        builder.append("–")
        if (timeOfDeath != null) {
          builder.append(timeOfDeath.asString())
        }
        builder.append(".")
        writeSpan(writer, "lifetime")
        writer.write(builder.toString)
        writer.end()
      } else {
        writer.write(".")
        writer.end()
      }
    }
  }

  private def writePlace(writer: MarkupWriter, note: Note) {
    var place = note.getPlace
    if (place != null) {
      writeSpan(writer, "placeName")
      writer.write(place.getNormalized.getName + ".")
      writer.end()
    }
  }

  override def renderDocumentNotesAsXML(document: Document, documentNotes: List[DocumentNote], writer: MarkupWriter) {
    writer.element("document")
    write(writer, "path", document.getPath)
    write(writer, "revision", -1)
    var noteToDocumentNotes = new HashMap[Note, List[DocumentNote]]()
    for (documentNote <- documentNotes) {
      var notes = noteToDocumentNotes.get(documentNote.getNote)
      if (notes == null) {
        notes = new ArrayList[DocumentNote]()
        noteToDocumentNotes.put(documentNote.getNote, notes)
      }
      notes.add(documentNote)
    }
    for (entry <- noteToDocumentNotes.entrySet()) {
      var note = entry.getKey
      writer.element("note", "xml:id", "note" + note.getId)
      write(writer, "description", note.getDescription)
      write(writer, "format", note.getFormat)
      write(writer, "lemma", note.getLemma)
      write(writer, "lemmaMeaning", note.getLemmaMeaning)
      if (note.getPerson != null) {
        writer.element("person")
        var person = note.getPerson
        writeNameForm("normalizedForm", person.getNormalized, writer)
        writer.element("otherForms")
        for (otherForm <- person.getOtherForms) {
          writeNameForm("nameForm", otherForm, writer)
        }
        writer.end()
        writer.end()
      }
      if (note.getPlace != null) {
        writer.element("place")
        var place = note.getPlace
        writeNameForm("normalizedForm", place.getNormalized, writer)
        writer.element("otherForms")
        for (otherForm <- place.getOtherForms) {
          writeNameForm("nameForm", otherForm, writer)
        }
        writer.end()
        writer.end()
      }
      write(writer, "sources", note.getSources)
      write(writer, "subtextSources", note.getSubtextSources)
      if (!note.getTypes.isEmpty) {
        writer.element("types")
        for (`type` <- note.getTypes) {
          write(writer, TYPE, `type`)
        }
        writer.end()
      }
      writer.element("documentNotes")
      for (dn <- entry.getValue) {
        writer.element("documentNote", "xml:id", END + dn.getId)
        write(writer, "longText", dn.getFullSelection)
        write(writer, "svnRevision", dn.getRevision)
        write(writer, "createdOn", dn.getCreatedOn)
        writer.end()
      }
      writer.end()
      writer.end()
    }
    writer.end()
  }

  private def writeNameForm(name: String, nameForm: NameForm, writer: MarkupWriter) {
    if (nameForm != null) {
      writer.element(name)
      write(writer, "description", nameForm.getDescription)
      write(writer, "first", nameForm.getFirst)
      write(writer, "last", nameForm.getLast)
      writer.end()
    }
  }

  private def write(writer: MarkupWriter, element: String, content: AnyRef) {
    if (content != null) {
      writer.element(element)
      writer.write(content.toString)
      writer.end()
    }
  }

  override def renderDocumentNotes(documentNotes: List[DocumentNote], writer: MarkupWriter) {
    writer.element("ul", CLASS, "notes")
    for (documentNote <- documentNotes) {
      var note = documentNote.getNote
      if (note == null) {
        throw new IllegalStateException("Got no note for documentNote " + documentNote)
      }
      writer.element("li")
      writer.element("a", CLASS, "notelink", "href", "#start" + documentNote.getId)
      if (note.getLemma != null) {
        writer.element(SPAN, CLASS, "lemma")
        writer.write(note.getLemma)
        writer.end()
      }
      if (note.getTerm != null && note.getTerm.getBasicForm != null) {
        writer.element(SPAN, CLASS, "basicForm")
        writer.write(note.getTerm.getBasicForm)
        writer.end()
      }
      writer.end()
      if (note.getFormat != null) {
        if (note.getFormat == NoteFormat.NOTE) {
          writeNote(writer, note)
        }
        if (note.getFormat == NoteFormat.PERSON) {
          writePerson(writer, note)
        }
        if (note.getFormat == NoteFormat.PLACE) {
          writePlace(writer, note)
        }
      }
      if (note.getDescription != null) {
        if (note.getFormat != null && note.getFormat != NoteFormat.NOTE) {
          writer.element(SPAN)
          writer.write("–")
          writer.end()
        }
        writeSpan(writer, "description")
        writeParagraph(writer, ParagraphParser.parseSafe(note.getDescription))
        writer.end()
      }
      if (note.getSources != null) {
        writeSpan(writer, "sources")
        writer.write("(")
        writeParagraph(writer, ParagraphParser.parseSafe(note.getSources))
        writer.write(")")
        writer.end()
      }
      writer.end()
    }
    writer.end()
  }

  private def writeParagraph(writer: MarkupWriter, paragraph: Paragraph) {
    var builder = new StringBuilder()
    for (element <- paragraph.getElements) {
      if (element.isInstanceOf[LinkElement]) {
        var linkElement = element.asInstanceOf[LinkElement]
        var reference = StringEscapeUtils.escapeHtml(linkElement.getReference)
        var string = StringEscapeUtils.escapeHtml(linkElement.getString)
        var result = "<a" + (if (reference == null) "" else " href=\"" + bibliographUrl + reference + "\"") + ">" + string + "</a>"
        builder.append(result)
      } else if (element.isInstanceOf[UrlElement]) {
        var urlElement = element.asInstanceOf[UrlElement]
        var url = StringEscapeUtils.escapeHtml(urlElement.getUrl)
        var string = StringEscapeUtils.escapeHtml(urlElement.getString)
        var result = "<a" + (if (url == null) "" else " href=\"" + url + "\"") + ">" + string + "</a>"
        builder.append(result)
      } else {
        builder.append(element.toString)
      }
    }
    writer.writeRaw(builder.toString)
  }

  override def renderPageLinks(document: Document, writer: MarkupWriter) {
    var is = documentDao.getDocumentStream(document)
    var reader = inFactory.createXMLStreamReader(is)
    try {
      writer.element("ul", CLASS, "pages")
      while (true) {
        var event = reader.next()
        if (event == XMLStreamConstants.START_ELEMENT) {
          var localName = reader.getLocalName
          if (localName == "pb") {
            var page = reader.getAttributeValue(null, "n")
            if (page != null) {
              writer.element("li")
              writer.element("a", "href", "#page" + page)
              writer.writeRaw(page)
              writer.end()
              writer.end()
            }
          }
        } else if (event == XMLStreamConstants.END_DOCUMENT) {
          //break
        }
      }
      writer.end()
    } finally {
      reader.close()
      is.close()
    }
  }

  override def renderDocument(document: Document, writer: MarkupWriter) {
    renderDocument(document, null, writer)
  }

  override def renderDocumentAsXML(document: Document, documentNotes: List[DocumentNote], out: OutputStream) {
    var is = documentDao.getDocumentStream(document)
    var reader = inFactory.createXMLEventReader(is)
    var writer = outFactory.createXMLEventWriter(out)
    var note = new QName("note")
    var openAnchor: XMLEvent = null
    try {
      while (reader.hasNext) {
        var event = reader.nextEvent()
        if (event.isStartElement) {
          var startElement = event.asStartElement()
          var attributes = startElement.getAttributes
          if (startElement.getName.getLocalPart == ANCHOR && attributes.next().getValue.startsWith(END)) {
            var id = (startElement.getAttributes.asInstanceOf[Iterator[Attribute]]).next().getValue.substring(3)
            var atts = new ArrayList[Attribute]()
            atts.add(eventFactory.createAttribute(TYPE, "editor"))
            atts.add(eventFactory.createAttribute("xml:id", END + id))
            atts.add(eventFactory.createAttribute("target", "#start" + id))
            event = eventFactory.createStartElement(note, atts.iterator(), null)
            openAnchor = event
          } else {
            openAnchor = null
          }
        } else if (event.isEndElement) {
          var endElement = event.asEndElement()
          if (openAnchor != null && endElement.getName.getLocalPart == ANCHOR) {
            event = eventFactory.createEndElement(note, null)
          }
          openAnchor = null
        }
        writer.add(event)
      }
    } finally {
      writer.close()
      out.close()
      reader.close()
      is.close()
    }
  }

  override def renderDocument(document: Document, documentNotes: List[DocumentNote], writer: MarkupWriter) {
    var publishIds: Set[Long] = null
    if (documentNotes != null) {
      publishIds = new HashSet[Long]()
      for (documentNote <- documentNotes) {
        publishIds.add(documentNote.getId)
      }
    }
    var is = documentDao.getDocumentStream(document)
    var reader = inFactory.createXMLStreamReader(is)
    var noteContent = new MutableBoolean(false)
    var noteIds = new HashSet[Long]()
    var context = new ElementContext(3)
    try {
      while (true) {
        var event = reader.next()
        if (event == XMLStreamConstants.START_ELEMENT) {
          handleStartElement(reader, writer, context, noteIds, noteContent, publishIds)
        } else if (event == XMLStreamConstants.END_ELEMENT) {
          handleEndElement(reader, writer, context)
        } else if (event == XMLStreamConstants.CHARACTERS) {
          handleCharactersElement(reader, writer, noteIds, noteContent)
        } else if (event == XMLStreamConstants.END_DOCUMENT) {
          //break
        }
      }
    } finally {
      reader.close()
      is.close()
    }
  }

  private def handleStartElement(reader: XMLStreamReader, 
      writer: MarkupWriter, 
      context: ElementContext, 
      noteIds: Set[Long], 
      noteContent: MutableBoolean, 
      publishIds: Set[Long]) {
    var localName = reader.getLocalName
    var name = extractName(reader, localName)
    context.push(name)
    var path = context.getPath
    if (UL_ELEMENTS.contains(localName)) {
      writer.element("ul", CLASS, localName)
      if (path != null) {
        writer.attributes("id", path)
      }
    } else if (LI_ELEMENTS.contains(localName)) {
      writer.element("li", CLASS, localName)
      if (path != null) {
        writer.attributes("id", path)
      }
    } else if (localName == DIV) {
      var `type` = reader.getAttributeValue(null, TYPE)
      writer.element(localName, CLASS, `type`)
      if (path != null) {
        writer.attributes("id", path)
      }
    } else if (localName == "TEI" || localName == "TEI.2") {
      writer.element(DIV, CLASS, "tei")
    } else if (localName == "lb") {
      writer.element("br")
      writer.end()
    } else if (localName == "pb") {
      var page = reader.getAttributeValue(null, "n")
      if (page != null) {
        writer.element(DIV, "id", "page" + page, CLASS, "page")
        writer.writeRaw(page + ".")
        writer.end()
      }
    } else if (localName == ANCHOR) {
      var id = reader.getAttributeValue(XML_NS, "id")
      if (id == null) {
        return
      } else if (id.startsWith("start")) {
        if (publishIds != null && !publishIds.contains(Long.parseLong(id.replace("start", "")))) {
          return
        }
        writer.element(SPAN, CLASS, "notestart", "id", id)
        writer.end()
        noteContent.setValue(true)
        noteIds.add(Long.parseLong(id.substring("start".length())))
      } else if (id.startsWith(END)) {
        writer.element(SPAN, "class", "noteanchor", "id", id)
        writer.write(" [*] ")
        writer.end()
        noteIds.remove(Long.parseLong(id.substring(END.length())))
        if (noteIds.isEmpty) {
          noteContent.setValue(false)
        }
      }
    } else {
      writer.element(DIV, CLASS, name)
      if (path != null) {
        writer.attributes("id", path)
      }
    }
  }

  private def extractName(reader: XMLStreamReader, localName: String): String = {
    if (localName == DIV) {
      return reader.getAttributeValue(null, TYPE)
    }
    localName
  }

  private def handleEndElement(reader: XMLStreamReader, writer: MarkupWriter, context: ElementContext) {
    context.pop()
    var localName = reader.getLocalName
    if (!EMPTY_ELEMENTS.contains(localName)) {
      writer.end()
    }
  }

  private def handleCharactersElement(reader: XMLStreamReader, 
      writer: MarkupWriter, 
      noteIds: Set[Long], 
      noteContent: MutableBoolean) {
    var text = WHITESPACE.matcher(reader.getText).replaceAll(" ")
    if (noteContent.booleanValue() && !text.trim().isEmpty) {
      var classes = new StringBuilder("notecontent")
      for (noteId <- noteIds) {
        classes.append(" n").append(noteId)
      }
      writer.element(SPAN, CLASS, classes)
      writer.writeRaw(StringEscapeUtils.escapeXml(text))
      writer.end()
    } else {
      writer.writeRaw(StringEscapeUtils.escapeXml(text))
    }
  }
}

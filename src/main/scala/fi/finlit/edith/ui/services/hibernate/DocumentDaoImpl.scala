package fi.finlit.edith.ui.services.hibernate

import fi.finlit.edith.sql.domain.QDocument.document
import fi.finlit.edith.sql.domain.QDocumentNote.documentNote
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.ArrayList
import java.util.Collection
import java.util.Collections
import java.util.Comparator
import java.util.Enumeration
import java.util.HashSet
import java.util.List
import java.util.Map
import java.util.Set
import javax.xml.namespace.QName
import javax.xml.stream.EventFilter
import javax.xml.stream.XMLEventFactory
import javax.xml.stream.XMLEventReader
import javax.xml.stream.XMLEventWriter
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLOutputFactory
import javax.xml.stream.XMLStreamException
import javax.xml.stream.events.Attribute
import javax.xml.stream.events.StartElement
import javax.xml.stream.events.XMLEvent
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipFile
import org.apache.commons.io.IOUtils
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.mutable.MutableInt
import org.apache.tapestry5.ioc.annotations.Inject
import org.apache.tapestry5.ioc.annotations.Symbol
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.mysema.commons.lang.Assert
import com.mysema.query.jpa.hibernate.HibernateDeleteClause
import fi.finlit.edith.EDITH
import fi.finlit.edith.dto.SelectedText
import fi.finlit.edith.sql.domain.Document
import fi.finlit.edith.sql.domain.DocumentNote
import fi.finlit.edith.sql.domain.Note
import fi.finlit.edith.ui.services.AuthService
import fi.finlit.edith.ui.services.DocumentDao
import fi.finlit.edith.ui.services.DocumentNoteDao
import fi.finlit.edith.ui.services.NoteAdditionFailedException
import fi.finlit.edith.ui.services.NoteDao
import fi.finlit.edith.ui.services.ServiceException
import fi.finlit.edith.ui.services.svn.FileItem
import fi.finlit.edith.ui.services.svn.FileItemWithDocumentId
import fi.finlit.edith.ui.services.svn.SubversionService
import fi.finlit.edith.ui.services.svn.UpdateCallback
import fi.finlit.edith.util.ElementContext
import DocumentDaoImpl._
import scala.reflect.BeanProperty
//remove if not needed
import scala.collection.JavaConversions._

object DocumentDaoImpl {

  private val logger = LoggerFactory.getLogger(classOf[DocumentDaoImpl])

  private val XML_NS = "http://www.w3.org/XML/1998/namespace"

  private val TEI_NS = "http://www.tei-c.org/ns/1.0"

  private val XML_ID_QNAME = new QName(XML_NS, "id")

  private val TEI_TYPE_QNAME = new QName(null, "type")

  private class Matched {

    @BeanProperty
    var startMatched: Boolean = _

    @BeanProperty
    var endMatched: Boolean = _

    def areBothMatched(): Boolean = startMatched && endMatched

    def matchEnd() {
      endMatched = true
    }

    def matchStart() {
      startMatched = true
    }
  }

  def extractName(element: StartElement): String = {
    var localName = element.getName.getLocalPart
    var name = localName
    if (localName == "div") {
      var attribute = element.getAttributeByName(TEI_TYPE_QNAME)
      if (attribute != null) {
        name = attribute.getValue
      }
    }
    name
  }

  def getIndex(str: String, word: String, occurrence: Int): Int = {
    var index = -1
    var n = occurrence
    while (n > 0) {
      index = str.indexOf(word, index + 1)
      if (index == -1) {
        return -1
      }
      n -= 1
    }
    index
  }

  private def createRemoveFilter(documentNotes: DocumentNote*): EventFilter = {
    val anchors = new HashSet[String](documentNotes.length * 2)
    for (note <- documentNotes) {
      anchors.add("start" + note.getId)
      anchors.add("end" + note.getId)
    }
    new EventFilter() {

      private var removeNextEndElement = false

      override def accept(event: XMLEvent): Boolean = {
        if (event.isStartElement) {
          var attr = event.asStartElement().getAttributeByName(XML_ID_QNAME)
          if (attr != null && anchors.contains(attr.getValue)) {
            removeNextEndElement = true
            return false
          }
        } else if (event.isEndElement && removeNextEndElement) {
          removeNextEndElement = false
          return false
        }
        true
      }
    }
  }
}

class DocumentDaoImpl(@Inject versioningService: SubversionService, 
    @Inject authService: AuthService, 
    @Inject noteDao: NoteDao, 
    @Inject documentNoteDao: DocumentNoteDao, 
    @Inject @Symbol(EDITH.SVN_DOCUMENT_ROOT) documentRoot: String) extends AbstractDao[Document] with DocumentDao {

  private val eventFactory = XMLEventFactory.newInstance()

  private val inFactory = XMLInputFactory.newInstance()

  private val outFactory = XMLOutputFactory.newInstance()

  override def getById(id: java.lang.Long): Document = {
    query.from(document).where(document.id eq id).singleResult(document)
  }

  override def addDocument(path: String, file: File) {
    versioningService.importFile(path, file)
  }

  override def addDocumentsFromZip(parentPath: String, file: File): Int = {
    try {
      var parent = if (parentPath.endsWith("/")) parentPath else parentPath + "/"
      var zipFile = new ZipFile(file)
      var entries = zipFile.getEntries
      var rv = 0
      while (entries.hasMoreElements()) {
        var entry = entries.nextElement()
        if (!entry.getName.endsWith(".xml")) {
          continue;
        }
        var in = zipFile.getInputStream(entry)
        var outFile = File.createTempFile("tei", ".xml")
        var out = new FileOutputStream(outFile)
        try {
          IOUtils.copy(in, out)
        } finally {
          in.close()
          out.close()
        }
        addDocument(parent + entry.getName, outFile)
        outFile.delete()
        rv += 1
      }
      rv
    } catch {
      case e: IOException => throw new ServiceException(e)
    }
  }

  override def addNote(note: Note, document: Document, selection: SelectedText): DocumentNote = {
    val documentNote = new DocumentNote()
    getSession.save(documentNote)
    val position = new MutableInt(0)
    versioningService.commit(document.getPath, -1, authService.getUsername, new UpdateCallback() {

      override def update(source: InputStream, target: OutputStream) {
        try {
          position.setValue(addNote(inFactory.createXMLEventReader(source), outFactory.createXMLEventWriter(target), selection, documentNote.getId))
        } catch {
          case e: XMLStreamException => throw new ServiceException(e)
          case e: NoteAdditionFailedException => throw new ServiceException(e)
        }
      }
    })
    var updatedDocumentNote = noteDao.createDocumentNote(documentNote, note, document, selection.getSelection, position.intValue())
    updatedDocumentNote
  }

  def addNote(reader: XMLEventReader, 
      writer: XMLEventWriter, 
      sel: SelectedText, 
      localId: java.lang.Long): Int = {
    logger.info(sel.toString)
    var context = new ElementContext(3)
    var allStrings = new StringBuilder()
    var startStrings = new StringBuilder()
    var endStrings = new StringBuilder()
    var events = new ArrayList[XMLEvent]()
    var endOffset = new MutableInt(0)
    var position = new MutableInt(0)
    var matched = new Matched()
    try {
      var buffering = false
      var startedBuffering = false
      while (reader.hasNext) {
        var handled = false
        var event = reader.nextEvent()
        if (event.isStartElement) {
          position.increment()
          context.push(extractName(event.asStartElement()))
          if (buffering && !matched.areBothMatched()) {
            handled = true
            if (context.equalsAny(sel.getStartId)) {
              var tempContext = context.clone().asInstanceOf[ElementContext]
              tempContext.pop()
              if (sel.isStartChildOfEnd) {
                for (i <- 1 until sel.howDeepIsStartInEnd()) {
                  tempContext.pop()
                }
              }
              flush(writer, endStrings.toString, sel, events, tempContext, matched, localId, endOffset)
              allStrings = new StringBuilder()
              events.clear()
              handled = false
            } else if (context.equalsAny(sel.getEndId)) {
              var tempContext = context.clone().asInstanceOf[ElementContext]
              tempContext.pop()
              if (sel.isEndChildOfStart) {
                for (i <- 1 until sel.howDeepIsEndInStart()) {
                  tempContext.pop()
                }
              }
              flush(writer, startStrings.toString, sel, events, tempContext, matched, localId, endOffset)
              allStrings = new StringBuilder()
              events.clear()
              handled = false
            } else {
              events.add(event)
            }
          }
          if (context.equalsAny(sel.getStartId, sel.getEndId) && !matched.areBothMatched()) {
            buffering = true
            startedBuffering = true
          }
        } else if (event.isCharacters) {
          position.increment()
          if (buffering && !matched.areBothMatched()) {
            events.add(event)
            handled = true
            if (context.equalsAny(sel.getStartId)) {
              startStrings.append(event.asCharacters().getData)
              allStrings.append(event.asCharacters().getData)
            } else if (context.equalsAny(sel.getEndId)) {
              endStrings.append(event.asCharacters().getData)
              allStrings.append(event.asCharacters().getData)
            }
          }
        } else if (event.isEndElement) {
          position.increment()
          if (context.equalsAny(sel.getStartId, sel.getEndId)) {
            flush(writer, if (!matched.isStartMatched) allStrings.toString else endStrings.toString, sel, events, context, matched, localId, endOffset)
            buffering = false
            events.clear()
            allStrings = new StringBuilder()
          }
          context.pop()
          if (buffering && !matched.areBothMatched()) {
            events.add(event)
            handled = true
          }
          if (startedBuffering && (sel.isStartChildOfEnd || sel.isEndChildOfStart)) {
            buffering = !matched.areBothMatched()
          }
        }
        if (!handled) {
          writer.add(event)
        }
      }
    } catch {
      case e: XMLStreamException => logger.error("", e)
      case e: CloneNotSupportedException => logger.error("", e)
    } finally {
      try {
        writer.close()
        reader.close()
      } catch {
        case e: XMLStreamException => logger.error("", e)
      }
      if (!matched.areBothMatched()) {
        throw new NoteAdditionFailedException(sel, String.valueOf(localId), matched.isStartMatched, matched.isEndMatched)
      }
    }
    position.intValue()
  }

  private def flush(writer: XMLEventWriter, 
      string: String, 
      sel: SelectedText, 
      events: List[XMLEvent], 
      context: ElementContext, 
      matched: Matched, 
      localId: java.lang.Long, 
      endOffset: MutableInt) {
    var startAnchor = "start" + localId
    var endAnchor = "end" + localId
    var startAndEndInSameElement = sel.getStartId == sel.getEndId
    var offset = 0
    var startIndex = getIndex(string, sel.getFirstWord, sel.getStartIndex)
    var endIndex = getIndex(string, sel.getLastWord, sel.getEndIndex) + sel.getLastWord.length()
    for (e <- events) {
      var handled = false
      if (e.isStartElement) {
        context.push(extractName(e.asStartElement()))
      } else if (e.isEndElement) {
        context.pop()
      } else if (e.isCharacters && context.equalsAny(sel.getStartId, sel.getEndId)) {
        var eventString = e.asCharacters().getData
        var relativeStart = startIndex - offset
        var relativeEnd = endIndex - (if (context.equalsAny(sel.getEndId) && sel.isStartChildOfEnd) endOffset.intValue() else offset)
        var index = -1
        offset += eventString.length()
        if (context.equalsAny(sel.getEndId) && sel.isStartChildOfEnd) {
          endOffset.add(eventString.length())
        }
        if (context.equalsAny(sel.getStartId) && !matched.isStartMatched && startIndex <= offset) {
          writer.add(eventFactory.createCharacters(eventString.substring(0, relativeStart)))
          writeAnchor(writer, startAnchor)
          matched.matchStart()
          handled = true
          index = relativeStart
        }
        if (context.equalsAny(sel.getEndId) && matched.isStartMatched && !matched.isEndMatched && endIndex <= (if (context.equalsAny(sel.getEndId) && sel.isStartChildOfEnd) endOffset.intValue() else offset)) {
          if (!startAndEndInSameElement) {
            writer.add(eventFactory.createCharacters(eventString.substring(0, relativeEnd)))
          } else {
            writer.add(eventFactory.createCharacters(eventString.substring(if (relativeStart > -1) relativeStart else 0, relativeEnd)))
          }
          writeAnchor(writer, endAnchor)
          matched.matchEnd()
          handled = true
          index = relativeEnd
        }
        if (handled) {
          writer.add(eventFactory.createCharacters(eventString.substring(index)))
        }
      }
      if (!handled) {
        writer.add(e)
      }
    }
  }

  private def writeAnchor(writer: XMLEventWriter, anchorId: String) {
    writer.add(eventFactory.createStartElement("", TEI_NS, "anchor"))
    writer.add(eventFactory.createAttribute("xml", XML_NS, "id", anchorId))
    writer.add(eventFactory.createEndElement("", TEI_NS, "anchor"))
  }

  override def getDocumentForPath(svnPath: String): Document = {
    Assert.notNull(svnPath, "svnPath was null")
    getDocumentMetadata(svnPath)
  }

  override def getDocumentsOfFolder(svnFolder: String): List[Document] = {
    Assert.notNull(svnFolder, "svnFolder was null")
    var entries = versioningService.getEntries(svnFolder, -1)
    var documents = new ArrayList[Document](entries.size)
    for (path <- entries.keySet()) {
      documents.add(getDocumentMetadata(path))
    }
    documents
  }

  private def getDocumentMetadata(path: String): Document = {
    var doc = query.from(document).where(document.path eq path).uniqueResult(document)
    if (doc != null) {
      doc
    } else {
      createDocument(path, path.substring(path.lastIndexOf('/') + 1))
    }
  }

  private def createDocument(path: String, title: String): Document = {
    var doc = new Document()
    doc.setPath(path)
    doc.setTitle(title)
    getSession.save(doc)
    doc
  }

  override def getDocumentStream(document: Document): InputStream = {
    Assert.notNull(document, "document was null")
    versioningService.getStream(document.getPath, -1)
  }

  override def removeDocumentNotes(document: Document, documentNotes: DocumentNote*) {
    var revision: Long = _
    revision = versioningService.commit(document.getPath, -1, authService.getUsername, new UpdateCallback() {

      override def update(source: InputStream, target: OutputStream) {
        try {
          streamEvents(inFactory.createFilteredReader(inFactory.createXMLEventReader(source), createRemoveFilter(documentNotes)), outFactory.createXMLEventWriter(target))
        } catch {
          case e: XMLStreamException => throw new ServiceException(e)
        }
      }
    })
    for (dn <- documentNotes) {
      dn.setRevision(revision)
      documentNoteDao.remove(dn)
    }
  }

  private def streamEvents(reader: XMLEventReader, writer: XMLEventWriter) {
    try {
      while (reader.hasNext) {
        writer.add(reader.nextEvent())
      }
    } finally {
      writer.close()
      reader.close()
    }
  }

  override def updateNote(documentNote: DocumentNote, selection: SelectedText): DocumentNote = {
    var doc = documentNote.getDocument
    var newRevision: Long = _
    val position = new MutableInt(0)
    newRevision = versioningService.commit(doc.getPath, -1, authService.getUsername, new UpdateCallback() {

      override def update(source: InputStream, target: OutputStream) {
        try {
          var eventReader = inFactory.createFilteredReader(inFactory.createXMLEventReader(source), createRemoveFilter(Array[DocumentNote](documentNote)))
          position.setValue(addNote(eventReader, outFactory.createXMLEventWriter(target), selection, documentNote.getId))
        } catch {
          case e: XMLStreamException => throw new ServiceException(e)
          case e: NoteAdditionFailedException => throw new ServiceException(e)
        }
      }
    })
    var fetchedDocumentNote = getSession.get(classOf[DocumentNote], documentNote.getId).asInstanceOf[DocumentNote]
    fetchedDocumentNote.setFullSelection(selection.getSelection)
    fetchedDocumentNote.setRevision(newRevision)
    fetchedDocumentNote.setPosition(position.intValue())
    fetchedDocumentNote
  }

  override def remove(doc: Document) {
    Assert.notNull(doc, "document was null")
    versioningService.delete(doc.getPath)
    new HibernateDeleteClause(getSession, documentNote).where(documentNote.document eq doc).execute()
    getSession.delete(doc)
  }

  override def remove(id: java.lang.Long) {
    var document = getSession.get(classOf[Document], id).asInstanceOf[Document]
    remove(document)
  }

  override def removeAll(documents: Collection[Document]) {
    for (document <- documents) {
      remove(document)
    }
  }

  override def rename(id: java.lang.Long, newPath: String) {
    var doc = getById(id)
    var fullPath = doc.getPath
    var directoryPath = fullPath.substring(0, fullPath.lastIndexOf('/') + 1)
    var documents = query.from(document).where(document.path.contains(doc.getPath)).list(document)
    for (d <- documents if d.getId != id) {
      d.setPath(d.getPath.replace(doc.getPath, directoryPath + newPath))
    }
    versioningService.move(fullPath, directoryPath + newPath)
    doc.setPath(directoryPath + newPath)
    doc.setTitle(newPath.substring(newPath.lastIndexOf('/') + 1))
  }

  override def fromPath(path: String, id: java.lang.Long): List[FileItemWithDocumentId] = {
    var files = if (StringUtils.isEmpty(path)) versioningService.getFileItems(documentRoot, -1) else versioningService.getFileItems(path, -1)
    var rv = new ArrayList[FileItemWithDocumentId]()
    for (file <- files) {
      var doc = getDocumentForPath(file.getPath)
      rv.add(new FileItemWithDocumentId(file.getTitle, file.getPath, file.isFolder, file.getChildren, file.hasChildren(), doc.getId, doc.getId == id, documentNoteDao.getNoteCountForDocument(doc.getId)))
    }
    Collections.sort(rv, new Comparator[FileItemWithDocumentId]() {

      override def compare(o1: FileItemWithDocumentId, o2: FileItemWithDocumentId): Int = {
        if (o1.isFolder && !o2.isFolder) {
          return -1
        } else if (!o1.isFolder && o2.isFolder) {
          return 1
        }
        return o1.getTitle.toLowerCase().compareTo(o2.getTitle.toLowerCase())
      }
    })
    rv
  }
}

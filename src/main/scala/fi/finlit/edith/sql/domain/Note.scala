package fi.finlit.edith.sql.domain

import java.util.ArrayList
import java.util.Collection
import java.util.HashSet
import java.util.Set
import java.util.regex.Pattern
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table
import org.apache.commons.lang.StringUtils
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.Cascade
import org.hibernate.annotations.CascadeType
import org.joda.time.DateTime
import fi.finlit.edith.Identifiable
import Note._
import scala.reflect.BeanProperty
//remove if not needed
import scala.collection.JavaConversions._

object Note {

  private val WHITESPACE = Pattern.compile("\\s+")

  def createLemmaFromLongText(text: String): String = {
    var result: String = null
    if (WHITESPACE.matcher(text).find()) {
      var words = StringUtils.split(text)
      if (words.length == 2) {
        result = words(0) + " " + words(1)
      } else if (words.length > 1) {
        result = words(0) + " – – " + words(words.length - 1)
      } else {
        result = words(0)
      }
    } else {
      result = text
    }
    result
  }
}

@Entity
@Table(name = "note")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
class Note extends Identifiable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty
  var id: java.lang.Long = _

  @BeanProperty
  var lemma: String = _

  @ManyToOne
  @Cascade(value = CascadeType.SAVE_UPDATE)
  @BeanProperty
  var term: Term = _

  @BeanProperty
  var editedOn: java.lang.Long = _

  @ManyToMany(fetch = FetchType.LAZY)
  @BeanProperty
  var allEditors = new HashSet[User]()

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "note")
  @BeanProperty
  var comments = new HashSet[NoteComment]()

  @ManyToOne
  @BeanProperty
  var lastEditedBy: User = _

  @BeanProperty
  var sources: String = _

  @BeanProperty
  var subtextSources: String = _

  @BeanProperty
  var deleted: Boolean = _

  @Column(columnDefinition = "TEXT")
  @BeanProperty
  var description: String = _

  @Enumerated(EnumType.STRING)
  @BeanProperty
  var status = NoteStatus.INITIAL

  @ElementCollection(fetch = FetchType.LAZY)
  @Enumerated(EnumType.STRING)
  @JoinTable(name = "note_types")
  @BeanProperty
  var types = new HashSet[NoteType]()

  @Enumerated(EnumType.STRING)
  @BeanProperty
  var format: NoteFormat = _

  @BeanProperty
  var lemmaMeaning: String = _

  @ManyToOne
  @Cascade(value = CascadeType.SAVE_UPDATE)
  @BeanProperty
  var person: Person = _

  @ManyToOne
  @Cascade(value = CascadeType.SAVE_UPDATE)
  @BeanProperty
  var place: Place = _

  @BeanProperty
  var documentNoteCount = 0

  def getEditedOnDate(): DateTime = new DateTime(editedOn)

  def addComment(comment: NoteComment) {
    comments.add(comment)
  }

  def removeComment(comment: NoteComment) {
    comments.remove(comment)
  }

  def decDocumentNoteCount() {
    if (documentNoteCount > 0) {
      documentNoteCount -= 1
    }
  }

  def incDocumentNoteCount() {
    documentNoteCount += 1
  }

  def addEditor(user: User) {
    allEditors.add(user)
  }

  def getEditors(): String = {
    var result = new ArrayList[String]()
    for (user <- getAllEditors if getLastEditedBy == user) {
      result.add(user.getUsername)
    }
    StringUtils.join(result, ", ")
  }
}

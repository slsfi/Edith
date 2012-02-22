package fi.finlit.edith.ui.components

import java.util.regex.Pattern
import org.apache.commons.lang.StringEscapeUtils
import org.apache.tapestry5.MarkupWriter
import org.apache.tapestry5.annotations.BeginRender
import org.apache.tapestry5.annotations.Parameter
import Markdown._
//remove if not needed
import scala.collection.JavaConversions._

object Markdown {

  private val ITALIC_REPLACEMENT = "<em>$2</em>"

  private val BOLD_REPLACEMENT = "<strong>$2</strong>"

  private val ITALIC_PATTERN = Pattern.compile("(\\*|_)(?=\\S)(.+?)(?<=\\S)\\1")

  private val BOLD_PATTERN = Pattern.compile("(\\*\\*|__)(?=\\S)(.+?[*_]*)(?<=\\S)\\1")
}

class Markdown {

  @Parameter(required = true)
  private var value: String = _

  @Parameter
  private var raw = false

  @BeginRender
  def render(writer: MarkupWriter) {
    if (value == null) {
      return
    }
    var result = if (raw) value else StringEscapeUtils.escapeHtml(value)
    result = BOLD_PATTERN.matcher(result).replaceAll(BOLD_REPLACEMENT)
    result = ITALIC_PATTERN.matcher(result).replaceAll(ITALIC_REPLACEMENT)
    writer.writeRaw(result)
  }
}

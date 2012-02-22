package fi.finlit.edith.util

import java.util.HashMap
import java.util.Map
import java.util.Stack
import javax.annotation.Nullable
import org.apache.commons.lang.mutable.MutableInt
import ElementContext._
//remove if not needed
import scala.collection.JavaConversions._

object ElementContext {

  class Item(name: String) extends Cloneable {

    private var counts = new HashMap[String, MutableInt]()

    def getName(elemName: String): String = {
      var intValue = counts.get(elemName)
      if (intValue == null) {
        intValue = new MutableInt(1)
        counts.put(elemName, intValue)
        return elemName
      }
      intValue.add(1)
      elemName + intValue
    }

    override def toString(): String = name

    override def clone(): AnyRef = {
      var item = super.clone().asInstanceOf[Item]
      item.counts = new HashMap[String, MutableInt]()
      for (key <- counts.keySet()) {
        item.counts.put(key, counts.get(key))
      }
      item
    }
  }
}

class ElementContext(offset: Int) extends Cloneable {

  private var stack = new Stack[Item]()

  private var path = null

  def push(name: String) {
    var s = name
    if (!stack.isEmpty) {
      s = stack.peek().getName(s)
    }
    stack.push(new Item(s))
    path = null
  }

  def pop() {
    stack.pop()
    path = null
  }

  override def toString(): String = stack.toString

  def getPath(): String = {
    if (path != null) {
      return path
    }
    if (stack.size > offset) {
      var b = new StringBuilder()
      for (i <- offset until stack.size) {
        if (i > offset) {
          b.append("-")
        }
        b.append(stack.get(i).name)
      }
      path = b.toString
      return path
    }
    null
  }

  override def clone(): AnyRef = {
    var clone = super.clone().asInstanceOf[ElementContext]
    clone.stack = new Stack[Item]()
    for (item <- stack) {
      clone.stack.push(item.clone().asInstanceOf[Item])
    }
    clone
  }

  def equalsAny(strings: String*): Boolean = {
    for (s <- strings if s == getPath) {
      return true
    }
    false
  }
}

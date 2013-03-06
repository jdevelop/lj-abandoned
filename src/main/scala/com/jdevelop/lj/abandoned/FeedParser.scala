package com.jdevelop.lj.abandoned

import xml.Elem
import java.text.SimpleDateFormat
import java.util.Date

/**
 * User: Eugene Dzhurinsky
 * Date: 3/5/13
 */
trait FeedParser extends FeedProvider {

  private type ItemTuple = (Journal, String)

  def readFeedKnows(url: String): Option[Seq[ItemTuple]] = {
    withFeedStream {
      case streamData =>
        streamData \\ "Person" \ "knows" map {
          case z =>
            val person = z \ "Person"
            val name = (person \ "nick").text
            val foafUrl = (person \ "seeAlso").head.attributes.value.head.text
            (Journal(name, null), foafUrl)
        }
    }(url)
  }

  def readFeedLastDate(input: ItemTuple): Option[Journal] = {
    val sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val (journal, url) = input
    withFeedStream {
      case z: Elem =>
        try {
          val dateStr = (z \\ "Person" \ "weblog").head.attributes.asAttrMap("lj:dateLastUpdated")
          val date = sdf.parse(dateStr)
          journal.copy(lastUpdate = date)
        } catch {
          case e => journal.copy(lastUpdate = new Date(0))
        }
    }(url)
  }

}

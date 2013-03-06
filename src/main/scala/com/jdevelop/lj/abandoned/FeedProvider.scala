package com.jdevelop.lj.abandoned

import xml.{XML, Elem}
import org.apache.http.impl.client.{DefaultRedirectStrategy, DefaultHttpClient}
import org.apache.http.impl.conn.PoolingClientConnectionManager
import org.apache.http.client.methods.HttpGet
import org.apache.http.params.CoreProtocolPNames

/**
 * User: Eugene Dzhurinsky
 * Date: 3/5/13
 */
trait FeedProvider {

  private val client = new DefaultHttpClient(new PoolingClientConnectionManager())
  client.setRedirectStrategy(new DefaultRedirectStrategy())
  client.getParams.setParameter(CoreProtocolPNames.USER_AGENT,
    "Mozilla/5.0 (X11; Linux x86_64; rv:19.0) Gecko/20100101 Firefox/19.0")

  def withFeedStream[T](f: Elem => T)(url: String): Option[T] = {
    //    println("Requesting: " + url)
    val get: HttpGet = new HttpGet(url)
    val response = client.execute(get)
    //    println("Response: " + url + " => " + response.getStatusLine)
    (response.getStatusLine.getStatusCode match {
      case 200 =>
        val content = response.getEntity.getContent
        try {
          Some(XML.load(content))
        } catch {
          case e => None
        } finally {
          content.close()
          get.releaseConnection()
          get.abort()
        }
      case _ => None
    }).map(f)
  }

}

package com.jdevelop.lj.abandoned

import java.util.concurrent.{TimeUnit, Executors}
import concurrent.{Await, ExecutionContext, future}
import akka.dispatch.Futures

import collection.JavaConversions._
import concurrent.duration.Duration
import java.text.SimpleDateFormat

/**
 * User: Eugene Dzhurinsky
 * Date: 3/5/13
 */
object Main {

  def main(args: Array[String]) {
    val pool = Executors.newFixedThreadPool(args(0).toInt)
    implicit val ec = ExecutionContext.fromExecutor(pool)
    val username = args(1)
    val god = new FeedParser with FeedProvider
    god.readFeedKnows("http://" + username + ".livejournal.com/data/foaf").map {
      knows =>
        val result = Futures.sequence(for (
          item <- knows
        ) yield future {
            god.readFeedLastDate(item)
          }, ec
        )
        val res = Await.result(result, Duration(1, TimeUnit.MINUTES))
        println("Done")

        implicit val order = new Ordering[Journal] {
          def compare(x: Journal, y: Journal): Int = {
            x.lastUpdate.compareTo(y.lastUpdate) match {
              case 0 => x.username.compareTo(y.username)
              case x => x
            }
          }
        }

        val sorted = res.flatten.toList.sorted
        val sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm")
        val html = <html>
          <head>
            <title>Friends list for
              {username}
            </title>
            <body>
              <table>
                <tr>
                  <th>Username</th> <th>Last journal update</th>
                </tr>{for (Journal(ljuser, ljdate) <- sorted;
                url = "http://"+ljuser+".livejournal.com/profile"
              ) yield {
                <tr>
                  <td>
                    <a href={url}>{ljuser}</a>
                  </td>
                  <td>
                    {sdf.format(ljdate)}
                  </td>
                </tr>
              }}<tr>
              </tr>
              </table>
            </body>
          </head>
        </html>
        print(html)
        pool.shutdownNow()
    }
  }

}
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
    if (args.length == 0 || (args.length == 2 && !args(0).forall(_.isDigit))) {
      println("Usage: java -jar abandoned-0.1.jar <number of threads> username")
      sys.exit(1)
    }
    val (username, threads) = if (args.length == 2) {
      (args(1), args(0).toInt)
    } else {
      (args(0), 20)
    }

    val pool = Executors.newFixedThreadPool(threads)
    implicit val ec = ExecutionContext.fromExecutor(pool)
    val god = new FeedParser with FeedProvider
    try {
      god.readFeedKnows("http://" + username + ".livejournal.com/data/foaf").map {
        knows =>
          val result = Futures.sequence(for (
            item <- knows
          ) yield future {
              god.readFeedLastDate(item)
            }, ec
          )
          val res = Await.result(result, Duration(5, TimeUnit.MINUTES))
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
                             url = "http://" + ljuser + ".livejournal.com/profile"
                ) yield {
                  <tr>
                    <td>
                      <a href={url}>
                        {ljuser}
                      </a>
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
      }
    } finally {
      pool.shutdownNow()
    }
  }

}
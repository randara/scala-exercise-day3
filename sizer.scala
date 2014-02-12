import scala.io._
import scala.actors._
import Actor._
import scala.xml._

// START:loader
object PageLoader {
  def getPageSize(url : String) = {
    io.Source.fromURL(url).mkString.length
  }

  def countLinks(url: String) = {
    var html = io.Source.fromURL(url).mkString
    var links = html.split("href")
    println(links.length-1+" links on "+url)
  }

}
// END:loader

val urls = List(
               "http://www.apple.com",
               "http://www.cnn.com",
               "https://www.twitter.com/" )

// START:time
def timeMethod(method: () => Unit) = {
 val start = System.nanoTime
 method()
 val end = System.nanoTime
 println("Method took " + (end - start)/1000000000.0 + " seconds.")
}
// END:time

// START:sequential
def getPageSizeSequentially() = {
 for(url <- urls) {
   println("Size for " + url + ": " + PageLoader.getPageSize(url))
 }
}
// END:sequential

// START:concurrent
def getPageSizeConcurrently() = {
 val caller = self

 for(url <- urls) {
   actor { 
      caller ! (url, PageLoader.getPageSize(url)) 
      caller ! (url, PageLoader.countLinks(url))
    }
 }

 for(i <- 1 to urls.size) {
   receive {
     case (url, size) =>
       println("Size for " + url + ": " + size)            
   }
 }
}
// END:concurrent

// START:script
println("Sequential run:")
timeMethod { getPageSizeSequentially }

println("Concurrent run")
timeMethod { getPageSizeConcurrently }
// END:script
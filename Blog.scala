object Blog extends App {
  import java.io.{File, PrintWriter}

  val layout = readFile("layout.html")
  val posts = new File("posts").listFiles.filter(_.getName.endsWith(".html")).filter(_.getName != "index.html")

  val linksToPosts = posts.map((f: File) => {
    val content = readFile("posts/" + f.getName)
    val title = "<h2>(.*)</h2>".r.findFirstMatchIn(content).map(_.group(1)).getOrElse("Panun Blogsta")
    val time = "<time>(.*)</time>".r.findFirstMatchIn(content).map(_.group(1)).getOrElse("")
    val html = layout
      .replaceAllLiterally("<title>Panun Blogsta</title>", s"<title>$title</title>")
      .replaceAllLiterally("<body></body>", s"<body>$content</body>")

    writeFile(f.getName, html)

    (f.getName.substring(0, f.getName.length - 5), title, time)
  }).reverse

  val index = readFile("index.html").replaceAllLiterally("<li></li>", linksToPosts.map {
    case (slug, title, time) => s"""<li><time>$time</time><a href="/$slug">$title</a></li>"""
  }.mkString)

  writeFile("index.html", layout.replaceAllLiterally("<body></body>", s"<body>$index</body>"))

  println("\nBoom!\nBlog generated.\n")

  def readFile(file: String) = scala.io.Source.fromFile("./" + file).mkString
  def writeFile(file: String, content: String) = {
    val writer = new PrintWriter(new File("./web/" + file))
    writer.write(content)
    writer.close()
  }
}



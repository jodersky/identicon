import mill._, scalalib._, scalajslib._, scalanativelib._, publish._

trait Publish extends PublishModule {

  def publishVersion = T.input {
    os.proc("git", "describe", "--always", "--dirty=-SNAPSHOT", "--match=[0-9].*")
      .call().out.text.trim
  }

  def pomSettings = PomSettings(
    description = artifactName(),
    organization = "io.crashbox",
    url = "https://github.com/jodersky/identicon",
    licenses = Seq(License.`Apache-2.0`),
    versionControl = VersionControl.github("jodersky", "identicon"),
    developers = Seq(
      Developer("jodersky", "Jakob Odersky","https://github.com/jodersky")
    )
  )

}

object identicon extends Module {

  class JvmModule(val crossScalaVersion: String)
      extends CrossScalaModule
      with Publish {
    def millSourcePath = super.millSourcePath / os.up
    def sources = T.sources{
      super.sources() ++
      Seq(PathRef(millSourcePath / s"src-jvm"))
    }
    def artifactName = "identicon"
  }
  object jvm extends Cross[JvmModule]("2.13.4", "3.0.0-M2")

  class JsModule(val crossScalaVersion: String, val crossScalaJsVersion: String)
      extends CrossScalaModule
      with ScalaJSModule
      with Publish {
    def millSourcePath = super.millSourcePath / os.up / os.up
    def scalaJSVersion = crossScalaJsVersion
    def sources = T.sources{
      super.sources() ++
      Seq(PathRef(millSourcePath / s"src-js"))
    }
    def artifactName = "identicon"
  }
  object js extends Cross[JsModule](("2.13.4", "1.0.0"))

  class NativeModule(val crossScalaVersion: String, val crossScalaNativeVersion: String)
      extends CrossScalaModule
      with ScalaNativeModule
      with Publish {
    def millSourcePath = super.millSourcePath / os.up / os.up
    def scalaNativeVersion = crossScalaNativeVersion
    def sources = T.sources{
      super.sources() ++
      Seq(PathRef(millSourcePath / s"src-native"))
    }
    def artifactName = "identicon"
  }
  object native extends Cross[NativeModule](("2.11.12", "0.4.0-M2"))

}

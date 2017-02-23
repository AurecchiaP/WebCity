
// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/paolo/Documents/6th semester/thesis/webcity/conf/routes
// @DATE:Thu Feb 23 14:17:10 CET 2017


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}

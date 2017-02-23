
package views.html

import play.twirl.api._
import play.twirl.api.TemplateMagic._


     object main_Scope0 {
import models._
import controllers._
import play.api.i18n._
import views.html._
import play.api.templates.PlayMagic._
import java.lang._
import java.util._
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import play.core.j.PlayMagicForJava._
import play.mvc._
import play.data._
import play.api.data.Field
import play.mvc.Http.Context.Implicit._

class main extends BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with play.twirl.api.Template2[String,Html,play.twirl.api.HtmlFormat.Appendable] {

  /*
 * This template is called from the `index` template. This template
 * handles the rendering of the page header and body tags. It takes
 * two arguments, a `String` for the title of the page and an `Html`
 * object to insert into the body of the page.
 */
  def apply/*7.2*/(title: String)(content: Html):play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*7.32*/("""

"""),format.raw/*9.1*/("""<!DOCTYPE html>
<html lang="en">
    <head>
        """),format.raw/*12.62*/("""
        """),format.raw/*13.9*/("""<title>"""),_display_(/*13.17*/title),format.raw/*13.22*/("""</title>
        <link rel="stylesheet" media="screen" href=""""),_display_(/*14.54*/routes/*14.60*/.Assets.versioned("stylesheets/main.css")),format.raw/*14.101*/("""">
        <link rel="shortcut icon" type="image/png" href=""""),_display_(/*15.59*/routes/*15.65*/.Assets.versioned("images/favicon.png")),format.raw/*15.104*/("""">

    </head>
    <body>
        """),format.raw/*20.32*/("""
        """),_display_(/*21.10*/content),format.raw/*21.17*/("""

        """),format.raw/*23.9*/("""<script src=""""),_display_(/*23.23*/routes/*23.29*/.Assets.versioned("javascripts/three.min.js")),format.raw/*23.74*/("""" type="text/javascript"></script>
        <script src=""""),_display_(/*24.23*/routes/*24.29*/.Assets.versioned("javascripts/OrbitControls.js")),format.raw/*24.78*/("""" type="text/javascript"></script>
        <script src=""""),_display_(/*25.23*/routes/*25.29*/.Assets.versioned("javascripts/cube.js")),format.raw/*25.69*/("""" type="text/javascript"></script>
        <script src=""""),_display_(/*26.23*/routes/*26.29*/.Assets.versioned("javascripts/main.js")),format.raw/*26.69*/("""" type="text/javascript"></script>

    </body>
</html>
"""))
      }
    }
  }

  def render(title:String,content:Html): play.twirl.api.HtmlFormat.Appendable = apply(title)(content)

  def f:((String) => (Html) => play.twirl.api.HtmlFormat.Appendable) = (title) => (content) => apply(title)(content)

  def ref: this.type = this

}


}

/*
 * This template is called from the `index` template. This template
 * handles the rendering of the page header and body tags. It takes
 * two arguments, a `String` for the title of the page and an `Html`
 * object to insert into the body of the page.
 */
object main extends main_Scope0.main
              /*
                  -- GENERATED --
                  DATE: Thu Feb 23 16:40:42 CET 2017
                  SOURCE: /Users/paolo/Documents/6th semester/thesis/webcity/app/views/main.scala.html
                  HASH: c56fc3ac3f3f345f40cdd92292c8fff9de3d14a1
                  MATRIX: 1002->260|1127->290|1155->292|1235->397|1271->406|1306->414|1332->419|1421->481|1436->487|1499->528|1587->589|1602->595|1663->634|1726->759|1763->769|1791->776|1828->786|1869->800|1884->806|1950->851|2034->908|2049->914|2119->963|2203->1020|2218->1026|2279->1066|2363->1123|2378->1129|2439->1169
                  LINES: 32->7|37->7|39->9|42->12|43->13|43->13|43->13|44->14|44->14|44->14|45->15|45->15|45->15|49->20|50->21|50->21|52->23|52->23|52->23|52->23|53->24|53->24|53->24|54->25|54->25|54->25|55->26|55->26|55->26
                  -- GENERATED --
              */
          
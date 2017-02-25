
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

  /**/
  def apply/*1.2*/(title: String)(content: Html):play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*1.32*/("""

"""),format.raw/*3.1*/("""<!DOCTYPE html>
<html lang="en">
    <head>
        """),format.raw/*6.62*/("""
        """),format.raw/*7.9*/("""<title>"""),_display_(/*7.17*/title),format.raw/*7.22*/("""</title>
        <link rel="stylesheet" media="screen" href=""""),_display_(/*8.54*/routes/*8.60*/.Assets.versioned("stylesheets/main.css")),format.raw/*8.101*/("""">
        <link rel="shortcut icon" type="image/png" href=""""),_display_(/*9.59*/routes/*9.65*/.Assets.versioned("images/favicon.png")),format.raw/*9.104*/("""">

    </head>
    <body>
        """),format.raw/*14.32*/("""
        """),_display_(/*15.10*/content),format.raw/*15.17*/("""
        """),format.raw/*16.9*/("""<script type="text/javascript" src=""""),_display_(/*16.46*/routes/*16.52*/.HomeController.javascriptRoutes),format.raw/*16.84*/(""""></script>
        <script src=""""),_display_(/*17.23*/routes/*17.29*/.Assets.versioned("javascripts/jquery-3.1.1.min.js")),format.raw/*17.81*/("""" type="text/javascript"></script>
        <script src=""""),_display_(/*18.23*/routes/*18.29*/.Assets.versioned("javascripts/three.min.js")),format.raw/*18.74*/("""" type="text/javascript"></script>
        <script src=""""),_display_(/*19.23*/routes/*19.29*/.Assets.versioned("javascripts/OrbitControls.js")),format.raw/*19.78*/("""" type="text/javascript"></script>
        <script src=""""),_display_(/*20.23*/routes/*20.29*/.Assets.versioned("javascripts/draw.js")),format.raw/*20.69*/("""" type="text/javascript"></script>
        <script src=""""),_display_(/*21.23*/routes/*21.29*/.Assets.versioned("javascripts/main.js")),format.raw/*21.69*/("""" type="text/javascript"></script>
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

/**/
object main extends main_Scope0.main
              /*
                  -- GENERATED --
                  DATE: Sat Feb 25 10:46:46 CET 2017
                  SOURCE: /Users/paolo/Documents/6th semester/thesis/webcity/app/views/main.scala.html
                  HASH: 843c4d00108cfbcf0e48d1bd8aaafe440dfb0613
                  MATRIX: 748->1|873->31|901->33|980->138|1015->147|1049->155|1074->160|1162->222|1176->228|1238->269|1325->330|1339->336|1399->375|1462->500|1499->510|1527->517|1563->526|1627->563|1642->569|1695->601|1756->635|1771->641|1844->693|1928->750|1943->756|2009->801|2093->858|2108->864|2178->913|2262->970|2277->976|2338->1016|2422->1073|2437->1079|2498->1119
                  LINES: 27->1|32->1|34->3|37->6|38->7|38->7|38->7|39->8|39->8|39->8|40->9|40->9|40->9|44->14|45->15|45->15|46->16|46->16|46->16|46->16|47->17|47->17|47->17|48->18|48->18|48->18|49->19|49->19|49->19|50->20|50->20|50->20|51->21|51->21|51->21
                  -- GENERATED --
              */
          
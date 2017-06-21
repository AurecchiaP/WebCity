# WebCity

WebCity is a web application that, given some Java system hosted on GitHub, it produces a navigable and interactive 3D representation of the system.

It is built with Play Framework 2.5.12, and it requires Scala version 2.11, SBT 0.13.15, and Java 1.8 or higher.

The server can be downloaded and ran locally. Alternatively, an already deployed version can be found at http://rio.inf.usi.ch:39000

## IntelliJ simple setup

Download or clone the repository, import it as an SBT project, go under "Run > Edit Configurations..." and add
 a new default Play 2 App configuration. Run this configuration to start the local server.

## Use guide
Open the browser page (http://rio.inf.usi.ch:39000 or http://localhost:9000), input a public Java repository, such as https://github.com/AurecchiaP/webcity, wait for the download to be done and choose between commits or tags. When the visualization is ready, on the bottom right you can find a question mark icon that pops up a list of controls to navigate the visualization. On the header bar you can find a search bar, camera button for the video generation feature, a cog button to change the sizes of the visualization, and on the far right the list of versions.


[![Build Status](https://travis-ci.org/AurecchiaP/webcity.svg?branch=master)](https://travis-ci.org/AurecchiaP/webcity)

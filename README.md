# Piskvorky
Piskvorky is TicTacToe multiplayer game build with Kotlin Multiplatform.

Table of Contents
============
<!--ts-->
* [Technologies](#technologies)
* [Installation](#installation)
* [Project description](#project-description)
* [Screenshots](#screenshots)
* [Notes](#notes)
<!--te-->
Technologies
============
Project is using these technologies and libraries:
- [Kotlin Multiplatform](https://kotlinlang.org/docs/reference/multiplatform.html)
  \- for sharing code between server and client(s) modules
- [Ktor](https://github.com/ktorio/ktor)
  \- as http server
- [Serialization](https://github.com/Kotlin/kotlinx.serialization)
  \- for serialization of json api messages 
- [kotlin-react](https://github.com/JetBrains/kotlin-wrappers/tree/master/kotlin-react)
  \- Kotlin wrapper of ReactJS for building js client application 
- [Exposed](https://github.com/JetBrains/Exposed)
  \- an ORM framework for Kotlin
- [OpenApi](https://www.openapis.org/)
  \- for generating api modal classes, see [specification](domain/api/specs/piskvorky-v1.0.yaml)
- [Docker](https://www.docker.com/)
  \- for build and deploy, see [installation](#installation)


Installation
============

Project description
============

Screenshots
============
Game
-----
![](doc/img/game.png)

Lobby
-----
![](doc/img/lobby.png)

Login page
-----
![](doc/img/login.png)

Admin
-----
![](doc/img/admin.png)

Notes
============
Repository used Alpha features such as Kotlin Multiplatform and some problems with that may occur.

There is list of known issues:
- Netty exception on every request: https://youtrack.jetbrains.com/issue/KTOR-646
- Ktor's websocket auto ping/pong seems not working

KIV/PIA
-----
This repository is semestral work of KIV/PIA.

Please see [this doc](doc/DOC.md).

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
- [Koin](https://github.com/InsertKoinIO/koin)
  \- for dependency injection, using alpha version with multiplatform support
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
**1. Clone this repository**
```
git clone https://github.com/MFori/Piskvorky.git
```
**2. Build using gradle wrapper**
```
gradlew build
```
**3. Setup database**

Create mysql db and pass connection params as environmental variables as below.

**4. Run server**
```
set SERVER_DB_ADDRESS=localhost
set SERVER_DB_USER=root
set SERVER_DB_PASSWORD=password
set SERVER_DB_PORT=3306
set SERVER_DB_NAME=piskvorky_db
java -jar server/build/libs/piskvorky-server.jar
```
Server will listen at ```http://localhost:9090```, admin is located at ```http://localhost:9090/admin```

**5. Start client**

Copy content of ```web/distributions``` to some web server as nginx (see nginx [configuration](web/nginx.conf)) or run kotlin-browser gradle task as ```browserProductionRun``` 

**6. Play**

Create new account or use predefined admin account:
```
email: admin@admin.com
pass: test123
```

### One-Command Build&Deploy
You can use prepared files [run.bat](run.bat)/[run.sh](run.sh) to build project using gradle wrapper and deploy to docker
(Docker must  be installed.) with this commands:
```
gradlew build 
docker-compose build
docker-compose up
```
Server will be listening at ```http://localhost:9090``` client app will be accessible at ```http://localhost:80```.

Project description
============
Project is using gradle and is split into these modules:
- **domain**
  \- contains domain objects and other shared code between server and client(s) modules
- **server**
  \- http server using [Ktor](https://github.com/ktorio/ktor)
- **web**
  \- web client app in kotlin/js and ReactJS [wrapper](https://github.com/JetBrains/kotlin-wrappers/tree/master/kotlin-react)

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

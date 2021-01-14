# Piskvorky - Semestrální práce KIV/PIA 2020/2021
Autor: Martin Forejt, A20N0079P, mforejt@students.zcu.cz

Obsah
============
<!--ts-->
* [Zadání](#zadání)
* [Technologie](#technologie)
* [Instalace](#instalace)
* [Použití](#použití)
* [Architektura](#architektura)
* [Testování](#testování)
* [Možná vylepšení](#možná-vylepšení)
* [Známé problémy](#známé-problémy)
* [Bonusové části](#bonusové-části)
<!--te-->

Zadání
============
viz https://github.com/osvetlik/pia2020/tree/master/semester-project

Technologie
============
Aplikace je z 95% napsaná v Kotlinu a jsou mimo jiné použity tyto technologie a knihovny:
- [Kotlin Multiplatform](https://kotlinlang.org/docs/reference/multiplatform.html)
  \- pro možnost sdílení kódu mezi serverem napsaným v kotlin/jvm a klientem v kotlin/js
- [Ktor](https://github.com/ktorio/ktor)
  \- framework pro tvorbu http serveru a internetových aplikací
- [Serialization](https://github.com/Kotlin/kotlinx.serialization)
  \- knihovna pro serializaci zpráv v json api
- [Koin](https://github.com/InsertKoinIO/koin)
  \- dependency injection framework pro kotlin (je použita alpha verze s podporou pro kotlin/multiplatform)
- [kotlin-react](https://github.com/JetBrains/kotlin-wrappers/tree/master/kotlin-react)
  \- knihovna pro kotlin/js obalující frontend framework ReactJS
- [Exposed](https://github.com/JetBrains/Exposed)
  \- ORM framework pro Kotlin
- [OpenApi](https://www.openapis.org/)
  \- pro generování api kódu ze [specifikace](domain/api/specs/piskvorky-v1.0.yaml)
- [SASS](https://sass-lang.com/)
  \- pro psaní a generování css kódu
- Gradle - pro automatizaci sestavení 
- MySQL

Instalace
============
K dispozici jsou soubory [run.bat](../run.bat) a [run.sh](../run.sh), který přeloží projekt pomocí gradlu
a spustí v dockeru za pomoci těchto příkazů:
```
gradlew build 
docker-compose build
docker-compose up
```
Server potom bude poslouchat na ```http://localhost:9090``` 

Klient bude dostupný na ```http://localhost:80```


Použití
============

Architektura
============

Testování
============
Z časových důvodů byla aplikace testována pouze manuálně.

Možná vylepšení
============

Známé problémy
============
- Netty exception on every request: https://youtrack.jetbrains.com/issue/KTOR-646
  - Netty vyhazuje výjimku při každém requestu viz odkaz výše, jinak to nemá žádný vliv
- Ktor's websocket auto ping/pong seems not working
  - Ktor poskytuje automatickou synchronizaci serveru a klienta přes websocket, tzv. ping/pong, tedy posílání zpráv pro ověření stálosti připojení.
  Toto se mi bohužel nepodařilo zprovoznit.
- Spousta warningů při překladu webu a generování javascriptu. Je to způsobeno závislostmi v obalujících kotlin knihovnách. Jinak to nemá žádný vliv.    
- Projekt využívá několik knihoven ve verzi alpha, mohou se tedy vyskytnout další problémy

Bonusové části
============
V aplikaci jsou implementovány ty bonusové části:
- an unlimited board - **3 points**
- password reset using an e-mail (reset link) - **5 points**
    - je potřeba nastavit object ```email``` v soubor [application.conf](../server/src/main/resources/application.conf)
- in-game chat - **5 points**
- HTML canvas for the gameplay - **2 points**
- [OpenApi](https://swagger.io/specification/) modeling/specification language with code generation - **10 points**
    - specifikace je v souboru [piskvorky-v1.0.yaml](../domain/api/specs/piskvorky-v1.0.yaml)
- React frontend technology - **10 points**
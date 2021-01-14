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

Instalace
============

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
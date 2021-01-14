# Piskvorky - Semestrální práce KIV/PIA 2020/2021
Autor: Martin Forejt, A20N0079P, mforejt@students.zcu.cz

Obsah
============
<!--ts-->
* [Zadání](#zadn)
* [Technologie](#technologie)
* [Instalace](#instalace)
* [Architektura](#architektura)
* [Testování](#testovn)
* [Známé problémy](#znm-problmy)
* [Bonusové části](#bonusov-sti)
<!--te-->

Zadání
============
viz https://github.com/osvetlik/pia2020/tree/master/semester-project

Technologie
============

Instalace
============

Architektura
============

Testování
============

Známé problémy
============

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
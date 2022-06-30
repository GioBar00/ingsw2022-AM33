[not-implemented]: https://img.shields.io/badge/Status-not_implemented-red.svg?style=flat
[implementing]: https://img.shields.io/badge/Status-implementing-yellow.svg?style=flat
[testing]: https://img.shields.io/badge/Status-testing-blue.svg?style=flat
[done]: https://img.shields.io/badge/Status-done-green.svg?style=flat

[game-site]: https://www.craniocreations.it/prodotto/eriantys/
[game-rules]: https://www.craniocreations.it/wp-content/uploads/2021/11/Eriantys_ITA_bassa.pdf
[project-requirements]: requirements.pdf

# Final examination of Software Engineering 2021-22

[![Build](https://github.com/GioBar00/ingsw2022-AM33/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/GioBar00/ingsw2022-AM33/actions/workflows/maven.yml)
[![](https://img.shields.io/badge/JavaDoc--blue.svg?style=flat&logo=openjdk)](https://giobar00.github.io/ingsw2022-AM33/)

## Eriantys

<img src="https://www.craniocreations.it/wp-content/uploads/2021/06/Eriantys_scatola3Dombra.png" width="256px" height="256px" alt="Eriantys box" align="right"/>

The project consists in developing a software version of the board
game [Eriantys][game-site].</br>
The project includes:

- initial UML diagrams;
- final UML diagrams, automatically generated from the source code;
- implementation of the game;
- documentation of the communication protocol;
- documents of the peer review;
- source code of the game;
- source code of unit tests.

The implementation must comply with
the [rules][game-rules] of the game.

## Functionalities

| Functionality          |                        Status                         |
|:-----------------------|:-----------------------------------------------------:|
| Simplified rules       |           [![][done]][project-requirements]           |
| Complete rules         |           [![][done]][project-requirements]           |
| Socket                 |  [![][done]](src/main/java/it/polimi/ingsw/network)   |
| CLI                    | [![][done]](src/main/java/it/polimi/ingsw/client/cli) |
| GUI                    | [![][done]](src/main/java/it/polimi/ingsw/client/gui) |
| All character cards    |                [![][done]][game-rules]                |
| 4 players              |                [![][done]][game-rules]                |
| Multiple Games         |     [![][not-implemented]][project-requirements]      |
| Persistence            |     [![][not-implemented]][project-requirements]      |
| Disconnections control |           [![][done]][project-requirements]           |

| Legend               |
|:---------------------|
| ![][not-implemented] |
| ![][implementing]    |
| ![][testing]         |
| ![][done]            |

## Code coverage
All classes for model, lobby, controller and messages were tested.

| Package       |  Line Coverage  |
|:--------------|:---------------:|
| **_ingsw_**   | 91% (2455/2671) |
| **_server_**  | 91% (1949/2127) |
| controller    |  92% (184/199)  |
| lobby         |  98% (119/121)  |
| model         | 92% (1489/1610) |
| **_network_** |  93% (506/544)  |
| messages      |  96% (483/503)  |

## Building
### Dependencies
* JDK 17
* Maven 3

### Run Maven
`mvn clean install`

This will build the project and install all dependencies creating a jar file inside in the `target` directory.

## Start Eriantys
After building the project, you can start the game in 3 different modes by appending the following arguments to the command:

`java -jar AM33-1.0-SNAPSHOT.jar`

- `-s`: starts the game in **Server** mode
- `-c`: starts the game in **CLI** mode
- ` ` or `-g`: starts the game in **GUI** mode

### Server mode
When starting the game in **Server** mode, you can also specify the port number to use by appending the following argument to the command:
- `-p <port>`: the port number to use


## Team

- [Andrea Alari](https://github.com/andrea-alari)
- [Giovanni Barbiero](https://github.com/GioBar00)
- [Chiara Bordegari](https://github.com/Chiara-Bordegari)

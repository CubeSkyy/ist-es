# Adventure Builder [![Build Status](https://travis-ci.com/tecnico-softeng/es19tg_13-project.svg?token=18mQisuv59o2ZBZknWxY&branch=develop)](https://travis-ci.com/tecnico-softeng/es19tg_13-project)[![codecov](https://codecov.io/gh/tecnico-softeng/es19tg_13-project/branch/develop/graph/badge.svg?token=3UtdufKikD)](https://codecov.io/gh/tecnico-softeng/es19tg_13-project)


To run tests execute: mvn clean install

To see the coverage reports, go to <module name>/target/site/jacoco/index.html.

### Rastreabilidade do trabalho

Ordene a tabela por ordem crescente da data de término.

|   124      |   ist187635             |    BSantosCoding        |   [162](https://github.com/tecnico-softeng/es19tg_13-project/pull/162)                  |    04/20/2019                |
|   123      |   ist187635             |     BSantosCoding       |   [137](https://github.com/tecnico-softeng/es19tg_13-project/pull/137)                  |    04/19/2019                |
|   121      |   ist187635             |    BSantosCoding        |   [163](https://github.com/tecnico-softeng/es19tg_13-project/pull/163)                 |     04/20/2019               |
|   120      |   ist187635             |    BSantosCoding        |   [139](https://github.com/tecnico-softeng/es19tg_13-project/pull/139)                  |     04/19/2019               |
|   147      |   ist187700             |    genlike              |   [151](https://github.com/tecnico-softeng/es19tg_13-project/pull/151)                  |    04/21/2019                |
|   152      |   ist187700             |    genlike              |   [157](https://github.com/tecnico-softeng/es19tg_13-project/pull/157)                  |    04/21/2019                |
|   158      |   ist187700             |    genlike              |   [159](https://github.com/tecnico-softeng/es19tg_13-project/pull/159)                  |    04/21/2019                |
|   125      |   ist187687             |    CubeSkyy              |   [161](https://github.com/tecnico-softeng/es19tg_13-project/pull/161)                  |    04/21/2019                |
|   128      |   ist187631             |    Andrempp              |   [133](https://github.com/tecnico-softeng/es19tg_13-project/pull/133)                  |    04/21/2019                |
|   127      |   ist187631             |    Andrempp              |   [132](https://github.com/tecnico-softeng/es19tg_13-project/pull/132)                  |    04/21/2019                |
| 146        | ist187633          | esteveste               | [154](https://github.com/tecnico-softeng/es19tg_13-project/pull/154) | 04/21/2019 |
| 145        | ist187633          | esteveste               | [156](https://github.com/tecnico-softeng/es19tg_13-project/pull/156) | 04/21/2019 |
| 134        | ist187633          | esteveste               | [165](https://github.com/tecnico-softeng/es19tg_13-project/pull/165), [153](https://github.com/tecnico-softeng/es19tg_13-project/pull/153) | 04/22/2019 |
|   130      |   ist187631             |    Andrempp              |   [164](https://github.com/tecnico-softeng/es19tg_13-project/pull/164)                  |    04/22/2019                |





### Infrastructure

This project includes the persistent layer, as offered by the FénixFramework.
This part of the project requires to create databases in mysql as defined in `resources/fenix-framework.properties` of each module.

See the lab about the FénixFramework for further details.

#### Docker (Alternative to installing Mysql in your machine)

To use a containerized version of mysql, follow these stesp:

```
docker-compose -f local.dev.yml up -d
docker exec -it mysql sh
```

Once logged into the container, enter the mysql interactive console

```
mysql --password
```

And create the 6 databases for the project as specified in
the `resources/fenix-framework.properties`.

To launch a server execute in the module's top directory: mvn clean spring-boot:run

To launch all servers execute in bin directory: startservers

To stop all servers execute: bin/shutdownservers

To run jmeter (nogui) execute in project's top directory: mvn -Pjmeter verify. Results are in target/jmeter/results/, open the .jtl file in jmeter, by associating the appropriate listeners to WorkBench and opening the results file in listener context

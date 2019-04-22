# Adventure Builder [![Build Status](https://travis-ci.com/tecnico-softeng/es19tg_13-project.svg?token=18mQisuv59o2ZBZknWxY&branch=develop)](https://travis-ci.com/tecnico-softeng/es19tg_13-project)[![codecov](https://codecov.io/gh/tecnico-softeng/es19tg_13-project/branch/develop/graph/badge.svg?token=3UtdufKikD)](https://codecov.io/gh/tecnico-softeng/es19tg_13-project)


To run tests execute: mvn clean install

To see the coverage reports, go to <module name>/target/site/jacoco/index.html.

### Rastreabilidade do trabalho

Ordene a tabela por ordem crescente da data de término.

|   Issue id | Owner (ist number)      | Owner (github username) | PRs id (with link)  |            Date    |
| ---------- | ----------------------- | ----------------------- | ------------------- | ------------------ |
|   124         |   ist187635                      |    BSantosCoding                     |   [162](https://github.com/tecnico-softeng/es19tg_13-project/pull/162)                  |    04/20/2019                |
|   123         |   ist187635                      |     BSantosCoding                    |   [137](https://github.com/tecnico-softeng/es19tg_13-project/pull/137)                  |    04/19/2019                |
|   121         |   ist187635                      |    BSantosCoding                     |   [163](https://github.com/tecnico-softeng/es19tg_13-project/pull/163)                 |     04/20/2019               |
|   120         |   ist187635                      |    BSantosCoding                     |   [139](https://github.com/tecnico-softeng/es19tg_13-project/pull/139)                  |     04/19/2019               |
|            |                         |                         |                     |                    |
|            |                         |                         |                     |                    |
|            |                         |                         |                     |                    |


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

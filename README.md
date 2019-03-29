# Adventure Builder [![Build Status](https://travis-ci.com/tecnico-softeng/es19tg_13-project.svg?token=18mQisuv59o2ZBZknWxY&branch=develop)](https://travis-ci.com/tecnico-softeng/es19tg_13-project)[![codecov](https://codecov.io/gh/tecnico-softeng/es19tg_13-project/branch/master/graph/badge.svg?token=3UtdufKikD)](https://codecov.io/gh/tecnico-softeng/es19tg_13-project)



To run tests execute: mvn clean install

To see the coverage reports, go to <module name>/target/site/jacoco/index.html.


| Number    | Name             | Email                                          | GitHub Username | Group |
| --------- | ---------------- | ---------------------------------------------- | --------------- | ----- |
| ist187631 | Andre Patricio   | andrepatricio98@tecnico.ulisboa.pt             | Andrempp        | 1     |
| ist187633 | Bernardo Esteves | bernardo.esteves@tecnico.ulisboa.pt            | esteveste       | 1     |
| ist187635 | Bernardo Santos  | bernardo.d.santos@tecnico.ulisboa.pt           | BSantosCoding   | 1     |
| ist187687 | Miguel Coelho    | miguelmendescoelho@tecnico.ulisboa.pt          | CubeSkyy        | 2     |
| ist187636 | Bernardo Faria   | bernardo.faria@tecnico.ulisboa.pt              | BernardoFaria   | 2     |
| ist187700 | Ricardo Silva    | ricardo.f.oliveira.da.silva@tecnico.ulisboa.pt | genlike         | 2     |



**Nome:** Bernardo Carreira dos Santos **Número:** ist187635 **Email:** bernardo.d.santos@tecnico.ulisboa.pt **Github Username:** BSantosCoding

| Class/Task                                        | Package                 | Issue |
| ------------------------------------------------- | ----------------------- | ----- |
| ActivityOfferHasVacancyMethodSpockTest            | activity.domain         | #61   |
| InvoiceProcessorSubmitBookingMethodSpockTest      | activity.domain         | #62   |
| ActivityInterfaceCancelReservationMethodSpockTest | activity.services.local | #60   |

**Nome:** Miguel Coelho **Numero:** ist187687 **Email:** miguelmendescoelho@tecnico.ulisboa.pt  **Github Username:** CubeSkyy

| Class/Task                                   | Package             | Issue |
| -------------------------------------------- | ------------------- | ----- |
| RentVehicleStateMethodSpockTest              | broker.domain       | #63   |
| ConfirmedStateProcessMethodSpockTest         | bank.domain         | #65   |
| Changed Static Method to non Static          | broker              | #70/#90 |
| Changed Static Method to non Static          | hotel              | #92 |

**Nome:** Bernardo Esteves **Número:** ist187633 **Email:** bernardo.esteves@tecnico.ulisboa.pt **Github Username:** esteveste

| Class                                           | Package                 | Issue |
| ----------------------------------------------- | ----------------------- | ----- |
| ActivityInterfaceReserveActivityMethodSpockTest | activity.services.local | #72   |
| CancelledStateProcessMethodSpockTest            | broker.domain           | #64   |
| ReserveActivityStateProcessMethodSpockTest      | broker.domain           | #74   |
| UndoStateProcessMethodSpockTest                 | broker.domain           | #75   |

**Nome:** Ricardo Silva **Número:** ist187700 **Email:** ricardofsilva@live.com **Github Username:** genlike

| Class/Task                                        | Package                 | Issue |
| ------------------------------------------------- | ----------------------- | ----- |
| BookRoomState	(added functionality )				| broker.domain			  | #94   |
| BulkRoomBookingGetRoomBookingData4TypeMethodTest	| broker.domain			  | #104  |
| BookRoomStateMethodTest (added functionality )	| broker.domain			  | #115  |

**Nome:** André Patrício **Número:** ist187631 **Email:** andrepatricio98@tecnico.ulisboa.pt **Github Username:** Andrempp

| Class                                           | Package                 | Issue |
| ----------------------------------------------- | ----------------------- | ----- |
| TaxPaymentStateMethodSpockTest 				  | broker.domain           | #99   |
| BulkRoomBookingProcessBookingMethodSpockTest    | broker.domain           | #79   |
| ProcessPaymentStateProcessMethodSpockTest       | broker.domain           | #77   |
| BookRoomStateMethodSpockTest	                  | broker.domain           | #78   |


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


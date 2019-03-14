# Adventure Builder [![Build Status](https://travis-ci.com/tecnico-softeng/prototype-2018.svg?token=fJ1UzWxWjpuNcHWPhqjT&branch=master)](https://travis-ci.com/tecnico-softeng/prototype-2018) [![codecov](https://codecov.io/gh/tecnico-softeng/prototype-2018/branch/master/graph/badge.svg?token=OPjXGqoNEm)](https://codecov.io/gh/tecnico-softeng/prototype-2018)


To run tests execute: mvn clean install

To see the coverage reports, go to <module name>/target/site/jacoco/index.html.


|   Number   |          Name           |            Email        |   GitHub Username  | Group |
| ---------- | ----------------------- | ----------------------- | -------------------| ----- |
|ist187631   |Andre Patricio           |andrepatricio98@tecnico.ulisboa.pt            |Andrempp                    |   1   |
|ist187633 |  Bernardo Esteves           | bernardo.esteves@tecnico.ulisboa.pt        | esteveste                   |   1   |
| ist187635  | Bernardo Santos         | bernardo.d.santos@tecnico.ulisboa.pt         | BSantosCoding                   |   1   |
| ist187687  |  Miguel Coelho          |   miguelmendescoelho@tecnico.ulisboa.pt      |  CubeSkyy                  |   2   |
|ist187636   |Bernardo Faria           |bernardo.faria@tecnico.ulisboa.pt             |BernardoFaria                    |   2   |
|ist187700   |Ricardo Silva            |ricardo.f.oliveira.da.silva@tecnico.ulisboa.pt|genlike|2|


**Nome:** Miguel Coelho **Numero:** ist187687 **Email:** miguelmendescoelho@tecnico.ulisboa.pt  **Github Username:** CubeSkyy

|   Class                                      |          Package      | Issue |
| -------------------------------------------- | --------------------- | ----- |
| AccountContructorMethodSpockTest             |bank.domain            | #13   |
| AccountDepositMethodSpockTest                |bank.domain            | #14   |
| AccountWithdrawMethodSpockTest               |bank.domain            | #15   |
| BankConstructorSpockTest                     |bank.domain            | #16   |
| BankGetAccountMethodSpockTest                |bank.domain            | #17   |
| RollbackSpockTestAbstractClass               |bank.domain            | #22   |
| BankInterfaceCancelPaymentSpockTest          |bank.services.local    | #19   |
| BankInterfaceGetOperationDataMethodSpockTest |bank.services.local    | #20   |
| BankInterfaceProcessPaymentMethodSpockTest   |bank.services.local    | #21   |

**Nome:** Bernardo Santos **Numero:** ist187635 **Email:** bernardo.d.santos@tecnico.ulisboa.pt  **Github Username:** BSantosCoding

|   Class                                                    |          Package      | Issue |
| ---------------------------------------------------------- | --------------------- | ----- |
| ActivityContructorMethodSpockTest                          |activity.domain        | #1    |
| ActivityMatchAgeMethodSpockTest                            |activity.domain        | #3    |
| ActivityOfferConstructorMethodSpockTest                    |activity.domain        | #4    |
| ActivityOfferGetBookingMethodSpockTest                     |activity.domain        | #5    |
| ActivityOfferMatchDateMethodSpockTest                      |activity.domain        | #6    |
| RollbackSpockTestAbstractClass                             |activity.domain        | #2    |
| BookingContructorMethodSpockTest                           |activity.domain        | #12   |
| ActivityProviderConstructorSpockMethodTest                 |activity.domain        | #10   |
| ActivityProviderFindOfferMethodSpockTest                   |activity.domain        | #11   |
| ActivityInterfaceGetActivityReservationDataMethodSpockTest |activity.services.local| #8    |

**Nome:** André Patrício **Numero:** ist187631 **Email:** andrepatricio98@tecnico.ulisboa.pt  **Github Username:** Andrempp

|   Class                                                    |          Package      | Issue |
| ---------------------------------------------------------- | --------------------- | ----- |
| TaxPayerGetTaxesPerYearMethodsSpockTest                    |tax.domain        	 | #57   |
| TaxPayerGetInvoiceByReferenceSpockTest                     |tax.domain        	 | #55   |
| ActivityPersistenceSpockTest                  	 		 |activity.domain        | #9    |
| OperationConstructorMethodSpockTest                        |bank.domain        	 | #45   |
| OperationRevertMethodSpockTest                             |bank.domain            | #46   |
| BankPersistenceSpockTest                                   |bank.domain            | #23   |
| ClientConstructorMethodTestSpock                           |bank.domain            | #44   |
| TaxInterfaceSubmitInvoiceSpockTest                  		 |tax.domain             | #56   |

**Nome:** Bernardo Faria **Numero:** ist187636 **Email:** bernardo.faria@tecnico.ulisboa.pt  **Github Username:** BernardoFaria

|   Class                                                    |          Package      | Issue |
| ---------------------------------------------------------- | --------------------- | ----- |
| BookingConflictMethodSpockTest                             |hotel.domain        	 | #29   |
| BookingConstructorSpockTest                                |hotel.domain        	 | #30   |
| HotelConstructorSpockTeste                     	 		 |hotel.domain           | #31   |
| HotelGetPriceMethodSpockTest                               |hotel.domain        	 | #32   |
| HotelHasVacancyMethodSpockTest                             |hotel.domain           | #33   |
| HotelPersistenceSpockTest                                  |hotel.domain           | #34   |
| HotelSetPriceMethodSpockTest                               |hotel.domain           | #35   |
| HotelInterfaceGetRoomBookingDataMethodTest                 |hotel.domain           | #58   |

**Nome:** Bernardo Esteves **Numero:** ist187633 **Email:** bernardo.esteves@tecnico.ulisboa.pt  **Github Username:** esteveste

|   Class                                                    |          Package      | Issue |
| ---------------------------------------------------------- | --------------------- | ----- |
|    SpockPersistenceTestAbstractClass                 	 		 |tax.domain           | #28   |
|   SpockRollbackTestAbstractClass                              |tax.domain        	 | #27   |
|   TaxPersistenceSpockTest                             |tax.domain        	 | #26   |
|     BuyerConstructorSpockTest                        |tax.domain           | #25   |
|       IRSCancelInvoiceMethodSpockTest                           |tax.domain.services.local           | #24   |
|     SellerToPaySpockTest                          |tax.domain           | #48   |
|   SellerConstructorSpockTest              |tax.domain           | #49   |
|   ItemTypeConstructorSpockTest              |tax.domain           | #50   |
|   IRSGetTaxPayerByNIFSpockTest              |tax.domain           | #51   |
|   InvoiceConstructorSpockTest               |tax.domain           | #52   |
|  BuyerToReturnSpockTest                            |tax.domain        	 | #54   |

- **Group 1:**
- **Group 2:**

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

package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRentingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.CarException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException

class RentVehicleStateMethodSpockTest extends SpockRollbackTestAbstractClass {

    def broker
    def client
    def adventure
    def rentingData
    def taxInterface
    def carInterface

    @Override
    def populate4Test() {
        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
        client = new Client(this.broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
        adventure = new Adventure(this.broker, BEGIN, END, this.client, MARGIN)

        rentingData = new RestRentingData()
        rentingData.setReference(RENTING_CONFIRMATION)
        rentingData.setPrice(76.78)

        taxInterface = Mock(TaxInterface)
        carInterface = Mock(CarInterface)

        adventure.setState(Adventure.State.RENT_VEHICLE)
        adventure.setTaxInterface(taxInterface)
        adventure.setCarInterface(carInterface)

    }


    def 'successRentVehicle'() {
        when: 'processing an adventure where renting succeeded'
        adventure.process()

        then: 'renting was called once and adventure is now in the "Process Payment" stage'
        1 * carInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN,
                BEGIN, END, _ as String) >> rentingData

        adventure.getState().getValue() == Adventure.State.PROCESS_PAYMENT

    }

    def 'carException'() {
        when: 'processing an adventure where renting failed due to car Exception'
        adventure.process()

        then: 'renting was called once and the adventure is now in the "Undo" stage'
        1 * carInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN,
                BEGIN, END, _ as String) >> { throw new CarException() }
        adventure.getState().getValue() == Adventure.State.UNDO
    }


    def 'singleRemoteAccessException'() {

        when: 'processing an adventure where renting failed due to a remote Exception'
        adventure.process()

        then: 'renting was called once and the adventure is now in the "Rent Vehicle" stage'
        1 * carInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN,
                BEGIN, END, _ as String) >> { throw new RemoteAccessException() }
        adventure.getState().getValue() == Adventure.State.RENT_VEHICLE
    }


    def 'maxRemoteAccessException'() {
        when: 'processing as many adventures as the maximum remote errors while throwing the remote exception'

        for (int i = 0; i < RentVehicleState.MAX_REMOTE_ERRORS; i++) {
            adventure.process()
        }
        then: 'renting was called once per cycle and the adventure is now in the "Undo" stage'
        RentVehicleState.MAX_REMOTE_ERRORS * carInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN,
                BEGIN, END, _ as String) >> { throw new RemoteAccessException() }
        adventure.getState().getValue() == Adventure.State.UNDO
    }

    def 'maxMinusOneRemoteAccessException'() {
        when: 'processing as many adventures as the maximum remote errors minus one while throwing the remote exception'

        for (int i = 0; i < RentVehicleState.MAX_REMOTE_ERRORS - 1; i++) {
            adventure.process()
        }

        then: 'renting was called once per cycle and the adventure is now in the "Rent Vehicle" stage'
        (RentVehicleState.MAX_REMOTE_ERRORS - 1) * carInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN,
                BEGIN, END, _ as String) >> { throw new RemoteAccessException() }
        adventure.getState().getValue() == Adventure.State.RENT_VEHICLE
    }

    def 'twoRemoteAccessExceptionOneSuccess'() {
        given:
        def i = 0

        when: 'processing 3 adventures where the first two throw an exception and the third succeeds'
        adventure.process()
        adventure.process()
        adventure.process()

        then: 'renting was called once per cycle and the adventure is now in the "Process Payment" stage'
        3 * carInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN,
                BEGIN, END, _ as String) >>
                {
                    if (i < 2) {
                        i++
                        throw new RemoteAccessException()
                    } else {
                        return rentingData
                    }
                }
        adventure.getState().getValue() == Adventure.State.PROCESS_PAYMENT
    }


    def 'oneRemoteAccessExceptionOneCarException'() {
        given:
        def i = 0

        when: 'processing 2 adventures where the first throws a Remote exception and the second a Car exception'
        adventure.process()
        adventure.process()

        then: 'renting was called once per cycle and the adventure is now in the "Undo" stage'
        2 * carInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN,
                BEGIN, END, _ as String) >>
                {
                    if (i < 1) {
                        i++
                        throw new RemoteAccessException()
                    } else {
                        throw new CarException()
                    }
                }

        adventure.getState().getValue() == Adventure.State.UNDO

    }

}
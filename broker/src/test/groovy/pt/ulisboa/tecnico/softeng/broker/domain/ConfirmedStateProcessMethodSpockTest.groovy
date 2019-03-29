package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestActivityBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestBankOperationData
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRentingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.ActivityException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.BankException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.CarException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException

class ConfirmedStateProcessMethodSpockTest extends SpockRollbackTestAbstractClass {

    def broker
    def client
    def adventure
    def activityReservationData
    def rentingData
    def roomBookingData
    def roomInterface
    def taxInterface
    def activityInterface
    def carInterface
    def bankInterface


    @Override
    def populate4Test() {
        activityReservationData = Mock(RestActivityBookingData)
        rentingData = Mock(RestRentingData)
        roomBookingData = Mock(RestRoomBookingData)
        roomInterface = Mock(HotelInterface)
        taxInterface = Mock(TaxInterface)
        activityInterface = Mock(ActivityInterface)
        carInterface = Mock(CarInterface)
        bankInterface = Mock(BankInterface)


        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN, roomInterface,
                taxInterface, activityInterface, carInterface, bankInterface)
        client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
        adventure = new Adventure(broker, BEGIN, END, client, MARGIN)

        adventure.setState(Adventure.State.CONFIRMED)
    }


    def 'successAll'() {
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)

        when:
        adventure.process()

        then:

        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> new RestBankOperationData()
        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData
        carInterface.getRentingData(RENTING_CONFIRMATION) >> rentingData
        roomInterface.getRoomBookingData(ROOM_CONFIRMATION) >> roomBookingData

        activityReservationData.getPaymentReference() >> REFERENCE
        activityReservationData.getInvoiceReference() >> REFERENCE
        rentingData.getPaymentReference() >> REFERENCE
        rentingData.getInvoiceReference() >> REFERENCE
        roomBookingData.getPaymentReference() >> REFERENCE
        roomBookingData.getInvoiceReference() >> REFERENCE


        adventure.getState().getValue() == Adventure.State.CONFIRMED
    }


    def 'successActivityAndHotel'() {
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)

        when:
        adventure.process()

        then:
        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> new RestBankOperationData()
        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData
        roomInterface.getRoomBookingData(ROOM_CONFIRMATION) >> roomBookingData

        activityReservationData.getPaymentReference() >> REFERENCE
        activityReservationData.getInvoiceReference() >> REFERENCE
        roomBookingData.getPaymentReference() >> REFERENCE
        roomBookingData.getInvoiceReference() >> REFERENCE


        adventure.getState().getValue() == Adventure.State.CONFIRMED
    }


    def 'successActivityAndCar'() {
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)

        when:
        adventure.process()

        then:

        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> new RestBankOperationData()
        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData
        carInterface.getRentingData(RENTING_CONFIRMATION) >> rentingData

        activityReservationData.getPaymentReference() >> REFERENCE
        activityReservationData.getInvoiceReference() >> REFERENCE
        rentingData.getPaymentReference() >> REFERENCE
        rentingData.getInvoiceReference() >> REFERENCE

        adventure.getState().getValue() == Adventure.State.CONFIRMED
    }

    def 'successActivity'() {
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)

        when:
        adventure.process()

        then:
        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> new RestBankOperationData()
        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData

        activityReservationData.getPaymentReference() >> REFERENCE
        activityReservationData.getInvoiceReference() >> REFERENCE

        adventure.getState().getValue() == Adventure.State.CONFIRMED
    }

    def 'oneBankException'() {
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)

        when:
        adventure.process()

        then:
        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> { throw new BankException() }
        adventure.getState().getValue() == Adventure.State.CONFIRMED
    }


    def 'maxBankException'() {
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)

        when:
        for (int i = 0; i < ConfirmedState.MAX_BANK_EXCEPTIONS; i++) {
            adventure.process()
        }

        then:
        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> { throw new BankException() }
        adventure.getState().getValue() == Adventure.State.UNDO
    }


    def 'maxMinusOneBankException'() {
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)

        when:
        for (int i = 0; i < ConfirmedState.MAX_BANK_EXCEPTIONS - 1; i++) {
            adventure.process()
        }

        then:
        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> { throw new BankException() }
        adventure.getState().getValue() == Adventure.State.CONFIRMED
    }

    def 'oneRemoteAccessExceptionInPayment'() {
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)

        when:
        adventure.process()

        then:
        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> { throw new RemoteAccessException() }
        adventure.getState().getValue() == Adventure.State.CONFIRMED
    }

    def 'activityException'() {
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)

        when:
        adventure.process()

        then:
        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> new RestBankOperationData()
        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> { throw new ActivityException() }
        adventure.getState().getValue() == Adventure.State.UNDO
    }

    def 'oneRemoteAccessExceptionInActivity'() {
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)

        when:
        adventure.process()

        then:
        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> new RestBankOperationData()
        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> { throw new RemoteAccessException() }

        adventure.getState().getValue() == Adventure.State.CONFIRMED
    }

    def 'activityNoPaymentConfirmation'() {
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)

        when:
        adventure.process()

        then:
        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> new RestBankOperationData()
        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData
        activityReservationData.getPaymentReference() >> null

        adventure.getState().getValue() == Adventure.State.UNDO
    }


    def 'activityNoInvoiceReference'() {
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)

        when:
        adventure.process()

        then:
        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> new RestBankOperationData()
        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData
        activityReservationData.getPaymentReference() >> REFERENCE
        activityReservationData.getInvoiceReference() >> null

        adventure.getState().getValue() == Adventure.State.UNDO
    }


    def 'carException'() {
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)

        when:
        adventure.process()

        then:
        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> new RestBankOperationData()
        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData
        activityReservationData.getPaymentReference() >> REFERENCE
        activityReservationData.getInvoiceReference() >> REFERENCE
        carInterface.getRentingData(RENTING_CONFIRMATION) >> { throw new CarException() }

        adventure.getState().getValue() == Adventure.State.UNDO
    }


    def 'oneRemoteExceptionInCar'() {
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)

        when:
        adventure.process()

        then:
        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> new RestBankOperationData()
        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData
        activityReservationData.getPaymentReference() >> REFERENCE
        activityReservationData.getInvoiceReference() >> REFERENCE
        carInterface.getRentingData(RENTING_CONFIRMATION) >> { throw new RemoteAccessException() }

        adventure.getState().getValue() == Adventure.State.CONFIRMED
    }


    def 'carNoPaymentConfirmation'() {
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)

        when:
        adventure.process()

        then:
        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> new RestBankOperationData()
        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData
        activityReservationData.getPaymentReference() >> REFERENCE
        activityReservationData.getInvoiceReference() >> REFERENCE
        carInterface.getRentingData(RENTING_CONFIRMATION) >> rentingData
        rentingData.getPaymentReference() >> null

        adventure.getState().getValue() == Adventure.State.UNDO
    }


    def 'carNoInvoiceReference'() {
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)

        when:
        adventure.process()

        then:
        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> new RestBankOperationData()
        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData
        activityReservationData.getPaymentReference() >> REFERENCE
        activityReservationData.getInvoiceReference() >> REFERENCE
        carInterface.getRentingData(RENTING_CONFIRMATION) >> rentingData
        rentingData.getPaymentReference() >> REFERENCE
        rentingData.getInvoiceReference() >> null

        adventure.getState().getValue() == Adventure.State.UNDO
    }


    def 'hotelException'() {
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)

        when:
        adventure.process()

        then:
        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> new RestBankOperationData()
        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData
        activityReservationData.getPaymentReference() >> REFERENCE
        activityReservationData.getInvoiceReference() >> REFERENCE
        roomInterface.getRoomBookingData(ROOM_CONFIRMATION) >> { throw new HotelException() }

        adventure.getState().getValue() == Adventure.State.UNDO
    }


    def 'oneRemoteAccessExceptionInHotel'() {
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)

        when:
        adventure.process()

        then:
        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> new RestBankOperationData()
        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData
        activityReservationData.getPaymentReference() >> REFERENCE
        activityReservationData.getInvoiceReference() >> REFERENCE
        roomInterface.getRoomBookingData(ROOM_CONFIRMATION) >> { throw new RemoteAccessException() }

        adventure.getState().getValue() == Adventure.State.CONFIRMED
    }


    def 'hotelNoPaymentConfirmation'() {
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)

        when:
        adventure.process()

        then:
        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> new RestBankOperationData()
        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData
        activityReservationData.getPaymentReference() >> REFERENCE
        activityReservationData.getInvoiceReference() >> REFERENCE
        roomInterface.getRoomBookingData(ROOM_CONFIRMATION) >> roomBookingData
        roomBookingData.getPaymentReference() >> null

        adventure.getState().getValue() == Adventure.State.UNDO
    }


    def 'hotelNoInvoiceReference'() {
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        when:
        adventure.process()

        then:
        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> new RestBankOperationData()
        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData
        activityReservationData.getPaymentReference() >> REFERENCE
        activityReservationData.getInvoiceReference() >> REFERENCE
        roomInterface.getRoomBookingData(ROOM_CONFIRMATION) >> roomBookingData
        roomBookingData.getPaymentReference() >> REFERENCE
        roomBookingData.getInvoiceReference() >> null

        adventure.getState().getValue() == Adventure.State.UNDO
    }
}

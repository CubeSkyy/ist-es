package pt.ulisboa.tecnico.softeng.broker.domain


import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State
import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestActivityBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestBankOperationData
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestInvoiceData
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRentingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.ActivityException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.BankException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.CarException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.TaxException

import java.time.LocalDate

class AdventureSequenceSpockTest extends SpockRollbackTestAbstractClass {

    def bookingActivityData
    def bookingRoomData
    def rentingData
    def bankData

    def broker
    def client

    def taxInterface = Mock(TaxInterface)
    def bankInterface = Mock(BankInterface)
    def activityInterface = Mock(ActivityInterface)
    def roomInterface = Mock(HotelInterface)
    def carInterface = Mock(CarInterface)


    @Override
    def populate4Test() {
        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, BROKER_NIF_AS_BUYER, BROKER_IBAN,
                roomInterface, taxInterface, activityInterface, carInterface, bankInterface)
        client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)

        bookingActivityData = new RestActivityBookingData()
        bookingActivityData.setReference(ACTIVITY_CONFIRMATION)
        bookingActivityData.setPrice(70.0)
        bookingActivityData.setPaymentReference(PAYMENT_CONFIRMATION)
        bookingActivityData.setInvoiceReference(INVOICE_REFERENCE)

        bookingRoomData = new RestRoomBookingData()
        bookingRoomData.setReference(ROOM_CONFIRMATION)
        bookingRoomData.setPrice(80.0)
        bookingRoomData.setPaymentReference(PAYMENT_CONFIRMATION)
        bookingRoomData.setInvoiceReference(INVOICE_REFERENCE)

        rentingData = new RestRentingData()
        rentingData.setReference(RENTING_CONFIRMATION)
        rentingData.setPrice(60.0)
        rentingData.setPaymentReference(PAYMENT_CONFIRMATION)
        rentingData.setInvoiceReference(INVOICE_REFERENCE)

        bankData = new RestBankOperationData()
    }


    def 'success sequence'() {
        given:
        activityInterface.reserveActivity(_ as RestActivityBookingData) >> bookingActivityData

        roomInterface.reserveRoom(_ as RestRoomBookingData) >> bookingRoomData

        carInterface.rentCar(* _) >> rentingData

        bankInterface.processPayment(_ as RestBankOperationData) >> PAYMENT_CONFIRMATION

        taxInterface.submitInvoice(_ as RestInvoiceData) >> INVOICE_DATA

        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> bankData

        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> bookingActivityData

        carInterface.getRentingData(RENTING_CONFIRMATION) >> rentingData

        roomInterface.getRoomBookingData(ROOM_CONFIRMATION) >> bookingRoomData

        and:
        def adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN, true)


        when:
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()

        then:
        adventure.getState().getValue() == State.CONFIRMED

    }


    def 'success sequence one no car' () {
        given:
        activityInterface.reserveActivity(_ as RestActivityBookingData) >> bookingActivityData
        roomInterface.reserveRoom(_ as RestRoomBookingData) >> bookingRoomData
        bankInterface.processPayment(_ as RestBankOperationData) >> PAYMENT_CONFIRMATION
        taxInterface.submitInvoice(_ as RestInvoiceData) >> INVOICE_DATA
        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> bankData
        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> bookingActivityData
        roomInterface.getRoomBookingData(ROOM_CONFIRMATION) >> bookingRoomData

        and:
        def adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN)

        when:
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()

        then:
        State.CONFIRMED == adventure.getState().getValue()
    }


    def 'success sequence no hotel' () {
        given:
        activityInterface.reserveActivity(_ as RestActivityBookingData) >> bookingActivityData
        carInterface.rentCar(* _) >> rentingData
        bankInterface.processPayment(_ as RestBankOperationData) >> PAYMENT_CONFIRMATION
        taxInterface.submitInvoice(_ as RestInvoiceData) >> INVOICE_DATA
        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> bankData
        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> bookingActivityData
        carInterface.getRentingData(RENTING_CONFIRMATION) >> rentingData

        and:
        def adventure = new Adventure(broker, ARRIVAL, ARRIVAL, client, MARGIN, true)

        when:
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()

        then:
        adventure.getState().getValue() == State.CONFIRMED

    }


    def 'success sequence no hotel no car' () {
        given:
        activityInterface.reserveActivity(_ as RestActivityBookingData) >> bookingActivityData
        bankInterface.processPayment(_ as RestBankOperationData) >> PAYMENT_CONFIRMATION
        taxInterface.submitInvoice(_ as RestInvoiceData) >> INVOICE_DATA
        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> bankData
        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> bookingActivityData

        and:
        def adventure = new Adventure(broker, ARRIVAL, ARRIVAL, client, MARGIN)

        when:
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()

        then:
        adventure.getState().getValue() == State.CONFIRMED
    }


    def 'unsuccess sequence fail activity' () {
        given:
        activityInterface.reserveActivity(_ as RestActivityBookingData) >> {throw new ActivityException()}

        and:
        def adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN)

        when:
        adventure.process()
        adventure.process()

        then:
        adventure.getState().getValue() == State.CANCELLED
    }


    def 'unsuccess sequence fail hotel' () {
        given:
        activityInterface.reserveActivity(_ as RestActivityBookingData) >> bookingActivityData
        roomInterface.reserveRoom(_ as RestRoomBookingData) >> {throw new HotelException()}

        activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> ACTIVITY_CANCELLATION

        and:
        def adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN)

        when:
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()

        then:
        adventure.getState().getValue() == State.CANCELLED
    }


    def 'unsucess sequence fail car' () {
        given:
        activityInterface.reserveActivity(_ as RestActivityBookingData) >> bookingActivityData
        carInterface.rentCar(* _) >> {throw new CarException()}
        activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> ACTIVITY_CANCELLATION

        and:
        def adventure = new Adventure(broker, ARRIVAL, ARRIVAL, client, MARGIN, true)

        when:
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()

        then:
        adventure.getState().getValue() == State.CANCELLED
    }


    def 'unsucess sequence fail payment' () {
        given:
        activityInterface.reserveActivity(_ as RestActivityBookingData) >> bookingActivityData

        roomInterface.reserveRoom(_ as RestRoomBookingData) >> bookingRoomData

        carInterface.rentCar(* _) >> rentingData

        bankInterface.processPayment(_ as RestBankOperationData) >> {throw new BankException()}

        activityInterface.cancelReservation(_ as String) >> ACTIVITY_CANCELLATION

        roomInterface.cancelBooking(ROOM_CONFIRMATION) >> ROOM_CANCELLATION

        carInterface.cancelRenting(RENTING_CONFIRMATION) >> RENTING_CANCELLATION

        and:
        def adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN, true)

        when:
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()

        then:
        adventure.getState().getValue() == State.CANCELLED
    }


    def 'unsucess sequence fail tax' () {
        given:
        activityInterface.reserveActivity(_ as RestActivityBookingData) >> bookingActivityData
        roomInterface.reserveRoom(_ as RestRoomBookingData) >> bookingRoomData
        carInterface.rentCar(* _) >>rentingData
        bankInterface.processPayment(_ as RestBankOperationData) >> PAYMENT_CONFIRMATION
        taxInterface.submitInvoice(_ as RestInvoiceData) >> {throw new TaxException()}
        activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> ACTIVITY_CANCELLATION
        roomInterface.cancelBooking(ROOM_CONFIRMATION) >> ROOM_CANCELLATION
        carInterface.cancelRenting(RENTING_CONFIRMATION) >> RENTING_CANCELLATION
        bankInterface.cancelPayment(PAYMENT_CONFIRMATION) >> PAYMENT_CANCELLATION

        and:
        def adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN, true)

        when:
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()

        then:
        adventure.getState().getValue() == State.CANCELLED
    }


}
package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State
import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.ActivityException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.BankException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.CarException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.TaxException
import spock.lang.Unroll


class UndoStateProcessMethodSpockTest extends SpockRollbackTestAbstractClass {

    def taxInterface
    def bankInterface
    def roomInterface
    def carInterface
    def activityInterface

    def broker
    def client
    def adventure

    @Override
    def populate4Test() {
        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
        client = new Client(this.broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
        adventure = new Adventure(this.broker, BEGIN, END, this.client, MARGIN)

        taxInterface = Mock(TaxInterface)
        bankInterface = Mock(BankInterface)
        roomInterface = Mock(HotelInterface)
        carInterface = Mock(CarInterface)
        activityInterface = Mock(ActivityInterface)


        this.adventure.setState(State.UNDO)
        adventure.setTaxInterface(taxInterface)
        adventure.setBankInterface(bankInterface)
        adventure.setHotelInterface(roomInterface)
        adventure.setCarInterface(carInterface)
        adventure.setActivityInterface(activityInterface)
    }


    @Unroll("exceçao lancada #label #exception")
    def 'revert payment'() {
        when:
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        this.adventure.process()

        then:
        bankInterface.cancelPayment(PAYMENT_CONFIRMATION) >> functionReturn
        this.adventure.getState().getValue() == state

        where:
        label                                  | functionReturn                        | state
        "success revert payment"               | PAYMENT_CANCELLATION                  | State.CANCELLED
        "fail payment bank exception"          | { throw new BankException() }         | State.UNDO
        "fail payment remote access exception" | { throw new RemoteAccessException() } | State.UNDO
    }
    

    @Unroll('#label #state #functionReturn')
    def 'revert activity'() {
        when:
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        this.adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        this.adventure.process()

        then:
        activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> functionReturn
        this.adventure.getState().getValue() == state

        where:
        label                                     | functionReturn                        | state
        "success revert activity"                 | ACTIVITY_CANCELLATION                 | State.CANCELLED
        "fail revert activity activity Exception" | { throw new ActivityException() }     | State.UNDO
        "fail revert activity remote Exception"   | { throw new RemoteAccessException() } | State.UNDO

    }

    @Unroll('#label #state #functionReturn')
    def 'revert RoomBooking'() {
        when:
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        this.adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        this.adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        this.adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        this.adventure.process()

        then:
        roomInterface.cancelBooking(ROOM_CONFIRMATION) >> functionReturn
        this.adventure.getState().getValue() == state

        where:
        label                                       | functionReturn                        | state
        "success RevertRoomBooking"                 | ROOM_CANCELLATION                     | State.CANCELLED
        "success RevertRoomBooking HotelException"  | { throw new HotelException() }        | State.UNDO
        "success RevertRoomBooking RemoteException" | { throw new RemoteAccessException() } | State.UNDO
    }

    @Unroll('#label #state #functionReturn')
    def 'revert RentCar'() {
        when:
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        this.adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        this.adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        this.adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        this.adventure.setRoomCancellation(ROOM_CANCELLATION)
        this.adventure.setRentingConfirmation(RENTING_CONFIRMATION)
        this.adventure.process()

        then:
        carInterface.cancelRenting(RENTING_CONFIRMATION) >> functionReturn
        this.adventure.getState().getValue() == state

        where:
        label                                 | functionReturn                        | state
        "success Revert RentCar"              | RENTING_CANCELLATION                  | State.CANCELLED
        "fail Revert RentCar CarException"    | { throw new CarException() }          | State.UNDO
        "fail Revert RentCar RemoteException" | { throw new RemoteAccessException() } | State.UNDO
    }

    @Unroll('#label #state #functionReturn')
    def 'cancel Invoice'() {
        when:
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        this.adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        this.adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        this.adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        this.adventure.setRoomCancellation(ROOM_CANCELLATION)
        this.adventure.setRentingConfirmation(RENTING_CONFIRMATION)
        this.adventure.setRentingCancellation(RENTING_CONFIRMATION)
        this.adventure.setInvoiceReference(INVOICE_REFERENCE)
        this.adventure.process()

        then:
        taxInterface.cancelInvoice(INVOICE_REFERENCE) >> functionReturn

        this.adventure.getState().getValue() == state

        where:
        label                                 | functionReturn                        | state
        "success Cancel Invoice"              | _                                     | State.CANCELLED
        "fail Cancel Invoice TaxException"    | { throw new TaxException() }          | State.UNDO
        "fail Cancel Invoice RemoteException" | { throw new RemoteAccessException() } | State.UNDO
    }

}

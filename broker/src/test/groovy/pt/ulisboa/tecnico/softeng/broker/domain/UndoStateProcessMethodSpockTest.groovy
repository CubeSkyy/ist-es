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
    
    def 'success revert payment'() {
        when:
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        this.adventure.process()

        then:
        bankInterface.cancelPayment(PAYMENT_CONFIRMATION) >> PAYMENT_CANCELLATION
        this.adventure.getState().getValue() == State.CANCELLED
    }

    def 'fail payment bank exception'() {
        when:
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        this.adventure.process()

        then:
        bankInterface.cancelPayment(PAYMENT_CONFIRMATION) >> { throw new BankException() }
        this.adventure.getState().getValue() == State.UNDO
    }

    def 'fail payment remote access exception'() {
        when:
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        this.adventure.process()

        then:
        bankInterface.cancelPayment(PAYMENT_CONFIRMATION) >> { throw new RemoteAccessException() }
        this.adventure.getState().getValue() == State.UNDO
    }


    def 'success revert activity'() {
        when:
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        this.adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        this.adventure.process()

        then:
        activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> ACTIVITY_CANCELLATION
        this.adventure.getState().getValue() == State.CANCELLED
    }

    def 'fail revert activity activity Exception'() {
        when:
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        this.adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        this.adventure.process()

        then:
        activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> { throw new ActivityException() }
        this.adventure.getState().getValue() == State.UNDO
    }

    def 'fail revert activity remote Exception'() {
        when:
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        this.adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        this.adventure.process()

        then:
        activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> { throw new RemoteAccessException() }
        this.adventure.getState().getValue() == State.UNDO
    }


    def 'success RevertRoomBooking'() {
        when:
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        this.adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        this.adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        this.adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        this.adventure.process()

        then:
        roomInterface.cancelBooking(ROOM_CONFIRMATION) >> ROOM_CANCELLATION
        this.adventure.getState().getValue() == State.CANCELLED
    }

    def 'success RevertRoomBooking HotelException'() {
        when:
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        this.adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        this.adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        this.adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        this.adventure.process()

        then:
        roomInterface.cancelBooking(ROOM_CONFIRMATION) >> { throw new HotelException() }
        this.adventure.getState().getValue() == State.UNDO
    }

    def 'success RevertRoomBooking RemoteException'() {
        when:
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        this.adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        this.adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        this.adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        this.adventure.process()

        then:
        roomInterface.cancelBooking(ROOM_CONFIRMATION) >> { throw new RemoteAccessException() }
        this.adventure.getState().getValue() == State.UNDO
    }


    def 'success Revert RentCar'() {
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
        carInterface.cancelRenting(RENTING_CONFIRMATION) >> RENTING_CANCELLATION
        this.adventure.getState().getValue() == State.CANCELLED
    }

    def 'fail Revert RentCar CarException'() {
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
        carInterface.cancelRenting(RENTING_CONFIRMATION) >> { throw new CarException() }
        this.adventure.getState().getValue() == State.UNDO
    }

    def 'fail Revert RentCar RemoteException'() {
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
        carInterface.cancelRenting(RENTING_CONFIRMATION) >> { throw new RemoteAccessException() }
        this.adventure.getState().getValue() == State.UNDO
    }

    def 'success Cancel Invoice'() {
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
        taxInterface.cancelInvoice(INVOICE_REFERENCE) >> _

        this.adventure.getState().getValue() == State.CANCELLED
    }

    def 'fail Cancel Invoice TaxException'() {
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
        taxInterface.cancelInvoice(INVOICE_REFERENCE) >> { throw new TaxException() }

        this.adventure.getState().getValue() == State.UNDO
    }

    def 'fail Cancel Invoice RemoteException'() {
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
        taxInterface.cancelInvoice(INVOICE_REFERENCE) >> { throw new RemoteAccessException() }

        this.adventure.getState().getValue() == State.UNDO
    }

}

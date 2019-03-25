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
        client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
        adventure = new Adventure(broker, BEGIN, END, client, MARGIN)

        taxInterface = Mock(TaxInterface)
        bankInterface = Mock(BankInterface)
        roomInterface = Mock(HotelInterface)
        carInterface = Mock(CarInterface)
        activityInterface = Mock(ActivityInterface)


        adventure.setState(State.UNDO)
        adventure.setTaxInterface(taxInterface)
        adventure.setBankInterface(bankInterface)
        adventure.setHotelInterface(roomInterface)
        adventure.setCarInterface(carInterface)
        adventure.setActivityInterface(activityInterface)
    }

    def 'success revert payment'() {
        when:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.process()

        then:
        bankInterface.cancelPayment(PAYMENT_CONFIRMATION) >> PAYMENT_CANCELLATION
        adventure.getState().getValue() == State.CANCELLED
    }

    def 'fail payment bank exception'() {
        when:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.process()

        then:
        bankInterface.cancelPayment(PAYMENT_CONFIRMATION) >> { throw new BankException() }
        adventure.getState().getValue() == State.UNDO
    }

    def 'fail payment remote access exception'() {
        when:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.process()

        then:
        bankInterface.cancelPayment(PAYMENT_CONFIRMATION) >> { throw new RemoteAccessException() }
        adventure.getState().getValue() == State.UNDO
    }


    def 'success revert activity'() {
        when:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.process()

        then:
        activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> ACTIVITY_CANCELLATION
        adventure.getState().getValue() == State.CANCELLED
    }

    def 'fail revert activity activity Exception'() {
        when:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.process()

        then:
        activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> { throw new ActivityException() }
        adventure.getState().getValue() == State.UNDO
    }

    def 'fail revert activity remote Exception'() {
        when:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.process()

        then:
        activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> { throw new RemoteAccessException() }
        adventure.getState().getValue() == State.UNDO
    }


    def 'success RevertRoomBooking'() {
        when:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        adventure.process()

        then:
        roomInterface.cancelBooking(ROOM_CONFIRMATION) >> ROOM_CANCELLATION
        adventure.getState().getValue() == State.CANCELLED
    }

    def 'success RevertRoomBooking HotelException'() {
        when:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        adventure.process()

        then:
        roomInterface.cancelBooking(ROOM_CONFIRMATION) >> { throw new HotelException() }
        adventure.getState().getValue() == State.UNDO
    }

    def 'success RevertRoomBooking RemoteException'() {
        when:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        adventure.process()

        then:
        roomInterface.cancelBooking(ROOM_CONFIRMATION) >> { throw new RemoteAccessException() }
        adventure.getState().getValue() == State.UNDO
    }


    def 'success Revert RentCar'() {
        when:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        adventure.setRoomCancellation(ROOM_CANCELLATION)
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
        adventure.process()

        then:
        carInterface.cancelRenting(RENTING_CONFIRMATION) >> RENTING_CANCELLATION
        adventure.getState().getValue() == State.CANCELLED
    }

    def 'fail Revert RentCar CarException'() {
        when:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        adventure.setRoomCancellation(ROOM_CANCELLATION)
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
        adventure.process()

        then:
        carInterface.cancelRenting(RENTING_CONFIRMATION) >> { throw new CarException() }
        adventure.getState().getValue() == State.UNDO
    }

    def 'fail Revert RentCar RemoteException'() {
        when:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        adventure.setRoomCancellation(ROOM_CANCELLATION)
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
        adventure.process()

        then:
        carInterface.cancelRenting(RENTING_CONFIRMATION) >> { throw new RemoteAccessException() }
        adventure.getState().getValue() == State.UNDO
    }

    def 'success Cancel Invoice'() {
        when:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        adventure.setRoomCancellation(ROOM_CANCELLATION)
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
        adventure.setRentingCancellation(RENTING_CONFIRMATION)
        adventure.setInvoiceReference(INVOICE_REFERENCE)
        adventure.process()

        then:
        taxInterface.cancelInvoice(INVOICE_REFERENCE) >> _

        adventure.getState().getValue() == State.CANCELLED
    }

    def 'fail Cancel Invoice TaxException'() {
        when:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        adventure.setRoomCancellation(ROOM_CANCELLATION)
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
        adventure.setRentingCancellation(RENTING_CONFIRMATION)
        adventure.setInvoiceReference(INVOICE_REFERENCE)
        adventure.process()

        then:
        taxInterface.cancelInvoice(INVOICE_REFERENCE) >> { throw new TaxException() }

        adventure.getState().getValue() == State.UNDO
    }

    def 'fail Cancel Invoice RemoteException'() {
        when:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        adventure.setRoomCancellation(ROOM_CANCELLATION)
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
        adventure.setRentingCancellation(RENTING_CONFIRMATION)
        adventure.setInvoiceReference(INVOICE_REFERENCE)
        adventure.process()

        then:
        taxInterface.cancelInvoice(INVOICE_REFERENCE) >> { throw new RemoteAccessException() }

        adventure.getState().getValue() == State.UNDO
    }

}

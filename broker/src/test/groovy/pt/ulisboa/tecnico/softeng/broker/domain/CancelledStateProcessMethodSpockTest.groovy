package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestBankOperationData
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.BankException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException

class CancelledStateProcessMethodSpockTest extends SpockRollbackTestAbstractClass {

    def taxInterface = Mock(TaxInterface)
    def bankInterface = Mock(BankInterface)
    def activityInterface = Mock(ActivityInterface)
    def hotelInterface = Mock(HotelInterface)
    def carInterface = Mock(CarInterface)

    def adventure

    @Override
    def populate4Test() {
        def broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN,
                hotelInterface, taxInterface, activityInterface, carInterface, bankInterface)
        def client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
        adventure = new Adventure(broker, BEGIN, END, client, MARGIN)

        adventure.setState(State.CANCELLED)
    }

    def 'did not payed'() {
        when:
        adventure.process()
        then:
        0 * bankInterface.getOperationData(_ as String)
        0 * activityInterface.getActivityReservationData(_ as String)
        0 * hotelInterface.getRoomBookingData(_ as String)
    }

    def 'cancelled payment first RemoteAccessException'() {
        when:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        adventure.process()
        then:
        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> { throw new RemoteAccessException() }
        adventure.getState().getValue() == State.CANCELLED
    }

    def 'cancelled payment second BankException'() {
        when:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        adventure.process()

        then:
        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> new RestBankOperationData() >> {
            throw new BankException()
        }
        adventure.getState().getValue() == State.CANCELLED
    }

    def 'cancelled payment second RemoteAccessException'() {
        when:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        adventure.process()

        then:
        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> new RestBankOperationData() >> {
            throw new RemoteAccessException()
        }
        adventure.getState().getValue() == State.CANCELLED
    }

    def 'cancelled payment'() {
        when:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        adventure.process()

        then:
        adventure.getState().getValue() == State.CANCELLED
    }

    def 'cancelled activity'() {
        when:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)

        adventure.process()

        then:
        _ * bankInterface.getOperationData(PAYMENT_CONFIRMATION)
        _ * activityInterface.getActivityReservationData(ACTIVITY_CANCELLATION)

        adventure.getState().getValue() == State.CANCELLED
    }

    def 'cancelled room'() {
        when:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        adventure.setRoomCancellation(ROOM_CANCELLATION)

        adventure.process()
        then:
        _ * bankInterface.getOperationData(PAYMENT_CONFIRMATION)
        _ * activityInterface.getActivityReservationData(ACTIVITY_CANCELLATION)
        _ * hotelInterface.getRoomBookingData(ROOM_CANCELLATION)

        adventure.getState().getValue() == State.CANCELLED
    }

    def 'cancelled renting'() {
        when:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
        adventure.setRentingCancellation(RENTING_CANCELLATION)

        adventure.process()
        then:
        _ * bankInterface.getOperationData(PAYMENT_CONFIRMATION)
        _ * activityInterface.getActivityReservationData(ACTIVITY_CANCELLATION)
        _ * carInterface.getRentingData(RENTING_CANCELLATION)

        adventure.getState().getValue() == State.CANCELLED
    }

    def 'cancelled book and renting'() {
        when:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        adventure.setRoomCancellation(ROOM_CANCELLATION)
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
        adventure.setRentingCancellation(RENTING_CANCELLATION)

        adventure.process()
        then:
        _ * bankInterface.getOperationData(PAYMENT_CONFIRMATION)
        _ * activityInterface.getActivityReservationData(ACTIVITY_CANCELLATION)
        _ * hotelInterface.getRoomBookingData(ROOM_CANCELLATION)
        _ * carInterface.getRentingData(RENTING_CANCELLATION)

        adventure.getState().getValue() == State.CANCELLED
    }


}

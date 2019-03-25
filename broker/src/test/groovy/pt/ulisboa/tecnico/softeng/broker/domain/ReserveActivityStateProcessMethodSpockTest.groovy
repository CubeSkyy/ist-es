package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State
import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestActivityBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.ActivityException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException

class ReserveActivityStateProcessMethodSpockTest extends SpockRollbackTestAbstractClass {

    def taxInterface
    def activityInterface

    def bookingData

    def broker
    def client
    def adventure

    @Override
    def populate4Test() {
        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
        client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
        adventure = new Adventure(broker, BEGIN, END, client, MARGIN)
        bookingData = new RestActivityBookingData()
        bookingData.setReference(ACTIVITY_CONFIRMATION)
        bookingData.setPrice(76.78)

        taxInterface = Mock(TaxInterface)
        activityInterface = Mock(ActivityInterface)

        adventure.setState(Adventure.State.RESERVE_ACTIVITY)
        adventure.setTaxInterface(taxInterface)
        adventure.setActivityInterface(activityInterface)
    }

    def 'success no BookRoom'() {
        when:
        Adventure sameDayAdventure = new Adventure(broker, BEGIN, BEGIN, client, MARGIN)
        sameDayAdventure.setState(State.RESERVE_ACTIVITY)
        sameDayAdventure.setTaxInterface(taxInterface)
        sameDayAdventure.setActivityInterface(activityInterface)
        sameDayAdventure.process()

        then:
        activityInterface.reserveActivity(_ as RestActivityBookingData) >> bookingData
        sameDayAdventure.getState().getValue() == State.PROCESS_PAYMENT
    }

    def 'successToRentVehicle'() {
        when:
        Adventure adv = new Adventure(broker, BEGIN, BEGIN, client, MARGIN, true)
        adv.setState(State.RESERVE_ACTIVITY)
        adv.setTaxInterface(taxInterface)
        adv.setActivityInterface(activityInterface)
        adv.process()

        then:
        activityInterface.reserveActivity(_ as RestActivityBookingData) >> bookingData
        adv.getState().getValue() == State.RENT_VEHICLE

    }

    def 'success BookRoom'() {
        when:
        this.adventure.process()

        then:
        activityInterface.reserveActivity(_ as RestActivityBookingData) >> bookingData
        this.adventure.getState().getValue() == State.BOOK_ROOM
    }

    def 'activityException'() {
        when:
        adventure.process()

        then:
        activityInterface.reserveActivity(_ as RestActivityBookingData) >> { throw new ActivityException() }
        adventure.getState().getValue() == State.UNDO
    }

    def 'single RemoteAccessException'() {
        when:
        adventure.process()

        then:
        activityInterface.reserveActivity(_ as RestActivityBookingData) >> { throw new RemoteAccessException() }
        adventure.getState().getValue() == State.RESERVE_ACTIVITY
    }

    def 'max RemoteAccessException'() {
        when:
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()

        then:
        activityInterface.reserveActivity(_ as RestActivityBookingData) >> { throw new RemoteAccessException() }
        adventure.getState().getValue() == State.UNDO
    }

    def 'max MinusOne RemoteAccessException'() {
        when:
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()

        then:
        activityInterface.reserveActivity(_ as RestActivityBookingData) >> { throw new RemoteAccessException() }
        adventure.getState().getValue() == State.RESERVE_ACTIVITY
    }

    def 'twoRemoteAccessExceptionOneSuccess'() {
        when:
        adventure.process()
        adventure.process()
        adventure.process()

        then:
        3 * activityInterface.reserveActivity(_ as RestActivityBookingData) >> {
            throw new RemoteAccessException()
        } >> {
            throw new RemoteAccessException()
        } >> bookingData
        adventure.getState().getValue() == State.BOOK_ROOM
    }

    def 'one RemoteAccessException one ActivityException'() {
        when:
        adventure.process()
        adventure.process()

        then:
        2 * activityInterface.reserveActivity(_ as RestActivityBookingData) >> {
            throw new RemoteAccessException()
        } >> {
            throw new ActivityException()
        }
        adventure.getState().getValue() == State.UNDO
    }
}

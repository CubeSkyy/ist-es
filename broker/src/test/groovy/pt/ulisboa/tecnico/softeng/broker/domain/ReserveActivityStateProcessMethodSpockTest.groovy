package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestActivityBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.ActivityException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException

class ReserveActivityStateProcessMethodSpockTest extends SpockRollbackTestAbstractClass{

    def taxInterface
    def activityInterface

    def bookingData;

    def broker
    def client
    def adventure

    @Override
    def populate4Test() {
        this.broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN);
        this.client = new Client(this.broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE);
        this.adventure = new Adventure(this.broker, this.BEGIN, this.END, this.client, MARGIN);
        this.bookingData = new RestActivityBookingData();
        this.bookingData.setReference(ACTIVITY_CONFIRMATION);
        this.bookingData.setPrice(76.78);

        taxInterface = Mock(TaxInterface)
        activityInterface = Mock(ActivityInterface)

        this.adventure.setState(Adventure.State.RESERVE_ACTIVITY);
        adventure.setTaxInterface(taxInterface);
        adventure.setActivityInterface(activityInterface);
    }

    def 'success no BookRoom'(){
        when:
        Adventure sameDayAdventure = new Adventure(this.broker, this.BEGIN, this.BEGIN, this.client, MARGIN);
        sameDayAdventure.setState(State.RESERVE_ACTIVITY);
        sameDayAdventure.setTaxInterface(taxInterface);
        sameDayAdventure.setActivityInterface(activityInterface);
        sameDayAdventure.process();

        then:
        activityInterface.reserveActivity(_)>>bookingData
        sameDayAdventure.getState().getValue() == State.PROCESS_PAYMENT
    }

    def 'successToRentVehicle'(){
        when:
        Adventure adv = new Adventure(this.broker, this.BEGIN, this.BEGIN, this.client, MARGIN, true);
        adv.setState(State.RESERVE_ACTIVITY);
        adv.setTaxInterface(taxInterface);
        adv.setActivityInterface(activityInterface);
        adv.process()

        then:
        activityInterface.reserveActivity(_)>>bookingData
        adv.getState().getValue()==State.RENT_VEHICLE

    }

    def 'activityException'(){
        when:
        this.adventure.process();

        then:
        activityInterface.reserveActivity(_)>>{throw new ActivityException()}
        this.adventure.getState().getValue() == State.UNDO
    }
    def 'single RemoteAccessException'(){
        when:
        this.adventure.process();

        then:
        activityInterface.reserveActivity(_)>>{throw new RemoteAccessException()}
        this.adventure.getState().getValue() == State.RESERVE_ACTIVITY
    }

}

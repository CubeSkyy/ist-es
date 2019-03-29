package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;

class BookRoomStateMethodSpockTest extends SpockRollbackTestAbstractClass {

    def bookingData
    def hotelInterface
    def broker
    def client
    def adventure

    @Override
    def populate4Test(){
        hotelInterface = Mock(HotelInterface)
        broker =  new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN, hotelInterface,
                new TaxInterface(), new ActivityInterface(), new CarInterface(), new BankInterface())
        client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
        adventure = new Adventure(broker, BEGIN, END, client, MARGIN)



        bookingData = new RestRoomBookingData()
        bookingData.setReference(ROOM_CONFIRMATION);
        bookingData.setPrice(80.0);
        adventure.setState(Adventure.State.BOOK_ROOM);
    }

    def 'successBookRoom'(){
        when: 'processing an adventure where booking a room succeeded'
        adventure.process()

        then: 'adventure is now in the "Process Payment" stage'
        hotelInterface.reserveRoom(_ as RestRoomBookingData) >> bookingData
        adventure.getState().getValue() == Adventure.State.PROCESS_PAYMENT
    }

    def 'successBookRoomToRenting'(){
        given:
        def adv =  new Adventure(broker, BEGIN, END, client, MARGIN, true)
        adv.setState(Adventure.State.BOOK_ROOM)

        when:
        adv.process()

        then:
        hotelInterface.reserveRoom(_ as RestRoomBookingData) >> bookingData
        adv.getState().getValue() == Adventure.State.RENT_VEHICLE
    }

    def 'hotelException'(){
        when:
        adventure.process()

        then:
        hotelInterface.reserveRoom(_ as RestRoomBookingData) >> { throw new HotelException() }
        adventure.getState().getValue() == Adventure.State.UNDO
    }

    def 'singleRemoteAccessException'(){
        when:
        adventure.process()

        then:
        hotelInterface.reserveRoom(_ as RestRoomBookingData) >> { throw new RemoteAccessException() }
        adventure.getState().getValue() == Adventure.State.BOOK_ROOM
    }

    def 'maxRemoteAccessException'(){
        when:
        for (int i = 0; i < BookRoomState.MAX_REMOTE_ERRORS; i++) {
            adventure.process()
        }

        then:
        BookRoomState.MAX_REMOTE_ERRORS * hotelInterface.reserveRoom(_ as RestRoomBookingData) >>
                                                                                { throw new RemoteAccessException() }
        adventure.getState().getValue() == Adventure.State.UNDO
    }


    def 'maxMinusOneRemoteAccessException'() {
        when:
        for (int i = 0; i < BookRoomState.MAX_REMOTE_ERRORS - 1; i++) {
            adventure.process();
        }

        then:
        (BookRoomState.MAX_REMOTE_ERRORS - 1) * hotelInterface.reserveRoom(_ as RestRoomBookingData) >>
                                                                                { throw new RemoteAccessException() }
        adventure.getState().getValue() == Adventure.State.BOOK_ROOM
    }


    def 'fiveRemoteAccessExceptionOneSuccess'(){
        given:
        int i

        when:
        for (i = 0; i<6; i++) {
            adventure.process()
        }

        then:

        6 * hotelInterface.reserveRoom(_ as RestRoomBookingData) >>{ if (i < 5) {
                                                                        throw new RemoteAccessException()}
                                                                    else {
                                                                        return bookingData}
                                                                    }
        adventure.getState().getValue() == Adventure.State.PROCESS_PAYMENT
    }


    def 'oneRemoteAccessExceptionOneHotelException'(){
        when:
        for (int i = 0; i<2; i++) {
            a dventure.process()
        }

        then:
        2 * hotelInterface.reserveRoom(_ as RestRoomBookingData) >> { throw new RemoteAccessException() } >>
                                                                    {throw new HotelException()}
        adventure.getState().getValue() == Adventure.State.UNDO
    }


}












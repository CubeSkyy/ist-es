package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;

class BulkRoomBookingProcessBookingMethodSpockTest extends SpockRollbackTestAbstractClass {

    def bulk
    def broker
    def roomInterface

    def "populate4Test"(){
        roomInterface = Mock(HotelInterface)

        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
        bulk = new BulkRoomBooking(this.broker, NUMBER_OF_BULK, this.BEGIN, this.END, NIF_AS_BUYER, IBAN_BUYER)
        broker.setHotelInterface(roomInterface)
    }

    def "success"(){
        when:
        bulk.processBooking()

        then:
        roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >>
                                                                         new HashSet<>(Arrays.asList("ref1", "ref2"))
        bulk.getReferences().size() == 2
    }

    def "successTwice"(){
        when:
        bulk.processBooking()
        bulk.processBooking()

        then:
        roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >>
                                                                    new HashSet<>(Arrays.asList("ref1", "ref2")) >>
                                                                    new HashSet<>(Arrays.asList("ref1", "ref2"))
        bulk.getReferences().size() == 2
    }

    def "oneHotelException"(){
        when:
        bulk.processBooking()
        bulk.processBooking()

        then:
        roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >>
                {throw new HotelException()} >>
                new HashSet<>(Arrays.asList("ref1", "ref2"))
        bulk.getReferences().size() == 2
        bulk.getCancelled() == false
    }

    def "maxHotelException"(){
        when:
        bulk.processBooking()
        bulk.processBooking()
        bulk.processBooking()

        then:
        roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >>
                {throw new HotelException()}
        bulk.getReferences().size() == 0
        bulk.getCancelled()
    }

    def "maxMinusOneHotelException"(){
        given:
        int i = 0

        when:
        bulk.processBooking()
        bulk.processBooking()
        bulk.processBooking()

        then:
        roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >>
                { if (i < BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1) {
                    i++
                    throw new HotelException()}
                else {
                    i++
                    return new HashSet<>(Arrays.asList("ref1", "ref2"))}
                }
        bulk.getReferences().size() == 2
        bulk.getCancelled() == false
    }

    def "hotelExceptionValueIsResetByRemoteException"() {
        given:
        int i = 0

        when:
        bulk.processBooking()
        bulk.processBooking()
        bulk.processBooking()
        bulk.processBooking()
        bulk.processBooking()
        bulk.processBooking()

        then:
        roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >>
                { if (i < BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1) {
                    i++
                    throw new HotelException()
                }
                else if (i == BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1){
                    i++
                    throw new RemoteAccessException()
                }
                else if (i < 2 * BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1) {
                    i++
                    throw new HotelException()
                }
                else {
                    i++
                    return new HashSet<>(Arrays.asList("ref1", "ref2"))}
                }
        bulk.getReferences().size() == 2
        bulk.getCancelled() == false
    }

    def "oneRemoteException"(){
        when:
        bulk.processBooking()
        bulk.processBooking()

        then:
        roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >>
                {throw new RemoteAccessException()} >>
                {return new HashSet<>(Arrays.asList("ref1", "ref2"))}
        bulk.getReferences().size() == 2
        bulk.getCancelled() == false
    }

    def "maxRemoteException"(){
        given:
        int i = 0

        when:
        for (i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS + 1; i++) {
            this.bulk.processBooking()
        }

        then:
        roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >>
                {throw new RemoteAccessException()}
        bulk.getReferences().size() == 0
        bulk.getCancelled()
    }

    def "maxMinusOneRemoteException"(){
        given:
        int i = 0
        int j = 0

        when:
        for (j = 0; j < BulkRoomBooking.MAX_REMOTE_ERRORS; j++) {
            bulk.processBooking()
        }

        then:
        (BulkRoomBooking.MAX_REMOTE_ERRORS - 1)*
                roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >>
                            {if (i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
                                i++
                                throw new RemoteAccessException()
                            } else {
                                i++
                                return new HashSet<>(Arrays.asList("ref1", "ref2"))
                            } } >> {throw new RemoteAccessException()}

        roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >>
                new HashSet<>(Arrays.asList("ref1", "ref2"))

        bulk.getReferences().size() == 2
        bulk.getCancelled() == false
    }
    
    def "remoteExceptionValueIsResetByHotelException"(){
        given:
        int i = 0
        int j = 0
        
        when:
        for (i = 0; i < 2*BulkRoomBooking.MAX_REMOTE_ERRORS ; i++) {
            this.bulk.processBooking()
        }
        
        then:
        roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >>
                {if (j < BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
                    j++
                    throw new RemoteAccessException();
                } else if (j == BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
                    j++
                    throw new HotelException()
                } else if (j < 2 * BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
                    j++
                    throw new RemoteAccessException()
                } else {
                    j++
                    return new HashSet<>(Arrays.asList("ref1", "ref2"));
                }}
        bulk.getReferences().size() == 2
        bulk.getCancelled() == false
    }


}



















package pt.ulisboa.tecnico.softeng.broker.domain;

import mockit.Delegate;
import mockit.Expectations
import mockit.Mock;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import pt.ulisboa.tecnico.softeng.broker.services.remote.*;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*

class BulkRoomBookingProcessBookingMethodSpockTest extends SpockRollbackTestAbstractClass {
    def bulk
    def broker
    def roomInterface

    def MAX_HOTEL_EXCEPTIONS = BulkRoomBooking.MAX_HOTEL_EXCEPTIONS

    @Override
    def populate4Test() {
        roomInterface = Mock(HotelInterface)
        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN,
                roomInterface, new TaxInterface(), new ActivityInterface(), new CarInterface(), new BankInterface())
        bulk = new BulkRoomBooking(broker, NUMBER_OF_BULK, this.BEGIN, this.END, NIF_AS_BUYER, IBAN_BUYER);
    }

    def 'success'() {
        given:
        roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,bulk.getId()) >> new HashSet<>(Arrays.asList("ref1", "ref2"))

        when:
        bulk.processBooking()

        then:
        bulk.getReferences().size() == 2
    }

    def 'successTwice'() {
        given:
        roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >>
            new HashSet<>(Arrays.asList("ref1", "ref2")) >> new HashSet<>(Arrays.asList("ref3", "ref4"))

        when:
        bulk.processBooking()
        bulk.processBooking()

        then:
        bulk.getReferences().size() == 2
    }


    def 'oneHotelException'() {
        given:
        roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> { throw new HotelException()} >> new HashSet<>(Arrays.asList("ref1", "ref2"))

        when:
        bulk.processBooking()
        bulk.processBooking()

        then:
        bulk.getReferences().size() == 2
        !bulk.getCancelled()
    }

    def 'maxHotelException'() {
        given:
        roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> { throw new HotelException()}

        when:
        bulk.processBooking()
        bulk.processBooking()
        bulk.processBooking()

        then:
        bulk.getReferences().size() == 0
        bulk.getCancelled()
    }

    def 'maxMinusOneHotelException'() {
        given:
        def i = 0
        roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> {
            i++
            if (i < BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1) {
                throw new HotelException()
            } else {
                return new HashSet<>(Arrays.asList("ref1", "ref2"))
            }
        }

        when:
        bulk.processBooking()
        bulk.processBooking()
        bulk.processBooking()

        then:
        bulk.getReferences().size() == 2
        !bulk.getCancelled()
    }

    def 'hotelExceptionValueIsResetByRemoteException'() {
        given:
        def i = 0
        roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> {
            i++
            if (i < MAX_HOTEL_EXCEPTIONS - 1) {
                throw new HotelException()
            } else if (i == MAX_HOTEL_EXCEPTIONS - 1) {
                throw new RemoteAccessException()
            } else if (i < 2 * MAX_HOTEL_EXCEPTIONS - 1) {
                throw new HotelException()
            } else {
                return new HashSet<>(Arrays.asList("ref1", "ref2"));
            }
        }


        when:
        bulk.processBooking()
        bulk.processBooking()
        bulk.processBooking()
        bulk.processBooking()
        bulk.processBooking()
        bulk.processBooking()

        then:
        bulk.getReferences().size() == 2
        !bulk.getCancelled()
    }

    def 'oneRemoteException'() {
        given:
        roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> { throw new RemoteAccessException() } >> new HashSet<>(Arrays.asList("ref1", "ref2"))

        when:
        bulk.processBooking()
        then:
        bulk.getReferences().size() == 2
        !bulk.getCancelled()
    }

    def 'maxRemoteException'() {
        given:
        def i
        roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> { throw new RemoteAccessException() }

        when:
        for (i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS; i++) {
            bulk.processBooking()
        }
        bulk.processBooking()

        then:
        bulk.getReferences().size() == 0
        bulk.getCancelled()
    }

    def 'maxMinusOneRemoteException'() {
        given:
        def i = 0
        (BulkRoomBooking.MAX_REMOTE_ERRORS - 1)*roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> {
            i++
            if (i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
                throw new RemoteAccessException()
            } else {
                return new HashSet<>(Arrays.asList("ref1", "ref2"));
            }
        } >> {
             throw new RemoteAccessException()
        }
        roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >>
                new HashSet<>(Arrays.asList("ref1", "ref2"))


        when:
        for (i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1; i++) {
            bulk.processBooking()
        }
        bulk.processBooking()
        then:
        bulk.getReferences().size() == 2
        !bulk.getCancelled()
    }

    def 'remoteExceptionValueIsResetByHotelException'() {
        given:
        def i = 0
        roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> {
            i++
            if (i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
                throw new RemoteAccessException();
            } else if (i == BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
                throw new HotelException();
            } else if (i < 2 * BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
                throw new RemoteAccessException();
            } else {
                return new HashSet<>(Arrays.asList("ref1", "ref2"))
            }
        }

        when:
        for (i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1; i++) {
            bulk.processBooking()
        }
        bulk.processBooking()

        and:
        for (i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1; i++) {
            bulk.processBooking()
        }
        bulk.processBooking()

        then:
        bulk.getReferences().size() == 2
        !bulk.getCancelled()
    }
}

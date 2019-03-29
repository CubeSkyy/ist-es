package pt.ulisboa.tecnico.softeng.broker.domain;

import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit
import net.bytebuddy.implementation.bytecode.Throw;
import org.junit.Test;
import org.junit.runner.RunWith;
import pt.ulisboa.tecnico.softeng.broker.services.remote.*
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;

import static org.junit.Assert.*

class BulkRoomBookingGetRoomBookingData4TypeMethodSpockTest extends SpockRollbackTestAbstractClass {
    def bulk
    def broker
    def roomInterface


    def MAX_REMOTE_ERRORS = BulkRoomBooking.MAX_REMOTE_ERRORS

    @Override
    def populate4Test() {
        roomInterface = Mock(HotelInterface)
        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN,roomInterface,
                new TaxInterface(), new ActivityInterface(), new CarInterface(), new BankInterface())
        bulk = new BulkRoomBooking(broker, NUMBER_OF_BULK, BEGIN, END, NIF_AS_BUYER, CLIENT_IBAN)
        new Reference(bulk, REF_ONE)
        new Reference(bulk, REF_TWO)
    }

    def 'successSINGLE'() {
        when:
        roomInterface.getRoomBookingData(_ as String) >> {
            def roomBookingData = new RestRoomBookingData()
            roomBookingData.setRoomType(SINGLE)
            return roomBookingData
        }
        bulk.getRoomBookingData4Type(SINGLE)
        then:
        bulk.getReferences().size() == 1
    }

    def 'successDOUBLE'() {
        when:
        roomInterface.getRoomBookingData(_ as String) >>
                {
                    def roomBookingData = new RestRoomBookingData();
                    roomBookingData.setRoomType(DOUBLE);
                    return roomBookingData
                }
        bulk.getRoomBookingData4Type(DOUBLE)

        then:
        bulk.getReferences().size() == 1
    }

    def 'hotelException'() {
        given:
        2 * roomInterface.getRoomBookingData(_) >> { throw  new HotelException()}

        expect:
        bulk.getRoomBookingData4Type(DOUBLE) == null
        bulk.getReferences().size() == 2
    }

    def 'remoteException'() {
        given:
        2 * roomInterface.getRoomBookingData(_) >> {throw  new RemoteAccessException()}

        expect:
        bulk.getRoomBookingData4Type(DOUBLE) == null
        bulk.getReferences().size() == 2
    }

    def 'maxRemoteException'() {
        given:
        def i

        when:
        MAX_REMOTE_ERRORS * roomInterface.getRoomBookingData(_ as String) >> {throw  new RemoteAccessException()}


        then:
        for (i = 0; i < MAX_REMOTE_ERRORS / 2; i++) {
            bulk.getRoomBookingData4Type(DOUBLE) == null
        }

        bulk.getReferences().size() == 2
        bulk.getCancelled()
    }

    def 'maxMinusOneRemoteException'() {
        given:
        def i

        when:
        roomInterface.getRoomBookingData(_ as String) >> {
             i++
             if (i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
                 throw new RemoteAccessException()
             } else {
                 def roomBookingData = new RestRoomBookingData()
                 roomBookingData.setRoomType(DOUBLE)
                 return roomBookingData
             }
        }


        then:
        for (i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS / 2 - 1; i++) {
            bulk.getRoomBookingData4Type(DOUBLE) == null
        }

        when:
        bulk.getRoomBookingData4Type(DOUBLE)

        then:
        bulk.getReferences().size() == 1
    }

    def 'remoteExceptionValueIsResetBySuccess'() {
        given:
        def i = 0

        when:
            roomInterface.getRoomBookingData(_ as String) >> {
                i++
            if (i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
                throw new RemoteAccessException()
            } else if (this.i == BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
                def roomBookingData = new RestRoomBookingData()
                roomBookingData.setRoomType(DOUBLE)
                return roomBookingData
            } else {
                throw new RemoteAccessException()
            }
        }

        then:
        for (i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS / 2 - 1; i++) {
            bulk.getRoomBookingData4Type(DOUBLE) == null
        }

        when:
        bulk.getRoomBookingData4Type(DOUBLE)

        then:
        bulk.getReferences().size() == 1

        for (i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS / 2 - 1; i++) {
            bulk.getRoomBookingData4Type(DOUBLE) == null
        }
        !bulk.getCancelled()
    }

    def 'remoteExceptionValueIsResetByHotelException'(){
        given:
        def i = 0

        when:
        roomInterface.getRoomBookingData(_ as String) >> {
            i++
            if (i < MAX_REMOTE_ERRORS - 1) {
                throw new RemoteAccessException()
            } else if (i == MAX_REMOTE_ERRORS - 1) {
                throw new HotelException()
            } else {
                throw new RemoteAccessException()
            }
        }

        then:
        for (i = 0; i < MAX_REMOTE_ERRORS / 2 - 1; i++) {
            bulk.getRoomBookingData4Type(DOUBLE) == null
        }
        when:
        bulk.getRoomBookingData4Type(DOUBLE)
        then:
        bulk.getReferences().size() == 2
        !bulk.getCancelled()
    }
}
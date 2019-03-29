package pt.ulisboa.tecnico.softeng.broker.domain;

import mockit.Delegate;
import mockit.Expectations;
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

class BulkRoomBookingProcessBookingMethodTest extends SpockRollbackTestAbstractClass {
    def bulk
    def broker
    def roomInterface
    
    @Override
    def populate4Test() {
        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN,
                roomInterface, new TaxInterface(), new ActivityInterface(), new CarInterface(), new BankInterface())
        bulk = new BulkRoomBooking(broker, NUMBER_OF_BULK, this.BEGIN, this.END, NIF_AS_BUYER, IBAN_BUYER);
    }

    def 'success'() {
        given:
        roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,bulk.getId()) >> bulkBooking
        bulkBooking = new HashSet<>(Arrays.asList("ref1", "ref2"))

        bulk.processBooking()
        bulk.getReferences().size() == 2
    }

    @Test
    public void successTwice() {
        new Expectations() {
            {
                roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
                        BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
                this.result = new HashSet<>(Arrays.asList("ref1", "ref2"));
                this.result = new HashSet<>(Arrays.asList("ref3", "ref4"));
            }
        };

        this.bulk.processBooking();
        this.bulk.processBooking();

        assertEquals(2, this.bulk.getReferences().size());
    }

    @Test
    public void oneHotelException() {
        new Expectations() {
            {
                roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
                        BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
                this.result = new HotelException();
                this.result = new HashSet<>(Arrays.asList("ref1", "ref2"));
            }
        };

        this.bulk.processBooking();
        this.bulk.processBooking();

        assertEquals(2, this.bulk.getReferences().size());
        assertFalse(this.bulk.getCancelled());
    }

    @Test
    public void maxHotelException() {
        new Expectations() {
            {
                roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
                        BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
                this.result = new HotelException();
            }
        };

        this.bulk.processBooking();
        this.bulk.processBooking();
        this.bulk.processBooking();

        assertEquals(0, this.bulk.getReferences().size());
        assertTrue(this.bulk.getCancelled());
    }

    @Test
    public void maxMinusOneHotelException() {
        new Expectations() {
            {
                roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
                        BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
                this.result = new Delegate() {
                    int i = 0;

                    Set<String> delegate() {
                        this.i++;
                        if (this.i < BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1) {
                            throw new HotelException();
                        } else {
                            return new HashSet<>(Arrays.asList("ref1", "ref2"));
                        }
                    }
                };
            }
        };

        this.bulk.processBooking();
        this.bulk.processBooking();
        this.bulk.processBooking();

        assertEquals(2, this.bulk.getReferences().size());
        assertFalse(this.bulk.getCancelled());
    }

    @Test
    public void hotelExceptionValueIsResetByRemoteException() {
        new Expectations() {
            {
                roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
                        BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
                this.result = new Delegate() {
                    int i = 0;

                    Set<String> delegate() {
                        this.i++;
                        if (this.i < BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1) {
                            throw new HotelException();
                        } else if (this.i == BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1) {
                            throw new RemoteAccessException();
                        } else if (this.i < 2 * BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1) {
                            throw new HotelException();
                        } else {
                            return new HashSet<>(Arrays.asList("ref1", "ref2"));
                        }
                    }
                };
            }
        };

        this.bulk.processBooking()
        this.bulk.processBooking()
        this.bulk.processBooking()
        this.bulk.processBooking()
        this.bulk.processBooking()
        this.bulk.processBooking()

        assertEquals(2, bulk.getReferences().size())
        assertFalse(bulk.getCancelled())
    }

    @Test
    public void oneRemoteException() {
        new Expectations() {
            {
                roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
                        BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
                this.result = new RemoteAccessException();
                this.result = new HashSet<>(Arrays.asList("ref1", "ref2"));
            }
        };

        this.bulk.processBooking();
        this.bulk.processBooking();

        assertEquals(2, this.bulk.getReferences().size());
        assertFalse(this.bulk.getCancelled());
    }

    @Test
    public void maxRemoteException() {
        new Expectations() {
            {
                roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
                        BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
                this.result = new RemoteAccessException();
            }
        };

        for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS; i++) {
            this.bulk.processBooking();
        }
        this.bulk.processBooking();

        assertEquals(0, this.bulk.getReferences().size());
        assertTrue(this.bulk.getCancelled());
    }

    @Test
    def 'maxMinusOneRemoteException'() {
        new Expectations() {
            {
                roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
                        BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
                this.result = new Delegate() {
                    int i = 0;

                    Set<String> delegate() {
                        this.i++;
                        if (this.i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
                            throw new RemoteAccessException();
                        } else {
                            return new HashSet<>(Arrays.asList("ref1", "ref2"));
                        }
                    }
                };
                this.result = new RemoteAccessException();
                this.times = BulkRoomBooking.MAX_REMOTE_ERRORS - 1;

                roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
                        BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
                this.result = new HashSet<>(Arrays.asList("ref1", "ref2"));
            }
        };

        for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1; i++) {
            this.bulk.processBooking();
        }
        this.bulk.processBooking();

        assertEquals(2, this.bulk.getReferences().size());
        assertFalse(this.bulk.getCancelled());
    }

    @Test
    def 'remoteExceptionValueIsResetByHotelException'() {
        new Expectations() {
            {
                roomInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
                        BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
                this.result = new Delegate() {
                    int i = 0;

                    Set<String> delegate() {
                        this.i++;
                        if (this.i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
                            throw new RemoteAccessException();
                        } else if (this.i == BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
                            throw new HotelException();
                        } else if (this.i < 2 * BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
                            throw new RemoteAccessException();
                        } else {
                            return new HashSet<>(Arrays.asList("ref1", "ref2"));
                        }
                    }
                };
            }
        };

        for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1; i++) {
            this.bulk.processBooking();
        }
        this.bulk.processBooking();
        for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1; i++) {
            this.bulk.processBooking();
        }
        this.bulk.processBooking();

        assertEquals(2, this.bulk.getReferences().size());
        assertFalse(this.bulk.getCancelled());
    }
}
package pt.ulisboa.tecnico.softeng.broker.domain;

import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(JMockit.class)
public class BulkRoomBookingProcessBookingMethodTest extends RollbackTestAbstractClass {
    private BulkRoomBooking bulk;

    @Mocked private HotelInterface roomInterface;
    
    @Override
    public void populate4Test() {
        this.broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN);
        this.bulk = new BulkRoomBooking(this.broker, NUMBER_OF_BULK, this.BEGIN, this.END, NIF_AS_BUYER, IBAN_BUYER);
        this.bulk.setHotelInterface(roomInterface);
    }

    @Test
    public void success() {
        new Expectations() {
            {
                HotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
                        BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
                this.result = new HashSet<>(Arrays.asList("ref1", "ref2"));
            }
        };

        this.bulk.processBooking();

        assertEquals(2, this.bulk.getReferences().size());
    }

    @Test
    public void successTwice() {
        new Expectations() {
            {
                HotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
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
                HotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
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
                HotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
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
                HotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
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
                HotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
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

        this.bulk.processBooking();
        this.bulk.processBooking();
        this.bulk.processBooking();
        this.bulk.processBooking();
        this.bulk.processBooking();
        this.bulk.processBooking();

        assertEquals(2, this.bulk.getReferences().size());
        assertFalse(this.bulk.getCancelled());
    }

    @Test
    public void oneRemoteException() {
        new Expectations() {
            {
                HotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
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
                HotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
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
    public void maxMinusOneRemoteException() {
        new Expectations() {
            {
                HotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
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

                HotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
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
    public void remoteExceptionValueIsResetByHotelException() {
        new Expectations() {
            {
                HotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
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
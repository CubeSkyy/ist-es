package pt.ulisboa.tecnico.softeng.broker.domain;

import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;

import static org.junit.Assert.*;

@RunWith(JMockit.class)
public class BulkRoomBookingGetRoomBookingData4TypeMethodTest extends RollbackTestAbstractClass {
    private BulkRoomBooking bulk;

    @Mocked private HotelInterface roomInterface;
    
    @Override
    public void populate4Test() {
        this.broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN);
        this.bulk = new BulkRoomBooking(this.broker, NUMBER_OF_BULK, this.BEGIN, this.END, NIF_AS_BUYER, CLIENT_IBAN);
        this.broker.setHotelInterface(roomInterface);
        new Reference(this.bulk, REF_ONE);
        new Reference(this.bulk, REF_TWO);
    }

    @Test
    public void successSINGLE() {
        new Expectations() {
            {
                roomInterface.getRoomBookingData(this.anyString);
                this.result = new Delegate() {
                    RestRoomBookingData delegate() {
                        RestRoomBookingData roomBookingData = new RestRoomBookingData();
                        roomBookingData.setRoomType(SINGLE);
                        return roomBookingData;
                    }
                };
            }
        };

        this.bulk.getRoomBookingData4Type(SINGLE);

        assertEquals(1, this.bulk.getReferences().size());
    }

    @Test
    public void successDOUBLE() {
        new Expectations() {
            {
                roomInterface.getRoomBookingData(this.anyString);
                this.result = new Delegate() {
                    RestRoomBookingData delegate() {
                        RestRoomBookingData roomBookingData = new RestRoomBookingData();
                        roomBookingData.setRoomType(DOUBLE);
                        return roomBookingData;
                    }
                };
            }
        };

        this.bulk.getRoomBookingData4Type(DOUBLE);

        assertEquals(1, this.bulk.getReferences().size());
    }

    @Test
    public void hotelException() {
        new Expectations() {
            {
                roomInterface.getRoomBookingData(this.anyString);
                this.result = new HotelException();
                this.times = 2;
            }
        };

        assertNull(this.bulk.getRoomBookingData4Type(DOUBLE));

        assertEquals(2, this.bulk.getReferences().size());
    }

    @Test
    public void remoteException() {
        new Expectations() {
            {
                roomInterface.getRoomBookingData(this.anyString);
                this.result = new RemoteAccessException();
                this.times = 2;
            }
        };

        assertNull(this.bulk.getRoomBookingData4Type(DOUBLE));

        assertEquals(2, this.bulk.getReferences().size());
    }

    @Test
    public void maxRemoteException() {
        new Expectations() {
            {
                roomInterface.getRoomBookingData(this.anyString);
                this.result = new RemoteAccessException();
                this.times = BulkRoomBooking.MAX_REMOTE_ERRORS;
            }
        };

        for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS / 2; i++) {
            assertNull(this.bulk.getRoomBookingData4Type(DOUBLE));
        }

        assertEquals(2, this.bulk.getReferences().size());
        assertTrue(this.bulk.getCancelled());
    }

    @Test
    public void maxMinusOneRemoteException() {
        new Expectations() {
            {
                roomInterface.getRoomBookingData(this.anyString);
                this.result = new Delegate() {
                    int i = 0;

                    RestRoomBookingData delegate() {
                        this.i++;
                        if (this.i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
                            throw new RemoteAccessException();
                        } else {
                            RestRoomBookingData roomBookingData = new RestRoomBookingData();
                            roomBookingData.setRoomType(DOUBLE);
                            return roomBookingData;
                        }
                    }
                };
            }
        };

        for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS / 2 - 1; i++) {
            assertNull(this.bulk.getRoomBookingData4Type(DOUBLE));
        }
        this.bulk.getRoomBookingData4Type(DOUBLE);

        assertEquals(1, this.bulk.getReferences().size());
    }

    @Test
    public void remoteExceptionValueIsResetBySuccess() {
        new Expectations() {
            {
                roomInterface.getRoomBookingData(this.anyString);
                this.result = new Delegate() {
                    int i = 0;

                    RestRoomBookingData delegate() {
                        this.i++;
                        if (this.i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
                            throw new RemoteAccessException();
                        } else if (this.i == BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
                            RestRoomBookingData roomBookingData = new RestRoomBookingData();
                            roomBookingData.setRoomType(DOUBLE);
                            return roomBookingData;
                        } else {
                            throw new RemoteAccessException();
                        }
                    }
                };
            }
        };

        for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS / 2 - 1; i++) {
            assertNull(this.bulk.getRoomBookingData4Type(DOUBLE));
        }

        this.bulk.getRoomBookingData4Type(DOUBLE);

        assertEquals(1, this.bulk.getReferences().size());

        for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS / 2 - 1; i++) {
            assertNull(this.bulk.getRoomBookingData4Type(DOUBLE));
        }

        assertFalse(this.bulk.getCancelled());
    }

    @Test
    public void remoteExceptionValueIsResetByHotelException() {
        new Expectations() {
            {
                roomInterface.getRoomBookingData(this.anyString);
                this.result = new Delegate() {
                    int i = 0;

                    RestRoomBookingData delegate() {
                        this.i++;
                        if (this.i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
                            throw new RemoteAccessException();
                        } else if (this.i == BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
                            throw new HotelException();
                        } else {
                            throw new RemoteAccessException();
                        }
                    }
                };
            }
        };

        for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS / 2 - 1; i++) {
            assertNull(this.bulk.getRoomBookingData4Type(DOUBLE));
        }

        this.bulk.getRoomBookingData4Type(DOUBLE);

        assertEquals(2, this.bulk.getReferences().size());

        for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS / 2 - 1; i++) {
            assertNull(this.bulk.getRoomBookingData4Type(DOUBLE));
        }

        assertFalse(this.bulk.getCancelled());
    }

}
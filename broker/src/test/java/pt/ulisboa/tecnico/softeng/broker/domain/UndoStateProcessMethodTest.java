package pt.ulisboa.tecnico.softeng.broker.domain;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.ActivityException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.BankException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.CarException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.TaxException;

@RunWith(JMockit.class)
public class UndoStateProcessMethodTest extends RollbackTestAbstractClass {
    @Mocked
    private TaxInterface taxInterface;

    @Mocked
    private BankInterface bankInterface;

    @Mocked
    private HotelInterface roomInterface;
    @Mocked
    private CarInterface carInterface;

    @Mocked
    private ActivityInterface activityInterface;


    @Override
    public void populate4Test() {
        this.broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN,
                roomInterface, taxInterface, activityInterface, carInterface, bankInterface);
        this.client = new Client(this.broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE);
        this.adventure = new Adventure(this.broker, BEGIN, END, this.client, MARGIN);

        this.adventure.setState(State.UNDO);

    }

    @Test
    public void successRevertPayment() {
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
        new Expectations() {
            {
                bankInterface.cancelPayment(PAYMENT_CONFIRMATION);
                this.result = PAYMENT_CANCELLATION;
            }
        };

        this.adventure.process();

        Assert.assertEquals(State.CANCELLED, this.adventure.getState().getValue());
    }

    @Test
    public void failRevertPaymentBankException() {
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
        new Expectations() {
            {
                bankInterface.cancelPayment(PAYMENT_CONFIRMATION);
                this.result = new BankException();
            }
        };

        this.adventure.process();

        Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
    }

    @Test
    public void failRevertPaymentRemoteAccessException() {
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
        new Expectations() {
            {
                bankInterface.cancelPayment(PAYMENT_CONFIRMATION);
                this.result = new RemoteAccessException();
            }
        };

        this.adventure.process();

        Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
    }

    @Test
    public void successRevertActivity() {
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
        this.adventure.setPaymentCancellation(PAYMENT_CANCELLATION);
        this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
        new Expectations() {
            {
                activityInterface.cancelReservation(ACTIVITY_CONFIRMATION);
                this.result = ACTIVITY_CANCELLATION;
            }
        };

        this.adventure.process();

        Assert.assertEquals(State.CANCELLED, this.adventure.getState().getValue());
    }

    @Test
    public void failRevertActivityActivityException() {
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
        this.adventure.setPaymentCancellation(PAYMENT_CANCELLATION);
        this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
        new Expectations() {
            {
                activityInterface.cancelReservation(ACTIVITY_CONFIRMATION);
                this.result = new ActivityException();
            }
        };

        this.adventure.process();

        Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
    }

    @Test
    public void failRevertActivityRemoteException() {
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
        this.adventure.setPaymentCancellation(PAYMENT_CANCELLATION);
        this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
        new Expectations() {
            {
                activityInterface.cancelReservation(ACTIVITY_CONFIRMATION);
                this.result = new RemoteAccessException();
            }
        };

        this.adventure.process();

        Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
    }

    @Test
    public void successRevertRoomBooking() {
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
        this.adventure.setPaymentCancellation(PAYMENT_CANCELLATION);
        this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
        this.adventure.setActivityCancellation(ACTIVITY_CANCELLATION);
        this.adventure.setRoomConfirmation(ROOM_CONFIRMATION);
        new Expectations() {
            {
                roomInterface.cancelBooking(ROOM_CONFIRMATION);
                this.result = ROOM_CANCELLATION;
            }
        };

        this.adventure.process();

        Assert.assertEquals(State.CANCELLED, this.adventure.getState().getValue());
    }

    @Test
    public void successRevertRoomBookingHotelException() {
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
        this.adventure.setPaymentCancellation(PAYMENT_CANCELLATION);
        this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
        this.adventure.setActivityCancellation(ACTIVITY_CANCELLATION);
        this.adventure.setRoomConfirmation(ROOM_CONFIRMATION);
        new Expectations() {
            {
                roomInterface.cancelBooking(ROOM_CONFIRMATION);
                this.result = new HotelException();
            }
        };

        this.adventure.process();

        Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
    }

    @Test
    public void successRevertRoomBookingRemoteException() {
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
        this.adventure.setPaymentCancellation(PAYMENT_CANCELLATION);
        this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
        this.adventure.setActivityCancellation(ACTIVITY_CANCELLATION);
        this.adventure.setRoomConfirmation(ROOM_CONFIRMATION);
        new Expectations() {
            {
                roomInterface.cancelBooking(ROOM_CONFIRMATION);
                this.result = new RemoteAccessException();
            }
        };

        this.adventure.process();

        Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
    }

    @Test
    public void successRevertRentCar() {
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
        this.adventure.setPaymentCancellation(PAYMENT_CANCELLATION);
        this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
        this.adventure.setActivityCancellation(ACTIVITY_CANCELLATION);
        this.adventure.setRoomConfirmation(ROOM_CONFIRMATION);
        this.adventure.setRoomCancellation(ROOM_CANCELLATION);
        this.adventure.setRentingConfirmation(RENTING_CONFIRMATION);
        new Expectations() {
            {
                carInterface.cancelRenting(RENTING_CONFIRMATION);
                this.result = RENTING_CANCELLATION;
            }
        };

        this.adventure.process();

        Assert.assertEquals(State.CANCELLED, this.adventure.getState().getValue());
    }

    @Test
    public void failRevertRentCarCarException() {
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
        this.adventure.setPaymentCancellation(PAYMENT_CANCELLATION);
        this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
        this.adventure.setActivityCancellation(ACTIVITY_CANCELLATION);
        this.adventure.setRoomConfirmation(ROOM_CONFIRMATION);
        this.adventure.setRoomCancellation(ROOM_CANCELLATION);
        this.adventure.setRentingConfirmation(RENTING_CONFIRMATION);
        new Expectations() {
            {
                carInterface.cancelRenting(RENTING_CONFIRMATION);
                this.result = new CarException();
            }
        };

        this.adventure.process();

        Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
    }

    @Test
    public void failRevertRentCarRemoteException() {
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
        this.adventure.setPaymentCancellation(PAYMENT_CANCELLATION);
        this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
        this.adventure.setActivityCancellation(ACTIVITY_CANCELLATION);
        this.adventure.setRoomConfirmation(ROOM_CONFIRMATION);
        this.adventure.setRoomCancellation(ROOM_CANCELLATION);
        this.adventure.setRentingConfirmation(RENTING_CONFIRMATION);
        new Expectations() {
            {
                carInterface.cancelRenting(RENTING_CONFIRMATION);
                this.result = new RemoteAccessException();
            }
        };

        this.adventure.process();

        Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
    }

    @Test
    public void successCancelInvoice() {
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
        this.adventure.setPaymentCancellation(PAYMENT_CANCELLATION);
        this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
        this.adventure.setActivityCancellation(ACTIVITY_CANCELLATION);
        this.adventure.setRoomConfirmation(ROOM_CONFIRMATION);
        this.adventure.setRoomCancellation(ROOM_CANCELLATION);
        this.adventure.setRentingConfirmation(RENTING_CONFIRMATION);
        this.adventure.setRentingCancellation(RENTING_CONFIRMATION);
        this.adventure.setInvoiceReference(INVOICE_REFERENCE);
        new Expectations() {
            {
                taxInterface.cancelInvoice(INVOICE_REFERENCE);
            }
        };

        this.adventure.process();

        Assert.assertEquals(State.CANCELLED, this.adventure.getState().getValue());
    }

    @Test
    public void failCancelInvoiceTaxException() {
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
        this.adventure.setPaymentCancellation(PAYMENT_CANCELLATION);
        this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
        this.adventure.setActivityCancellation(ACTIVITY_CANCELLATION);
        this.adventure.setRoomConfirmation(ROOM_CONFIRMATION);
        this.adventure.setRoomCancellation(ROOM_CANCELLATION);
        this.adventure.setRentingConfirmation(RENTING_CONFIRMATION);
        this.adventure.setRentingCancellation(RENTING_CONFIRMATION);
        this.adventure.setInvoiceReference(INVOICE_REFERENCE);
        new Expectations() {
            {
                taxInterface.cancelInvoice(INVOICE_REFERENCE);
                this.result = new TaxException();
            }
        };

        this.adventure.process();

        Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
    }

    @Test
    public void failCancelInvoiceRemoteException() {
        this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
        this.adventure.setPaymentCancellation(PAYMENT_CANCELLATION);
        this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
        this.adventure.setActivityCancellation(ACTIVITY_CANCELLATION);
        this.adventure.setRoomConfirmation(ROOM_CONFIRMATION);
        this.adventure.setRoomCancellation(ROOM_CANCELLATION);
        this.adventure.setRentingConfirmation(RENTING_CANCELLATION);
        this.adventure.setRentingCancellation(RENTING_CANCELLATION);
        this.adventure.setInvoiceReference(INVOICE_REFERENCE);
        new Expectations() {
            {
                taxInterface.cancelInvoice(INVOICE_REFERENCE);
                this.result = new RemoteAccessException();
            }
        };

        this.adventure.process();

        Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
    }

}
package pt.ulisboa.tecnico.softeng.broker.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.services.remote.*;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;

@RunWith(JMockit.class)
public class BookRoomStateMethodTest extends RollbackTestAbstractClass {
	private RestRoomBookingData bookingData;

	@Mocked private HotelInterface hotelInterface;

	@Override
	public void populate4Test() {
		this.broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN,
				hotelInterface, new TaxInterface(), new ActivityInterface(), new CarInterface(), new BankInterface());
		this.client = new Client(this.broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE);
		this.adventure = new Adventure(this.broker, this.BEGIN, this.END, this.client, MARGIN);

		this.bookingData = new RestRoomBookingData();
		this.bookingData.setReference(ROOM_CONFIRMATION);
		this.bookingData.setPrice(80.0);
		this.adventure.setState(State.BOOK_ROOM);
	}

	@Test
	public void successBookRoom() {
		new Expectations() {
			{
				hotelInterface.reserveRoom((RestRoomBookingData) this.any);
				this.result = BookRoomStateMethodTest.this.bookingData;
			}
		};

		this.adventure.process();

		assertEquals(State.PROCESS_PAYMENT, this.adventure.getState().getValue());
	}

	@Test
	public void successBookRoomToRenting() {
		Adventure adv = new Adventure(this.broker, this.BEGIN, this.END, this.client, MARGIN, true);
		adv.setState(State.BOOK_ROOM);
		new Expectations() {
			{
				hotelInterface.reserveRoom((RestRoomBookingData) this.any);
				this.result = BookRoomStateMethodTest.this.bookingData;
			}
		};

		adv.process();

		assertEquals(State.RENT_VEHICLE, adv.getState().getValue());
	}

	@Test
	public void hotelException() {
		new Expectations() {
			{
				hotelInterface.reserveRoom((RestRoomBookingData) this.any);
				this.result = new HotelException();
			}
		};

		this.adventure.process();

		Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
	}

	@Test
	public void singleRemoteAccessException() {
		new Expectations() {
			{
				hotelInterface.reserveRoom((RestRoomBookingData) this.any);
				this.result = new RemoteAccessException();
			}
		};

		this.adventure.process();

		Assert.assertEquals(State.BOOK_ROOM, this.adventure.getState().getValue());
	}

	@Test
	public void maxRemoteAccessException() {
		new Expectations() {
			{
				hotelInterface.reserveRoom((RestRoomBookingData) this.any);
				this.result = new RemoteAccessException();
				this.times = BookRoomState.MAX_REMOTE_ERRORS;
			}
		};

		for (int i = 0; i < BookRoomState.MAX_REMOTE_ERRORS; i++) {
			this.adventure.process();
		}

		Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
	}

	@Test
	public void maxMinusOneRemoteAccessException() {
		new Expectations() {
			{
				hotelInterface.reserveRoom((RestRoomBookingData) this.any);
				this.result = new RemoteAccessException();
				this.times = BookRoomState.MAX_REMOTE_ERRORS - 1;
			}
		};

		for (int i = 0; i < BookRoomState.MAX_REMOTE_ERRORS - 1; i++) {
			this.adventure.process();
		}

		Assert.assertEquals(State.BOOK_ROOM, this.adventure.getState().getValue());
	}

	@Test
	public void fiveRemoteAccessExceptionOneSuccess() {
		new Expectations() {
			{
				hotelInterface.reserveRoom((RestRoomBookingData) this.any);
				this.result = new Delegate() {
					int i = 0;

					public RestRoomBookingData delegate() {
						if (this.i < 5) {
							this.i++;
							throw new RemoteAccessException();
						} else {
							return BookRoomStateMethodTest.this.bookingData;
						}
					}
				};
				this.times = 6;
			}
		};

		this.adventure.process();
		this.adventure.process();
		this.adventure.process();
		this.adventure.process();
		this.adventure.process();
		this.adventure.process();

		Assert.assertEquals(State.PROCESS_PAYMENT, this.adventure.getState().getValue());
	}

	@Test
	public void oneRemoteAccessExceptionOneHotelException() {
		new Expectations() {
			{
				hotelInterface.reserveRoom((RestRoomBookingData) this.any);
				this.result = new Delegate() {
					int i = 0;

					public String delegate() {
						if (this.i < 1) {
							this.i++;
							throw new RemoteAccessException();
						} else {
							throw new HotelException();
						}
					}
				};
				this.times = 2;
			}
		};

		this.adventure.process();
		this.adventure.process();

		Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
	}

}
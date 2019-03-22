package pt.ulisboa.tecnico.softeng.broker.domain;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestActivityBookingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.ActivityException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;

@RunWith(JMockit.class)
public class ReserveActivityStateProcessMethodTest extends RollbackTestAbstractClass {
	@Mocked
	private TaxInterface taxInterface;

    @Mocked
    private ActivityInterface activityInterface;

	private RestActivityBookingData bookingData;

	@Override
	public void populate4Test() {
		this.broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN);
		this.client = new Client(this.broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE);
		this.adventure = new Adventure(this.broker, this.BEGIN, this.END, this.client, MARGIN);
		this.bookingData = new RestActivityBookingData();
		this.bookingData.setReference(ACTIVITY_CONFIRMATION);
		this.bookingData.setPrice(76.78);

		this.adventure.setState(State.RESERVE_ACTIVITY);
        adventure.setTaxInterface(taxInterface);
        adventure.setActivityInterface(activityInterface);

	}

	@Test
	public void successNoBookRoom() {
		Adventure sameDayAdventure = new Adventure(this.broker, this.BEGIN, this.BEGIN, this.client, MARGIN);
		sameDayAdventure.setState(State.RESERVE_ACTIVITY);
        sameDayAdventure.setTaxInterface(taxInterface);
        sameDayAdventure.setActivityInterface(activityInterface);
		new Expectations() {
			{
				activityInterface.reserveActivity((RestActivityBookingData) this.any);
				this.result = ReserveActivityStateProcessMethodTest.this.bookingData;
			}
		};

		sameDayAdventure.process();

		Assert.assertEquals(State.PROCESS_PAYMENT, sameDayAdventure.getState().getValue());
	}

	@Test
	public void successToRentVehicle() {
		Adventure adv = new Adventure(this.broker, this.BEGIN, this.BEGIN, this.client, MARGIN, true);
		adv.setState(State.RESERVE_ACTIVITY);
        adv.setTaxInterface(taxInterface);
        adv.setActivityInterface(activityInterface);
		new Expectations() {
			{
				activityInterface.reserveActivity((RestActivityBookingData) this.any);
				this.result = ReserveActivityStateProcessMethodTest.this.bookingData;
			}
		};

		adv.process();

		Assert.assertEquals(State.RENT_VEHICLE, adv.getState().getValue());
	}

	@Test
	public void successBookRoom() {
		new Expectations() {
			{
				activityInterface.reserveActivity((RestActivityBookingData) this.any);
				this.result = ReserveActivityStateProcessMethodTest.this.bookingData;
			}
		};

		this.adventure.process();

		Assert.assertEquals(State.BOOK_ROOM, this.adventure.getState().getValue());
	}

	@Test
	public void activityException() {
		new Expectations() {
			{
				activityInterface.reserveActivity((RestActivityBookingData) this.any);
				this.result = new ActivityException();
			}
		};

		this.adventure.process();

		Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
	}

	@Test
	public void singleRemoteAccessException() {
		new Expectations() {
			{
				activityInterface.reserveActivity((RestActivityBookingData) this.any);
				this.result = new RemoteAccessException();
			}
		};


		this.adventure.process();

		Assert.assertEquals(State.RESERVE_ACTIVITY, this.adventure.getState().getValue());
	}

	@Test
	public void maxRemoteAccessException() {
		new Expectations() {
			{
				activityInterface.reserveActivity((RestActivityBookingData) this.any);
				this.result = new RemoteAccessException();
			}
		};

		this.adventure.process();
		this.adventure.process();
		this.adventure.process();
		this.adventure.process();
		this.adventure.process();

		Assert.assertEquals(State.UNDO, this.adventure.getState().getValue());
	}

	@Test
	public void maxMinusOneRemoteAccessException() {
		new Expectations() {
			{
				activityInterface.reserveActivity((RestActivityBookingData) this.any);
				this.result = new RemoteAccessException();
			}
		};

		this.adventure.process();
		this.adventure.process();
		this.adventure.process();
		this.adventure.process();

		Assert.assertEquals(State.RESERVE_ACTIVITY, this.adventure.getState().getValue());
	}

	@Test
	public void twoRemoteAccessExceptionOneSuccess() {
		new Expectations() {
			{
				activityInterface.reserveActivity((RestActivityBookingData) this.any);
				this.result = new Delegate() {
					int i = 0;

					public RestActivityBookingData delegate() {
						if (this.i < 2) {
							this.i++;
							throw new RemoteAccessException();
						} else {
							return ReserveActivityStateProcessMethodTest.this.bookingData;
						}
					}
				};
				this.times = 3;

			}
		};

		this.adventure.process();
		this.adventure.process();
		this.adventure.process();

		Assert.assertEquals(State.BOOK_ROOM, this.adventure.getState().getValue());
	}

	@Test
	public void oneRemoteAccessExceptionOneActivityException() {
		new Expectations() {
			{
				activityInterface.reserveActivity((RestActivityBookingData) this.any);
				this.result = new Delegate() {
					int i = 0;

					public String delegate() {
						if (this.i < 1) {
							this.i++;
							throw new RemoteAccessException();
						} else {
							throw new ActivityException();
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
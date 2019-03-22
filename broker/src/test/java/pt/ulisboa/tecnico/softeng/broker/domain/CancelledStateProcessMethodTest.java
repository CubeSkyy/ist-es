package pt.ulisboa.tecnico.softeng.broker.domain;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestBankOperationData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.BankException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;

@RunWith(JMockit.class)
public class CancelledStateProcessMethodTest extends RollbackTestAbstractClass {
	@Mocked
	private TaxInterface taxInterface;
	@Mocked
	private BankInterface bankInterface;
	@Mocked
	private ActivityInterface activityInterface;
	@Mocked
	private HotelInterface hotelInterface;
	@Mocked
	private CarInterface carInterface;


	@Override
	public void populate4Test() {
		this.broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN);
		this.client = new Client(this.broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE);
		this.adventure = new Adventure(this.broker, this.BEGIN, this.END, this.client, MARGIN);

		this.adventure.setState(State.CANCELLED);
		adventure.setActivityInterface(activityInterface);
		adventure.setCarInterface(carInterface);
		adventure.setHotelInterface(hotelInterface);
		adventure.setBankInterface(bankInterface);
		adventure.setTaxInterface(taxInterface);

	}

	@Test
	public void didNotPayed() {


		this.adventure.process();

		Assert.assertEquals(Adventure.State.CANCELLED, this.adventure.getState().getValue());

		new Verifications() {
			{
				bankInterface.getOperationData(this.anyString);
				this.times = 0;

				activityInterface.getActivityReservationData(this.anyString);
				this.times = 0;

				hotelInterface.getRoomBookingData(this.anyString);
				this.times = 0;
			}
		};
	}

	@Test
	public void cancelledPaymentFirstBankException() {
		this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
		this.adventure.setPaymentCancellation(PAYMENT_CANCELLATION);

		new Expectations() {
			{
				bankInterface.getOperationData(PAYMENT_CONFIRMATION);
				this.result = new BankException();
			}
		};

		this.adventure.process();

		Assert.assertEquals(Adventure.State.CANCELLED, this.adventure.getState().getValue());
	}

	@Test
	public void cancelledPaymentFirstRemoteAccessException() {

		this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
		this.adventure.setPaymentCancellation(PAYMENT_CANCELLATION);

		new Expectations() {
			{
				bankInterface.getOperationData(PAYMENT_CONFIRMATION);
				this.result = new RemoteAccessException();
			}
		};

		this.adventure.process();

		Assert.assertEquals(Adventure.State.CANCELLED, this.adventure.getState().getValue());
	}

	@Test
	public void cancelledPaymentSecondBankException() {
		this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
		this.adventure.setPaymentCancellation(PAYMENT_CANCELLATION);

		new Expectations() {
			{
				bankInterface.getOperationData(PAYMENT_CONFIRMATION);
				this.result = new RestBankOperationData();
				this.result = new BankException();
			}
		};

		this.adventure.process();

		Assert.assertEquals(Adventure.State.CANCELLED, this.adventure.getState().getValue());
	}

	@Test
	public void cancelledPaymentSecondRemoteAccessException() {

		this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
		this.adventure.setPaymentCancellation(PAYMENT_CANCELLATION);

		new Expectations() {
			{
				bankInterface.getOperationData(PAYMENT_CONFIRMATION);
				this.result = new RestBankOperationData();
				this.result = new RemoteAccessException();
			}
		};

		this.adventure.process();

		Assert.assertEquals(Adventure.State.CANCELLED, this.adventure.getState().getValue());
	}

	@Test
	public void cancelledPayment() {

		this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
		this.adventure.setPaymentCancellation(PAYMENT_CANCELLATION);

		new Expectations() {
			{
				bankInterface.getOperationData(PAYMENT_CONFIRMATION);

				bankInterface.getOperationData(PAYMENT_CANCELLATION);
			}
		};

		this.adventure.process();

		Assert.assertEquals(Adventure.State.CANCELLED, this.adventure.getState().getValue());
	}

	@Test
	public void cancelledActivity() {

		this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
		this.adventure.setPaymentCancellation(PAYMENT_CANCELLATION);
		this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
		this.adventure.setActivityCancellation(ACTIVITY_CANCELLATION);

		new Expectations() {
			{
				bankInterface.getOperationData(PAYMENT_CONFIRMATION);

				bankInterface.getOperationData(PAYMENT_CANCELLATION);

				activityInterface.getActivityReservationData(ACTIVITY_CANCELLATION);
			}
		};

		this.adventure.process();

		Assert.assertEquals(Adventure.State.CANCELLED, this.adventure.getState().getValue());
	}

	@Test
	public void cancelledRoom() {


		this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
		this.adventure.setPaymentCancellation(PAYMENT_CANCELLATION);
		this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
		this.adventure.setActivityCancellation(ACTIVITY_CANCELLATION);
		this.adventure.setRoomConfirmation(ROOM_CONFIRMATION);
		this.adventure.setRoomCancellation(ROOM_CANCELLATION);

		new Expectations() {
			{
				bankInterface.getOperationData(PAYMENT_CONFIRMATION);

				bankInterface.getOperationData(PAYMENT_CANCELLATION);

				activityInterface.getActivityReservationData(ACTIVITY_CANCELLATION);

				hotelInterface.getRoomBookingData(ROOM_CANCELLATION);
			}
		};

		this.adventure.process();

		Assert.assertEquals(Adventure.State.CANCELLED, this.adventure.getState().getValue());
	}

	@Test
	public void cancelledRenting() {

		this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
		this.adventure.setPaymentCancellation(PAYMENT_CANCELLATION);
		this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
		this.adventure.setActivityCancellation(ACTIVITY_CANCELLATION);
		this.adventure.setRentingConfirmation(RENTING_CONFIRMATION);
		this.adventure.setRentingCancellation(RENTING_CANCELLATION);

		new Expectations() {
			{
				bankInterface.getOperationData(PAYMENT_CONFIRMATION);
				bankInterface.getOperationData(PAYMENT_CANCELLATION);
				activityInterface.getActivityReservationData(ACTIVITY_CANCELLATION);
				carInterface.getRentingData(RENTING_CANCELLATION);
			}
		};

		this.adventure.process();

		Assert.assertEquals(Adventure.State.CANCELLED, this.adventure.getState().getValue());
	}

	@Test
	public void cancelledBookAndRenting() {

		this.adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
		this.adventure.setPaymentCancellation(PAYMENT_CANCELLATION);
		this.adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
		this.adventure.setActivityCancellation(ACTIVITY_CANCELLATION);
		this.adventure.setRoomConfirmation(ROOM_CONFIRMATION);
		this.adventure.setRoomCancellation(ROOM_CANCELLATION);
		this.adventure.setRentingConfirmation(RENTING_CONFIRMATION);
		this.adventure.setRentingCancellation(RENTING_CANCELLATION);

		new Expectations() {
			{
				bankInterface.getOperationData(PAYMENT_CONFIRMATION);
				bankInterface.getOperationData(PAYMENT_CANCELLATION);
				activityInterface.getActivityReservationData(ACTIVITY_CANCELLATION);
				hotelInterface.getRoomBookingData(ROOM_CANCELLATION);
				carInterface.getRentingData(RENTING_CANCELLATION);
			}
		};

		this.adventure.process();

		Assert.assertEquals(Adventure.State.CANCELLED, this.adventure.getState().getValue());
	}

}
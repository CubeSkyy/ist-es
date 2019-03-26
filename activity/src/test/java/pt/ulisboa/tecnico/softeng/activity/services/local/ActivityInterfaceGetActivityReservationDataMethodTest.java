package pt.ulisboa.tecnico.softeng.activity.services.local;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.joda.time.LocalDate;
import org.junit.Test;

import pt.ulisboa.tecnico.softeng.activity.domain.*;
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface;
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestActivityBookingData;

public class ActivityInterfaceGetActivityReservationDataMethodTest extends RollbackTestAbstractClass {
	private static final String NAME = "ExtremeAdventure";
	private static final String CODE = "XtremX";
	private final LocalDate begin = new LocalDate(2016, 12, 19);
	private final LocalDate end = new LocalDate(2016, 12, 21);
	private ActivityProvider provider;
	private ActivityOffer offer;
	private Booking booking;
	private RestActivityBookingData data;
	private ActivityInterface activityInterface;

	@Override
	public void populate4Test() {
		activityInterface = new ActivityInterface();
		TaxInterface taxInterface = new TaxInterface();
		BankInterface bankInterface = new BankInterface();
		Processor processor = new Processor(taxInterface, bankInterface);
		this.provider = new ActivityProvider(CODE, NAME, "NIF", "IBAN", processor);
		Activity activity = new Activity(this.provider, "Bush Walking", 18, 80, 3);

		this.offer = new ActivityOffer(activity, this.begin, this.end, 30);
	}

	@Test
	public void success() {
		this.booking = new Booking(this.provider, this.offer, "123456789", "IBAN");

		this.data = activityInterface.getActivityReservationData(this.booking.getReference());

		assertEquals(this.booking.getReference(), data.getReference());
		assertNull(data.getCancellation());
		assertEquals(NAME, data.getName());
		assertEquals(CODE, data.getCode());
		assertEquals(this.begin, data.getBegin());
		assertEquals(this.end, data.getEnd());
		assertNull(data.getCancellationDate());
	}

	@Test
	public void successCancelled() {
		this.booking = new Booking(this.provider, this.offer, "123456789", "IBAN");
		this.provider.getProcessor().submitBooking(this.booking);
		this.booking.cancel();
		this.data = activityInterface.getActivityReservationData(this.booking.getCancel());

		assertEquals(this.booking.getReference(), data.getReference());
		assertEquals(this.booking.getCancel(), data.getCancellation());
		assertEquals(NAME, data.getName());
		assertEquals(CODE, data.getCode());
		assertEquals(this.begin, data.getBegin());
		assertEquals(this.end, data.getEnd());
		assertNotNull(data.getCancellationDate());
	}

	@Test(expected = ActivityException.class)
	public void nullReference() {
		activityInterface.getActivityReservationData(null);
	}

	@Test(expected = ActivityException.class)
	public void emptyReference() {
		activityInterface.getActivityReservationData("");
	}

	@Test(expected = ActivityException.class)
	public void notExistsReference() {
		activityInterface.getActivityReservationData("XPTO");
	}

}

package pt.ulisboa.tecnico.softeng.activity.domain;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface;
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestBankOperationData;
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestInvoiceData;

@RunWith(JMockit.class)
public class ActivityOfferHasVacancyMethodTest extends RollbackTestAbstractClass {
	private static final String IBAN = "IBAN";
	private static final String NIF = "123456789";
	private ActivityProvider provider;
	private ActivityOffer offer;

	@Mocked
	private TaxInterface taxInterface;
	@Mocked
	private BankInterface bankInterface;

	@Override
	public void populate4Test() {
		taxInterface = new TaxInterface();
		bankInterface = new BankInterface();
		Processor processor = new Processor(taxInterface, bankInterface);
		this.provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", IBAN, processor);
		Activity activity = new Activity(this.provider, "Bush Walking", 18, 80, 3);

		LocalDate begin = new LocalDate(2016, 12, 19);
		LocalDate end = new LocalDate(2016, 12, 21);

		this.offer = new ActivityOffer(activity, begin, end, 30);
	}

	@Test
	public void success() {
		new Booking(this.provider, this.offer, NIF, IBAN);

		Assert.assertTrue(this.offer.hasVacancy());
	}

	@Test
	public void bookingIsFull() {
		new Booking(this.provider, this.offer, NIF, IBAN);
		new Booking(this.provider, this.offer, NIF, IBAN);
		new Booking(this.provider, this.offer, NIF, IBAN);

		Assert.assertFalse(this.offer.hasVacancy());
	}

	@Test
	public void bookingIsFullMinusOne() {
		new Booking(this.provider, this.offer, NIF, IBAN);
		new Booking(this.provider, this.offer, NIF, IBAN);

		Assert.assertTrue(this.offer.hasVacancy());
	}

	@Test
	public void hasCancelledBookings() {
		new Expectations() {
			{
				bankInterface.processPayment((RestBankOperationData) this.any);

				taxInterface.submitInvoice((RestInvoiceData) this.any);
			}
		};
		this.provider.getProcessor().submitBooking(new Booking(this.provider, this.offer, NIF, IBAN));
		this.provider.getProcessor().submitBooking(new Booking(this.provider, this.offer, NIF, IBAN));
		Booking booking = new Booking(this.provider, this.offer, NIF, IBAN);
		this.provider.getProcessor().submitBooking(booking);

		booking.cancel();

		Assert.assertTrue(this.offer.hasVacancy());
	}

	public void hasCancelledBookingsButFull() {
		new Expectations() {
			{
				bankInterface.processPayment((RestBankOperationData) this.any);

				taxInterface.submitInvoice((RestInvoiceData) this.any);
			}
		};
		this.provider.getProcessor().submitBooking(new Booking(this.provider, this.offer, NIF, IBAN));
		this.provider.getProcessor().submitBooking(new Booking(this.provider, this.offer, NIF, IBAN));
		Booking booking = new Booking(this.provider, this.offer, NIF, IBAN);
		this.provider.getProcessor().submitBooking(booking);
		booking.cancel();
		booking = new Booking(this.provider, this.offer, NIF, IBAN);
		this.provider.getProcessor().submitBooking(booking);

		Assert.assertFalse(this.offer.hasVacancy());
	}

}

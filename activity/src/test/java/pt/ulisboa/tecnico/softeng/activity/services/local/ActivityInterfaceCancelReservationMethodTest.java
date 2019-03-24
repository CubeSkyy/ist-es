package pt.ulisboa.tecnico.softeng.activity.services.local;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import pt.ulisboa.tecnico.softeng.activity.domain.*;
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface;
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestBankOperationData;
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestInvoiceData;

@RunWith(JMockit.class)
public class ActivityInterfaceCancelReservationMethodTest extends RollbackTestAbstractClass {
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
		this.provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", IBAN);
		Activity activity = new Activity(this.provider, "Bush Walking", 18, 80, 3);

		LocalDate begin = new LocalDate(2016, 12, 19);
		LocalDate end = new LocalDate(2016, 12, 21);
		this.offer = new ActivityOffer(activity, begin, end, 30);
		this.provider.setBankInterface(bankInterface);
		this.provider.setTaxInterface(taxInterface);
	}

	public void success() {
		new Expectations() {
			{
				bankInterface.processPayment((RestBankOperationData) this.any);

				taxInterface.submitInvoice((RestInvoiceData) this.any);
			}
		};

		Booking booking = new Booking(this.provider, this.offer, NIF, IBAN);
		this.provider.getProcessor().submitBooking(booking);

		String cancel = ActivityInterface.cancelReservation(booking.getReference());

		assertTrue(booking.isCancelled());
		assertEquals(cancel, booking.getCancel());
	}

	@Test(expected = ActivityException.class)
	public void doesNotExist() {
		new Expectations() {
			{
				bankInterface.processPayment((RestBankOperationData) this.any);

				taxInterface.submitInvoice((RestInvoiceData) this.any);
			}
		};

		this.provider.getProcessor().submitBooking(new Booking(this.provider, this.offer, NIF, IBAN));

		ActivityInterface.cancelReservation("XPTO");
	}

}

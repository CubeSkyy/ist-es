package pt.ulisboa.tecnico.softeng.activity.services.local;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import pt.ulisboa.tecnico.softeng.activity.domain.*;
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface;
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestActivityBookingData;
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestBankOperationData;
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestInvoiceData;

@RunWith(JMockit.class)
public class ActivityInterfaceReserveActivityMethodTest extends RollbackTestAbstractClass {
	private static final String IBAN = "IBAN";
	private static final String NIF = "123456789";
	private static final int MIN_AGE = 18;
	private static final int MAX_AGE = 50;
	private static final int CAPACITY = 30;

	private static ActivityProvider provider1;
	private static ActivityProvider provider2;

	@Mocked
	private TaxInterface taxInterface;
	@Mocked
	private BankInterface bankInterface;

	private ActivityInterface activityInterface;
	private Processor processor;

	@Override
	public void populate4Test() {
		activityInterface = new ActivityInterface();

		processor = new Processor(taxInterface, bankInterface);
		provider1 = new ActivityProvider("XtremX", "Adventure++", "NIF", IBAN, processor);

		processor = new Processor(taxInterface, bankInterface);
		provider2 = new ActivityProvider("Walker", "Sky", "NIF2", IBAN, processor);
	}

	@Test
	public void reserveAcitivity() {
		new Expectations() {
			{
				bankInterface.processPayment((RestBankOperationData) this.any);

				taxInterface.submitInvoice((RestInvoiceData) this.any);
			}
		};

		Activity activity = new Activity(provider1, "XtremX", MIN_AGE, MAX_AGE, CAPACITY);
		new ActivityOffer(activity, new LocalDate(2018, 02, 19), new LocalDate(2018, 12, 20), 30);

		RestActivityBookingData activityBookingData = new RestActivityBookingData();
		activityBookingData.setAge(20);
		activityBookingData.setBegin(new LocalDate(2018, 02, 19));
		activityBookingData.setEnd(new LocalDate(2018, 12, 20));
		activityBookingData.setIban(IBAN);
		activityBookingData.setNif(NIF);

		RestActivityBookingData bookingData = activityInterface.reserveActivity(activityBookingData);

		Assert.assertTrue(bookingData != null);
		Assert.assertTrue(bookingData.getReference().startsWith("XtremX"));
	}

	@Test(expected = ActivityException.class)
	public void reserveAcitivityNoOption() {
		RestActivityBookingData activityBookingData = new RestActivityBookingData();
		activityBookingData.setAge(20);
		activityBookingData.setBegin(new LocalDate(2018, 02, 19));
		activityBookingData.setEnd(new LocalDate(2018, 12, 20));
		activityBookingData.setIban(IBAN);
		activityBookingData.setNif(NIF);

		RestActivityBookingData bookingData = activityInterface.reserveActivity(activityBookingData);
	}

}
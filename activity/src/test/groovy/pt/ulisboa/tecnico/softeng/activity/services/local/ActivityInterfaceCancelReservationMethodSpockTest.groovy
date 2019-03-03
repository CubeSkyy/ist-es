package pt.ulisboa.tecnico.softeng.activity.services.local;

import org.joda.time.LocalDate;
import spock.lang.*;


import mockit.Expectations;
import mockit.Mocked;
import pt.ulisboa.tecnico.softeng.activity.domain.Activity;
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityOffer;
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityProvider;
import pt.ulisboa.tecnico.softeng.activity.domain.Booking;
import pt.ulisboa.tecnico.softeng.activity.domain.RollbackSpockTestAbstractClass;
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface;
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestBankOperationData;
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestInvoiceData;

public class ActivityInterfaceCancelReservationMethodSpockTest extends RollbackSpockTestAbstractClass {
	def IBAN = "IBAN";
	def NIF = "123456789";
	def provider;
	def offer;

	@Override
	def populate4Test() {
		this.provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", IBAN);
		def activity = new Activity(this.provider, "Bush Walking", 18, 80, 3);

		def begin = new LocalDate(2016, 12, 19);
		def end = new LocalDate(2016, 12, 21);
		this.offer = new ActivityOffer(activity, begin, end, 30);
	}

	def 'success'(@Mocked TaxInterface taxInterface, @Mocked BankInterface bankInterface) {
		given:
		new Expectations() {
			{
				BankInterface.processPayment((RestBankOperationData) this.any);

				TaxInterface.submitInvoice((RestInvoiceData) this.any);
			}
		};

		def booking = new Booking(this.provider, this.offer, NIF, IBAN)
		this.provider.getProcessor().submitBooking(booking)

		def cancel = ActivityInterface.cancelReservation(booking.getReference())

		expect:
		booking.isCancelled()
		cancel == booking.getCancel()
	}

	def doesNotExist(@Mocked TaxInterface taxInterface, @Mocked BankInterface bankInterface) {
		given:
		new Expectations() {
			{
				BankInterface.processPayment((RestBankOperationData) this.any);

				TaxInterface.submitInvoice((RestInvoiceData) this.any);
			}
		};

		this.provider.getProcessor().submitBooking(new Booking(this.provider, this.offer, NIF, IBAN))

		ActivityInterface.cancelReservation("XPTO")

		expect:
		thrown(ActivityException)
	}

}

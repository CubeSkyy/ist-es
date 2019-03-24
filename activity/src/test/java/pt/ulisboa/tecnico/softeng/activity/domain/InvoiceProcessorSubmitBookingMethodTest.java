package pt.ulisboa.tecnico.softeng.activity.domain;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Delegate;
import mockit.Expectations;
import mockit.FullVerifications;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface;
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestBankOperationData;
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestInvoiceData;
import pt.ulisboa.tecnico.softeng.activity.services.remote.exceptions.BankException;
import pt.ulisboa.tecnico.softeng.activity.services.remote.exceptions.RemoteAccessException;
import pt.ulisboa.tecnico.softeng.activity.services.remote.exceptions.TaxException;

@RunWith(JMockit.class)
public class InvoiceProcessorSubmitBookingMethodTest extends RollbackTestAbstractClass {
	private static final String CANCEL_PAYMENT_REFERENCE = "CancelPaymentReference";
	private static final String INVOICE_REFERENCE = "InvoiceReference";
	private static final String PAYMENT_REFERENCE = "PaymentReference";
	private static final int AMOUNT = 30;
	private static final String IBAN = "IBAN";
	private static final String NIF = "123456789";
	private ActivityProvider provider;
	private ActivityOffer offer;
	private Booking booking;

	@Mocked
	private TaxInterface taxInterface;

	@Mocked
	private BankInterface bankInterface;

	@Override
	public void populate4Test() {
		this.provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", IBAN);
		Activity activity = new Activity(this.provider, "Bush Walking", 18, 80, 10);

		LocalDate begin = new LocalDate(2016, 12, 19);
		LocalDate end = new LocalDate(2016, 12, 21);
		this.offer = new ActivityOffer(activity, begin, end, AMOUNT);
		this.booking = new Booking(this.provider, this.offer, NIF, IBAN);

		this.provider.setBankInterface(bankInterface);
		this.provider.setTaxInterface(taxInterface);
	}

	@Test
	public void success() {
		new Expectations() {
			{
				bankInterface.processPayment((RestBankOperationData) this.any);
				taxInterface.submitInvoice((RestInvoiceData) this.any);
			}
		};

		this.provider.getProcessor().submitBooking(this.booking);

		new FullVerifications() {
			{
			}
		};
	}

	@Test
	public void oneTaxFailureOnSubmitInvoice() {
		new Expectations() {
			{
				bankInterface.processPayment((RestBankOperationData) this.any);
				this.result = PAYMENT_REFERENCE;
				taxInterface.submitInvoice((RestInvoiceData) this.any);
				this.result = new TaxException();
				this.result = INVOICE_REFERENCE;
			}
		};

		this.provider.getProcessor().submitBooking(this.booking);
		this.provider.getProcessor().submitBooking(new Booking(this.provider, this.offer, NIF, IBAN));

		new FullVerifications(taxInterface) {
			{
				taxInterface.submitInvoice((RestInvoiceData) this.any);
				this.times = 3;
			}
		};
	}

	@Test
	public void oneRemoteFailureOnSubmitInvoice() {
		new Expectations() {
			{
				bankInterface.processPayment((RestBankOperationData) this.any);
				this.result = PAYMENT_REFERENCE;
				taxInterface.submitInvoice((RestInvoiceData) this.any);
				this.result = new RemoteAccessException();
				this.result = INVOICE_REFERENCE;
			}
		};

		this.provider.getProcessor().submitBooking(this.booking);
		this.provider.getProcessor().submitBooking(new Booking(this.provider, this.offer, NIF, IBAN));

		new FullVerifications(taxInterface) {
			{
				taxInterface.submitInvoice((RestInvoiceData) this.any);
				this.times = 3;
			}
		};
	}

	@Test
	public void oneBankFailureOnProcessPayment() {
		new Expectations() {
			{
				bankInterface.processPayment((RestBankOperationData) this.any);
				this.result = new BankException();
				this.result = PAYMENT_REFERENCE;
				taxInterface.submitInvoice((RestInvoiceData) this.any);
				this.result = INVOICE_REFERENCE;
			}
		};

		this.provider.getProcessor().submitBooking(this.booking);
		this.provider.getProcessor().submitBooking(new Booking(this.provider, this.offer, NIF, IBAN));

		new FullVerifications(bankInterface) {
			{
				bankInterface.processPayment((RestBankOperationData) this.any);
				this.times = 3;
			}
		};
	}

	@Test
	public void oneRemoteFailureOnProcessPayment() {
		new Expectations() {
			{
				bankInterface.processPayment((RestBankOperationData) this.any);
				this.result = new RemoteAccessException();
				this.result = PAYMENT_REFERENCE;
				taxInterface.submitInvoice((RestInvoiceData) this.any);
				this.result = INVOICE_REFERENCE;
			}
		};

		this.provider.getProcessor().submitBooking(this.booking);
		this.provider.getProcessor().submitBooking(new Booking(this.provider, this.offer, NIF, IBAN));

		new FullVerifications(bankInterface) {
			{
				bankInterface.processPayment((RestBankOperationData) this.any);
				this.times = 3;
			}
		};
	}

	@Test
	public void successCancel() {
		new Expectations() {
			{
				taxInterface.submitInvoice((RestInvoiceData) this.any);
				bankInterface.processPayment((RestBankOperationData) this.any);

				taxInterface.cancelInvoice(this.anyString);
				bankInterface.cancelPayment(this.anyString);
			}
		};

		this.provider.getProcessor().submitBooking(this.booking);
		this.booking.cancel();

		new FullVerifications() {
			{
			}
		};
	}

	@Test
	public void oneBankExceptionOnCancelPayment() {
		new Expectations() {
			{
				taxInterface.submitInvoice((RestInvoiceData) this.any);
				bankInterface.processPayment((RestBankOperationData) this.any);

				bankInterface.cancelPayment(this.anyString);
				this.result = new BankException();
				this.result = CANCEL_PAYMENT_REFERENCE;
				taxInterface.cancelInvoice(this.anyString);
			}
		};

		this.provider.getProcessor().submitBooking(this.booking);
		this.booking.cancel();
		this.provider.getProcessor().submitBooking(new Booking(this.provider, this.offer, NIF, IBAN));

		new FullVerifications(bankInterface) {
			{
				bankInterface.cancelPayment(this.anyString);
				this.times = 2;
			}
		};
	}

	@Test
	public void oneRemoteExceptionOnCancelPayment() {
		new Expectations() {
			{
				taxInterface.submitInvoice((RestInvoiceData) this.any);
				bankInterface.processPayment((RestBankOperationData) this.any);

				bankInterface.cancelPayment(this.anyString);
				this.result = new RemoteAccessException();
				this.result = CANCEL_PAYMENT_REFERENCE;
				taxInterface.cancelInvoice(this.anyString);
			}
		};

		this.provider.getProcessor().submitBooking(this.booking);
		this.booking.cancel();
		this.provider.getProcessor().submitBooking(new Booking(this.provider, this.offer, NIF, IBAN));

		new FullVerifications(bankInterface) {
			{
				bankInterface.cancelPayment(this.anyString);
				this.times = 2;
			}
		};
	}

	@Test
	public void oneTaxExceptionOnCancelInvoice() {
		new Expectations() {
			{
				bankInterface.processPayment((RestBankOperationData) this.any);
				taxInterface.submitInvoice((RestInvoiceData) this.any);
				bankInterface.cancelPayment(this.anyString);
				this.result = CANCEL_PAYMENT_REFERENCE;
				taxInterface.cancelInvoice(this.anyString);
				this.result = new Delegate() {
					int i = 0;

					public void delegate() {
						if (this.i < 1) {
							this.i++;
							throw new TaxException();
						}
					}
				};
			}
		};

		this.provider.getProcessor().submitBooking(this.booking);
		this.booking.cancel();
		this.provider.getProcessor().submitBooking(new Booking(this.provider, this.offer, NIF, IBAN));

		new FullVerifications(taxInterface) {
			{
				taxInterface.cancelInvoice(this.anyString);
				this.times = 2;
			}
		};
	}

	@Test
	public void oneRemoteExceptionOnCancelInvoice() {
		new Expectations() {
			{
				bankInterface.processPayment((RestBankOperationData) this.any);
				taxInterface.submitInvoice((RestInvoiceData) this.any);

				bankInterface.cancelPayment(this.anyString);
				this.result = CANCEL_PAYMENT_REFERENCE;
				taxInterface.cancelInvoice(this.anyString);
				this.result = new Delegate() {
					int i = 0;

					public void delegate() {
						if (this.i < 1) {
							this.i++;
							throw new RemoteAccessException();
						}
					}
				};
			}
		};

		this.provider.getProcessor().submitBooking(this.booking);
		this.booking.cancel();
		this.provider.getProcessor().submitBooking(new Booking(this.provider, this.offer, NIF, IBAN));

		new FullVerifications(taxInterface) {
			{
				taxInterface.cancelInvoice(this.anyString);
				this.times = 2;
			}
		};
	}

}

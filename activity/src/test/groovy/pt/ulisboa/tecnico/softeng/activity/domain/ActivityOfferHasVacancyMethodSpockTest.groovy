package pt.ulisboa.tecnico.softeng.activity.domain;

import org.joda.time.LocalDate;
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface;
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestBankOperationData;
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestInvoiceData;

class ActivityOfferHasVacancyMethodSpockTest extends SpockRollbackTestAbstractClass {
	def IBAN = "IBAN"
	def NIF = "123456789"
	def provider
	def offer
	def bankInterface
	def taxInterface

	@Override
	def populate4Test() {
		provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", IBAN)

		taxInterface = Mock(TaxInterface)
		bankInterface = Mock(BankInterface)

		provider.setBankInterface(bankInterface)
		provider.setTaxInterface(taxInterface)

		def activity = new Activity(provider, "Bush Walking", 18, 80, 3)

		def begin = new LocalDate(2016, 12, 19)
		def end = new LocalDate(2016, 12, 21)

		offer = new ActivityOffer(activity, begin, end, 30)

	}

	def 'success'() {
		when:
		new Booking(provider, offer, NIF, IBAN);

		then:
		offer.hasVacancy()
	}

	def 'booking is full'() {
		when:
		new Booking(provider, offer, NIF, IBAN);
		new Booking(provider, offer, NIF, IBAN);
		new Booking(provider, offer, NIF, IBAN);

		then:
		!offer.hasVacancy()
	}

	def 'booking is full minus one'() {
		when:
		new Booking(provider, offer, NIF, IBAN);
		new Booking(provider, offer, NIF, IBAN);

		then:
		offer.hasVacancy()
	}

	def 'has cancelled bookings'() {
		when:
		provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))
		provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))
		def booking = new Booking(provider, offer, NIF, IBAN)
		provider.getProcessor().submitBooking(booking)
		booking.cancel()

		then:
		offer.hasVacancy()
	}

	def 'has cancelled bookings but full'() {
		when:
		provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))
		provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))
		def booking = new Booking(provider, offer, NIF, IBAN)
		provider.getProcessor().submitBooking(booking)
		booking.cancel()
		booking = new Booking(provider, offer, NIF, IBAN)
		provider.getProcessor().submitBooking(booking)

		then:
		!offer.hasVacancy()
	}

}

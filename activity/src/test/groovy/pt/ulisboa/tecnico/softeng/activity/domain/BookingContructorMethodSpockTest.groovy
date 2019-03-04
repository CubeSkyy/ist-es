package pt.ulisboa.tecnico.softeng.activity.domain;

import org.joda.time.LocalDate;
import spock.lang.*;

import mockit.FullVerifications;
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;

class BookingContructorMethodSpockTest extends RollbackSpockTestAbstractClass {
	@Shared def provider;
	@Shared def offer;
	static final AMOUNT = 30;
	@Shared def IBAN = "IBAN";
	@Shared def NIF = "123456789";

	@Override
	def populate4Test() {
		this.provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", IBAN);
		def activity = new Activity(this.provider, "Bush Walking", 18, 80, 3);

		def begin = new LocalDate(2016, 12, 19);
		def end = new LocalDate(2016, 12, 21);
		this.offer = new ActivityOffer(activity, begin, end, AMOUNT);
	}

	def 'success'() {
		given:
		def booking = new Booking(this.provider, this.offer, NIF, IBAN);

		expect:
		booking.getReference().startsWith(this.provider.getCode())
		booking.getReference().length() > ActivityProvider.CODE_SIZE
		1 == this.offer.getNumberActiveOfBookings()
		NIF == booking.getBuyerNif()
		IBAN == booking.getIban()
		AMOUNT == booking.getAmount()
	}

	@Unroll('Booking: #provider, #offer, #nif, #iban')
	def 'exceptions'() {
		when:
		new Booking(provider, offer, nif, iban)

		then:
		thrown(ActivityException)

		where:
		provider      | offer      | nif   | iban
		null          | this.offer | NIF   | IBAN
		this.provider | null       | NIF   | IBAN
		null          | this.offer | null  | IBAN
		this.provider | null       | NIF   | ' '
		null          | this.offer | NIF   | null
		this.provider | null       | ' '   | IBAN
	}

	def 'booking equal capacity'() {
		when:
		new Booking(this.provider, this.offer, NIF, IBAN);
		new Booking(this.provider, this.offer, NIF, IBAN);
		new Booking(this.provider, this.offer, NIF, IBAN);

		then:
		try {
			new Booking(this.provider, this.offer, NIF, IBAN);
			fail();
		} catch (ActivityException ae) {
			3 == this.offer.getNumberActiveOfBookings()
		}
	}

	def 'booking equal capacity but has cancelled'() {
		when:
		new Booking(this.provider, this.offer, NIF, IBAN);
		new Booking(this.provider, this.offer, NIF, IBAN);
		def booking = new Booking(this.provider, this.offer, NIF, IBAN);
		booking.cancel();
		new Booking(this.provider, this.offer, NIF, IBAN);

		then:
		3 == this.offer.getNumberActiveOfBookings()
	}

}

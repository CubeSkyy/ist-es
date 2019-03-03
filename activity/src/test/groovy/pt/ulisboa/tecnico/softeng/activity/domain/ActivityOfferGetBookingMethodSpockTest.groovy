package pt.ulisboa.tecnico.softeng.activity.domain;

import org.joda.time.LocalDate;
import spock.lang.*;

class ActivityOfferGetBookingMethodSpockTest extends RollbackSpockTestAbstractClass {
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

  def 'sucess'() {
    given:
    def booking = new Booking(this.provider, this.offer, NIF, IBAN)

    expect:
    this.offer.getBooking(booking.getReference()) == booking
  }

  def 'sucess cancelled'() {
    given:
    def booking = new Booking(this.provider, this.offer, NIF, IBAN)

    when:
    booking.cancel()

    then:
    this.offer.getBooking(booking.getCancel()) == booking
  }

  def 'does not exist'() {
    given:
    def booking = new Booking(this.provider, this.offer, NIF, IBAN)

    expect:
    this.offer.getBooking('XPTO') == null
  }
}

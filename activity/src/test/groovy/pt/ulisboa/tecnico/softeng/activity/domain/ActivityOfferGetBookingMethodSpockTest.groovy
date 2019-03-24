package pt.ulisboa.tecnico.softeng.activity.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface

class ActivityOfferGetBookingMethodSpockTest extends SpockRollbackTestAbstractClass {
	def IBAN = 'IBAN'
	def NIF = '123456789'
	def provider
	def offer


	@Override
	def populate4Test() {
		provider = new ActivityProvider('XtremX','ExtremeAdventure','NIF',IBAN)

		def activity = new Activity(this.provider,'Bush Walking',18,80,3)
		def begin = new LocalDate(2016,12,19)
		def end = new LocalDate(2016,12,21)

		offer = new ActivityOffer(activity,begin,end,30)
	}

	def 'success'() {
		when:
		def booking = new Booking(provider, offer, NIF, IBAN)

		then: 'the booking can be obtained through the confirmation reference'
		offer.getBooking(booking.getReference()) == booking
	}

	def 'success cancelled'() {
		given:
		def booking = new Booking(provider, offer, NIF, IBAN)
		BankInterface bankInterface = new BankInterface();
		provider.setBankInterface(bankInterface)

		when:
		booking.cancel()

		then: 'the booking can be obtained through the cancellation reference'
		offer.getBooking(booking.getCancel()) == booking
	}

	def 'does not exist'() {
		expect:
		offer.getBooking('XPTO') == null
	}
}

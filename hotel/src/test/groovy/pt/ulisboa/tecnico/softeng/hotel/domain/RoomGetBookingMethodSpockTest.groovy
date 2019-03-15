package pt.ulisboa.tecnico.softeng.hotel.domain

import org.joda.time.LocalDate

class RoomGetBookingMethodSpockTest extends RollbackSpockTestAbstractClass {
	def arrival = new LocalDate(2016, 12, 19)
	def departure = new LocalDate(2016, 12, 24)
	def hotel
	def room
	def booking
	def taxInterface
	def bankInterface
	def NIF_BUYER = "123456789"
	def IBAN_BUYER = "IBAN_BUYER"

	@Override
	def populate4Test() {
		hotel = new Hotel("XPTO123", "Lisboa", "NIF", "IBAN", 20.0, 30.0)
		room = new Room(hotel, "01", Type.SINGLE)
		booking = room.reserve(Type.SINGLE, arrival, departure, NIF_BUYER, IBAN_BUYER)
	}

	def 'success'() {
		room.getBooking(this.booking.getReference()) == booking
	}

	def 'successCancelled'() {
		booking.cancel()
		room.getBooking(booking.getCancellation()) == booking
	}

	def 'doesNotExist'() {
		room.getBooking("XPTO") == null
	}
}
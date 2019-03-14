package pt.ulisboa.tecnico.softeng.hotel.domain

import spock.lang.Shared
import spock.lang.Unroll;

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException

class RoomReserveMethodSpockTest extends RollbackSpockTestAbstractClass {
	@Shared
	def arrival = new LocalDate(2016, 12, 19)
	@Shared
	def departure = new LocalDate(2016, 12, 24)

	def room
	@Shared
	def NIF_HOTEL = "123456700"
	@Shared
	def NIF_BUYER = "123456789"
	@Shared
	def IBAN_BUYER = "IBAN_BUYER"

	def taxInterface
	def bankInterface

	@Override
	def populate4Test() {
		def hotel = new Hotel("XPTO123", "Lisboa", NIF_HOTEL, "IBAN", 20.0, 30.0);
		room = new Room(hotel, "01", Type.SINGLE)
	}

	def 'success'() {
		def booking = room.reserve(Type.SINGLE, arrival, departure, NIF_BUYER, IBAN_BUYER)

		room.getBookingSet().size() == 1
		booking.getReference().length() > 0
		booking.getArrival() == arrival
		booking.getDeparture() == departure
	}

	@Unroll('Room reservation: #_type, #_arrival, #_departure, #_NIF_BUYER, #_IBAN_BUYER')
	def 'exceptions'(){
		when:
		room.reserve(_type, _arrival,_departure,_NIF_BUYER,_IBAN_BUYER)
		then:
		thrown(HotelException)

		where:
		_type	 	| _arrival	| _departure| _NIF_BUYER| _IBAN_BUYER
		Type.DOUBLE	| arrival	| departure	| NIF_BUYER	| IBAN_BUYER
		null		| arrival	| departure	| NIF_BUYER	| IBAN_BUYER
		Type.SINGLE	| null		| departure	| NIF_BUYER	| IBAN_BUYER
		Type.SINGLE	| arrival	| null		| NIF_BUYER	| IBAN_BUYER
	}


	def 'allConflict'() {
		given:
		room.reserve(Type.SINGLE, arrival, departure, NIF_BUYER, IBAN_BUYER)

		when:
		room.reserve(Type.SINGLE, arrival, departure, NIF_BUYER, IBAN_BUYER)

	 	then:
		thrown(HotelException)
		room.getBookingSet().size() == 1
	}

}

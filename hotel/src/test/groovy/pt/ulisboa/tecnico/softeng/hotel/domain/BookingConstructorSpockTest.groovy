package pt.ulisboa.tecnico.softeng.hotel.domain

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import spock.lang.Unroll


class BookingConstructorSpockTest extends RollbackSpockTestAbstractClass {
    def ARRIVAL = new LocalDate(2016, 12, 19)
    def DEPARTURE = new LocalDate(2016, 12, 21)
    def ROOM_PRICE = 20.0
    def NIF_BUYER = "123456789"
    def IBAN_BUYER = "IBAN_BUYER"
    def room

    @Override
    def populate4Test() {
        Hotel hotel = new Hotel("XPTO123", "Londres", "NIF", "IBAN", 20.0, 30.0)
        room = new Room(hotel, "01", Room.Type.SINGLE)
    }


    def 'success'() {
        given:
        def booking = new Booking(room, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER)

        expect:
        booking.getReference().startsWith(room.getHotel().getCode())
        booking.getReference().length() > Hotel.CODE_SIZE
        ARRIVAL == booking.getArrival()
        DEPARTURE == booking.getDeparture()
        ROOM_PRICE * 2 == booking.getPrice()
    }

    def 'null room' () {
        when:
        new Booking(null, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER)

        then:
        thrown(HotelException)
    }

    def 'null arrival' () {
        when:
        new Booking(room, null, DEPARTURE, NIF_BUYER, IBAN_BUYER)

        then:
        thrown(HotelException)
    }

    def 'null departure'(){
        when:
        new Booking(room, ARRIVAL, null, NIF_BUYER, IBAN_BUYER)

        then:
        thrown(HotelException)
    }

    def 'departure before arrival'(){
        when:
        new Booking(room, ARRIVAL, ARRIVAL.minusDays(1), NIF_BUYER, IBAN_BUYER)

        then:
        thrown(HotelException)
    }

    def 'arrival equal departure'() {
        expect:
        new Booking(room, ARRIVAL, ARRIVAL, NIF_BUYER, IBAN_BUYER)
    }
}

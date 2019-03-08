package pt.ulisboa.tecnico.softeng.hotel.domain

import org.joda.time.LocalDate
import spock.lang.*

import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface


class BookingConflictMethodSpockTest extends RollbackSpockTestAbstractClass {

    @Shared def arrival = new LocalDate(2016, 12, 19)
    @Shared def departure = new LocalDate(2016, 12, 24)
    @Shared def booking
    def NIF_HOTEL = "123456700"
    def NIF_BUYER = "123456789"
    def IBAN_BUYER = "IBAN_BUYER"

    def taxInterface
    def bankInterface

    @Override
    def populate4Test() {
        Hotel hotel = new Hotel("XPTO123", "Londres", NIF_HOTEL, "IBAN", 20.0, 30.0);
        Room room = new Room(hotel, "01", Room.Type.SINGLE);

        booking = new Booking(room, arrival, departure, NIF_BUYER, IBAN_BUYER);
    }

    def 'arguments are consistent'() {
        expect:
        booking.conflict(new LocalDate(2016, 12, 9), new LocalDate(2016, 12, 15)) == false
    }

    @Unroll('Booking: #arri, #depar')
    def 'no conflict because it is cancelled' () {
        when:
        booking.cancel()

        then: 'booking no conflict'
        !booking.conflict(booking.getArrival(), booking.getDeparture())
    }

    def 'arguments are inconsistent'() {
        when: 'throws an exception if arguments are inconsistens'
        booking.conflict(new LocalDate(2016, 12, 15), new LocalDate(2016, 12, 9))

        then:
        thrown(HotelException)
    }

    def 'arguments same day' () {
        expect:
        booking.conflict(new LocalDate(2016, 12, 9), new LocalDate(2016, 12, 9))
    }

    def 'arrival and departure are before booked' () {
        expect:
        !booking.conflict(arrival.minusDays(10), arrival.minusDays(4))
    }

    def 'arrival and departure are before booked but departure is equal to booked arrival'() {
        expect:
        !booking.conflict(arrival.minusDays(10), arrival)
    }

    def 'arrival and departure are after booked'() {
        expect:
        !booking.conflict(departure.plusDays(4), departure.plusDays(10))
    }

    def 'arrival and departure are after booked but arrival is equal to booked departure'(){
        expect:
        !booking.conflict(departure, departure.plusDays(10))
    }

    def 'arrival is before booked arrival and departure is after booked departure' () {
        expect:
        booking.conflict(arrival.minusDays(4), departure.plusDays(4))
    }

    def 'arrival is equal booked arrival and departure is after booked departure' () {
        expect:
        booking.conflict(arrival, departure.plusDays(4))
    }

    def 'arrival is before booked arrival and departure is equal booked departure' () {
        expect:
        booking.conflict(arrival.minusDays(4), departure)
    }

    def 'arrival is before booked arrival and departure is between booked' () {
        expect:
        booking.conflict(arrival.minusDays(4), departure.minusDays(3))
    }

    def 'arrival is between booked and departure is after booked departure'() {
        expect:
        booking.conflict(arrival.plusDays(3), departure.plusDays(6))
    }


}
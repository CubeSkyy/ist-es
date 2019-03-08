package pt.ulisboa.tecnico.softeng.hotel.domain

import static org.junit.Assert.assertNull

import org.joda.time.LocalDate
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith


import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface


class HotelHasVacancyMethodSpockTest extends RollbackSpockTestAbstractClass {
    def arrival = new LocalDate(2016, 12, 19)
    def departure = new LocalDate(2016, 12, 21)
    def hotel
    def room
    def NIF_HOTEL = "123456700"
    def NIF_BUYER = "123456789"
    def IBAN_BUYER = "IBAN_BUYER"

    @Override
    def populate4Test() {
        hotel = new Hotel("XPTO123", "Paris", NIF_HOTEL, "IBAN", 20.0, 30.0)
        room = new Room(hotel, "01", Type.DOUBLE)
    }


    def 'has vacancy'() {
        given:
        def room = hotel.hasVacancy(Type.DOUBLE, arrival, departure)

        expect:
        room != null
        room.getNumber() == "01"
    }


    def 'no vacancy'() {
        given:
        room.reserve(Type.DOUBLE, arrival, departure, NIF_BUYER, IBAN_BUYER)

        expect:
        hotel.hasVacancy(Type.DOUBLE, this.arrival, this.departure) == null
    }


    def 'no vacancy empty room set'() {
        given:
        def otherHotel = new Hotel("XPTO124", "Paris Germain", "NIF2", "IBAN", 25.0, 35.0)

        expect:
        otherHotel.hasVacancy(Type.DOUBLE, arrival, departure) == null
    }

    def 'null type'() {
        when:
        hotel.hasVacancy(null, arrival, departure)

        then:
        thrown(HotelException)
    }

    def 'null arrival'() {
        when:
        hotel.hasVacancy(Type.DOUBLE, null, departure)

        then:
        thrown(HotelException)
    }

    def 'null departure'() {
        when:
        hotel.hasVacancy(Type.DOUBLE, arrival, null)

        then:
        thrown(HotelException)
    }

}
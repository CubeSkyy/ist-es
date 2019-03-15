package pt.ulisboa.tecnico.softeng.hotel.services.local

import spock.lang.Unroll

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull

import org.joda.time.LocalDate
import org.junit.Test
import org.junit.runner.RunWith

import mockit.Mocked
import mockit.integration.junit4.JMockit
import pt.ulisboa.tecnico.softeng.hotel.domain.Booking
import pt.ulisboa.tecnico.softeng.hotel.domain.Hotel
import pt.ulisboa.tecnico.softeng.hotel.domain.RollbackSpockTestAbstractClass
import pt.ulisboa.tecnico.softeng.hotel.domain.Room
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.hotel.services.remote.dataobjects.RestRoomBookingData


class HotelInterfaceGetRoomBookingDataMethodSpockTest extends RollbackSpockTestAbstractClass {
    def arrival = new LocalDate(2016, 12, 19)
    def departure = new LocalDate(2016, 12, 24)
    def hotel
    def room
    def booking
    def NIF_HOTEL = "123456700"
    def NIF_BUYER = "123456789"
    def IBAN_BUYER = "IBAN_BUYER"

    def taxInterface
    def bankInterface

    @Override
    def populate4Test() {
        hotel = new Hotel("XPTO123", "Lisboa", NIF_HOTEL, "IBAN", 20.0, 30.0)
        room = new Room(hotel, "01", Type.SINGLE)
        booking = room.reserve(Type.SINGLE, arrival, departure, NIF_BUYER, IBAN_BUYER)
    }


    def 'success' () {
        given:
        def data = HotelInterface.getRoomBookingData(booking.getReference())

        expect:
        booking.getReference() == data.getReference()
        data.getCancellation() == null
        data.getCancellationDate() == null
        data.getHotelName() == hotel.getName()
        data.getHotelCode() == hotel.getCode()
        data.getRoomNumber() == room.getNumber()
        data.getRoomType() == room.getType().name()
        data.getArrival() == booking.getArrival()
        data.getDeparture() == booking.getDeparture()
        data.getPrice() == booking.getPrice()
    }


    def ' success cancellation'() {
        given: 'a cancellation on a date'
        booking.cancel()
        def data = HotelInterface.getRoomBookingData(booking.getCancellation())

        expect:
        data.getReference() == booking.getReference()
        data.getCancellation() == booking.getCancellation()
        data.getCancellationDate() == booking.getCancellationDate()
        data.getHotelName() == hotel.getName()
        data.getHotelCode() == hotel.getCode()
        data.getRoomNumber() == room.getNumber()
        data.getRoomType() == room.getType().name()
        data.getArrival() == booking.getArrival()
        data.getDeparture() == booking.getDeparture()
        data.getPrice() == booking.getPrice()
    }


    def 'null reference'() {
        when:
        HotelInterface.getRoomBookingData(null)

        then:
        thrown(HotelException)
    }

    def 'empty reference'() {
        when:
        HotelInterface.getRoomBookingData("")

        then:
        thrown(HotelException)
    }

    def 'reference does not exist'() {
        when:
        HotelInterface.getRoomBookingData("XPTO")

        then:
        thrown(HotelException)
    }
}
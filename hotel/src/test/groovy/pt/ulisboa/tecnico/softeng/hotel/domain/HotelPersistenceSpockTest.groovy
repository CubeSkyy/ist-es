package pt.ulisboa.tecnico.softeng.hotel.domain

import static org.junit.Assert.assertArrayEquals
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

import java.util.ArrayList
import java.util.List

import org.joda.time.LocalDate
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import pt.ist.fenixframework.Atomic
import pt.ist.fenixframework.Atomic.TxMode
import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type

abstract class HotelPersistenceSpockTest extends RollbackSpockTestAbstractClass{
    def logger = LoggerFactory.getLogger(HotelPersistenceSpockTest.class)

    def HOTEL_NIF = "123456789"
    def HOTEL_IBAN = "IBAN"
    def HOTEL_NAME = "Berlin Plaza"
    def HOTEL_CODE = "H123456"
    def ROOM_NUMBER = "01"
    def CLIENT_NIF = "123458789"
    def CLIENT_IBAN = "IBANC"

    def arrival = new LocalDate(2017, 12, 15)
    def departure = new LocalDate(2017, 12, 19)

    def setUpThings() {
        for (def hotel : FenixFramework.getDomainRoot().getHotelSet()) {
            hotel.delete();
        }
    }

    def whenCreateInDatabase() {
        def hotel = new Hotel(HOTEL_CODE, HOTEL_NAME, HOTEL_NIF, HOTEL_IBAN, 10.0, 20.0)
        new Room(hotel, ROOM_NUMBER, Type.DOUBLE)
        hotel.reserveRoom(Type.DOUBLE, arrival, departure, CLIENT_NIF, CLIENT_IBAN, "adventureId")
    }

    def thenAssert() {
        assert FenixFramework.getDomainRoot().getHotelSet().size() == 1

        List<Hotel> hotels = new ArrayList<>(FenixFramework.getDomainRoot().getHotelSet())
        def hotel = hotels.get(0)

        hotel.getName() == HOTEL_NAME
        hotel.getCode() == HOTEL_CODE
        hotel.getIban() == HOTEL_IBAN
        hotel.getNif() == HOTEL_NIF
        hotel.getPriceSingle() == 10.0
        hotel.getPriceDouble() == 20.0
        hotel.getRoomSet().size() == 1
        def processor = hotel.getProcessor()
        processor != null
        processor.getBookingSet().size() == 1


        List<Room> rooms = new ArrayList<>(hotel.getRoomSet())
        def room = rooms.get(0)

        room.getNumber() == ROOM_NUMBER
        room.getType() == Type.DOUBLE
        room.getBookingSet().size() == 1


        List<Booking> bookings = new ArrayList<>(room.getBookingSet())
        def booking = bookings.get(0)

        booking.getReference() != null
        booking.getArrival() == arrival
        booking.getDeparture() == departure
        booking.getBuyerIban() == CLIENT_IBAN
        booking.getBuyerNif() == CLIENT_NIF
        booking.getProviderNif() == HOTEL_NIF
        booking.getPrice() == 80.0
        booking.getRoom() == room
        booking.getTime() != null
        booking.getProcessor() != null
    }


    def deleteFromDatabase() {
        for (def h : FenixFramework.getDomainRoot().getHotelSet()) {
            h.delete()
        }
    }


}
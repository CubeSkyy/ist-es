package pt.ulisboa.tecnico.softeng.hotel.services.local


import org.joda.time.LocalDate

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.hotel.domain.Hotel
import pt.ulisboa.tecnico.softeng.hotel.domain.RollbackSpockTestAbstractClass
import pt.ulisboa.tecnico.softeng.hotel.domain.Room
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import pt.ulisboa.tecnico.softeng.hotel.services.remote.dataobjects.RestRoomBookingData

class HotelInterfaceReserveRoomMethodSpockTest extends RollbackSpockTestAbstractClass {

    def arrival = new LocalDate(2016, 12, 19)
    def departure = new LocalDate(2016, 12, 24)
    def hotel
    def NIF_HOTEL = "123456789"
    def NIF_BUYER = "123456700"
    def IBAN_BUYER = "IBAN_CUSTOMER"
    def IBAN_HOTEL = "IBAN_HOTEL"
    def ADVENTURE_ID = "ADVENTURE_ID"

    @Override
    def populate4Test() {
        hotel = new Hotel("XPTO123", "Lisboa", NIF_HOTEL, IBAN_HOTEL, 20.0, 30.0)
        new Room(hotel, "01", Room.Type.SINGLE)
    }

    def 'success'() {
        given:
        def bookingData = new RestRoomBookingData("SINGLE", arrival, departure, NIF_BUYER,
                IBAN_BUYER, ADVENTURE_ID)
        when:
        bookingData = HotelInterface.reserveRoom(bookingData)

        then:
        (bookingData.getReference() != null)
        (bookingData.getReference().startsWith("XPTO123"))
    }


    def 'noHotels'() {
        given:
        for (def h: FenixFramework.getDomainRoot().getHotelSet().stream() ) {
            h.delete()
        }

        when:
        def bookingData = new RestRoomBookingData("SINGLE", arrival, departure, NIF_BUYER,
                IBAN_BUYER, ADVENTURE_ID)
        HotelInterface.reserveRoom(bookingData)

        then:
        thrown(HotelException)
    }

    def 'noVacancy'() {
        given:
        def bookingData = new RestRoomBookingData("SINGLE", arrival, new LocalDate(2016, 12, 25),
                NIF_BUYER, IBAN_BUYER, ADVENTURE_ID)

        HotelInterface.reserveRoom(bookingData)


        when:
        bookingData = new RestRoomBookingData("SINGLE", this.arrival, new LocalDate(2016, 12, 25), NIF_BUYER,
                IBAN_BUYER, ADVENTURE_ID + "1")
        HotelInterface.reserveRoom(bookingData)


        then:
        thrown(HotelException)
    }

    def 'noRooms'() {
        given:
        for(def r: hotel.getRoomSet().stream()) {
            r.delete()
        }

        when:
        def bookingData = new RestRoomBookingData("SINGLE", arrival, new LocalDate(2016, 12, 25),
                NIF_BUYER, IBAN_BUYER, ADVENTURE_ID)
        HotelInterface.reserveRoom(bookingData)

        then:
        thrown(HotelException)
    }
}
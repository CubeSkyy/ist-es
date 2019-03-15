package pt.ulisboa.tecnico.softeng.hotel.domain

import org.junit.Assert
import org.junit.Test

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException

class HotelConstructorSpockTest extends RollbackSpockTestAbstractClass {
    def IBAN = "IBAN"
    def NIF = "NIF"

    def HOTEL_NAME = "Londres"
    def HOTEL_CODE = "XPTO123"

    def PRICE_SINGLE = 20.0
    def PRICE_DOUBLE = 30.0

    @Override
    def populate4Test() {
    }

    def 'success'() {
        given:
        def hotel = new Hotel(HOTEL_CODE, HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

        expect:
        HOTEL_NAME == hotel.getName()
        (hotel.getCode().length() == Hotel.CODE_SIZE)
        hotel.getRoomSet().size() == 0
        FenixFramework.getDomainRoot().getHotelSet().size() == 1
        PRICE_SINGLE == hotel.getPrice(Room.Type.SINGLE)
        PRICE_DOUBLE == hotel.getPrice(Room.Type.DOUBLE)
    }

    def 'null code'() {
        when:
        new Hotel(null, HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

        then:
        thrown(HotelException)
    }

    def 'blank code'() {
        when:
        new Hotel("      ", HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

        then:
        thrown(HotelException)
    }

    def 'empty code'() {
        when:
        new  Hotel("", HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

        then:
        thrown(HotelException)
    }

    def 'null name'() {
        when:
        new Hotel(HOTEL_CODE, null, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

        then:
        thrown(HotelException)
    }

    def 'blank name'() {
        when:
        new Hotel(HOTEL_CODE, "  ", NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

        then:
        thrown(HotelException)
    }

    def 'empty name'() {
        when:
        new Hotel(HOTEL_CODE, "", NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

        then:
        thrown(HotelException)
    }

    def 'code size less'() {
        when:
        new Hotel("123456", HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

        then:
        thrown(HotelException)
    }

    def 'code size more'() {
        when:
        new Hotel("12345678", HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

        then:
        thrown(HotelException)
    }

    def 'code not unique'() {
        when:
        new Hotel(HOTEL_CODE, HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)
        new Hotel(HOTEL_CODE, HOTEL_NAME + " City", NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

        then:
        thrown(HotelException)
    }

    def 'nif not unique'() {
        when:
        new Hotel(HOTEL_CODE, HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)
        new Hotel(HOTEL_CODE + "_new", HOTEL_NAME + "_New", NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

        then:
        thrown(HotelException)
    }

    def 'negative price single'() {
        when:
        new Hotel(HOTEL_CODE, HOTEL_NAME, NIF, IBAN, -1.0, PRICE_DOUBLE)

        then:
        thrown(HotelException)
    }

    def 'negative price double'() {
        when:
        new Hotel(HOTEL_CODE, HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, -1.0)

        then:
        thrown(HotelException)
    }



}
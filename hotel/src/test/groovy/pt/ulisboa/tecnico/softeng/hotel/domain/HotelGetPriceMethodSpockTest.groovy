package pt.ulisboa.tecnico.softeng.hotel.domain

import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException

class HotelGetPriceMethodSpockTest extends RollbackSpockTestAbstractClass {
    def hotel
    def priceSingle = 20.0
    def priceDouble = 30.0

    @Override
    def populate4Test() {
        hotel = new Hotel("XPTO123", "Lisboa", "NIF", "IBAN", priceSingle, priceDouble)
    }


    def 'success single' () {
        expect:
        priceSingle == hotel.getPrice(Room.Type.SINGLE)
        priceDouble == hotel.getPrice(Room.Type.DOUBLE)
    }

    def 'null type' () {
        when:
        hotel.getPrice(null)

        then:
        thrown(HotelException)
    }
}
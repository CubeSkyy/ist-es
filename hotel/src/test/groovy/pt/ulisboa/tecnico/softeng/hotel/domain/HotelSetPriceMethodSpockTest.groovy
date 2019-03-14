package pt.ulisboa.tecnico.softeng.hotel.domain


import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException

class HotelSetPriceMethodSpockTest extends RollbackSpockTestAbstractClass {
    def hotel
    def price = 25.0

    @Override
    def populate4Test() {
        hotel = new Hotel("XPTO123", "Lisboa", "NIF", "IBAN", price + 5.0, price + 10.0)
    }

    def 'success single' () {
        given:
        hotel.setPrice(Room.Type.SINGLE, this.price)

        expect:
        price == hotel.getPrice(Room.Type.SINGLE)
    }

    def 'success double' () {
        given:
        hotel.setPrice(Room.Type.DOUBLE, this.price)

        expect:
        price == hotel.getPrice(Room.Type.DOUBLE)
    }

    def 'negative price single' ()  {
        when:
        hotel.setPrice(Room.Type.SINGLE, -1.0)

        then:
        thrown(HotelException)
    }

    def 'negative price double' ()  {
        when:
        hotel.setPrice(Room.Type.DOUBLE, -1.0)

        then:
        thrown(HotelException)
    }
}
package pt.ulisboa.tecnico.softeng.hotel.domain

import spock.lang.Shared
import spock.lang.Unroll

import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException

class RoomConstructorMethodSpockTest extends RollbackSpockTestAbstractClass {
    @Shared
    def hotel

    @Override
    def populate4Test() {
        hotel = new Hotel("XPTO123", "Lisboa", "NIF", "IBAN", 20.0, 30.0)
    }

    def 'success'() {
        given:
        def room = new Room(hotel, "01", Type.DOUBLE)

        expect:
        room.getHotel() == hotel
        room.getNumber() == "01"
        room.getType() == Type.DOUBLE
        hotel.getRoomSet().size() == 1
    }

    @Unroll('Room creation: #_hotel, #_number, #_type')
    def 'exceptions'() {
        when:
        new Room(_hotel, _number, _type)

        then:
        thrown(HotelException)

        where:
        _hotel | _number | _type
        null   | "01"    | Type.DOUBLE
        hotel  | null    | Type.DOUBLE
        hotel  | ""      | Type.DOUBLE
        hotel  | "     " | Type.DOUBLE
        hotel  | "JOSE"  | Type.DOUBLE
        hotel  | "01"    | null
    }

    def 'nonUniqueRoomNumber'() {
        given:
        new Room(hotel, "01", Type.SINGLE)
        when:
        new Room(hotel, "01", Type.DOUBLE)
        then:
        thrown(HotelException)
        hotel.getRoomSet().size() == 1

    }
}

package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException
import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface
import spock.lang.Unroll

class BrokerConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {

    @Override
    def populate4Test() {
    }

    def success() {
        when: 'a broker is created'
        def broker = new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN,
                new HotelInterface(), new TaxInterface(), new ActivityInterface(), new CarInterface(), new BankInterface())

        then: 'the attributes are correctly set'
        broker.getCode().equals(BROKER_CODE)
        broker.getName().equals(BROKER_NAME)
        broker.getAdventureSet().size() == 0
        FenixFramework.getDomainRoot().getBrokerSet().contains(broker)
    }

    @Unroll('#label: #broker, #name, #nif_seller, #nif_buyer, #iban')
    def 'invalid arguments'() {
        when: 'a broker is created'
        new Broker(broker, name, nif_seller, nif_buyer, iban, new HotelInterface(), new TaxInterface(),
                new ActivityInterface(), new CarInterface(), new BankInterface())

        then: 'an exception is thrown'
        thrown(BrokerException)
        FenixFramework.getDomainRoot().getBrokerSet().size() == 0

        where: 'the arguments are invalid'
        broker      | name        | nif_seller           | nif_buyer    | iban        | label
        null        | BROKER_NAME | BROKER_NIF_AS_SELLER | NIF_AS_BUYER | BROKER_IBAN | 'null code'
        ""          | BROKER_NAME | BROKER_NIF_AS_SELLER | NIF_AS_BUYER | BROKER_IBAN | 'empty code'
        "   "       | BROKER_NAME | BROKER_NIF_AS_SELLER | NIF_AS_BUYER | BROKER_IBAN | 'blank code'
        BROKER_CODE | null        | BROKER_NIF_AS_SELLER | NIF_AS_BUYER | BROKER_IBAN | 'null name'
        BROKER_CODE | ""          | BROKER_NIF_AS_SELLER | NIF_AS_BUYER | BROKER_IBAN | 'empty name'
        BROKER_CODE | "    "      | BROKER_NIF_AS_SELLER | NIF_AS_BUYER | BROKER_IBAN | 'blank name'
        BROKER_CODE | BROKER_NAME | null                 | NIF_AS_BUYER | BROKER_IBAN | 'null seller nif'
        BROKER_CODE | BROKER_NAME | ""                   | NIF_AS_BUYER | BROKER_IBAN | 'empty seller nif'
        BROKER_CODE | BROKER_NAME | "    "               | NIF_AS_BUYER | BROKER_IBAN | 'blank seller nif'
        BROKER_CODE | BROKER_NAME | BROKER_NIF_AS_SELLER | null         | BROKER_IBAN | 'null buyer nif'
        BROKER_CODE | BROKER_NAME | BROKER_NIF_AS_SELLER | ""           | BROKER_IBAN | 'empty buyer nif'
        BROKER_CODE | BROKER_NAME | BROKER_NIF_AS_SELLER | "  "         | BROKER_IBAN | 'blank buyer nif'
        BROKER_CODE | BROKER_NAME | BROKER_NIF_AS_SELLER | NIF_AS_BUYER | null        | 'null iban'
        BROKER_CODE | BROKER_NAME | BROKER_NIF_AS_SELLER | NIF_AS_BUYER | ""          | 'empty iban'
        BROKER_CODE | BROKER_NAME | BROKER_NIF_AS_SELLER | NIF_AS_BUYER | "   "       | 'blank iban'
    }

    @Unroll('duplicate #label')
    def 'unique verifications'() {
        given: 'a broker'
        def broker = new Broker(code_one, BROKER_NAME, seller_nif_one, buyer_nif_one, BROKER_IBAN,
                new HotelInterface(), new TaxInterface(), new ActivityInterface(), new CarInterface(), new BankInterface())

        when: 'another broker is created'
        new Broker(code_two, BROKER_NAME, seller_nif_two, buyer_nif_two, BROKER_IBAN,
                new HotelInterface(), new TaxInterface(), new ActivityInterface(), new CarInterface(), new BankInterface())

        then: 'an exception is thrown'
        thrown(BrokerException)
        FenixFramework.getDomainRoot().getBrokerSet().contains(broker)


        where: 'because it violates a unique constraint'
        label          | code_one    | code_two    | seller_nif_one       | seller_nif_two       | buyer_nif_one | buyer_nif_two
        'code'         | BROKER_CODE | BROKER_CODE | BROKER_NIF_AS_SELLER | "012345678"          | NIF_AS_BUYER  | "098765432"
        'seller nif'   | BROKER_CODE | "BR02"      | BROKER_NIF_AS_SELLER | BROKER_NIF_AS_SELLER | NIF_AS_BUYER  | "098765432"
        'buyer nif'    | BROKER_CODE | "BR02"      | BROKER_NIF_AS_SELLER | "012345678"          | NIF_AS_BUYER  | NIF_AS_BUYER
        'snif == bnif' | BROKER_CODE | "BR02"      | BROKER_NIF_AS_SELLER | NIF_AS_BUYER         | NIF_AS_BUYER  | "098765432"
        'bnif == snif' | BROKER_CODE | "BR02"      | BROKER_NIF_AS_SELLER | "012345678"          | NIF_AS_BUYER  | BROKER_NIF_AS_SELLER
    }
}

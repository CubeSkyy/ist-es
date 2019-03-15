package pt.ulisboa.tecnico.softeng.broker.domain

import org.joda.time.LocalDate
import org.junit.Test

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException
import spock.lang.Shared
import spock.lang.Unroll

class BrokerConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {

    @Shared
    def BROKER_CODE = "BR01"
    @Shared
    def BROKER_NAME = "WeExplore"
    @Shared
    def BROKER_IBAN = "BROKER_IBAN"
    @Shared
    def NIF_AS_BUYER = "buyerNIF"
    @Shared
    def BROKER_NIF_AS_SELLER = "sellerNIF"
    @Shared
    def CLIENT_NIF = "123456789"
    @Shared
    def DRIVING_LICENSE = "IMT1234"
    @Shared
    def AGE = 20
    @Shared
    def MARGIN = 0.3
    @Shared
    def CLIENT_IBAN = "BK011234567"

    def begin = new LocalDate(2016, 12, 19)
    def end = new LocalDate(2016, 12, 21)
    def arrival = new LocalDate(2016, 12, 19)
    def departure = new LocalDate(2016, 12, 21)


    @Override
    def populate4Test() {
    }

    @Test
    def 'success'() {
        given:
        def broker = new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)

        expect:
        broker.getCode() == BROKER_CODE
        broker.getName() == BROKER_NAME
        broker.getAdventureSet().size() == 0
        FenixFramework.getDomainRoot().getBrokerSet().contains(broker)

    }

    @Unroll('Adventure creation: #_broker, #_broker_name, #_broker_nif_as_seller, #_nif_as_buyer, #_broker_iban')
    def 'exceptions'() {
        when:
            new Broker(_broker, _broker_name, _broker_nif_as_seller, _nif_as_buyer, _broker_iban)

        then:
            thrown(BrokerException)
            FenixFramework.getDomainRoot().getBrokerSet().size() == 0

        where:
        _broker | _broker_name | _broker_nif_as_seller | _nif_as_buyer | _broker_iban
        null    | BROKER_NAME | BROKER_NIF_AS_SELLER | NIF_AS_BUYER | BROKER_IBAN
        ""      | BROKER_NAME | BROKER_NIF_AS_SELLER | NIF_AS_BUYER | BROKER_IBAN
        "	"   | BROKER_NAME | BROKER_NIF_AS_SELLER | NIF_AS_BUYER | BROKER_IBAN
        BROKER_CODE | null  | BROKER_NIF_AS_SELLER | NIF_AS_BUYER | BROKER_IBAN
        BROKER_CODE | ""    | BROKER_NIF_AS_SELLER | NIF_AS_BUYER | BROKER_IBAN
        BROKER_CODE | "    "| BROKER_NIF_AS_SELLER | NIF_AS_BUYER | BROKER_IBAN
        BROKER_CODE | BROKER_NAME | null | NIF_AS_BUYER | BROKER_IBAN
        BROKER_CODE | BROKER_NAME | "    "| NIF_AS_BUYER | BROKER_IBAN
        BROKER_CODE | BROKER_NAME | BROKER_NIF_AS_SELLER | null | BROKER_IBAN
        BROKER_CODE | BROKER_NAME | BROKER_NIF_AS_SELLER | "   " | BROKER_IBAN
        BROKER_CODE | BROKER_NAME | BROKER_NIF_AS_SELLER | NIF_AS_BUYER | null
        BROKER_CODE | BROKER_NAME | BROKER_NIF_AS_SELLER | NIF_AS_BUYER | "   "
        BROKER_CODE | BROKER_NAME | BROKER_NIF_AS_SELLER | BROKER_NIF_AS_SELLER | BROKER_IBAN
    }

    @Test
    def 'uniqueCode'() {
        given:
            def broker = new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN);
        when:
            new Broker(BROKER_CODE, "WeExploreX", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN);
        then:
            thrown(BrokerException)
            FenixFramework.getDomainRoot().getBrokerSet().size() == 1
            FenixFramework.getDomainRoot().getBrokerSet().contains(broker)
    }

    @Test
    def 'uniqueSellerNIF'() {
        given:
            new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, "123456789", BROKER_IBAN);
        when:
            new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN);
        then:
            thrown(BrokerException)
            FenixFramework.getDomainRoot().getBrokerSet().size() == 1
    }


    @Test
    def 'uniqueBuyerNIFOne'() {
        given:
            new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN);
        when:
            new Broker(BROKER_CODE, BROKER_NAME, "123456789", NIF_AS_BUYER, BROKER_IBAN);
        then:
            thrown(BrokerException)
            FenixFramework.getDomainRoot().getBrokerSet().size() == 1
    }

    @Test
    def 'uniqueBuyerSellerNIFTwo'() {
        given:
            new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
        when:
            new Broker(BROKER_CODE, BROKER_NAME, NIF_AS_BUYER, "123456789", BROKER_IBAN)
        then:
            thrown(BrokerException)
            FenixFramework.getDomainRoot().getBrokerSet().size() == 1
    }
}

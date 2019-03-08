package pt.ulisboa.tecnico.softeng.broker.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException
import spock.lang.Shared
import spock.lang.Unroll

import javax.validation.constraints.Null


class AdventureConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {

    @Shared
    def broker
    def client

    @Shared
    def BROKER_IBAN = "BROKER_IBAN"
    @Shared
    def NIF_AS_BUYER = "buyerNIF"
    @Shared
    def BROKER_NIF_AS_SELLER = "sellerNIF"
    @Shared
    def OTHER_NIF = "987654321"
    @Shared
    def CLIENT_NIF = "123456789"
    @Shared
    def DRIVING_LICENSE = "IMT1234"
    @Shared
    def AGE = 20
    @Shared
    def AGE_18 = 18
    @Shared
    def MARGIN = 0.3
    @Shared
    def CLIENT_IBAN = "BK011234567"


    @Shared
    def begin = new LocalDate(2016, 12, 19)
    @Shared
    def end = new LocalDate(2016, 12, 21)


    @Override
    def populate4Test() {
        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
        client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)

    }

    def 'success'() {
        given:
        def adventure = new Adventure(broker, begin, end, client, MARGIN)

        expect:
        adventure.getBroker() == broker
        adventure.getBegin() == begin
        adventure.getEnd() == end
        adventure.getClient() == client
        adventure.getMargin() == MARGIN
        broker.getAdventureSet().contains(adventure)

        adventure.getPaymentConfirmation() == null
        adventure.getActivityConfirmation() == null
        adventure.getRoomConfirmation() == null
    }

    @Unroll('Adventure creation: #_broker, #_begin, #_end, #_margin')
    def 'exceptions'() {
        when:
        new Adventure(_broker, _begin, _end, client, _MARGIN)

        then:
        thrown(BrokerException)

        where:
        _broker | _begin | _end | _MARGIN
        null    | begin  | end  | MARGIN
        broker  | null   | end  | MARGIN
        broker  | begin  | null | MARGIN
    }


    def 'successEqual18'() {
        given:
        def c = new Client(this.broker, CLIENT_IBAN, OTHER_NIF, DRIVING_LICENSE + "1", 18)
        def adventure = new Adventure(broker, begin, end, c, MARGIN)

        expect:
        adventure.getBroker() == broker
        adventure.getBegin() == begin
        adventure.getEnd() == end
        adventure.getAge() == AGE_18
        adventure.getIban() == CLIENT_IBAN
        adventure.getMargin() == MARGIN
        broker.getAdventureSet().contains(adventure)

        adventure.getPaymentConfirmation() == null
        adventure.getActivityConfirmation() == null
        adventure.getRoomConfirmation() == null
    }

    def 'negativeAge'() {
        when:
        def c = new Client(broker, CLIENT_IBAN, OTHER_NIF, DRIVING_LICENSE, 17)
        new Adventure(broker, begin, end, c, MARGIN)

        then:
        thrown(BrokerException)

    }

    def 'successEqual100'() {
        given:
        def c = new Client(broker, CLIENT_IBAN, OTHER_NIF, DRIVING_LICENSE + "1", 100)
        def adventure = new Adventure(broker, begin, end, c, MARGIN)

        expect:
        adventure.getBroker() == broker
        adventure.getBegin() == begin
        adventure.getEnd() == end
        adventure.getAge() == 100
        adventure.getIban() == CLIENT_IBAN
        adventure.getMargin() == MARGIN
        broker.getAdventureSet().contains(adventure)

        adventure.getPaymentConfirmation() == null
        adventure.getActivityConfirmation() == null
        adventure.getRoomConfirmation() == null

    }

    def 'over100'() {
        when:
        new Adventure(broker, begin, end,
                new Client(broker, CLIENT_IBAN, OTHER_NIF, DRIVING_LICENSE, 101), MARGIN)

        then:
        thrown(BrokerException)
    }

    def 'negativeAmount'() {
        when:
        new Adventure(broker, begin, end, client, -100)

        then:
        thrown(BrokerException)
    }

    def 'success1Amount'() {
        given:
        def adventure = new Adventure(broker, begin, end, client, 1);

        expect:
        adventure.getBroker() == broker
        adventure.getBegin() == begin
        adventure.getEnd() == end
        adventure.getAge() == 20
        adventure.getIban() == CLIENT_IBAN
        adventure.getMargin() == 1
        broker.getAdventureSet().contains(adventure)

        adventure.getPaymentConfirmation() == null
        adventure.getActivityConfirmation() == null
        adventure.getRoomConfirmation() == null
    }

    def 'zeroAmount'() {
        when:
        new Adventure(broker, begin, end, client, 0)

        then:
        thrown(BrokerException)
    }

    def 'successEqualDates'() {
        def adventure = new Adventure(broker, begin, begin, client, MARGIN)

        expect:
        adventure.getBroker() == broker
        adventure.getBegin() == begin
        adventure.getEnd() == begin
        adventure.getAge() == 20
        adventure.getIban() == CLIENT_IBAN
        adventure.getMargin() == MARGIN
        this.broker.getAdventureSet().contains(adventure)

        adventure.getPaymentConfirmation() == null
        adventure.getActivityConfirmation() == null
        adventure.getRoomConfirmation() == null

    }

    def 'inconsistentDates'() {
        when:
        new Adventure(broker, begin, begin.minusDays(1), client, MARGIN)

        then:
        thrown(BrokerException)
    }
}

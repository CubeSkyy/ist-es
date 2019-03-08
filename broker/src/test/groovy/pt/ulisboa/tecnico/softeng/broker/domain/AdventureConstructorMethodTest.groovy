package pt.ulisboa.tecnico.softeng.broker.domain


import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException

import spock.lang.Unroll

import javax.validation.constraints.Null


class AdventureConstructorMethodTest extends SpockRollbackTestAbstractClass {

    def broker
    def client

    def adventure


    @Override
    def populate4Test() {
        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
        client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
        adventure = new Adventure(broker,begin,end, client, MARGIN)
    }

    def 'success'() {

        expect:
        adventure.getBroker() == broker
        adventure.getBegin() == begin
        adventure.getEnd() == end
        adventure.getClient() == client
        adventure.getMargin() == MARGIN
        this.broker.getAdventureSet().contains(adventure)

        adventure.getPaymentConfirmation() == Null
        adventure.getActivityConfirmation() == Null
        adventure.getRoomConfirmation() == Null
    }

    @Unroll('Adventure creation: #broker, #begin, #end, #client, #margin')
    def 'exceptions'() {
        when:
        new Adventure(broker, begin, end, client, margin)

        then:
        thrown(BrokerException)

        where:
        broker      | begin      | end      | margin
        null        | this.begin | this.end | MARGIN
        this.broker | null       | this.end | MARGIN
        this.broker | this.begin | null     | MARGIN
    }


    def 'successEqual18'() {
        expect:
        adventure.getBroker() == this.broker
        adventure.getBegin() == this.begin
        adventure.getEnd() == this.end
        adventure.getAge() == 18
        adventure.getIban() == CLIENT_IBAN
        adventure.getMargin() == MARGIN
        this.broker.getAdventureSet().contains(adventure)

        adventure.getPaymentConfirmation() == Null
        adventure.getActivityConfirmation() == Null
        adventure.getRoomConfirmation() == Null
    }

    def 'negativeAge'() {
        given:
        def c = new Client(this.broker, CLIENT_IBAN, OTHER_NIF, DRIVING_LICENSE, 17)
        when:
        new Adventure(this.broker, this.begin, this.end, c, MARGIN)

        then:
        thrown(BrokerException)

    }

    def 'successEqual100'() {
        given:
        def c = new Client(this.broker, CLIENT_IBAN, OTHER_NIF, DRIVING_LICENSE, 17)
        new Adventure(this.broker, this.begin, this.end, c, MARGIN)

        expect:
        adventure.getBroker() == this.broker
        adventure.getBegin() == this.begin
        adventure.getEnd() == this.end
        adventure.getAge() == 100
        adventure.getIban() == CLIENT_IBAN
        adventure.getMargin() == MARGIN
        this.broker.getAdventureSet().contains(adventure)

        adventure.getPaymentConfirmation() == Null
        adventure.getActivityConfirmation() == Null
        adventure.getRoomConfirmation() == Null

    }
    def 'over100'() {
        given:
        def c = new Client(this.broker, CLIENT_IBAN, OTHER_NIF, DRIVING_LICENSE, 101)
        when:
        new Adventure(broker, begin, end, c, MARGIN)

        then:
        thrown(BrokerException)
    }

    def 'negativeAmount'() {
        when:
        new Adventure(broker, begin, end, client, -100)

        then:
        thrown(BrokerException)
    }

    def 'success1Amount'(){

        expect:
        adventure.getBroker() == broker
        adventure.getBegin() == begin
        adventure.getEnd() == end
        adventure.getAge() == 20
        adventure.getIban() == CLIENT_IBAN
        adventure.getMargin() == 1
        this.broker.getAdventureSet().contains(adventure)

        adventure.getPaymentConfirmation() == Null
        adventure.getActivityConfirmation() == Null
        adventure.getRoomConfirmation() == Null
    }
    def 'zeroAmount'(){
        when:
        new Adventure(broker, begin, end, client, 0)

        then:
        thrown(BrokerException)
    }

    def 'successEqualDates'(){

        expect:
        adventure.getBroker() == broker
        adventure.getBegin() == begin
        adventure.getEnd() == begin
        adventure.getAge() == 20
        adventure.getIban() == CLIENT_IBAN
        adventure.getMargin() == MARGIN
        this.broker.getAdventureSet().contains(adventure)

        adventure.getPaymentConfirmation() == Null
        adventure.getActivityConfirmation() == Null
        adventure.getRoomConfirmation() == Null

    }
    def 'inconsistentDates'() {
        when:
        new Adventure(broker, begin, begin.minusDays(1), client, MARGIN)

        then:
        thrown(BrokerException)
    }
}

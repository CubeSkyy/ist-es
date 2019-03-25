package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestBankOperationData
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.BankException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException

import spock.lang.Shared

class ProcessPaymentStateProcessMethodSpockTest extends SpockRollbackTestAbstractClass {

    @Shared def TRANSACTION_SOURCE = "ADVENTURE"

    def taxInterface
    def bankInterface

    def broker
    def client
    def adventure

    def "populate4Test"(){
        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
        client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
        adventure = new Adventure(broker, BEGIN, END, client, MARGIN)

        taxInterface = Mock(TaxInterface)
        bankInterface = Mock(BankInterface)

        adventure.setState(Adventure.State.PROCESS_PAYMENT)
        adventure.setTaxInterface(taxInterface)
        adventure.setBankInterface(bankInterface)
    }

    def "success"(){
        when:
        adventure.process()

        then:
        bankInterface.processPayment(_ as RestBankOperationData) >> PAYMENT_CONFIRMATION
        adventure.getState().getValue() == Adventure.State.TAX_PAYMENT
    }

    def "bankException"(){
        when:
        for (int i = 0; i < 2; i++) {
            adventure.process()
        }

        then:
        bankInterface.processPayment(_ as RestBankOperationData) >> { throw new BankException() }
        adventure.getState().getValue() == Adventure.State.CANCELLED
    }

    def "singleRemoteAccessException"(){
        when:
        adventure.process()

        then:
        bankInterface.processPayment(_ as RestBankOperationData) >> { throw new RemoteAccessException() }
        adventure.getState().getValue() == Adventure.State.PROCESS_PAYMENT
    }

    def "maxRemoteAccessException"(){
        when:
        for (int i = 0; i < 4; i++) {
            adventure.process()
        }

        then:
        bankInterface.processPayment(_ as RestBankOperationData) >> { throw new RemoteAccessException() }
        adventure.getState().getValue() == Adventure.State.CANCELLED
    }

    def "maxMinusOneRemoteAccessException"(){
        when:
        for (int i = 0; i < 2; i++) {
            adventure.process()
        }

        then:
        bankInterface.processPayment(_ as RestBankOperationData) >> { throw new RemoteAccessException() }
        adventure.getState().getValue() == Adventure.State.PROCESS_PAYMENT
    }

    def "twoRemoteAccessExceptionOneSuccess"(){
        given:
        int i

        when:
        for (i = 0; i < 3; i++) {
            adventure.process()
        }

        then:
        3 * bankInterface.processPayment(_ as RestBankOperationData) >>  { if (i < 2) {
                                                                            throw new RemoteAccessException()}
                                                                        else {
                                                                            return PAYMENT_CONFIRMATION}
                                                                        }
        adventure.getState().getValue() == Adventure.State.TAX_PAYMENT
    }

    def "oneRemoteAccessExceptionOneBankException"(){
        given:
        int i

        when:
        for (i = 0; i < 3; i++) {
            adventure.process()
        }

        then:
        2 * bankInterface.processPayment(_ as RestBankOperationData) >>  { if (i < 1) {
                                                                                throw new RemoteAccessException()}
                                                                            else {
                                                                                throw new BankException()}
                                                                            }
        adventure.getState().getValue() == Adventure.State.CANCELLED

    }

}

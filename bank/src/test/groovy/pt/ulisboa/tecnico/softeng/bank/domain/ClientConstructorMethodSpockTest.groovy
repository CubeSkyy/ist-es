package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException;
import pt.ist.fenixframework.FenixFramework
import spock.lang.Unroll
import spock.lang.Shared

class ClientConstructorMethodSpockTest extends RollbackSpockTestAbstractClass {
    @Shared def CLIENT_NAME = "António"
    @Shared def bank

    @Override
    def populate4Test(){
        bank = new Bank("Money", "BK01")
    }

    def 'success'() {
        given:
        def client = new Client(bank, CLIENT_NAME)

        expect:
        client.getName() == CLIENT_NAME
        client.getID().length() >= 1
        bank.getClientSet().contains(client)
    }

    @Unroll('Client creation: #bank, #name')
    def 'exceptions'() {
        when:
        new Client(bank_ex, name)

        then:
        thrown(BankException)

        where:
        bank_ex   | name
        null      | CLIENT_NAME
        bank      | null
        bank      | "    "
        bank      | ""
    }

}
package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll


class BankGetAccountMethodSpockTest extends RollbackSpockTestAbstractClass {

    private Bank bank
    private Client client

    @Override
    def 'populate4Test'() {
        this.bank = new Bank("Money", "BK01")
        this.client = new Client(this.bank, "António")
    }


    def 'success'() {
        given:
        def account = new Account(this.bank, this.client)
        def result = this.bank.getAccount(account.getIBAN())

        expect:
        account == result
    }

    @Unroll('Bank get account: #IBAN')
    def 'exceptions'() {
        when:
        this.bank.getAccount(IBAN)

        then:
        thrown(BankException)

        where:
        IBAN   | _
        ""     | _
        "    " | _


    }



    def 'emptySetOfAccounts'() {
        given:
        def account  = this.bank.getAccount("XPTO")
        expect:
        account == null
    }


    def 'severalAccountsDoNoMatch'() {
        given:
        new Account(this.bank, this.client)
        when:
        new Account(this.bank, this.client)
        then:
        this.bank.getAccount("XPTO") == null

    }

}

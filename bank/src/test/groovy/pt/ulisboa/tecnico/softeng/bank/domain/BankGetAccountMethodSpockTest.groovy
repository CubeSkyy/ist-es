package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll


class BankGetAccountMethodSpockTest extends RollbackSpockTestAbstractClass {

    Bank bank
    Client client

    @Override
    def 'populate4Test'() {
        this.bank = new Bank("Money", "BK01")
        this.client = new Client(this.bank, "António")
    }


    def 'success'() {
        given: 'A new account'
        def account = new Account(this.bank, this.client)

        when: 'Searching for the account in the bank'
        def result = this.bank.getAccount(account.getIBAN())

        then: 'Result matches account'
        result == account
    }

    @Unroll('Bank get account: #IBAN')
    def 'exceptions'() {
        when: 'Searching for an account with invalid IBAN'
        this.bank.getAccount(IBAN)

        then: 'An exception is thrown'
        thrown(BankException)

        where:
        IBAN   | _
        ""     | _
        "    " | _

    }

    def 'emptySetOfAccounts'() {
        when: 'Searching an account that doesn\'t exist'
        def account = this.bank.getAccount("XPTO")

        then: 'The account is not found'
        account == null
    }


    def 'severalAccountsDoNoMatch'() {
        given: 'A new Account'
        new Account(this.bank, this.client)
        when: 'Creating the same account'
        new Account(this.bank, this.client)
        then: 'Search does not match'
        this.bank.getAccount("XPTO") == null

    }

}

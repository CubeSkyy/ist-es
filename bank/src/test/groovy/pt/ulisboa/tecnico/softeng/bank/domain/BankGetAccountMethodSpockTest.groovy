package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll
import spock.lang.Shared

class BankGetAccountMethodSpockTest extends RollbackSpockTestAbstractClass {
    @Shared
    Bank bank
    @Shared
    Client client

    @Override
    def 'populate4Test'() {
        bank = new Bank("Money", "BK01")
        client = new Client(bank, "António")
    }


    def 'success'() {
        given: 'A new account'
        def account = new Account(bank, client)

        when: 'Searching for the account in the bank'
        def result = bank.getAccount(account.getIBAN())

        then: 'Result matches account'
        result == account
    }

    @Unroll('Bank get account: #_iban')
    def 'exceptions'() {
        when: 'Searching for an account with invalid IBAN'
        bank.getAccount(_iban)

        then: 'An exception is thrown'
        thrown(BankException)

        where:
        _iban  | _
        null   | _
        ""     | _
        "    " | _

    }

    def 'emptySetOfAccounts'() {
        when: 'Searching an account that doesn\'t exist'
        def account = bank.getAccount("XPTO")

        then: 'The account is not found'
        account == null
    }


    def 'severalAccountsDoNoMatch'() {
        given: 'A new Account'
        new Account(bank, client)

        when: 'Creating the same account'
        new Account(bank, client)

        then: 'Search does not match'
        bank.getAccount("XPTO") == null

    }

}

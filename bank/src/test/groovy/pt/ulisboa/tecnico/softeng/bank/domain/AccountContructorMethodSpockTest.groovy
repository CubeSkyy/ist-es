package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll
import spock.lang.Shared

class AccountContructorMethodSpockTest extends RollbackSpockTestAbstractClass {
    @Shared
    Bank bank
    @Shared
    Client client

    @Override
    def populate4Test() {
        bank = new Bank("Money", "BK01")
        client = new Client(bank, "António")
    }

    def 'success'() {
        given: 'A new account in defined bank'
        def account = new Account(bank, client)

        expect: 'Bank and client have the right information and balance is 0'
        with(account) {
            bank == getBank()
            getIBAN().startsWith(bank.getCode())
            client == getClient()
            getBalance() == 0
            bank.getAccountSet().size() == 1
            bank.getClientSet().contains(client)
        }

    }

    @Unroll('Account: #_bank, #_client')
    def 'exceptions'() {
        when: 'creating Account with invalid arguments'
        new Account(_bank, _client)

        then: 'throws an exception'
        thrown(BankException)

        where:
        _bank | _client
        null  | client
        bank  | null

    }

    def 'clientDoesNotBelongToBank'() {
        given: 'A client that doesn\'t belong to bank'
        def allien = new Client(new Bank("MoneyPlus", "BK02"), "António")

        when: 'An account is created'
        new Account(bank, allien)

        then: 'An exception is thrown'
        thrown(BankException)
    }
}

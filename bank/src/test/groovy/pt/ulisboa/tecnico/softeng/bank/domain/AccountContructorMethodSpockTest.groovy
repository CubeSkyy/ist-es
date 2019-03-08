package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll

class AccountContructorMethodSpockTest extends RollbackSpockTestAbstractClass {

    Bank bank
    Client client

    @Override
    def populate4Test() {
        this.bank = new Bank("Money", "BK01")
        this.client = new Client(this.bank, "António")
    }

    def 'success'() {
        given: 'A new account in defined bank'
        def account = new Account(this.bank, this.client)

        expect: 'Bank and client have the right information and balance is 0'
        this.bank == account.getBank()
        account.getIBAN().startsWith(this.bank.getCode())
        this.client == account.getClient()
        account.getBalance() == 0
        this.bank.getAccountSet().size() == 1
        this.bank.getClientSet().contains(this.client)
    }

    @Unroll('Account: #bank, #client')
    def 'exceptions'() {
        when: 'creating Account with invalid arguments'
        new Account(bank, client)

        then: 'throws an exception'
        thrown(BankException)

        where:
        bank      | client
        null      | this.client
        this.bank | null

    }

    def 'clientDoesNotBelongToBank'() {
        given: 'A client that doesn\'t belong to bank'
        def allien = new Client(new Bank("MoneyPlus", "BK02"), "António")

        when: 'An account is created'
        new Account(this.bank, allien)

        then: 'An exception is thrown'
        thrown(BankException)
    }


}

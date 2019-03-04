package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll

class AccountContructorMethodSpockTest extends RollbackSpockTestAbstractClass {

    private Bank bank
    private Client client

    @Override
    def populate4Test() {
        this.bank = new Bank("Money", "BK01")
        this.client = new Client(this.bank, "António")
    }

    def 'sucess'() {
        given:
        def account = new Account(this.bank, this.client)

        expect:
        this.bank == account.getBank()
        account.getIBAN().startsWith(this.bank.getCode())
        this.client == account.getClient()
        account.getBalance() == 0
        this.bank.getAccountSet().size() == 1
        this.bank.getClientSet().contains(this.client)
    }

    @Unroll('Account: #bank, #client')
    def 'exceptions'() {
        when:
        new Account(bank, client)

        then:
        thrown(BankException)

        where:
        bank      | client
        null      | this.client
        this.bank | null

    }

    def 'clientDoesNotBelongToBank'() {
        given:
        def allien = new Client(new Bank("MoneyPlus", "BK02"), "António")

        when:
        new Account(this.bank, allien)

        then:
        thrown(BankException)
    }


}

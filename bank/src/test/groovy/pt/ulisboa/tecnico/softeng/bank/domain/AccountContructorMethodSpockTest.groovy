package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

class AccountContructorMethodSpockTest extends RollbackSpockTestAbstractClass {

    private Bank bank
    private Client client

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

    def 'notEmptyBankArgument'(){
        when:
        new Account(null, this.client)

        then:
        thrown(BankException)
    }

    def 'notEmptyClientArgument'() {
        when:
        new Account(this.bank, null)

        then:
        thrown(BankException)
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

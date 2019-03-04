package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException

class AccountWithdrawMethodSpockTest extends RollbackSpockTestAbstractClass {
    private Bank bank;
    private Account account;

    @Override
    def populate4Test() {
        this.bank = new Bank("Money", "BK01")
        def client = new Client(this.bank, "António")
        this.account = new Account(this.bank, client)
        this.account.deposit(100)

    }

    def 'success'() {
        given:
        def reference = this.account.withdraw(40).getReference()
        def operation = this.bank.getOperation(reference)

        expect:
        this.account.getBalance() == 60
        operation != null
        operation.getType() == Operation.Type.WITHDRAW
        operation.getAccount() == this.account
        operation.getValue() == 40
    }

    def 'negativeAmount'() {
        when:
        this.account.withdraw(-20)

        then:
        thrown(BankException)
    }

    def 'zeroAmount'() {
        when:
        this.account.withdraw(0)

        then:
        thrown(BankException)
    }


    def 'oneAmount'() {
        when:
        this.account.withdraw(1)
        then:
        this.account.getBalance() == 99
    }


    def 'equalToBalance'() {
        when:
        this.account.withdraw(100)
        then:
        this.account.getBalance() == 0
    }

    def 'equalToBalancePlusOne'() {
        when:
        this.account.withdraw(101)
        then:
        thrown(BankException)
    }

    def 'moreThanBalance'() {
        when:
        this.account.withdraw(150)
        then:
        thrown(BankException)
    }
}
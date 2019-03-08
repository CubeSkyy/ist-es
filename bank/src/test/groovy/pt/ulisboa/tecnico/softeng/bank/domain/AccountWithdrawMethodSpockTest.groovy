package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll

class AccountWithdrawMethodSpockTest extends RollbackSpockTestAbstractClass {
    Bank bank
    Account account

    @Override
    def populate4Test() {
        this.bank = new Bank("Money", "BK01")
        def client = new Client(this.bank, "António")
        this.account = new Account(this.bank, client)
        this.account.deposit(100)

    }

    def 'success'() {
        when: 'A withdraw operation'
        def reference = this.account.withdraw(40).getReference()
        def operation = this.bank.getOperation(reference)

        then: 'Balance is consistent and operation was successful'
        this.account.getBalance() == 60
        operation != null
        operation.getType() == Operation.Type.WITHDRAW
        operation.getAccount() == this.account
        operation.getValue() == 40
    }


    @Unroll('Account withdraw: #amount')
    def 'exceptions'() {
        when: 'A withdraw with an invalid ammount'
        this.account.withdraw(amount)

        then: 'An exception is thrown'
        thrown(BankException)

        where:
        amount | _
        -20    | _
        0      | _
        101    | _
        150    | _

    }


    def 'oneAmount'() {
        when: 'A valid withdraw'
        this.account.withdraw(1)

        then: 'The account balance is consistent'
        this.account.getBalance() == 99
    }


    def 'equalToBalance'() {
        when: 'A valid full withdraw'
        this.account.withdraw(100)

        then: 'The account has no balance'
        this.account.getBalance() == 0
    }

}
package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll

class AccountDepositMethodSpockTest extends RollbackSpockTestAbstractClass {

    Bank bank
    Account account

    @Override
    def populate4Test() {
        this.bank = new Bank("Money", "BK01")
        def client = new Client(this.bank, "António")
        this.account = new Account(this.bank, client)
    }


    def 'success'() {
        when: 'depositing into created account'
        def reference = this.account.deposit(50).getReference()
        def operation = this.bank.getOperation(reference)

        then: 'balance matches deposit and operation was successful'
        this.account.getBalance() == 50
        operation != null
        operation.getType() == Operation.Type.DEPOSIT
        operation.getAccount() == this.account
        operation.getValue() == 50
    }


    @Unroll('Account deposit: #amount')
    def 'exceptions'() {
        when:  'depositing with invalid ammount'
        this.account.deposit(amount)

        then: 'throws an exception'
        thrown(BankException)


        where:
        amount | _
        0      | _
        -100   | _
    }

    def 'oneAmount'() {
        when: 'depositing a valid ammount'
        this.account.deposit(1)

        then: 'No exception is thrown'
        noExceptionThrown()
    }

}

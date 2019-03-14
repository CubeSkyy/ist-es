package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll
import spock.lang.Shared

class AccountDepositMethodSpockTest extends RollbackSpockTestAbstractClass {

    @Shared
    Bank bank
    @Shared
    Account account

    @Override
    def populate4Test() {
        bank = new Bank("Money", "BK01")
        def client = new Client(bank, "António")
        account = new Account(bank, client)
    }


    def 'success'() {
        when: 'depositing into created account'
        def reference = account.deposit(50).getReference()
        def operation = bank.getOperation(reference)

        then: 'balance matches deposit and operation was successful'

        account.getBalance() == 50
        operation != null

        with(operation){
            getType() == Operation.Type.DEPOSIT
            getAccount() == account
            getValue() == 50
        }

    }


    @Unroll('Account deposit: #_amount')
    def 'exceptions'() {
        when:  'depositing with invalid amount'
        account.deposit(_amount)

        then: 'throws an exception'
        thrown(BankException)


        where:
        _amount | _
        0      | _
        -100   | _
    }

    def 'oneAmount'() {
        when: 'depositing a valid ammount'
        account.deposit(1)

        then: 'No exception is thrown'
        noExceptionThrown()
    }

}

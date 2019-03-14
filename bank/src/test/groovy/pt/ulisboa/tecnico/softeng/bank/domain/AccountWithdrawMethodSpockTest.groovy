package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll
import spock.lang.Shared

class AccountWithdrawMethodSpockTest extends RollbackSpockTestAbstractClass {
    @Shared
    Bank bank
    @Shared
    Account account

    @Override
    def populate4Test() {
        bank = new Bank("Money", "BK01")
        def client = new Client(bank, "António")
        account = new Account(bank, client)
        account.deposit(100)

    }

    def 'success'() {
        when: 'A withdraw operation'
        def reference = account.withdraw(40).getReference()
        def operation = bank.getOperation(reference)

        then: 'Balance is consistent and operation was successful'
        account.getBalance() == 60
        operation != null

        with(operation){
            getType() == Operation.Type.WITHDRAW
            getAccount() == account
            getValue() == 40
        }
    }


    @Unroll('Account withdraw: #_amount')
    def 'exceptions'() {
        when: 'A withdraw with an invalid amount'
        account.withdraw(_amount)

        then: 'An exception is thrown'
        thrown(BankException)

        where:
        _amount | _
        -20    | _
        0      | _
        101    | _
        150    | _

    }


    def 'oneAmount'() {
        when: 'A valid withdraw'
        account.withdraw(1)

        then: 'The account balance is consistent'
        account.getBalance() == 99
    }


    def 'equalToBalance'() {
        when: 'A valid full withdraw'
        account.withdraw(100)

        then: 'The account has no balance'
        account.getBalance() == 0
    }

}
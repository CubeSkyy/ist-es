package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll

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


    @Unroll('Account withdraw: #amount')
    def 'exceptions'(Integer amount) {
        when:
        this.account.withdraw(amount)

        then:
        thrown(BankException)

        where:
        amount | _
        -20    | _
        0      | _
        101    | _
        150    | _

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

}
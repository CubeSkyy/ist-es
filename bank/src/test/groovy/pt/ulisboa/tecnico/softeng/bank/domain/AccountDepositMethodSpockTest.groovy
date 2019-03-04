package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll

class AccountDepositMethodSpockTest extends RollbackSpockTestAbstractClass {

    private Bank bank
    private Account account

    @Override
    def populate4Test() {
        this.bank = new Bank("Money", "BK01")
        Client client = new Client(this.bank, "António")
        this.account = new Account(this.bank, client)
    }


    def 'sucess'() {
        given:
        def reference = this.account.deposit(50).getReference()
        def operation = this.bank.getOperation(reference)

        expect:
        this.account.getBalance() == 50
        operation != null
        operation.getType() == Operation.Type.DEPOSIT
        operation.getAccount() == this.account
        operation.getValue() == 50
    }


    @Unroll('Account deposit: #amount')
    def 'exceptions'(Integer amount) {
        when:
        this.account.deposit(amount)

        then:
        thrown(BankException)

        where:
        amount | _
        0      | _
        -100   | _

    }

    def 'oneAmount'() {
        when:
        this.account.deposit(1)

        then:
        noExceptionThrown()
    }

}

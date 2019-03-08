package pt.ulisboa.tecnico.softeng.bank.services.local

import pt.ulisboa.tecnico.softeng.bank.domain.Account
import pt.ulisboa.tecnico.softeng.bank.domain.Bank
import pt.ulisboa.tecnico.softeng.bank.domain.Client
import pt.ulisboa.tecnico.softeng.bank.domain.RollbackSpockTestAbstractClass
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll


class BankInterfaceCancelPaymentSpockTest extends RollbackSpockTestAbstractClass {
    Bank bank
    Account account
    String reference

    @Override
    def populate4Test() {
        this.bank = new Bank("Money", "BK01")
        def client = new Client(this.bank, "António")
        this.account = new Account(this.bank, client)
        this.reference = this.account.deposit(100).getReference()
    }


    def 'success'() {
        when: 'Canceling a payment'
        def newReference = BankInterface.cancelPayment(this.reference)

        then: 'The operation with the same reference doesn\'t exist'
        this.bank.getOperation(newReference) != null
    }

    @Unroll('Bank cancel payment: #confirmation')
    def 'exceptions'() {
        when: 'Canceling a payment with an invalid confirmation'
        BankInterface.cancelPayment(confirmation)

        then: 'An exception is thrown'
        thrown(BankException)

        where:
        confirmation | _
        null         | _
        ""           | _
        "XPTO"       | _
    }

}

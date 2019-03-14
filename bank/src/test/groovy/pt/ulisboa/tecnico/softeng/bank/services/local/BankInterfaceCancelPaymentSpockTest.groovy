package pt.ulisboa.tecnico.softeng.bank.services.local

import pt.ulisboa.tecnico.softeng.bank.domain.Account
import pt.ulisboa.tecnico.softeng.bank.domain.Bank
import pt.ulisboa.tecnico.softeng.bank.domain.Client
import pt.ulisboa.tecnico.softeng.bank.domain.RollbackSpockTestAbstractClass
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll
import spock.lang.Shared

class BankInterfaceCancelPaymentSpockTest extends RollbackSpockTestAbstractClass {
    @Shared
    Bank bank
    @Shared
    Account account
    @Shared
    String reference

    @Override
    def populate4Test() {
        bank = new Bank("Money", "BK01")
        def client = new Client(bank, "António")
        account = new Account(bank, client)
        reference = account.deposit(100).getReference()
    }


    def 'success'() {
        when: 'Canceling a payment'
        def newReference = BankInterface.cancelPayment(reference)

        then: 'The operation with the same reference doesn\'t exist'
        bank.getOperation(newReference) != null
    }

    @Unroll('Bank cancel payment: #_confirmation')
    def 'exceptions'() {
        when: 'Canceling a payment with an invalid confirmation'
        BankInterface.cancelPayment(_confirmation)

        then: 'An exception is thrown'
        thrown(BankException)

        where:
        _confirmation | _
        null          | _
        ""            | _
        "XPTO"        | _
    }

}

package pt.ulisboa.tecnico.softeng.bank.services.local

import pt.ulisboa.tecnico.softeng.bank.domain.Account
import pt.ulisboa.tecnico.softeng.bank.domain.Bank
import pt.ulisboa.tecnico.softeng.bank.domain.Client
import pt.ulisboa.tecnico.softeng.bank.domain.Operation
import pt.ulisboa.tecnico.softeng.bank.domain.RollbackSpockTestAbstractClass
import pt.ulisboa.tecnico.softeng.bank.exception.BankException

import spock.lang.Shared
import spock.lang.Unroll

class BankInterfaceGetOperationDataMethodSpockTest extends RollbackSpockTestAbstractClass {

    Bank bank
    Account account
    @Shared
    private static double AMOUNT = 100
    @Shared
    private String reference

    @Override
    def 'populate4Test'() {
        this.bank = new Bank("Money", "BK01")
        def client = new Client(this.bank, "António")
        this.account = new Account(this.bank, client)
        this.reference = this.account.deposit(AMOUNT).getReference()
    }

    def 'success'() {
        when: 'Reading operation data'
        def data = BankInterface.getOperationData(this.reference)

        then: 'The data is consistent with the account'
        data.getReference() == this.reference
        this.account.getIBAN() == data.getIban()
        data.getType() == Operation.Type.DEPOSIT.name()
        data.getValue() == AMOUNT
        data.getTime() != null
    }


    @Unroll('Bank get operation data: #ref')
    def 'exceptions'() {
        when: 'reading operation data'
        BankInterface.getOperationData(ref)

        then: 'An exception is thrown'
        thrown(BankException)

        where:
        ref    | _
        null   | _
        ""     | _
        "XPTO" | _
    }

}
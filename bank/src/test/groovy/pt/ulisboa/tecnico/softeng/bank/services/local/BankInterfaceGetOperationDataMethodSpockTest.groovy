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
    @Shared
    private static double AMOUNT = 100
    @Shared
    private Bank bank
    @Shared
    private Account account
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
        given:
        def data = BankInterface.getOperationData(this.reference)

        expect:
        data.getReference() == this.reference
        this.account.getIBAN() == data.getIban()
        data.getType() == Operation.Type.DEPOSIT.name()
        data.getValue() == AMOUNT
        data.getTime() != null
    }


    @Unroll('Bank get operation data: #ref')
    def 'exceptions'() {
        when:
        BankInterface.getOperationData(ref);

        then:
        thrown(BankException)

        where:

        ref    | _
        null   | _
        ""     | _
        "XPTO" | _
    }

}
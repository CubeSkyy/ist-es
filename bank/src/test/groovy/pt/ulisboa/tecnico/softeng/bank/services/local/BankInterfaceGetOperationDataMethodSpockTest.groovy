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
    Bank bank
    @Shared
    Account account
    @Shared
    String reference
    @Shared
    def AMOUNT = 100


    @Override
    def 'populate4Test'() {
        bank = new Bank("Money", "BK01")
        def client = new Client(bank, "António")
        account = new Account(bank, client)
        reference = account.deposit(AMOUNT).getReference()
    }

    def 'success'() {
        when: 'Reading operation data'
        def data = BankInterface.getOperationData(reference)

        then: 'The data is consistent with the account'
        with(data) {
            getReference() == reference
            getIban() == account.getIBAN()
            getType() == Operation.Type.DEPOSIT.name()
            getValue() == AMOUNT
            getTime() != null
        }
    }


    @Unroll('Bank get operation data: #_ref')
    def 'exceptions'() {
        when: 'reading operation data'
        BankInterface.getOperationData(_ref)

        then: 'An exception is thrown'
        thrown(BankException)

        where:
        _ref   | _
        null   | _
        ""     | _
        "XPTO" | _
    }

}
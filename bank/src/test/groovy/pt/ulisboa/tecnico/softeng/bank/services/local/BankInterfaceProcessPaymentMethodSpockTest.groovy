package pt.ulisboa.tecnico.softeng.bank.services.local

import pt.ulisboa.tecnico.softeng.bank.domain.Account
import pt.ulisboa.tecnico.softeng.bank.domain.Bank
import pt.ulisboa.tecnico.softeng.bank.domain.Client
import pt.ulisboa.tecnico.softeng.bank.domain.Operation
import pt.ulisboa.tecnico.softeng.bank.domain.RollbackSpockTestAbstractClass
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.BankOperationData
import spock.lang.Shared
import spock.lang.Unroll

class BankInterfaceProcessPaymentMethodSpockTest extends RollbackSpockTestAbstractClass {

    @Shared
    Bank bank
    @Shared
    Account account
    @Shared
    String iban
    @Shared
    def TRANSACTION_SOURCE = "ADVENTURE"
    @Shared
    def TRANSACTION_REFERENCE = "REFERENCE"

    @Override
    def 'populate4Test'() {
        bank = new Bank("Money", "BK01")
        def client = new Client(bank, "António")
        account = new Account(bank, client)
        iban = account.getIBAN()
        account.deposit(500)
    }


    def 'success'() {
        when: 'Processing a payment'
        account.getIBAN()
        def newReference = BankInterface
                .processPayment(new BankOperationData(iban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

        then: 'The operation is successful'
        newReference != null
        newReference.startsWith("BK01")
        bank.getOperation(newReference) != null
        bank.getOperation(newReference).getType() == Operation.Type.WITHDRAW
    }


    def 'successTwoBanks'() {
        given: 'After creating another bank'
        def otherBank = new Bank("Money", "BK02")
        def otherClient = new Client(otherBank, "Manuel")
        def otherAccount = new Account(otherBank, otherClient)
        def otherIban = otherAccount.getIBAN()

        when: 'Processing a transaction between the two banks'
        otherAccount.deposit(1000)
        BankInterface.processPayment(new BankOperationData(otherIban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))
        BankInterface.processPayment(
                new BankOperationData(iban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE + "PLUS"))

        then: 'Both accounts have a consistent balance'
        otherAccount.getBalance() == 900
        account.getBalance() == 400
    }

    def 'redoAnAlreadyPayed'() {
        given: 'Two references of the same operation'
        account.getIBAN()
        def firstReference = BankInterface
                .processPayment(new BankOperationData(iban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))
        def secondReference = BankInterface
                .processPayment(new BankOperationData(iban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

        expect: 'They are equal and balance didn\'t deduct twice'
        firstReference == secondReference
        account.getBalance() == 400
    }


    @Unroll('Bank process payment: #_iban, #_value')
    def 'exceptions'() {
        when:
        BankInterface.processPayment(new BankOperationData(_iban, _value, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

        then:
        thrown(BankException)

        where:
        _iban     | _value
        null      | 100
        "  "      | 100
        iban | 0
        "other"   | 0
    }

    def 'oneAmount'() {
        given: 'A border transaction of 1'
        BankInterface.processPayment(new BankOperationData(iban, 1, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))
        expect: 'The balance gets deducted 1 unit'
        account.getBalance() == 499
    }


}

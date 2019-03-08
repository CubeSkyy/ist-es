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
    private static final String TRANSACTION_SOURCE = "ADVENTURE"
    @Shared
    private static final String TRANSACTION_REFERENCE = "REFERENCE"

    Bank bank
    Account account
    String iban

    @Override
    def 'populate4Test'() {
        this.bank = new Bank("Money", "BK01")
        def client = new Client(this.bank, "António")
        this.account = new Account(this.bank, client)
        this.iban = this.account.getIBAN()
        this.account.deposit(500)
    }


    def 'success'() {
        when: 'Processing a payment'
        this.account.getIBAN()
        def newReference = BankInterface
                .processPayment(new BankOperationData(this.iban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

        then: 'The operation is successful'
        newReference != null
        newReference.startsWith("BK01")
        this.bank.getOperation(newReference) != null
        this.bank.getOperation(newReference).getType() == Operation.Type.WITHDRAW
    }


    def 'successTwoBanks'() {
        given:
        def otherBank = new Bank("Money", "BK02")
        def otherClient = new Client(otherBank, "Manuel")
        def otherAccount = new Account(otherBank, otherClient)
        def otherIban = otherAccount.getIBAN()

        when:
        otherAccount.deposit(1000)
        BankInterface.processPayment(new BankOperationData(otherIban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))
        BankInterface.processPayment(
                new BankOperationData(this.iban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE + "PLUS"))

        then:
        otherAccount.getBalance() == 900
        this.account.getBalance() == 400
    }

    def 'redoAnAlreadyPayed'() {
        given:
        this.account.getIBAN()
        def firstReference = BankInterface
                .processPayment(new BankOperationData(this.iban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))
        def secondReference = BankInterface
                .processPayment(new BankOperationData(this.iban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

        expect:
        firstReference == secondReference
        this.account.getBalance() == 400
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
        this.iban | 0
        "other"   | 0
    }

    def 'oneAmount'() {
        given:
        BankInterface.processPayment(new BankOperationData(this.iban, 1, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))
        expect:
        this.account.getBalance() == 499

    }


}

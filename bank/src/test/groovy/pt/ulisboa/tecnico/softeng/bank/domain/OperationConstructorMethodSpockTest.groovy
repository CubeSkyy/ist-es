package pt.ulisboa.tecnico.softeng.bank.domain;

import pt.ulisboa.tecnico.softeng.bank.domain.Operation.Type
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class OperationConstructorMethodSpockTest extends RollbackSpockTestAbstractClass {
    @Shared def bank
    @Shared def account

    @Override
    def populate4Test(){
        bank = new Bank('Money', 'BK01')
        Client client = new Client(bank, 'António')
        account = new Account(bank, client)
    }

    def 'success'(){
        given:
        Operation operation = new Operation(Type.DEPOSIT, account, 1000)

        expect:
        operation.getReference().startsWith(bank.getCode())
        operation.getReference().length() > Bank.CODE_SIZE
        Type.DEPOSIT == operation.getType()
        account == operation.getAccount()
        1000 == operation.getValue()
        operation.getTime() !=  null
        operation == bank.getOperation(operation.getReference())
    }

    @Unroll('Operation creation: #type, #account, #value')
    def 'exceptions'() {
        when:
        new Operation(type, account_ex, value)

        then:
        thrown(BankException)

        where:
        type            | account_ex  | value
        null            | account     | 1000
        Type.WITHDRAW   | null        | 1000
        Type.DEPOSIT    | account     | 0
        Type.WITHDRAW   | account     | -1000

    }

    def 'one amount'() {
        given:
        Operation operation = new Operation(Type.DEPOSIT, account, 1)

        expect:
        operation == bank.getOperation(operation.getReference())
    }

}
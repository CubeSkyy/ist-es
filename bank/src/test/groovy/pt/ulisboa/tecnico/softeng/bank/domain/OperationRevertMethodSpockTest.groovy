package pt.ulisboa.tecnico.softeng.bank.domain;

import spock.lang.Unroll
import spock.lang.Shared

public class OperationRevertMethodSpockTest extends RollbackSpockTestAbstractClass{
    @Shared def bank
    @Shared def account

    @Override
    def populate4Test() {
        bank = new Bank("Money", "BK01")
        Client client = new Client(bank, "António")
        account = new Account(bank, client)
    }

    def 'revert deposit'(){
        given:
        String reference = account.deposit(100).getReference()
        Operation operation = bank.getOperation(reference)

        String newReference = operation.revert();

        expect:
        0 == account.getBalance()
        bank.getOperation(newReference) != null
        bank.getOperation(reference) != null
    }

    def 'revert withdraw'(){
        given:
        account.deposit(1000)
        String reference = account.withdraw(100).getReference()
        Operation operation = bank.getOperation(reference)

        String newReference = operation.revert()

        expect:
        1000 == account.getBalance()
        bank.getOperation(newReference) != null
        bank.getOperation(reference) != null
    }

}
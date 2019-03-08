package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import pt.ist.fenixframework.FenixFramework
import spock.lang.Unroll
import spock.lang.Shared

class BankConstructorSpockTest extends RollbackSpockTestAbstractClass {

    @Shared def BANK_CODE = "BK01"
    @Shared def BANK_NAME = "Money"

    @Override
    def populate4Test() {
    }

    def 'success'() {
        when: 'Creating a new bank'
        def bank = new Bank(BANK_NAME, BANK_CODE);

        then: 'The bank data is consistent with arguments'
        bank.getName() == BANK_NAME
        bank.getCode() == BANK_CODE
        FenixFramework.getDomainRoot().getBankSet().size() == 1
        bank.getAccountSet().size() == 0
        bank.getClientSet().size() == 0
    }

    @Unroll('Bank creation: #name, #code')
    def 'exceptions'() {
        when: 'Creating a Bank with invalid arguments'
        new Bank(name, code)

        then: 'An exception is thrown'
        thrown(BankException)

        where:
        name      | code
        null      | BANK_CODE
        "    "    | BANK_CODE
        BANK_NAME | null
        BANK_NAME | "    "
        BANK_NAME | "BK0"
        BANK_NAME | "BK011"
    }


    def 'notUniqueCode'() {
        given: 'A new valid bank'
        new Bank(BANK_NAME, BANK_CODE)

        when: 'Creating a bank with the same name and code'
        new Bank(BANK_NAME, BANK_CODE)

        then: 'An exception is thrown and the bank was not created'
        thrown(BankException)
        FenixFramework.getDomainRoot().getBankSet().size() == 1
    }

}

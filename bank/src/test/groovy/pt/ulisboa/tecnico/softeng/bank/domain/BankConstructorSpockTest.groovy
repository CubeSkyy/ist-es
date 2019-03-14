package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import pt.ist.fenixframework.FenixFramework
import spock.lang.Unroll
import spock.lang.Shared

class BankConstructorSpockTest extends RollbackSpockTestAbstractClass {

    @Shared
    def BANK_CODE = "BK01"
    @Shared
    def BANK_NAME = "Money"

    @Override
    def populate4Test() {
    }

    def 'success'() {
        when: 'Creating a new bank'
        def bank = new Bank(BANK_NAME, BANK_CODE)

        then: 'The bank data is consistent with arguments'

        with(bank){
            getName() == BANK_NAME
            getCode() == BANK_CODE
            FenixFramework.getDomainRoot().getBankSet().size() == 1
            getAccountSet().size() == 0
            getClientSet().size() == 0
        }
    }

    @Unroll('Bank creation: #_name, #_code')
    def 'exceptions'() {
        when: 'Creating a Bank with invalid arguments'
        new Bank(_name, _code)

        then: 'An exception is thrown'
        thrown(BankException)

        where:
        _name      | _code
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

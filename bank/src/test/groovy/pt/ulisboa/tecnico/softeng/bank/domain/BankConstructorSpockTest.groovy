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
        given:
        def bank = new Bank(BANK_NAME, BANK_CODE);

        expect:
        bank.getName() == BANK_NAME
        bank.getCode() == BANK_CODE
        FenixFramework.getDomainRoot().getBankSet().size() == 1
        bank.getAccountSet().size() == 0
        bank.getClientSet().size() == 0
    }

    @Unroll('Bank creation: #name, #code')
    def 'exceptions'() {
        when:
        new Bank(name, code)

        then:
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
        given:
        new Bank(BANK_NAME, BANK_CODE)
        when:
        new Bank(BANK_NAME, BANK_CODE)
        then:
        thrown(BankException)
        FenixFramework.getDomainRoot().getBankSet().size() == 1

    }

}

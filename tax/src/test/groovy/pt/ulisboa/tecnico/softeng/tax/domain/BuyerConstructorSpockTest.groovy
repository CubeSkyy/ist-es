package pt.ulisboa.tecnico.softeng.tax.domain

import pt.ulisboa.tecnico.softeng.tax.exception.TaxException
import spock.lang.Shared
import spock.lang.Unroll

class BuyerConstructorSpockTest extends SpockRollbackTestAbstractClass {
    @Shared def ADDRESS = "Somewhere"
    @Shared def NAME = "José Vendido"
    @Shared def NIF = "123456789"

    def irs


    @Override
    def populate4Test() {
        this.irs = IRS.getIRSInstance()
    }

    def 'success'() {
        given:
        def buyer = new Buyer(this.irs, NIF, NAME, ADDRESS)
        expect:
        buyer.getNif() == NIF
        buyer.getName() == NAME
        buyer.getAddress() == ADDRESS

        IRS.getIRSInstance().getTaxPayerByNIF(NIF) == buyer
    }


    def 'unique NIF'() {
        given: 'a seller'
        def seller = new Buyer(this.irs, NIF, NAME, ADDRESS)

        when: 'when creating with same arguments'
        new Buyer(this.irs, NIF, NAME, ADDRESS)

        then: 'throws an exception'
        thrown(TaxException)
        
        IRS.getIRSInstance().getTaxPayerByNIF(NIF) == seller

    }

    @Unroll('#nisf, #name, #address')
    def 'exceptions'() {
        when: 'creating a buyer with wrong parameters'
        new Buyer(irs, nif, name, address)

        then: 'throws an exception'
        thrown(TaxException)

        where:
        nif        | name | address
        null       | NAME | ADDRESS
        ""         | NAME | ADDRESS
        "12345678" | NAME | ADDRESS
        NIF        | null | ADDRESS
        NIF        | ""   | ADDRESS
        NIF        | NAME | null
        NIF        | NAME | ""
    }


}
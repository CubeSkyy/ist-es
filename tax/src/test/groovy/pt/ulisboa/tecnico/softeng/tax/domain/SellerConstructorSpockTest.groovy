package pt.ulisboa.tecnico.softeng.tax.domain

import pt.ulisboa.tecnico.softeng.tax.exception.TaxException
import spock.lang.Shared
import spock.lang.Unroll

class SellerConstructorSpockTest extends SpockRollbackTestAbstractClass {
    @Shared
    def ADDRESS = "Somewhere"
    @Shared
    def NAME = "José Vendido"
    @Shared
    def NIF = "123456789"

    def irs

    @Override
    def populate4Test() {
        this.irs = IRS.getIRSInstance()
    }

    def 'success'() {
        given:
        def seller = new Seller(this.irs, NIF, NAME, ADDRESS)
        expect:
        seller.getNif() == NIF
        seller.getName() == NAME
        seller.getAddress() == ADDRESS
        IRS.getIRSInstance().getTaxPayerByNIF(NIF) == seller
    }

    def 'unique NIF'() {
        given:
        def seller = new Seller(this.irs, NIF, NAME, ADDRESS)

        when:
        new Seller(this.irs, NIF, NAME, ADDRESS)

        then:
        thrown(TaxException)
        IRS.getIRSInstance().getTaxPayerByNIF(NIF) == seller

    }

    @Unroll('#nif, #name, #address')
    def 'exceptions'() {
        when:
        new Seller(this.irs, nif, name, address)

        then:
        thrown(TaxException)

        where:
        nif  | name | address
        null | NAME | ADDRESS
        ""   | NAME | ADDRESS
        NIF  | null | ADDRESS
        NIF  | ""   | ADDRESS
        NIF  | NAME | null
        NIF  | NAME | ""

    }

}
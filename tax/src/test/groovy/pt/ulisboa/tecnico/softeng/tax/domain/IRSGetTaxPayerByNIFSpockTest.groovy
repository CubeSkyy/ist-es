package pt.ulisboa.tecnico.softeng.tax.domain

import spock.lang.Unroll

class IRSGetTaxPayerByNIFSpockTest extends SpockRollbackTestAbstractClass {
    def SELLER_NIF = "123456789"
    def BUYER_NIF = "987654321"

    def irs

    @Override
    def populate4Test() {
        this.irs = IRS.getIRSInstance()
        new Seller(this.irs, SELLER_NIF, "José Vendido", "Somewhere")
        new Buyer(this.irs, BUYER_NIF, "Manuel Comprado", "Anywhere")
    }

    def 'success Buyer'() {
        given:
        def taxPayer = this.irs.getTaxPayerByNIF(BUYER_NIF)
        expect:
        taxPayer != null
        taxPayer.getNif() == BUYER_NIF
    }

    def 'success Seller'() {
        given:
        def taxPayer = this.irs.getTaxPayerByNIF(SELLER_NIF)
        expect:
        taxPayer != null
        taxPayer.getNif() == SELLER_NIF
    }

    @Unroll('#label,#nif')
    def 'null TaxPayer'() {
        given:
        def taxPayer = this.irs.getTaxPayerByNIF(nif)

        expect:
        taxPayer == null

        where:
        label          | nif
        "nullNIF"      | null
        "emptyNIF"     | ""
        "doesNotExist" | "122456789"
    }

}
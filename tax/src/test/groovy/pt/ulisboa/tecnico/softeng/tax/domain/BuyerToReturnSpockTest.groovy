package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException

class BuyerToReturnSpockTest extends SpockRollbackTestAbstractClass {

    def SELLER_NIF = "123456789"
    def BUYER_NIF = "987654321"
    def FOOD = "FOOD"
    def TAX = 10
    def date = new LocalDate(2018, 02, 13)

    def seller
    def buyer
    def itemType


    @Override
    def populate4Test() {
        def irs = IRS.getIRSInstance()
        this.seller = new Seller(irs, SELLER_NIF, "José Vendido", "Somewhere")
        this.buyer = new Buyer(irs, BUYER_NIF, "Manuel Comprado", "Anywhere")
        this.itemType = new ItemType(irs, FOOD, TAX)
    }

    def 'success'() {
        given:
        new Invoice(100, this.date, this.itemType, this.seller, this.buyer)
        new Invoice(100, this.date, this.itemType, this.seller, this.buyer)
        new Invoice(50, this.date, this.itemType, this.seller, this.buyer)
        expect:
        this.buyer.taxReturn(2018) == 1.25f
    }

    def 'year without invoices'() {
        given:
        new Invoice(100, this.date, this.itemType, this.seller, this.buyer)
        new Invoice(100, this.date, this.itemType, this.seller, this.buyer)
        new Invoice(50, this.date, this.itemType, this.seller, this.buyer)
        expect:
        this.buyer.taxReturn(2017) == 0.0f
    }

    def 'no invoices'() {
        expect:
        this.buyer.taxReturn(2018) == 0.0f
    }

    def 'before 1970'() {
        when:
        new Invoice(100, new LocalDate(1969, 02, 13), this.itemType, this.seller, this.buyer)
        assert this.buyer.taxReturn(1969) == 0.0f

        then:
        thrown(TaxException)
    }

    def 'equal 1970'() {
        when:
        new Invoice(100, new LocalDate(1970, 02, 13), this.itemType, this.seller, this.buyer)

        then:
        this.buyer.taxReturn(1970) == 0.5f
    }

    def 'ignore cancelled'() {
        given:
        new Invoice(100, this.date, this.itemType, this.seller, this.buyer)
        Invoice invoice = new Invoice(100, this.date, this.itemType, this.seller, this.buyer)
        new Invoice(50, this.date, this.itemType, this.seller, this.buyer)

        when:
        invoice.cancel()

        then:
        this.buyer.taxReturn(2018) == 0.75f


    }


}
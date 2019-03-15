package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException

class SellerToPaySpockTest extends SpockRollbackTestAbstractClass {
    def SELLER_NIF = "123456789"
    def BUYER_NIF = "987654321"
    def FOOD = "FOOD"
    def TAX = 10
    def date = new LocalDate(2018, 02, 13)

    def Seller seller
    def Buyer buyer
    def ItemType itemType

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
        this.seller.toPay(2018) == 25.0f
    }

    def 'year without invoices'() {
        given:
        new Invoice(100, this.date, this.itemType, this.seller, this.buyer)
        new Invoice(100, this.date, this.itemType, this.seller, this.buyer)
        new Invoice(50, this.date, this.itemType, this.seller, this.buyer)
        expect:
        this.seller.toPay(2015) == 0.0f
    }

    def 'no invoices'() {
        expect:
        this.seller.toPay(2018) == 0.0f
    }

    def 'before1970'() {
        when:
        new Invoice(100, new LocalDate(1969, 02, 13), this.itemType, this.seller, this.buyer)
        new Invoice(50, new LocalDate(1969, 02, 13), this.itemType, this.seller, this.buyer)
        this.seller.toPay(1969) == 0.0f
        then:
        thrown(TaxException)

    }

    def 'equals 1970'() {
        given:
        new Invoice(100, new LocalDate(1970, 02, 13), this.itemType, this.seller, this.buyer)
        new Invoice(50, new LocalDate(1970, 02, 13), this.itemType, this.seller, this.buyer)
        expect:
        this.seller.toPay(1970) == 15.0f
    }

    def 'ignore cancelled'() {
        given:
        new Invoice(100, this.date, this.itemType, this.seller, this.buyer)
        Invoice invoice = new Invoice(100, this.date, this.itemType, this.seller, this.buyer)
        new Invoice(50, this.date, this.itemType, this.seller, this.buyer)

        when:
        invoice.cancel()

        then:
        this.seller.toPay(2018) == 15.0f
    }

}
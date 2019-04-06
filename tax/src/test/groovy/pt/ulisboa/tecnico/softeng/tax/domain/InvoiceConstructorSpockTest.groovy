package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException
import spock.lang.Shared
import spock.lang.Unroll


class InvoiceConstructorSpockTest extends SpockRollbackTestAbstractClass {
    String SELLER_NIF = "123456789"
    String BUYER_NIF = "987654321"
    String FOOD = "FOOD"
    @Shared int VALUE = 16
    int TAX = 23
    @Shared LocalDate date = new LocalDate(2018, 02, 13)

    @Shared Seller seller
    @Shared Buyer buyer
    @Shared ItemType itemType

    @Override
    def populate4Test() {
        IRS irs = IRS.getIRSInstance()
        this.seller = new Seller(irs, SELLER_NIF, "José Vendido", "Somewhere")
        this.buyer = new Buyer(irs, BUYER_NIF, "Manuel Comprado", "Anywhere")
        this.itemType = new ItemType(irs, FOOD, TAX)
    }

    def 'success'() {
        given:
        Invoice invoice = new Invoice(VALUE, this.date, this.itemType, this.seller, this.buyer)

        expect:
        invoice.getReference() != null
        invoice.getValue() == VALUE
        invoice.getDate() == date
        invoice.getItemType() == itemType
        invoice.getSeller() == seller
        invoice.getBuyer() == buyer
        invoice.getIva() == VALUE * TAX / 100.0f

        !invoice.isCancelled()
        this.seller.getInvoiceByReference(invoice.getReference()) == invoice
        this.buyer.getInvoiceByReference(invoice.getReference()) == invoice

    }

    @Unroll("#_value #_date #_itemType #_seller #_buyer")
    def 'exceptions'() {
        when:
        new Invoice(_value, _date, _itemType, _seller, _buyer)

        then:
        thrown(TaxException)

        where:
        _value | _date                       | _itemType     | _seller     | _buyer
        VALUE  | this.date                   | this.itemType | null        | this.buyer
        VALUE  | this.date                   | this.itemType | this.seller | null
        VALUE  | this.date                   | null          | this.seller | this.buyer
        0      | this.date                   | this.itemType | this.seller | this.buyer
        -23.6f | this.date                   | this.itemType | this.seller | this.buyer
        VALUE  | null                        | this.itemType | this.seller | this.buyer
        VALUE  | new LocalDate(1969, 12, 31) | this.itemType | this.seller | this.buyer

    }

	// this was not a test. could be removed
    def 'create invoice equal to 1970'() {
        when:
        new Invoice(VALUE, new LocalDate(1970, 01, 01), this.itemType, this.seller, this.buyer)

        then:
        noExceptionThrown()
    }

}

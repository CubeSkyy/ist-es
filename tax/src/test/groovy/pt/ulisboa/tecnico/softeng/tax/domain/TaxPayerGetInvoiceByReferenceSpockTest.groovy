package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException
import spock.lang.Shared
import spock.lang.Unroll


class TaxPayerGetInvoiceByReferenceSpockTest extends SpockRollbackTestAbstractClass{
    @Shared def SELLER_NIF = "123456789"
    @Shared def BUYER_NIF = "987654321"
    @Shared def FOOD = "FOOD"
    @Shared def VALUE = 16
    @Shared def TAX = 23
    @Shared def date = new LocalDate(2018, 02, 13)
    
    def seller
    def buyer
    def itemType
    def invoice
    
    @Override
    def populate4Test(){
        def irs = IRS.getIRSInstance()
        seller = new Seller(irs, SELLER_NIF, "José Vendido", "Somewhere")
        buyer = new Buyer(irs, BUYER_NIF, "Manuel Comprado", "Anywhere")
        itemType = new ItemType(irs, FOOD, TAX)
        invoice = new Invoice(VALUE, date, itemType, seller, buyer) 
    }

    def "success"() {
        expect:
        invoice == seller.getInvoiceByReference(invoice.getReference())
    }

    @Unroll('getInvoiceByReference: #invoiceReference')
    def 'exceptions'() {
        when:
        seller.getInvoiceByReference(invoiceReference)

        then:
        thrown(TaxException)

        where:
        invoiceReference << [null, ""]
    }

    def 'desNotExist'(){
        expect:
        seller.getInvoiceByReference(BUYER_NIF) == null
    }
}


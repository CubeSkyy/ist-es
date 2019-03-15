package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.DateTime
import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.tax.exception.TaxException
import pt.ulisboa.tecnico.softeng.tax.services.local.TaxInterface
import pt.ulisboa.tecnico.softeng.tax.services.remote.dataobjects.RestInvoiceData

import spock.lang.Shared
import spock.lang.Unroll

import javax.validation.constraints.Null

class TaxInterfaceSubmitInvoiceSpockTest extends SpockRollbackTestAbstractClass {
    @Shared
    def REFERENCE = "123456789"
    @Shared
    def SELLER_NIF = "123456789"
    @Shared
    def BUYER_NIF = "987654321"
    @Shared
    def FOOD = "FOOD"
    @Shared
    def VALUE = 160
    @Shared
    def TAX = 16
    @Shared
    def date = new LocalDate(2018, 02, 13)
    @Shared
    def time = new DateTime(2018, 02, 13, 10, 10)

    @Shared
    def newDate
    @Shared
    def newTime
    @Shared
    def invoiceData

    @Shared
    def irs

    @Shared date1970 = new LocalDate(1970, 01, 01)
    @Shared time1970 = new DateTime(1970, 01, 01, 10, 10)


    @Override
    def populate4Test() {
        irs = IRS.getIRSInstance()
        new Seller(irs, SELLER_NIF, "José Vendido", "Somewhere")
        new Buyer(irs, BUYER_NIF, "Manuel Comprado", "Anywhere")
        new ItemType(irs, FOOD, TAX)
    }

    def "success"() {

        given:
        def invoiceData = new RestInvoiceData(REFERENCE, SELLER_NIF, BUYER_NIF, FOOD, VALUE,
                date, time)
        def invoiceReference = TaxInterface.submitInvoice(invoiceData)

        def invoice = irs.getTaxPayerByNIF(SELLER_NIF).getInvoiceByReference(invoiceReference)

        expect:
        invoiceReference == invoice.getReference()
        invoice.getSeller().getNif() == SELLER_NIF
        invoice.getBuyer().getNif() == BUYER_NIF
        invoice.getItemType().getName() == FOOD
        invoice.getValue() == VALUE
        invoice.getDate() == date
    }

    def "submit twice"() {
        given:
        def invoiceData = new RestInvoiceData(REFERENCE, SELLER_NIF, BUYER_NIF, FOOD, VALUE,
                date, time)
        def invoiceReference = TaxInterface.submitInvoice(invoiceData)
        def secondInvoiceReference = TaxInterface.submitInvoice(invoiceData)

        expect:
        invoiceReference == secondInvoiceReference
    }

    @Unroll('#reference, #sellerNif, #buyerNif, #food, #value, #dateE, #timeE')
    def "tax exceptions"() {

        given:
        newDate = new LocalDate(1969, 12, 31)
        newTime = new DateTime(1969, 12, 31, 10, 10)
        invoiceData = new RestInvoiceData(reference, sellerNif, buyerNif, food, value, dateE, timeE)

        when:
        TaxInterface.submitInvoice(invoiceData)

        then:
        thrown(TaxException)

        where:
        reference | sellerNif  | buyerNif  | food | value  | dateE   | timeE
        REFERENCE | null       | BUYER_NIF | FOOD | VALUE  | date    | time
        REFERENCE | ""         | BUYER_NIF | FOOD | VALUE  | date    | time
        REFERENCE | SELLER_NIF | null      | FOOD | VALUE  | date    | time
        REFERENCE | SELLER_NIF | ""        | FOOD | VALUE  | date    | time
        REFERENCE | SELLER_NIF | BUYER_NIF | null | VALUE  | date    | time
        REFERENCE | SELLER_NIF | BUYER_NIF | ""   | VALUE  | date    | time
        REFERENCE | SELLER_NIF | BUYER_NIF | FOOD | 0.0d   | date    | time
        REFERENCE | SELLER_NIF | BUYER_NIF | FOOD | -23.7d | date    | time
        REFERENCE | SELLER_NIF | BUYER_NIF | FOOD | VALUE  | null    | time
        REFERENCE | SELLER_NIF | BUYER_NIF | FOOD | VALUE  | date    | null
        REFERENCE | SELLER_NIF | BUYER_NIF | FOOD | VALUE  | newDate | newTime

    }
    
    def "null reference exception"() {
        when:
        def invoiceData =new RestInvoiceData(null, SELLER_NIF, BUYER_NIF, FOOD, VALUE, date1970, time1970)
        TaxInterface.submitInvoice(invoiceData)

        then:
        thrown(TaxException)
    }

    def "equal 1970 no exception"() {
        given:
        def invoiceData = new RestInvoiceData(REFERENCE, SELLER_NIF, BUYER_NIF, FOOD, VALUE, date1970, time1970)

        when:
        TaxInterface.submitInvoice(invoiceData)

        then:
        noExceptionThrown()
    }
}

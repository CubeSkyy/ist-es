package pt.ulisboa.tecnico.softeng.tax.domain.services.local

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.tax.domain.Buyer
import pt.ulisboa.tecnico.softeng.tax.domain.IRS
import pt.ulisboa.tecnico.softeng.tax.domain.Invoice
import pt.ulisboa.tecnico.softeng.tax.domain.ItemType
import pt.ulisboa.tecnico.softeng.tax.domain.Seller
import pt.ulisboa.tecnico.softeng.tax.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException
import pt.ulisboa.tecnico.softeng.tax.services.local.TaxInterface
import spock.lang.Unroll

class IRSCancelInvoiceMethodSpockest extends SpockRollbackTestAbstractClass{
    def SELLER_NIF = "123456789"
    def BUYER_NIF = "987654321"
    def FOOD = "FOOD"
    def VALUE = 16
    def date = new LocalDate(2018, 02, 13)



    def irs
    def reference
    def invoice

    @Override
    def populate4Test() {
        irs = IRS.getIRSInstance()
        def seller = new Seller(this.irs, SELLER_NIF, "José Vendido", "Somewhere")
        def buyer = new Buyer(this.irs, BUYER_NIF, "Manuel Comprado", "Anywhere")
        def itemType = new ItemType(this.irs, FOOD, VALUE)
        this.invoice = new Invoice(30.0, this.date, itemType, seller, buyer)
        this.reference = this.invoice.getReference()
    }

    def 'success'(){
        when:'when cancelling an invoice'
        TaxInterface.cancelInvoice(this.reference)

        then:'is cancelled'
        assert this.invoice.isCancelled()
    }

    @Unroll('#label')
    def 'exceptions'(){
        when:'canceling a wrong ref'
        TaxInterface.cancelInvoice(ref)

        then:'throws an exception'
        thrown(TaxException)

        where:
        label | ref
        'wrong ref' | 'XXXXXXXX'
        'null ref'    | null
        'empty ref'   | "   "

    }


}
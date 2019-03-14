package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate

class TaxPayerGetTaxesPerYearMethodsSpockTest extends SpockRollbackTestAbstractClass {
    def  SELLER_NIF = "123456788"
    def  BUYER_NIF = "987654311"
    def  FOOD = "FOOD"
    def TAX = 10
    def date = new LocalDate(2018, 02, 13)
    
    def seller
    def buyer
    def itemType
    
    @Override
    def populate4Test(){
        def irs = IRS.getIRSInstance()
        def seller = new Seller(irs, SELLER_NIF, "José Vendido", "Somewhere")
        def buyer = new Buyer(irs, BUYER_NIF, "Manuel Comprado", "Anywhere")
        def itemType = new ItemType(irs, FOOD, TAX)
    }
    
    def "success"(){
        given:
        new Invoice(100, new LocalDate(2017, 12, 12), itemType, seller, buyer)
        new Invoice(100, date, itemType, seller, buyer)
        new Invoice(100, date, itemType, seller, buyer)
        new Invoice(50, date, itemType, seller, buyer)

        def toPay = seller.getToPayPerYear()

        expect:
        toPay.keySet().size() == 2
        toPay.get(2017) == 10.0d
        toPay.get(2018) == 25.0d

        def taxReturn = buyer.getTaxReturnPerYear()

        taxReturn.keySet().size() == 2
        taxReturn.get(2017) == 0.5d
        taxReturn.get(2018) == 1.25d
    }

    def "success empty"(){
        def toPay = seller.getToPayPerYear()

        toPay.keySet().size() == 0

        def taxReturn = buyer.getTaxReturnPerYear()

        taxReturn.keySet().size() == 0
    }
}

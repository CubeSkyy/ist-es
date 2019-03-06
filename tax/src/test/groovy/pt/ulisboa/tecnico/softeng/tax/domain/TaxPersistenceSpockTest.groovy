package pt.ulisboa.tecnico.softeng.tax.domain

import spock.lang.Specification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Test;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

class TaxPersistenceSpockTest extends SpockPersistenceTestAbstractClass {

    def SELLER_NIF = "123456789"
    def BUYER_NIF = "987654321"
    def FOOD = "FOOD"
    def VALUE = 16
    def date = new LocalDate(2018, 02, 13)

    @Override
    def whenCreateInDatabase() {
        def irs = IRS.getIRSInstance()
        def seller = new Seller(irs, SELLER_NIF, "José Vendido", "Somewhere")
        def buyer = new Buyer(irs, BUYER_NIF, "Manuel Comprado", "Anywhere")
        def it = new ItemType(irs, FOOD, VALUE)

        new Invoice(VALUE, this.date, it, seller, buyer)
    }

    @Override
    def thenAssert() {
        def irs = IRS.getIRSInstance()

        assert irs.getTaxPayerSet().size() == 2

        def taxPayer1 = new ArrayList<TaxPayer>(irs.getTaxPayerSet()).get(0)

        if (taxPayer1 instanceof Seller) {
            assert taxPayer1.getNif() == SELLER_NIF
        } else {
            assert taxPayer1.getNif() == BUYER_NIF
        }

        def taxPayer2 = new ArrayList<TaxPayer>(irs.getTaxPayerSet()).get(1)

        if (taxPayer2 instanceof Seller) {
            assert taxPayer2.getNif() == SELLER_NIF
        } else {
            assert taxPayer2.getNif() == BUYER_NIF
        }

        assert irs.getItemTypeSet().size() == 1
        def itemType = new ArrayList<ItemType>(irs.getItemTypeSet()).get(0)
        assert itemType.getTax() == VALUE
        assert itemType.getName() == FOOD

        assert irs.getInvoiceSet().size() == 1
        def invoice = new ArrayList<Invoice>(irs.getInvoiceSet()).get(0)
        assert invoice.getValue() == VALUE
        assert invoice.getReference() != null
        assert date == invoice.getDate()
        assert invoice.getBuyer().getNif() == BUYER_NIF
        assert invoice.getSeller().getNif() == SELLER_NIF
        assert invoice.getItemType() == itemType
        assert invoice.getTime() != null
        assert !invoice.getCancelled()

    }

    @Override
    def deleteFromDatabase() {
        FenixFramework.getDomainRoot().getIrs().delete()
    }
}
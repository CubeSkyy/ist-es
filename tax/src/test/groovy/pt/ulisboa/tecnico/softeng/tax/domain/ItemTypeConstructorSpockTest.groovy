package pt.ulisboa.tecnico.softeng.tax.domain

import pt.ulisboa.tecnico.softeng.tax.exception.TaxException
import spock.lang.Shared
import spock.lang.Unroll

class ItemTypeConstructorSpockTest extends SpockRollbackTestAbstractClass {
    @Shared
    def CAR = "CAR"
    @Shared
    def TAX = 23

    def irs

    @Override
    def populate4Test() {
        this.irs = IRS.getIRSInstance()
    }

    def 'success'() {
        given:
        def itemType = new ItemType(irs, CAR, TAX)

        expect:
        itemType.getName() == CAR
        itemType.getTax() == TAX
        IRS.getIRSInstance().getItemTypeByName(CAR) != null

        irs.getItemTypeByName(CAR) == itemType
    }

    def 'unique name'() {
        given:
        def itemType = new ItemType(this.irs, CAR, TAX)

        when:
        new ItemType(this.irs, CAR, TAX)

        then:
        thrown(TaxException)
        IRS.getIRSInstance().getItemTypeByName(CAR) == itemType
    }

    @Unroll('#name, #tax')
    def 'exceptions'() {
        when:
        new ItemType(this.irs, name, tax)

        then:
        thrown(TaxException)

        where:
        name | tax
        null | TAX
        ""   | TAX
        CAR  | -34
    }

	// this was not a test. could be removed
    def 'zero tax'() {
        given:
        new ItemType(this.irs, CAR, 0)
    }
}

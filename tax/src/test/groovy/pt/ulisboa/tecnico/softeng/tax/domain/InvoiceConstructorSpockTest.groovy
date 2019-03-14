package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.tax.exception.TaxException
import spock.lang.Shared
import spock.lang.Unroll

class InvoiceConstructorSpockTest extends SpockRollbackTestAbstractClass {
	def SELLER_NIF = '123456789'
	def BUYER_NIF = '987654321'
	def FOOD = 'FOOD'
	@Shared def VALUE = 16
	def TAX = 23
	@Shared def date = new LocalDate(2018,02,13)
	@Shared def seller
	@Shared def buyer
	@Shared def itemType

	@Override
	def populate4Test() {
		def irs = IRS.getIRSInstance()

		seller = new Seller(irs,SELLER_NIF,'José Vendido','Somewhere')
		buyer = new Buyer(irs,BUYER_NIF,'Manuel Comprado','Anywhere')
		itemType = new ItemType(irs,FOOD,TAX)
	}

	def 'success'() {
		when:
		def invoice = new Invoice(VALUE, date, itemType, seller, buyer)

		then:
		with(invoice) {
			getReference() != null
			16.0 == getValue()
			getDate() == date
			getItemType() == itemType
			getSeller() == seller
			getBuyer() == buyer
			3.68 == getIva()
			!isCancelled()
		}

		seller.getInvoiceByReference(invoice.getReference()) == invoice
		buyer.getInvoiceByReference(invoice.getReference()) == invoice
	}

	@Unroll('testing exceptions: #value, #dt, #it, #sel, #buy')
	def 'testing exceptions'() {
		when:
		new Invoice(value, dt, it, sel, buy)

		then:
		thrown(TaxException)

		where:
		value  | dt   | it       | sel    | buy
		VALUE  | date | itemType | null   | buyer
		VALUE  | date | itemType | seller | null
		VALUE  | date | null     | seller | buyer
		0      | date | null     | seller | buyer
		-23.6f | date | null     | seller | buyer
		VALUE  | null | itemType | seller | buyer
		VALUE  | new LocalDate(1969,12,31) | itemType | seller | buyer
	}
}
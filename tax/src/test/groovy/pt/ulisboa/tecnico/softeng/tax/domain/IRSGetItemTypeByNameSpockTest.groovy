package pt.ulisboa.tecnico.softeng.tax.domain

import pt.ulisboa.tecnico.softeng.tax.domain.IRS
import pt.ulisboa.tecnico.softeng.tax.domain.ItemType


class IRSGetItemTypeByNameSpockTest extends SpockRollbackTestAbstractClass {
	def FOOD = "FOOD"
	def VALUE = 16

	def irs

	@Override
	def populate4Test() {
		irs = IRS.getIRSInstance()
		new ItemType(irs, FOOD, VALUE)
	}

	def 'success'() {
		given:
		def itemType = irs.getItemTypeByName(FOOD)
		expect:
		itemType.getName() != null
		itemType.getName() == FOOD
	}

	def 'nullName'() {
		given:
		def itemType = irs.getItemTypeByName(null)
		expect:
		itemType == null
	}

	def 'emptyName'() {
		given:
		def itemType = irs.getItemTypeByName("")
		expect:
		itemType == null
	}

	def 'doesNotExistName'() {
		given:
		def itemType = irs.getItemTypeByName("CAR")
		expect:
		itemType == null
	}
}

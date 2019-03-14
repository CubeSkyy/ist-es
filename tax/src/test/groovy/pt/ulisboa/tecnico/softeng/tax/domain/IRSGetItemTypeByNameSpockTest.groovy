package pt.ulisboa.tecnico.softeng.tax.domain

class IRSGetItemTypeByNameSpockTest extends SpockRollbackTestAbstractClass {
	def FOOD = "FOOD"
	def VALUE = 16

	def irs = IRS.getIRSInstance()

	def 'populate4Test'() {
		new ItemType(irs, FOOD, VALUE)
	}

	def 'success'() {
		def itemType = irs.getItemTypeByName(FOOD)

		itemType.getName() != null
		itemType.getName() == FOOD
	}

	def 'nullName'() {
		def itemType = irs.getItemTypeByName(null)

		itemType == null
	}

	def 'emptyName'() {
		def itemType = irs.getItemTypeByName("")

		itemType == null
	}

	def 'doesNotExistName'() {
		def itemType = irs.getItemTypeByName("CAR")

		itemType == null
	}
}

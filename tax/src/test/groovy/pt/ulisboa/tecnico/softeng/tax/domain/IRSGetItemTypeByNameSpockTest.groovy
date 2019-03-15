package pt.ulisboa.tecnico.softeng.tax.domain

class IRSGetItemTypeByNameSpockTest extends SpockRollbackTestAbstractClass {
	def FOOD = "FOOD"
	def VALUE = 16

	def irs = IRS.getIRSInstance()

	@Override
	def populate4Test() {
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

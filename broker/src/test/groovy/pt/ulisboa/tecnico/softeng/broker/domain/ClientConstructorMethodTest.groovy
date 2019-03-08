package pt.ulisboa.tecnico.softeng.broker.domain

import org.joda.time.LocalDate
import spock.lang.Shared
import spock.lang.Unroll

import org.junit.Test

import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException

class ClientConstructorMethodTest extends SpockRollbackTestAbstractClass {
	@Shared def broker
	@Shared def BROKER_IBAN = "BROKER_IBAN"
	@Shared def NIF_AS_BUYER = "buyerNIF"
	@Shared def BROKER_NIF_AS_SELLER = "sellerNIF"
	@Shared def CLIENT_NIF = "123456789"
	@Shared def DRIVING_LICENSE = "IMT1234"
	@Shared def AGE = 20
	@Shared def MARGIN = 0.3
	@Shared def CLIENT_IBAN = "BK011234567"

	@Shared def begin = new LocalDate(2016, 12, 19);
	@Shared def end = new LocalDate(2016, 12, 21);
	@Shared def arrival = new LocalDate(2016, 12, 19);
	@Shared def departure = new LocalDate(2016, 12, 21);

	@Override
	def populate4Test() {
		broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN);
	}

	def 'success'() {
		given:
		def client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE);

		expect:
		client.getIban() == CLIENT_IBAN
		client.getNif() == CLIENT_NIF
		client.getAge() == AGE
	}

	@Unroll('Adventure creation: #_broker, #_client_iban, #_client_nif, #driving_license, #_age')
	def 'exceptions'(){
		when:
		new Client(_broker, _client_iban, _client_nif, _driving_license, _age)

		then:
		thrown(BrokerException)

		where:
		_broker | _client_iban | _client_nif | _driving_license | _age
		null	| CLIENT_IBAN	| CLIENT_NIF | DRIVING_LICENSE | AGE
		broker	| null	| CLIENT_NIF | DRIVING_LICENSE | AGE
		broker	| "   "	| CLIENT_NIF | DRIVING_LICENSE | AGE
		broker	| CLIENT_IBAN	| null| DRIVING_LICENSE | AGE
		broker	| CLIENT_IBAN	| "    "| DRIVING_LICENSE | AGE
		broker	| CLIENT_IBAN	| CLIENT_NIF | DRIVING_LICENSE | -1
		broker  | CLIENT_IBAN	| CLIENT_NIF | "      " | AGE
	}

	def 'clientExistsWithSameIBAN'() {
		given:
			new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
		when:
			new Client(broker, CLIENT_IBAN, "OTHER_NIF", DRIVING_LICENSE + "1", AGE)
		then:
			thrown(BrokerException)
	}

	@Test
	def 'nullDrivingLicense'() {
		given:
		def client = new Client(this.broker, CLIENT_IBAN, CLIENT_NIF, null, AGE);

		expect:
		client.getIban() == CLIENT_IBAN
		client.getNif() == CLIENT_NIF
		client.getAge() == AGE
		client.getDrivingLicense() == null
	}

}

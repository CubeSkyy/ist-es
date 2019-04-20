package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Shared
import spock.lang.Unroll

class OperationConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def bank
	@Shared def account

	@Override
	def populate4Test() {
		bank = new Bank('Money', 'BK01')
		def client = new Client(bank, 'António')
		account = new Account(bank, client)
	}

	def 'success'() {
		when: 'when creating an operation'
		def operation = new DepositOperation(account, 1000);
//		def operation = new Operation(Type.DEPOSIT, account, 1000)

		then: 'the object should hold the proper values'
		with(operation) {
			getReference().startsWith(bank.getCode())
			getReference().length() > Bank.CODE_SIZE
			operation instanceof DepositOperation
//			getType() == Type.DEPOSIT
			getAccount() == account
			getValue() == 1000
			getTime() != null
			bank.getOperation(getReference()) == operation
		}
	}


//	@Unroll('operation: #type, #acc, #value')
//	def 'exception'() {
//		when: 'when creating an invalid operation'
//		new Operation(type, acc, value)
//
//		then: 'throw an exception'
//		thrown(BankException)
//
//		where:
//		type          | acc     | value
//		null          | account | 1000
//		Type.WITHDRAW | null    | 1000
//		Type.DEPOSIT  | account | 0
//		Type.DEPOSIT  | account | -1000
//		Type.WITHDRAW | account | 0
//		Type.WITHDRAW | account | -1000
//	}

	@Unroll('operation: #acc, #value')
	def 'exceptionWithdraw'() {
		when: 'when creating an invalid operation'
		new WithdrawOperation( acc, value)

		then: 'throw an exception'
		thrown(BankException)

		where:
		 acc     | value
		 null    | 1000
		 account | 0
		 account | -1000
	}

	@Unroll('operation: #acc, #value')
	def 'exceptionDeposit'() {
		when: 'when creating an invalid operation'
		new DepositOperation(acc, value)

		then: 'throw an exception'
		thrown(BankException)

		where:
		 acc     | value
		 account | 0
		 account | -1000
	}

	def 'one amount'() {
		when:
//		def operation = new Operation(Type.DEPOSIT, account, 1)
		def operation = new DepositOperation(account, 1)

		then:
		bank.getOperation(operation.getReference()) == operation
	}
}

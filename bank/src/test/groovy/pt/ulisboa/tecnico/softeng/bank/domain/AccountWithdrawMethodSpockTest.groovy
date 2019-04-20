package pt.ulisboa.tecnico.softeng.bank.domain
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll

class AccountWithdrawMethodSpockTest extends SpockRollbackTestAbstractClass {
	def bank
	def account

	@Override
	def populate4Test() {
		bank = new Bank('Money','BK01')
		def client = new Client(bank,'António')

		account = new Account(bank,client)
	}

	@Unroll('Withdraw: #label')
	def 'success'() {
		given: 'an account with balance 100'
		account.deposit(100000)

		when: 'when withdrawing 40'
		def reference= account.withdraw(amnt).getReference()

		then: 'success'
		balance == account.getBalance()
		def operation = bank.getOperation(reference)
		operation != null
		operation.getType() == Operation.Type.WITHDRAW
		operation.getAccount() == account
		amnt == operation.getValue()

		where:
		label              | amnt | balance
		'forty'            | 40000   | 60000
		'one amount'       | 1000    | 99000
		'equal to balance' | 100000  | 0
	}

	@Unroll('Withdraw: #label')
	def 'throwing exception'() {
		when: 'when withdrawing an invalid amount'
		account.withdraw(amnt)

		then: 'throw an exception'
		thrown(BankException)

		where:
		amnt | label
		0    | 'zero amount'
		-20000  | 'negative amount'
		101000  | 'equal to balance plus one'
		150000  | 'more than balance'
	}
}

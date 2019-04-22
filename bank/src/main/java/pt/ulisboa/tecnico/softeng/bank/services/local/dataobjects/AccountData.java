package pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects;

import pt.ulisboa.tecnico.softeng.bank.domain.Account;

public class AccountData {
	private String iban;
	private Double balance;
	private Double amount;

	public AccountData() {
	}

	public AccountData(Account account) {
		this.iban = account.getIBAN();
		this.balance = (double)account.getBalance()/1000;
	}

	public String getIban() {
		return this.iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public Long getBalance() {
		long l = (new Double(this.balance*1000)).longValue();
		return l;
	}

	public void setBalance(Long balance) {
		this.balance = (double)balance/1000;
	}

	public Long getAmount() {
		long l = (new Double(this.amount*1000)).longValue();
		return l;
	}

	public void setAmount(Long amount) {
		this.amount = (double)amount/1000;
	}

}

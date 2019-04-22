package pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects;

import org.joda.time.DateTime;

import pt.ulisboa.tecnico.softeng.bank.domain.Account;
import pt.ulisboa.tecnico.softeng.bank.domain.Operation;
import pt.ulisboa.tecnico.softeng.bank.domain.TransferOperation;

import java.util.Iterator;

public class BankOperationData {
	private String reference;
	private String iban;
	private String sourceIban;
	private String targetIban;
	private double value;
	private DateTime time;
	private String transactionSource;
	private String transactionReference;

	public BankOperationData() {
	}

	public BankOperationData(Operation operation) {
		Iterator<Account> it = operation.getAccountSet().iterator();
		this.reference = operation.getReference();
		this.iban = it.next().getIBAN();
		this.value = (double)operation.getValue()/1000;
		this.time = operation.getTime();
		this.transactionSource = operation.getTransactionSource();
		this.transactionReference = operation.getTransactionReference();
	}

	public BankOperationData(TransferOperation operation) {
		Iterator<Account> it = operation.getAccountSet().iterator();
		this.reference = operation.getReference();
		this.sourceIban = it.next().getIBAN();
		this.targetIban = it.next().getIBAN();
		this.value = (double)operation.getValue()/1000;
		this.time = operation.getTime();
		this.transactionSource = operation.getTransactionSource();
		this.transactionReference = operation.getTransactionReference();
	}


	public BankOperationData(String iban, double value, String transactionSource, String transactionReference) {
		this.iban = iban;
		this.value = (double)value/1000;
		this.transactionSource = transactionSource;
		this.transactionReference = transactionReference;
	}

	public BankOperationData(String sourceIban, String targetIban, double value, String transactionSource, String transactionReference) {
		this.sourceIban = sourceIban;
		this.targetIban = targetIban;
		this.value = (double)value/1000;
		this.transactionSource = transactionSource;
		this.transactionReference = transactionReference;
	}

	public String getReference() {
		return this.reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getIban() {
		return this.iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public String getSourceIban() {
		return this.sourceIban;
	}

	public void setSourceIban(String iban) {
		this.sourceIban = iban;
	}

	public String getTargetIban() {
		return this.targetIban;
	}

	public void setTargetIban(String iban) {
		this.targetIban = iban;
	}

	public Long getValue() {
		long l = (new Double(this.value*1000)).longValue();
		return l;
	}

	public void setValue(Long value) {
		this.value = (double)value/1000;
	}

	public DateTime getTime() {
		return this.time;
	}

	public void setTime(DateTime time) {
		this.time = time;
	}

	public String getTransactionSource() {
		return this.transactionSource;
	}

	public void setTransactionSource(String transactionSource) {
		this.transactionSource = transactionSource;
	}

	public String getTransactionReference() {
		return this.transactionReference;
	}

	public void setTransactionReference(String transactionReference) {
		this.transactionReference = transactionReference;
	}

}

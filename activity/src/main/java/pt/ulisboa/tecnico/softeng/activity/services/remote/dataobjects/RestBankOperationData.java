package pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.joda.deser.DateTimeDeserializer;

public class RestBankOperationData {
	private String reference;
	private String type;
	private String sourceIban;
	private String targetIban;
	private Long value;
	@JsonDeserialize(using = DateTimeDeserializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	private DateTime time;
	private String transactionSource;
	private String transactionReference;

	public RestBankOperationData() {
	}

	public RestBankOperationData(String sourceIban, String targetIban, long value, String transactionSource, String transactionReference) {
		this.sourceIban = sourceIban;
		this.targetIban = targetIban;
		this.value = value;
		this.transactionSource = transactionSource;
		this.transactionReference = transactionReference;
	}

	public String getReference() {
		return this.reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
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
		return this.value;
	}

	public void setValue(Long value) {
		this.value = value;
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

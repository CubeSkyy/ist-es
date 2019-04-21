package pt.ulisboa.tecnico.softeng.tax.domain;

import pt.ulisboa.tecnico.softeng.tax.exception.TaxException;
import pt.ulisboa.tecnico.softeng.tax.services.local.dataobjects.TaxPayerData;

import java.util.Map;
import java.util.Set;

public /*abstract */ class TaxPayer extends TaxPayer_Base {
	/*
	TaxPayer() {
		// this is a FenixFramework artifact; if not present, compilation fails.
		// the empty constructor is used by the base class to materialize objects from
		// the database, and in this case the classes Seller_Base and Buyer_Base, which
		// extend this class, have the empty constructor, which need to be present in
		// their superclass
		super();
	}
	*/

	public TaxPayer(IRS irs, String NIF, String name, String address) {
		checkArguments(irs, NIF, name, address);

		setNif(NIF);
		setName(name);
		setAddress(address);

		irs.addTaxPayer(this);
	}

	public void delete() {
		setIrs(null);

		deleteDomainObject();
	}

	protected void checkArguments(IRS irs, String NIF, String name, String address) {
		if (NIF == null || NIF.length() != 9) {
			throw new TaxException();
		}

		if (name == null || name.length() == 0) {
			throw new TaxException();
		}

		if (address == null || address.length() == 0) {
			throw new TaxException();
		}

		if (irs.getTaxPayerByNIF(NIF) != null) {
			throw new TaxException();
		}

	}

	public Invoice getInvoiceByReference(String invoiceReference) {
		if (invoiceReference == null || invoiceReference.isEmpty()) {
			throw new TaxException();
		}

		for (Invoice invoice : getBuyerinvoiceSet()) {
			if (invoice.getReference().equals(invoiceReference)) {
				return invoice;
			}
		}
		for (Invoice invoice : getSellerinvoiceSet()) {
			if (invoice.getReference().equals(invoiceReference)) {
				return invoice;
			}
		}
		return null;
	}

	public double calculate(TaxPayerStrategy tps, int year){
		return tps.calculate(year, this);
	}

	public Map<Integer, Double>  calculatePerYear (TaxPayerStrategy tps){
		return tps.calculate(this);
	}

	public Set<Invoice> getBuyerinvoiceSet(){ return super.getBuyerinvoiceSet(); }
	public Set<Invoice> getSellerinvoiceSet(){ return super.getSellerinvoiceSet(); }

}

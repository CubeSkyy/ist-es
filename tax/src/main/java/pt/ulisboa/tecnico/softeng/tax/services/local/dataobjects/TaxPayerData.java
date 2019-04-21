package pt.ulisboa.tecnico.softeng.tax.services.local.dataobjects;

import java.util.Map;
import java.util.TreeMap;

import pt.ulisboa.tecnico.softeng.tax.domain.*;

public class TaxPayerData {


	private String nif;
	private String name;
	private String address;

	private Map<Integer, Double> sellertaxes = new TreeMap<Integer, Double>();
	private Map<Integer, Double> buyertaxes = new TreeMap<Integer, Double>();

	public TaxPayerData() {
	}

	public TaxPayerData(TaxPayer taxPayer) {
		this.nif = taxPayer.getNif();
		this.name = taxPayer.getName();
		this.address = taxPayer.getAddress();
		this.buyertaxes = taxPayer.calculatePerYear(new BuyerStrategy());
		this.sellertaxes = taxPayer.calculatePerYear(new SellerStrategy());
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNif() {
		return this.nif;
	}

	public void setNif(String nif) {
		this.nif = nif;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Map<Integer, Double> getBuyerTaxes() {
		return this.buyertaxes;
	}
	public Map<Integer, Double> getSellertaxes() {
		return this.sellertaxes;
	}

	public void setBuyertaxes(Map<Integer, Double> taxes) {
		this.buyertaxes = taxes;
	}
	public void setSellertaxes(Map<Integer, Double> taxes) {
		this.sellertaxes = taxes;
	}

}

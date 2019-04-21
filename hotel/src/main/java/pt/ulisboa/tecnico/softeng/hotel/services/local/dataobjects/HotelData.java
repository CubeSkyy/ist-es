package pt.ulisboa.tecnico.softeng.hotel.services.local.dataobjects;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import pt.ulisboa.tecnico.softeng.hotel.domain.Hotel;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room;

public class HotelData {
	private String code;
	private String name;
	private String nif;
	private String iban;
	private Double priceSingle;
	private Double priceDouble;
	private List<RoomData> rooms;

	public HotelData() {
	}

	public HotelData(Hotel hotel) {
		this.code = hotel.getCode();
		this.name = hotel.getName();

		this.rooms = hotel.getRoomSet().stream().sorted(Comparator.comparing(Room::getNumber))
				.map(RoomData::new).collect(Collectors.toList());
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public String getIban() {
		return this.iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}


	public Long getPriceSingle() {
		long l = (new Double(this.priceSingle*1000)).longValue();
		return l;
	}

	public void setPriceSingle(Long priceSingle) {
		this.priceSingle = (double)priceSingle/1000;
	}


	public Long getPriceDouble() {
		long l = (new Double(this.priceDouble*1000)).longValue();
		return l;
	}

	public void setPriceDouble(Long priceDouble) {
		this.priceDouble = (double)priceDouble/1000;
	}

	public List<RoomData> getRooms() {
		return this.rooms;
	}

	public void setRooms(List<RoomData> rooms) {
		this.rooms = rooms;
	}

}

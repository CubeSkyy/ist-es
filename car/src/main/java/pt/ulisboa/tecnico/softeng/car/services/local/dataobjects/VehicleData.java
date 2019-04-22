package pt.ulisboa.tecnico.softeng.car.services.local.dataobjects;

import pt.ulisboa.tecnico.softeng.car.domain.Vehicle;

public class VehicleData {
    private Vehicle.Type type;
    private String plate;
    private Integer kilometers;
    private Double price;
    private RentACarData rentacar;

    public VehicleData() { }

    public VehicleData(Vehicle.Type type, String plate, int kilometers, Long price, RentACarData rentacar) {
        this.type = type;
        this.plate = plate;
        this.kilometers = kilometers;
        this.price = (double)price/1000;
        this.rentacar = rentacar;
    }

    public Vehicle.Type getType() {
        return type;
    }

    public void setType(Vehicle.Type type) {
        this.type = type;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public Integer getKilometers() {
        return kilometers;
    }

    public void setKilometers(Integer kilometers) {
        this.kilometers = kilometers;
    }

    public Long getPrice() {
        long l = (new Double(this.price*1000)).longValue() ;
        return l;
    }

    public void setPrice(Long price) {
        this.price = (double)price/1000;
    }

    public RentACarData getRentacar() {
        return rentacar;
    }

    public void setRentACar(RentACarData rentACar) {
        this.rentacar = rentACar;
    }
}

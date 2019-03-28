package pt.ulisboa.tecnico.softeng.broker.domain;

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface.Type;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;

public class BookRoomState extends BookRoomState_Base {
	public static final int MAX_REMOTE_ERRORS = 10;
	public Type type = Type.SINGLE;
	@Override
	public State getValue() {
		return State.BOOK_ROOM;
	}

	public void setType(Type t){ type = t;}
	@Override
	public void process() {
		try {


			boolean found = false;
			//Buscar o bulk associado ao broker desta aventura
			//Hierarquia: Aventura tem broker que tem Bulks que esses tem várias referencias
			for (BulkRoomBooking bulk: getAdventure().getBroker().getRoomBulkBookingSet()) {
				//A referencia do booking data
				RestRoomBookingData bulkbookingData = bulk.getRoomBookingData4Type(Type.SINGLE.toString());
				if (bulkbookingData != null){
					getAdventure().setRoomConfirmation(bulkbookingData.getReference());
					getAdventure().incAmountToPay(bulkbookingData.getPrice());
					found = true;
					break;
				}
			}
			if(!found) {
				RestRoomBookingData bookingData = getAdventure().getBroker().getHotelInterface().reserveRoom(new RestRoomBookingData(Type.SINGLE,
						getAdventure().getBegin(), getAdventure().getEnd(), getAdventure().getBroker().getNifAsBuyer(),
						getAdventure().getBroker().getIban(), getAdventure().getID()));
				getAdventure().setRoomConfirmation(bookingData.getReference());
				getAdventure().incAmountToPay(bookingData.getPrice());
			}
		} catch (HotelException he) {
			getAdventure().setState(State.UNDO);
			return;
		} catch (RemoteAccessException rae) {
			incNumOfRemoteErrors();
			if (getNumOfRemoteErrors() == MAX_REMOTE_ERRORS) {
				getAdventure().setState(State.UNDO);
			}
			return;
		}

		if (getAdventure().shouldRentVehicle()) {
			getAdventure().setState(State.RENT_VEHICLE);
		} else {
			getAdventure().setState(State.PROCESS_PAYMENT);
		}
	}

}

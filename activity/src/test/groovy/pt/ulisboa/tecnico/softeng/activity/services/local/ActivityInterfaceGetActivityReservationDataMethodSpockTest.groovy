package pt.ulisboa.tecnico.softeng.activity.services.local;

import org.joda.time.LocalDate;
import spock.lang.*;

import pt.ulisboa.tecnico.softeng.activity.domain.Activity;
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityOffer;
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityProvider;
import pt.ulisboa.tecnico.softeng.activity.domain.Booking;
import pt.ulisboa.tecnico.softeng.activity.domain.RollbackSpockTestAbstractClass;
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestActivityBookingData;

public class ActivityInterfaceGetActivityReservationDataMethodSpockTest extends RollbackSpockTestAbstractClass {
	def NAME = "ExtremeAdventure";
	def CODE = "XtremX";
	def begin = new LocalDate(2016, 12, 19);
	def end = new LocalDate(2016, 12, 21);
	def provider;
	def offer;
	def booking;

	@Override
	def populate4Test() {
		this.provider = new ActivityProvider(CODE, NAME, "NIF", "IBAN");
		def activity = new Activity(this.provider, "Bush Walking", 18, 80, 3);

		this.offer = new ActivityOffer(activity, this.begin, this.end, 30);
	}

	def 'success'() {
		given:
		this.booking = new Booking(this.provider, this.offer, "123456789", "IBAN");

		def data = ActivityInterface.getActivityReservationData(this.booking.getReference());

		expect:
		this.booking.getReference() == data.getReference()
		data.getCancellation() == null
		NAME == data.getName()
		CODE == data.getCode()
		this.begin == data.getBegin()
		this.end == data.getEnd()
		data.getCancellationDate() == null
	}

 	def 'success cancelled'() {
		given:
		this.booking = new Booking(this.provider, this.offer, "123456789", "IBAN");
		this.provider.getProcessor().submitBooking(this.booking);
		this.booking.cancel();
		def data = ActivityInterface.getActivityReservationData(this.booking.getCancel());

		expect:
		this.booking.getReference() == data.getReference()
		this.booking.getCancel() == data.getCancellation()
		NAME == data.getName()
		CODE == data.getCode()
		this.begin == data.getBegin()
		this.end == data.getEnd()
		data.getCancellationDate() != null
	}

	def 'null reference'() {
		given:
		ActivityInterface.getActivityReservationData(null);

		expect:
		thrown(ActivityException)
	}

	def 'empty reference'() {
		given:
		ActivityInterface.getActivityReservationData("")

		expect:
		thrown(ActivityException)
	}

	def 'not exists reference'() {
		given:
		ActivityInterface.getActivityReservationData("XPTO")

		expect:
		thrown(ActivityException)
	}

}

package pt.ulisboa.tecnico.softeng.activity.domain;

import java.util.List;

import org.joda.time.LocalDate;
import spock.lang.*;

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;

public class ActivityProviderFindOfferMethodSpockTest extends RollbackSpockTestAbstractClass {
	static final MIN_AGE = 25;
	static final MAX_AGE = 80;
	static final CAPACITY = 25;
	static final AGE = 40;
	def begin = new LocalDate(2016, 12, 19);
	def end = new LocalDate(2016, 12, 21);

  def provider;
	def activity;
	def offer;

	@Override
	def populate4Test() {
		this.provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", "IBAN");
		this.activity = new Activity(this.provider, "Bush Walking", MIN_AGE, MAX_AGE, CAPACITY);

		this.offer = new ActivityOffer(this.activity, this.begin, this.end, 30);
	}

	def 'success'() {
    given:
		def offers = this.provider.findOffer(this.begin, this.end, AGE);

    expect:
		1 == offers.size()
		offers.contains(this.offer)
	}

	@Unroll('findOffer: #bgn, #nd, #a')
	def 'exceptions'() {
		when:
		this.provider.findOffer(bgn, nd, a)

		then:
		thrown(ActivityException)

		where:
		bgn | nd | a
		null | this.end | AGE
		this.begin | null | AGE
	}
	
	def 'success age equal min'() {
    given:
		def offers = this.provider.findOffer(this.begin, this.end, MIN_AGE);

    expect:
	  1 == offers.size()
		offers.contains(this.offer)
	}

	def 'age minus one than minimal'() {
    given:
		def offers = this.provider.findOffer(this.begin, this.end, MIN_AGE - 1);

    expect:
		offers.isEmpty()
	}

	def 'success age equal max'() {
    given:
		def offers = this.provider.findOffer(this.begin, this.end, MAX_AGE);

    expect:
		1 == offers.size()
		offers.contains(this.offer)
	}

	def 'age plus one than minimal'() {
    given:
    def offers = this.provider.findOffer(this.begin, this.end, MAX_AGE + 1);

    expect:
		offers.isEmpty()
	}

	def 'empty activity set'() {
    given:
    def otherProvider = new ActivityProvider("Xtrems", "Adventure", "NIF2", "IBAN");

		def offers = otherProvider.findOffer(this.begin, this.end, AGE);

    expect:
		offers.isEmpty()
	}

	def 'empty activity offer set'() {
    when:
		def otherProvider = new ActivityProvider("Xtrems", "Adventure", "NIF2", "IBAN");
		new Activity(otherProvider, "Bush Walking", 18, 80, 25);

		def offers = otherProvider.findOffer(this.begin, this.end, AGE);

    then:
		offers.isEmpty()
	}

	def 'two match activity offers'() {
    when:
		new ActivityOffer(this.activity, this.begin, this.end, 30);

		def offers = this.provider.findOffer(this.begin, this.end, AGE);

    then:
		2 == offers.size()
	}

	def 'one match activity offer and one not match'() {
    when:
		new ActivityOffer(this.activity, this.begin, this.end.plusDays(1), 30);

		def offers = this.provider.findOffer(this.begin, this.end, AGE);

    then:
		1 == offers.size()
	}

	def 'one match activity offer and other no capacity'() {
    when:
		def otherActivity = new Activity(this.provider, "Bush Walking", MIN_AGE, MAX_AGE, 1);

		def otherActivityOffer = new ActivityOffer(otherActivity, this.begin, this.end, 30);
		new Booking(this.provider, otherActivityOffer, "123456789", "IBAN");

		def offers = this.provider.findOffer(this.begin, this.end, AGE);

    then:
		1 == offers.size()
	}

}

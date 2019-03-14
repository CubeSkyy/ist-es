package pt.ulisboa.tecnico.softeng.activity.domain;

import org.joda.time.LocalDate;
import spock.lang.*;

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;

class ActivityOfferMatchDateMethodSpockTest extends RollbackSpockTestAbstractClass {
	def begin = new LocalDate(2016, 12, 19);
	def end = new LocalDate(2016, 12, 23);

	def offer;

	@Override
  def populate4Test() {
		def provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", "IBAN");
		def activity = new Activity(provider, "Bush Walking", 18, 80, 3);

		this.offer = new ActivityOffer(activity, this.begin, this.end, 30);
	}

  def 'sucess'() {
    expect:
    this.offer.matchDate(this.begin, this.end)
  }

	@Unroll('matchDate: #bgn, #nd')
	def 'exceptions'() {
		when:
		this.offer.matchDate(bgn, nd)

		then:
		thrown(ActivityException)

		where:
		bgn        | nd
		null       | this.end
		this.begin | null
	}

  def 'begin plus one'() {
    expect:
    this.offer.matchDate(this.begin.plusDays(1), this.end) == false
  }

  def 'begin minus one'() {
    expect:
    this.offer.matchDate(this.begin.minusDays(1), this.end) == false
  }

  def 'end plus one'() {
    expect:
    this.offer.matchDate(this.begin, this.end.plusDays(1)) == false
  }

  def 'end minus one'() {
    expect:
    this.offer.matchDate(this.begin, this.end.minusDays(1)) == false
  }
}

package pt.ulisboa.tecnico.softeng.activity.domain;

import org.joda.time.LocalDate;
import spock.lang.*;

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;

class ActivityOfferConstructorMethodSpockTest extends RollbackSpockTestAbstractClass {
  static final CAPACITY = 25;
  static final MAX_AGE = 50;
  static final MIN_AGE = 25;
  def begin = new LocalDate(2016, 12, 19);
  def end = new LocalDate(2016, 12, 21);
  def activity;

  @Override
  def populate4Test() {
    def provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", "IBAN");
    this.activity = new Activity(provider, "Bush Walking", MIN_AGE, MAX_AGE, CAPACITY);
  }

  def 'sucess'() {
    given:
    def offer = new ActivityOffer(this.activity, this.begin, this.end, 30)

    expect:
    offer.getBegin() == this.begin
    offer.getEnd() == this.end
    this.activity.getActivityOfferSet().size() == 1
    offer.getNumberActiveOfBookings() == 0
    offer.getPrice() == 30
  }

  @Unroll('ActivityOffer: #activity, #begin, #end, #price')
  def 'activity exceptions'() {
    given:
    def offer = new ActivityOffer(activity, begin, end, capacity)

    expect:
    thrown(ActivityException)

    where:
    activity      | begin      | end                     | price
    null          | this.begin | this.end                | 30
    this.activity | null       | this.end                | 30
    this.activity | this.begin | null                    | 30
    this.activity | this.begin | this.begin.minusDays(1) | 30
    this.activity | this.begin | this.end                | 0
  }

  def 'sucess begin date qual end date'() {
    given:
    def offer = new ActivityOffer(this.activity, this.begin, this.begin, 30)

    expect:
    offer.getBegin() == this.begin
    offer.getEnd() == this.begin
    this.activity.getActivityOfferSet().size() == 1
    offer.getNumberActiveOfBookings() == 0 
  }
}

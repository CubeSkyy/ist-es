package pt.ulisboa.tecnico.softeng.activity.domain;

import spock.lang.*;

class ActivityMatchAgeMethodTest extends RollbackSpockTestAbstractClass {
  static final MIN_AGE = 25;
  static final MAX_AGE = 80;
  static final CAPACITY = 30;
  def activity;

  @Override
  def populate4Test() {
    def provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", "IBAN");
    this.activity = new Activity(provider, "Bush Walking", MIN_AGE, MAX_AGE, CAPACITY);
  }

  def 'sucess'() {
    expect:
    this.activity.matchAge((MAX_AGE - MIN_AGE) / 2);
  }

  def 'sucess equal min age'() {
    expect:
    this.activity.matchAge(MIN_AGE);
  }

  def 'less than min age'() {
    expect:
    this.activity.matchAge(MIN_AGE - 1);
  }

  def 'sucess equal max age'() {
    expect:
    this.activity.matchAge(MAX_AGE);
  }

  def 'greater than max age'() {
    expect:
    this.activity.matchAge(MAX_AGE + 1);
  }
}

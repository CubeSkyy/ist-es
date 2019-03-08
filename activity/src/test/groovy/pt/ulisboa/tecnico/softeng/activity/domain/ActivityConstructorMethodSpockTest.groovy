package pt.ulisboa.tecnico.softeng.activity.domain;

import spock.lang.*;

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;

class ActivityConstructorMethodSpockTest extends RollbackSpockTestAbstractClass {
  def IBAN = 'IBAN';
  def NIF = 'NIF';
  @Shared def PROVIDER_NAME = 'Bush Walking';
  static final MIN_AGE = 25;
  static final MAX_AGE = 50;
  static final CAPACITY = 30;
  @Shared def provider;


  @Override
  def populate4Test() {
    provider = new ActivityProvider('XtremX', 'ExtremeAdventure', NIF, IBAN);
  }


  def 'sucess'() {
    given:
    def activity = new Activity(provider, PROVIDER_NAME, MIN_AGE, MAX_AGE, CAPACITY);

    expect:
    activity.getCode().startsWith(provider.getCode())
    activity.getCode().length() > ActivityProvider.CODE_SIZE
    activity.getName() == 'Bush Walking'
    activity.getMinAge() == MIN_AGE
    activity.getMaxAge() == MAX_AGE
    activity.getCapacity() == CAPACITY
    activity.getActivityOfferSet().size() == 0
    this.provider.getActivitySet().size() == 1

  }

  @Unroll('Activity: #prov, #provname, #min, #max, #cap')
  def 'exceptions'() {
    when:
    def activity = new Activity(prov, provname, min, max, cap)

    then:
    thrown(ActivityException)

    where:
    prov          | provname      | min          | max     | cap
    null          | PROVIDER_NAME | MIN_AGE      | MAX_AGE | CAPACITY
    this.provider | null          | MIN_AGE      | MAX_AGE | CAPACITY
    this.provider | ' '           | MIN_AGE      | MAX_AGE | CAPACITY
    this.provider | PROVIDER_NAME | 17           | MAX_AGE | CAPACITY
    this.provider | PROVIDER_NAME | MIN_AGE      | 100     | CAPACITY
    this.provider | PROVIDER_NAME | MAX_AGE + 10 | MAX_AGE | CAPACITY
    this.provider | PROVIDER_NAME | MAX_AGE + 1  | MAX_AGE | CAPACITY
    this.provider | PROVIDER_NAME | MIN_AGE      | MAX_AGE | 0
  }

  def 'sucess min age equal 18'() {
    given:
    def activity = new Activity(this.provider, PROVIDER_NAME, 18, MAX_AGE, CAPACITY)

    expect:
    activity.getCode().startsWith(provider.getCode())
    activity.getCode().length() > ActivityProvider.CODE_SIZE
    activity.getName() == 'Bush Walking'
    activity.getMinAge() == 18
    activity.getMaxAge() == MAX_AGE
    activity.getCapacity() == CAPACITY
    activity.getActivityOfferSet().size() == 0
    this.provider.getActivitySet().size() == 1
  }


  def 'sucess max age 99'() {
    given:
    def activity = new Activity(this.provider, PROVIDER_NAME, MIN_AGE, 99, CAPACITY)

    expect:
    activity.getCode().startsWith(provider.getCode())
    activity.getCode().length() > ActivityProvider.CODE_SIZE
    activity.getName() == 'Bush Walking'
    activity.getMinAge() == MIN_AGE
    activity.getMaxAge() == 99
    activity.getCapacity() == CAPACITY
    activity.getActivityOfferSet().size() == 0
    this.provider.getActivitySet().size() == 1
  }


  def 'sucess min age equal max age'() {
    given:
    def activity = new Activity(this.provider, PROVIDER_NAME, MIN_AGE, MIN_AGE, CAPACITY)

    expect:
    activity.getCode().startsWith(provider.getCode())
    activity.getCode().length() > ActivityProvider.CODE_SIZE
    activity.getName() == 'Bush Walking'
    activity.getMinAge() == MIN_AGE
    activity.getMaxAge() == MIN_AGE
    activity.getCapacity() == CAPACITY
    activity.getActivityOfferSet().size() == 0
    this.provider.getActivitySet().size() == 1
  }


  def 'sucess capacity one'() {
    given:
    def activity = new Activity(this.provider, PROVIDER_NAME, MIN_AGE, MAX_AGE, 1)

    expect:
    activity.getCode().startsWith(provider.getCode())
    activity.getCode().length() > ActivityProvider.CODE_SIZE
    activity.getName() == 'Bush Walking'
    activity.getMinAge() == MIN_AGE
    activity.getMaxAge() == MAX_AGE
    activity.getCapacity() == 1
    activity.getActivityOfferSet().size() == 0
    this.provider.getActivitySet().size() == 1
  }
}

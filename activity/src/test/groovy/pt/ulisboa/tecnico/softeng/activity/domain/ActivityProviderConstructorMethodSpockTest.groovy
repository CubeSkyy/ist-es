package pt.ulisboa.tecnico.softeng.activity.domain;

import spock.lang.*;

import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;

public class ActivityProviderConstructorSpockMethodTest extends RollbackSpockTestAbstractClass {
	@Shared def PROVIDER_CODE = "XtremX";
	@Shared def PROVIDER_NAME = "Adventure++";
	@Shared def IBAN = "IBAN";
	@Shared def NIF = "NIF";

	@Override
	def populate4Test() {
	}

	def 'success'() {
    given:
    def provider = new ActivityProvider(PROVIDER_CODE, PROVIDER_NAME, NIF, IBAN);

    expect:
		PROVIDER_NAME == provider.getName()
		provider.getCode().length() == ActivityProvider.CODE_SIZE
		1 == FenixFramework.getDomainRoot().getActivityProviderSet().size()
		0 == provider.getActivitySet().size()
	}

  @Unroll('ActivityProvider: #provcode, #provname, #nif, #iban')
  def 'exceptions'() {
    when:
    new ActivityProvider(provcode, provname, nif, iban)

    then:
    thrown(ActivityException)

    where:
    provcode          | provname      | nif      | iban
    null              | PROVIDER_NAME | NIF      | IBAN
    ' '               | PROVIDER_NAME | NIF      | IBAN
    PROVIDER_CODE     | null          | NIF      | IBAN
    PROVIDER_CODE     | ' '           | NIF      | IBAN
    '12345'           | PROVIDER_NAME | NIF      | IBAN
    '1234567'         | PROVIDER_NAME | NIF      | IBAN
    PROVIDER_CODE     | PROVIDER_NAME | null     | IBAN
    PROVIDER_CODE     | PROVIDER_NAME | ' '      | IBAN
  }

  def 'note unique code'() {
    when:
		new ActivityProvider(PROVIDER_CODE, PROVIDER_NAME, NIF, IBAN);

    then:
		try {
			new ActivityProvider(PROVIDER_CODE, "Hello", NIF + "2", IBAN);
			fail();
		} catch (ActivityException ae) {
			1 == FenixFramework.getDomainRoot().getActivityProviderSet().size()
		}
	}

	def 'note unique name'() {
    when:
		new ActivityProvider(PROVIDER_CODE, PROVIDER_NAME, NIF, IBAN);

    then:
		try {
			new ActivityProvider("123456", PROVIDER_NAME, NIF + "2", IBAN);
			fail();
		} catch (ActivityException ae) {
			1 == FenixFramework.getDomainRoot().getActivityProviderSet().size()
		}
	}

	def 'note unique nif'() {
    when:
		new ActivityProvider(PROVIDER_CODE, PROVIDER_NAME, NIF, IBAN);

    then:
		try {
			new ActivityProvider("123456", "jdgdsk", NIF, IBAN);
			fail();
		} catch (ActivityException ae) {
			1 == FenixFramework.getDomainRoot().getActivityProviderSet().size()
		}
	}
}

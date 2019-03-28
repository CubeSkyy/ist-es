package pt.ulisboa.tecnico.softeng.activity.services.local

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.activity.domain.Activity
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityOffer
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityProvider
import pt.ulisboa.tecnico.softeng.activity.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestActivityBookingData
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestBankOperationData
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestInvoiceData

class ActivityInterfaceReserveActivityMethodSpockTest extends SpockRollbackTestAbstractClass {
    def IBAN = "IBAN"
    def NIF = "123456789"
    def MIN_AGE = 18
    def MAX_AGE = 50
    def CAPACITY = 30

    def provider1
    def provider2


    def taxInterface = Mock(TaxInterface)
    def bankInterface = Mock(BankInterface)

    @Override
    def populate4Test() {
        provider1 = new ActivityProvider("XtremX", "Adventure++", "NIF", IBAN)
        provider2 = new ActivityProvider("Walker", "Sky", "NIF2", IBAN)

        provider1.setBankInterface(bankInterface)
        provider2.setTaxInterface(taxInterface)

        provider2.setBankInterface(bankInterface)
        provider1.setTaxInterface(taxInterface)
    }

    def 'reserve activity'() {
        given:
        Activity activity = new Activity(provider1, "XtremX", MIN_AGE, MAX_AGE, CAPACITY)
        new ActivityOffer(activity, new LocalDate(2018, 02, 19), new LocalDate(2018, 12, 20), 30)

        when:
        def activityBookingData = new RestActivityBookingData()
        activityBookingData.setAge(20)
        activityBookingData.setBegin(new LocalDate(2018, 02, 19))
        activityBookingData.setEnd(new LocalDate(2018, 12, 20))
        activityBookingData.setIban(IBAN)
        activityBookingData.setNif(NIF)

        def bookingData = ActivityInterface.reserveActivity(activityBookingData)

        then:
        bankInterface.processPayment(_ as RestBankOperationData) >> _
        taxInterface.submitInvoice(_ as RestInvoiceData) >> _

        bookingData != null
        bookingData.getReference().startsWith("XtremX")
    }

    def 'reserve activity no option'() {
        given:
        def activityBookingData = new RestActivityBookingData()

        when:
        activityBookingData.setAge(20)
        activityBookingData.setBegin(new LocalDate(2018, 02, 19))
        activityBookingData.setEnd(new LocalDate(2018, 12, 20))
        activityBookingData.setIban(IBAN)
        activityBookingData.setNif(NIF)

        def bookingData = ActivityInterface.reserveActivity(activityBookingData)

        then:
        thrown(ActivityException)
    }

}

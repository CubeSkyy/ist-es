package pt.ulisboa.tecnico.softeng.activity.services.local;

import org.joda.time.LocalDate
import spock.lang.*

import pt.ulisboa.tecnico.softeng.activity.domain.*
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestBankOperationData
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestInvoiceData

class ActivityInterfaceCancelReservationMethodSpockTest extends SpockRollbackTestAbstractClass {
    def IBAN = "IBAN"
    def NIF = "123456789"
    def provider
    def offer
    def taxInterface
    def bankInterface
    def activityInterface
    def processor

    @Override
    def populate4Test() {
        activityInterface = new ActivityInterface()
        taxInterface = Mock(TaxInterface)
        bankInterface = Mock(BankInterface)
        processor = new Processor(taxInterface, bankInterface)
        provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", IBAN, processor)
        def activity = new Activity(provider, "Bush Walking", 18, 80, 3)

        def begin = new LocalDate(2016, 12, 19)
        def end = new LocalDate(2016, 12, 21)
        offer = new ActivityOffer(activity, begin, end, 30)

    }

    def 'success'() {
        when:
        def booking = new Booking(provider, offer, NIF, IBAN)
        provider.getProcessor().submitBooking(booking)
        def cancel = activityInterface.cancelReservation(booking.getReference())

        then:
        bankInterface.processPayment(_ as RestBankOperationData) >> null
        taxInterface.submitInvoice(_ as RestInvoiceData) >> null
        booking.isCancelled()
        cancel == booking.getCancel()
    }

    def 'doesNotExist'() {
        when:
        provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))
        activityInterface.cancelReservation("XPTO")

        then:
        bankInterface.processPayment(_ as RestBankOperationData) >> null
        taxInterface.submitInvoice(_ as RestInvoiceData) >> null
        thrown(ActivityException)
    }

}

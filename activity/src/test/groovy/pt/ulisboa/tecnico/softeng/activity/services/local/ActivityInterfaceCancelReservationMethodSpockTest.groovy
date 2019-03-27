package pt.ulisboa.tecnico.softeng.activity.services.local;

import org.joda.time.LocalDate

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

    @Override
    def populate4Test() {
        provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", IBAN)
        def activity = new Activity(provider, "Bush Walking", 18, 80, 3)

        def begin = new LocalDate(2016, 12, 19)
        def end = new LocalDate(2016, 12, 21)
        offer = new ActivityOffer(activity, begin, end, 30)

        taxInterface = Mock(TaxInterface)
        bankInterface = Mock(BankInterface)

        provider.setBankInterface(bankInterface)
        provider.setTaxInterface(taxInterface)
    }

    def 'success'() {
        when:
        def booking = new Booking(provider, offer, NIF, IBAN)
        provider.getProcessor().submitBooking(booking)
        def cancel = ActivityInterface.cancelReservation(booking.getReference())

        then:
        booking.isCancelled()
        cancel == booking.getCancel()
    }

    def 'doesNotExist'() {
        when:
        provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))
        ActivityInterface.cancelReservation("XPTO")

        then:
        thrown(ActivityException)
    }

}

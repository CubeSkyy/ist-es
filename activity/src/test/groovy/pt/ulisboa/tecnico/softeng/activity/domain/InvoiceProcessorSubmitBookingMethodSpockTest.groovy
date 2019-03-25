package pt.ulisboa.tecnico.softeng.activity.domain

import org.joda.time.LocalDate

import spock.lang.*

import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestBankOperationData
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestInvoiceData
import pt.ulisboa.tecnico.softeng.activity.services.remote.exceptions.BankException
import pt.ulisboa.tecnico.softeng.activity.services.remote.exceptions.RemoteAccessException
import pt.ulisboa.tecnico.softeng.activity.services.remote.exceptions.TaxException

class InvoiceProcessorSubmitBookingMethodSpockTest extends SpockRollbackTestAbstractClass {
    def CANCEL_PAYMENT_REFERENCE = "CancelPaymentReference"
    def INVOICE_REFERENCE = "InvoiceReference"
    def PAYMENT_REFERENCE = "PaymentReference"
    static final int AMOUNT = 30
    def IBAN = "IBAN"
    def NIF = "123456789"
    def provider
    def offer
    def booking
    def taxInterface
    def bankInterface

    @Override
    def populate4Test() {
        provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", IBAN)
        Activity activity = new Activity(provider, "Bush Walking", 18, 80, 10)

        taxInterface = Mock(TaxInterface)
        bankInterface = Mock(BankInterface)

        provider.setBankInterface(bankInterface)
        provider.setTaxInterface(taxInterface)

        LocalDate begin = new LocalDate(2016, 12, 19)
        LocalDate end = new LocalDate(2016, 12, 21)
        offer = new ActivityOffer(activity, begin, end, AMOUNT)
        booking = new Booking(provider, offer, NIF, IBAN)
    }


    def 'success'() {
        expect:
        provider.getProcessor().submitBooking(booking)
    }


    def 'oneTaxFailureOnSubmitInvoice'() {
        when:
        provider.getProcessor().submitBooking(booking)
        provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))

        then:
        bankInterface.processPayment(_ as RestBankOperationData) >> PAYMENT_REFERENCE
        2 * taxInterface.submitInvoice(_ as RestInvoiceData) >> new TaxException() >> INVOICE_REFERENCE
    }


    def 'oneRemoteFailureOnSubmitInvoice'() {
        when:
        provider.getProcessor().submitBooking(booking)
        provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))

        then:
        bankInterface.processPayment(_ as RestBankOperationData) >> PAYMENT_REFERENCE
        2 * taxInterface.submitInvoice(_ as RestInvoiceData) >> new RemoteAccessException() >> INVOICE_REFERENCE
    }


    def 'oneBankFailureOnProcessPayment'() {
        when:
        provider.getProcessor().submitBooking(booking)
        provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))

        then:
        taxInterface.submitInvoice(_ as RestInvoiceData) >> INVOICE_REFERENCE
        2 * bankInterface.processPayment(_ as RestBankOperationData) >> new BankException() >> PAYMENT_REFERENCE
    }


    def 'oneRemoteFailureOnProcessPayment'() {
        when:
        provider.getProcessor().submitBooking(booking)
        provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))

        then:
        taxInterface.submitInvoice(_ as RestInvoiceData) >> INVOICE_REFERENCE
        2 * bankInterface.processPayment(_ as RestBankOperationData) >> new RemoteAccessException() >> PAYMENT_REFERENCE
    }


    def 'successCancel'() {
        expect:
        provider.getProcessor().submitBooking(booking)
        booking.cancel()
    }


    def 'oneBankExceptionOnCancelPayment'() {
        when:
        provider.getProcessor().submitBooking(booking)
        booking.cancel()
        provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))

        then:
        2 * bankInterface.cancelPayment(_ as String) >> new BankException() >> CANCEL_PAYMENT_REFERENCE
    }


    def 'oneRemoteExceptionOnCancelPayment'() {
        when:
        provider.getProcessor().submitBooking(booking)
        booking.cancel()
        provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))

        then:
        2 * bankInterface.cancelPayment(_ as String) >> new RemoteAccessException() >> CANCEL_PAYMENT_REFERENCE
    }


    def 'oneTaxExceptionOnCancelInvoice'() {
        given:
        def i=0

        when:
        provider.getProcessor().submitBooking(booking)
        booking.cancel()
        provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))

        then:
        bankInterface.processPayment(_ as RestBankOperationData)
        taxInterface.submitInvoice(_ as RestInvoiceData)
        bankInterface.cancelPayment(_ as String) >> CANCEL_PAYMENT_REFERENCE
        2 * taxInterface.cancelInvoice(_ as String) >> {
            if (i < 1) {
                i++
                throw new TaxException()
            }
        }
    }


    def 'oneRemoteExceptionOnCancelInvoice'() {
        given:
        def i = 0

        when:
        provider.getProcessor().submitBooking(booking)
        booking.cancel()
        provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))

        then:
        bankInterface.cancelPayment(_ as String) >> CANCEL_PAYMENT_REFERENCE
        2 * taxInterface.cancelInvoice(_ as String) >> {
            if (i < 1) {
                i++
                throw new RemoteAccessException()
            }
        }
    }
}

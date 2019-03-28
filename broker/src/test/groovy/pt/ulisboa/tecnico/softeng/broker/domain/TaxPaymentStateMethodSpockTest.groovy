package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestBankOperationData
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestInvoiceData
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.TaxException

class TaxPaymentStateMethodSpockTest extends SpockRollbackTestAbstractClass {

    def invoiceData
    def adventure
    def broker
    def taxInterface
    def client

    @Override
    def populate4Test(){
        taxInterface = Mock(TaxInterface)
        broker =  new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN, new HotelInterface(),
                taxInterface, new ActivityInterface(), new CarInterface(), new BankInterface())

        client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)

        adventure = new Adventure(broker, BEGIN, END, client, MARGIN)

        invoiceData = new RestInvoiceData() //BROKER_NIF_AS_SELLER, CLIENT_NIF, "ADVENTURE", BEGIN

        adventure.setState(Adventure.State.TAX_PAYMENT)


    }

    def 'successTaxPayment'(){
        when:
        adventure.process()

        then:
        //hotelInterface.reserveRoom(_ as RestRoomBookingData) >> invoiceData
        taxInterface.submitInvoice(_ as RestInvoiceData) >> invoiceData
        adventure.getState().getValue() == Adventure.State.CONFIRMED
    }

    def 'taxException'(){
        when:
        adventure.process()

        then:
        taxInterface.submitInvoice(_ as RestInvoiceData) >> {throw new TaxException()}
        adventure.getState().getValue() == Adventure.State.UNDO
    }

    def 'singleRemoteAccessException'(){
        when:
        adventure.process()

        then:
        taxInterface.submitInvoice(_ as RestInvoiceData) >> { throw new RemoteAccessException() }
        adventure.getState().getValue() == Adventure.State.TAX_PAYMENT
    }

    def 'maxRemoteAccessException'() {
        when:
        for (int i = 0; i < TaxPaymentState.MAX_REMOTE_ERRORS; i++) {
            adventure.process()
        }

        then:
        TaxPaymentState.MAX_REMOTE_ERRORS * taxInterface.submitInvoice(_ as RestInvoiceData) >>
                { throw new RemoteAccessException() }
        adventure.getState().getValue() == Adventure.State.UNDO
    }

    def 'maxMinusOneRemoteAccessException'() {
        when:
        for (int i = 0; i < TaxPaymentState.MAX_REMOTE_ERRORS - 1; i++) {
            adventure.process()
        }

        then:
        (TaxPaymentState.MAX_REMOTE_ERRORS - 1) * taxInterface.submitInvoice(_ as RestInvoiceData) >>
                { throw new RemoteAccessException() }
        adventure.getState().getValue() == Adventure.State.TAX_PAYMENT
    }

    def "twoRemoteAccessExceptionOneSuccess"(){
        given:
        int i

        when:
        for (i = 0; i < 3; i++) {
            adventure.process()
        }

        then:
        3 * taxInterface.submitInvoice(_ as RestInvoiceData) >>  { if (i < 2) {
            throw new RemoteAccessException()}
        else {
            return invoiceData}
        }
        adventure.getState().getValue() == Adventure.State.CONFIRMED
    }


    def 'oneRemoteAccessExceptionOneHotelException'(){
        when:
        for (int i = 0; i<2; i++) {
            adventure.process()
        }

        then:
        2 * taxInterface.submitInvoice(_ as RestInvoiceData) >> { throw new RemoteAccessException() } >>
                {throw new TaxException()}
        adventure.getState().getValue() == Adventure.State.UNDO
    }

}

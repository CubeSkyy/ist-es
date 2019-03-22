package pt.ulisboa.tecnico.softeng.broker.domain;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestBankOperationData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.BankException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;

@RunWith(JMockit.class)
public class ProcessPaymentStateProcessMethodTest extends RollbackTestAbstractClass {
    private static final String TRANSACTION_SOURCE = "ADVENTURE";

    @Mocked
    private TaxInterface taxInterface;

    @Mocked
    private BankInterface bankInterface;

    @Override
    public void populate4Test() {
        this.broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN);
        this.client = new Client(this.broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE);
        this.adventure = new Adventure(this.broker, BEGIN, END, this.client, MARGIN);

        this.adventure.setState(State.PROCESS_PAYMENT);
        adventure.setTaxInterface(taxInterface);
        adventure.setBankInterface(bankInterface);
    }

    @Test
    public void success() {
        new Expectations() {
            {
                bankInterface.processPayment((RestBankOperationData) this.any);
                this.result = PAYMENT_CONFIRMATION;
            }
        };

        this.adventure.process();

        Assert.assertEquals(State.TAX_PAYMENT, this.adventure.getState().getValue());
    }

    @Test
    public void bankException() {
        new Expectations() {
            {
                bankInterface.processPayment((RestBankOperationData) this.any);
                this.result = new BankException();
            }
        };

        this.adventure.process();
        this.adventure.process();

        Assert.assertEquals(State.CANCELLED, this.adventure.getState().getValue());
    }

    @Test
    public void singleRemoteAccessException() {
        new Expectations() {
            {
                bankInterface.processPayment((RestBankOperationData) this.any);
                this.result = new RemoteAccessException();
            }
        };

        this.adventure.process();

        Assert.assertEquals(State.PROCESS_PAYMENT, this.adventure.getState().getValue());
    }

    @Test
    public void maxRemoteAccessException() {
        new Expectations() {
            {
                bankInterface.processPayment((RestBankOperationData) this.any);
                this.result = new RemoteAccessException();
            }
        };

        this.adventure.process();
        this.adventure.process();
        this.adventure.process();
        this.adventure.process();

        Assert.assertEquals(State.CANCELLED, this.adventure.getState().getValue());
    }

    @Test
    public void maxMinusOneRemoteAccessException() {
        new Expectations() {
            {
                bankInterface.processPayment((RestBankOperationData) this.any);
                this.result = new RemoteAccessException();
            }
        };

        this.adventure.process();
        this.adventure.process();

        Assert.assertEquals(State.PROCESS_PAYMENT, this.adventure.getState().getValue());
    }

    @Test
    public void twoRemoteAccessExceptionOneSuccess() {
        new Expectations() {
            {
                bankInterface.processPayment((RestBankOperationData) this.any);
                this.result = new Delegate() {
                    int i = 0;

                    public String delegate() {
                        if (this.i < 2) {
                            this.i++;
                            throw new RemoteAccessException();
                        } else {
                            return PAYMENT_CONFIRMATION;
                        }
                    }
                };
                this.times = 3;

            }
        };

        this.adventure.process();
        this.adventure.process();
        this.adventure.process();

        Assert.assertEquals(State.TAX_PAYMENT, this.adventure.getState().getValue());
    }

    @Test
    public void oneRemoteAccessExceptionOneBankException() {
        new Expectations() {
            {
                bankInterface.processPayment((RestBankOperationData) this.any);
                this.result = new Delegate() {
                    int i = 0;

                    public String delegate() {
                        if (this.i < 1) {
                            this.i++;
                            throw new RemoteAccessException();
                        } else {
                            throw new BankException();
                        }
                    }
                };
                this.times = 2;

            }
        };

        this.adventure.process();
        this.adventure.process();
        this.adventure.process();

        Assert.assertEquals(State.CANCELLED, this.adventure.getState().getValue());
    }

}
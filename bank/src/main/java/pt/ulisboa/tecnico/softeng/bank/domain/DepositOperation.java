package pt.ulisboa.tecnico.softeng.bank.domain;


public class DepositOperation  extends Operation{

    public DepositOperation(Account account1, double value) {

        super(account1, value);
    }

    public String revert() {
        setCancellation(getReference() + "_CANCEL");
        return getAccount().withdraw(getValue()).getReference();

    }

}

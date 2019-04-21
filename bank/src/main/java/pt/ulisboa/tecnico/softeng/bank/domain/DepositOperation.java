package pt.ulisboa.tecnico.softeng.bank.domain;


import java.util.Iterator;

public class DepositOperation  extends Operation{

    public DepositOperation(Account account1, double value) {

        super(account1, value);
    }

    public String revert() {
        Iterator<Account> it = getAccountSet().iterator();
        setCancellation(getReference() + "_CANCEL");
        return it.next().withdraw(getValue()).getReference();

    }

}

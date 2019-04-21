package pt.ulisboa.tecnico.softeng.bank.domain;

import java.util.Iterator;

public class WithdrawOperation extends Operation {

    public WithdrawOperation(Account account, double value) {
        super(account, value);
    }

    public String revert() {
        Iterator<Account> it = getAccountSet().iterator();
        setCancellation(getReference() + "_CANCEL");
        return it.next().deposit(getValue()).getReference();
    }
}



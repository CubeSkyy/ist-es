package pt.ulisboa.tecnico.softeng.bank.domain;

import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

public class TransferOperation extends Operation{


    public TransferOperation(Account account,Account account2, long value) {
        super(account, value);
        checkAccount(account2);
        getAccountSet().add(account2);
        getBankSet().add(account2.getBank());

    }

    private void checkAccount(Account account) {

        if (account == null) {
            throw new BankException();
        }
    }

    public String revert() {
        if (getCancellation() != null){
            throw new BankException();
        }
        setCancellation(getReference() + "_CANCEL");

        Iterator<Account> it = getAccountSet().iterator();

        it.next().deposit(getValue()).getReference();
        return it.next().withdraw(getValue()).getReference();
    }

}

package pt.ulisboa.tecnico.softeng.bank.domain;

import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public class TransferOperation extends Operation{
    Account account2;
    Bank bank2;

    public TransferOperation(Account account,Account account2, double value) {
        super(account, value);
        checkAccount(account2);
        setAccount2(account2);
        setBank2(account2.getBank());
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
        getAccount().deposit(getValue()).getReference();
        return getAccount2().withdraw(getValue()).getReference();
    }

    public void setAccount2(Account account){
        this.account2 = account;
    }

    public Account getAccount2(){
        return this.account2;
    }

    public void setBank2 (Bank bank){
        this.bank2 = bank;
    }

    public Bank getBank2(){
        return this.bank2;
    }

}

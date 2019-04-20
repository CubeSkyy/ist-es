package pt.ulisboa.tecnico.softeng.bank.domain;

public class WithdrawOperation extends Operation {

    public WithdrawOperation(Account account, double value) {
        super(account, value);
    }

    public String revert() {
        setCancellation(getReference() + "_CANCEL");
        return getAccount().deposit(getValue()).getReference();
    }
}



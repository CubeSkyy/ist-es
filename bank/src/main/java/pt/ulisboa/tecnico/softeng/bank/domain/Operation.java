package pt.ulisboa.tecnico.softeng.bank.domain;

import org.joda.time.DateTime;

import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public class Operation extends Operation_Base {

    public Operation(Account account, double value) {
        checkArguments(account, value);

        setReference(account.getBank().getCode() + account.getBank().getCounter());
        setValue(value);
        setTime(DateTime.now());

        getAccountSet().add(account);
        getBankSet().add(account.getBank());

    }

    public void delete() {
        getBankSet().clear();
        getAccountSet().clear();

        deleteDomainObject();
    }

    private void checkArguments(Account account, double value) {
        if (account == null || value <= 0) {
            throw new BankException();
        }
    }

    public String revert(){throw new BankException();}


}

package pt.ulisboa.tecnico.softeng.tax.domain;

import pt.ulisboa.tecnico.softeng.tax.exception.TaxException;

public class SellerStrategy {

    public double calculate(int year) {
        if (year < 1970) {
            throw new TaxException();
        }

        double result = 0;
        for (Invoice invoice : getInvoiceSet()) {
            if (!invoice.isCancelled() && invoice.getDate().getYear() == year) {
                result = result + invoice.getIva();
            }
        }
        return result;
    }
}

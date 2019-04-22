package pt.ulisboa.tecnico.softeng.tax.domain;

import pt.ulisboa.tecnico.softeng.tax.exception.TaxException;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SellerStrategy extends TaxPayerStrategy{

    public double calculate(int year, TaxPayer taxPayer) {
        if (year < 1970) {
            throw new TaxException();
        }

        double result = 0;
        for (Invoice invoice : taxPayer.getSellerinvoiceSet()) {
            if (!invoice.isCancelled() && invoice.getDate().getYear() == year) {
                result = result + invoice.getIva();
            }
        }
        return result;
    }

    public Map<Integer, Double> calculate(TaxPayer taxPayer) {
        return taxPayer.getSellerinvoiceSet().stream().map(i -> i.getDate().getYear()).distinct()
                .collect(Collectors.toMap(y -> y, y -> calculate(y, taxPayer)));
    }

}

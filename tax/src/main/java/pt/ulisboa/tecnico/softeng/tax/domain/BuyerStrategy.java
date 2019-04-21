package pt.ulisboa.tecnico.softeng.tax.domain;

import pt.ulisboa.tecnico.softeng.tax.exception.TaxException;

import java.util.Map;
import java.util.stream.Collectors;

public class BuyerStrategy extends TaxPayerStrategy {

    private final static int PERCENTAGE = 5;

    public double calculate(int year,TaxPayer taxPayer) {
        if (year < 1970) {
            throw new TaxException();
        }

        double result = 0;
        for (Invoice invoice : taxPayer.getBuyerinvoiceSet()) {
            if (!invoice.isCancelled() && invoice.getDate().getYear() == year) {
                result = result + invoice.getIva() * PERCENTAGE / 100;
            }
        }
        return result;
    }


    public Map<Integer, Double> calculate(TaxPayer taxPayer) {
        return taxPayer.getBuyerinvoiceSet().stream().map(i -> i.getDate().getYear()).distinct()
                .collect(Collectors.toMap(y -> y, y -> calculate(y, taxPayer)));
    }
}

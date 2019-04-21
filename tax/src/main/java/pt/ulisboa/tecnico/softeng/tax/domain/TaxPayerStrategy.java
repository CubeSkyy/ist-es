package pt.ulisboa.tecnico.softeng.tax.domain;

import java.util.Map;
import java.util.Set;

public abstract class TaxPayerStrategy {
    abstract double calculate(int year, TaxPayer taxPayer);
    abstract Map<Integer, Double> calculate(TaxPayer taxPayer);
}

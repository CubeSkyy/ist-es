package pt.ulisboa.tecnico.softeng.tax.domain;

public abstract class TaxPayerStrategy {
    abstract double calculate(int year);
}

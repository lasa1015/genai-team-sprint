package com.fx.core;

/**
 * Tasks 2 + 3 solution. Task 3 adds the InvalidRateException guards — an
 * unchecked exception for what can only be a programming/data bug.
 */
public class CurrencyConverter {
    private static int conversionCount = 0;   // shared across ALL converters -> static
    private final double rate;

    public CurrencyConverter() { this(1.0); }             // constructor chaining
    public CurrencyConverter(double rate) {
        // TODO Task 1 (exceptions recap): reject a non-positive rate with the UNCHECKED
        //   InvalidRateException (a negative rate is a bug, not an everyday condition).
        this.rate = rate;
    }

    public double convert(double amount) { conversionCount++; return amount * rate; }
    public double convert(double amount, double overrideRate) {
        // TODO Task 1 (exceptions recap): guard overrideRate <= 0 with InvalidRateException too.
        conversionCount++;
        return amount * overrideRate;
    }
    public double[] convert(double[] amounts) {
        double[] out = new double[amounts.length];
        for (int i = 0; i < amounts.length; i++) out[i] = convert(amounts[i]);
        return out;
    }
    public double[] convertAll(double... amounts) { return convert(amounts); } // varargs delegates to array

    public static int getConversionCount() { return conversionCount; }

    @Override public String toString() { return "Converter[rate=" + rate + "]"; }
}

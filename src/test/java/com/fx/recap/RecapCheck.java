package com.fx.recap;

// GIVEN — do not edit. This grader is TWO things at once:
//   1. Your Task 1 target. It starts RED (Friday's collections + exceptions work is
//      gutted in the baseline). Make it GREEN and Task 1 is done.
//   2. Your first worked example of a JUnit 5 test — read it before you write your own
//      in Task 2. Notice: one behaviour per @Test, a @DisplayName that reads like a
//      requirement, assertThrows for the unhappy path, a delta on the double compare.
//
// Run just this file:   ./mvnw test -Dtest=RecapCheck      (from the fx-app folder)

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fx.analyzer.Analyzer;
import com.fx.analyzer.CsvLoader;
import com.fx.analyzer.Transfer;
import com.fx.core.Account;
import com.fx.core.Currency;
import com.fx.core.CurrencyConverter;
import com.fx.core.FxException;
import com.fx.core.InsufficientFundsException;
import com.fx.core.InvalidRateException;

class RecapCheck {

    // ---- part A: the exception family (deck 15) ----

    @Test
    @DisplayName("E1 — the family splits: FxException is checked, InvalidRateException is unchecked")
    void familyCheckedness() {
        assertTrue(Exception.class.isAssignableFrom(FxException.class)
                        && !RuntimeException.class.isAssignableFrom(FxException.class),
                "FxException should extend Exception (checked) — a business condition callers must face.");
        assertTrue(RuntimeException.class.isAssignableFrom(InvalidRateException.class),
                "InvalidRateException should extend RuntimeException (unchecked) — a negative rate is a bug.");
    }

    @Test
    @DisplayName("E2 — InsufficientFundsException's message carries BOTH numbers (Task 2 asserts this)")
    void insufficientFundsMessage() {
        String msg = new InsufficientFundsException(999_999.0, 800.0).getMessage();
        assertTrue(msg.contains("999999") && msg.contains("800"),
                "The message must name the requested AND the available amount. Got: " + msg);
    }

    @Test
    @DisplayName("E3 — withdraw THROWS on insufficient funds, and leaves the balance untouched")
    void withdrawThrows() {
        Account a = new Account("Amira", Currency.EUR, 1000.0);
        assertThrows(InsufficientFundsException.class, () -> a.withdraw(999_999),
                "withdraw(999999) on a balance of 1000 must THROW now — the Friday warning grew up.");
        assertEquals(1000.0, a.getBalance(), 1e-9,
                "A refused withdrawal must leave the balance unchanged.");
    }

    @Test
    @DisplayName("E4 — a non-positive rate is rejected with InvalidRateException")
    void converterGuardsRate() {
        assertThrows(InvalidRateException.class, () -> new CurrencyConverter(-1),
                "new CurrencyConverter(-1) must throw InvalidRateException — restore the guard.");
    }

    // ---- part B: collections & dedupe (deck 13) ----

    private Transfer tx(int id) {
        return new Transfer(id, 14, 4, 573.92, Currency.AUD, "2026-01-12T09:38:00", "COMPLETED");
    }

    @Test
    @DisplayName("H1 — two transfers that differ ONLY by id are equal and share a hashCode")
    void equalsIgnoresId() {
        Transfer a = tx(1), b = tx(2);
        assertEquals(a, b,
                "equals() must compare the business fields and EXCLUDE id — same money, different "
                        + "receipt number is the same transfer. (Still on the default identity equals?)");
        assertEquals(a.hashCode(), b.hashCode(),
                "Equal objects must share a hashCode — build it from the SAME fields as equals.");
    }

    @Test
    @DisplayName("H2 — a genuinely different transfer (other amount) is NOT equal")
    void differentIsNotEqual() {
        Transfer other = new Transfer(1, 14, 4, 999.00, Currency.AUD, "2026-01-12T09:38:00", "COMPLETED");
        assertNotEquals(tx(1), other, "A different amount is a different transfer — equals must notice.");
    }

    @Test
    @DisplayName("H3 — 1005 loaded rows dedupe to 1000 through a LinkedHashSet")
    void dedupe() throws Exception {
        List<Transfer> loaded = CsvLoader.load("ops/transactions.csv");
        Set<Transfer> unique = new LinkedHashSet<>(loaded);
        assertEquals(1000, unique.size(),
                "5 rows are re-sends under new ids; a Set that uses your equals/hashCode drops them "
                        + "(1005 -> 1000). Got " + unique.size() + " — 1005 means the Set can't tell them apart.");
    }

    @Test
    @DisplayName("H4 — Analyzer groups by currency and names the busiest: SGD (154)")
    void busiestCurrency() {
        assertTrue(analyzerOut.contains("SGD") && analyzerOut.contains("154"),
                "Grouping the 1000 distinct transfers by currency, the busiest lane is SGD with 154. "
                        + "Got:\n" + analyzerOut);
    }

    // ---- plumbing: capture what Analyzer.main prints, once ----

    private static String analyzerOut = "";

    @BeforeAll
    static void runAnalyzer() {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
        try {
            Analyzer.main(new String[0]);
        } catch (Throwable t) {
            // Task 1 not done yet is fine — the assertions report it; a crash we surface here.
            System.setOut(original);
            if (!(t instanceof AssertionError)) System.out.println("Analyzer.main threw: " + t);
        } finally {
            System.setOut(original);
        }
        analyzerOut = buffer.toString();
    }
}

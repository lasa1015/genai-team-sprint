package com.fx.analyzer;

import java.util.*;

import com.fx.core.Currency;

/** Friday's collections recap — the loader works; the dedupe + grouping is Task 1.
 *  (No streams by design — Week 1 scope: plain loops, Set, Map.) */
public class Analyzer {

    public static void main(String[] args) throws Exception {
        List<Transfer> transfers = CsvLoader.load("ops/transactions.csv");
        System.out.println("Loaded: " + transfers.size());               // 1005 (3 corrupt skipped)

        // TODO Task 1 (collections recap) — two steps, both printing an exact line:
        //   1. DEDUPE with a LinkedHashSet (it uses YOUR Transfer.equals/hashCode):
        //        Set<Transfer> unique = new LinkedHashSet<>(transfers);
        //      print   "After dedupe: 1000 (removed 5)"
        //   2. GROUP BY CURRENCY into a Map<Currency, List<Transfer>> (computeIfAbsent),
        //      print each currency's count, then the busiest lane:
        //        "Busiest currency: SGD (154)"
        //
        // The RecapCheck grader asserts the dedupe count (1000) and the busiest line
        // (SGD, 154). The sort/top-5, removeIf(<100)->998, filters and Map.merge are
        // OPTIONAL — see "Extra challenges" in index.html.
    }
}

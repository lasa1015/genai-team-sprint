package com.fx.analyzer;

import com.fx.core.Currency;
// TODO Task 1 (collections recap): you'll need `import java.util.Objects;` for hashCode.

/**
 * Task 1 solution — equals/hashCode on business fields (id excluded) so the
 * Set can recognise "same money, different receipt number". One row of Day 3's
 * `transfer` table.
 */
public class Transfer {
    private final int id;
    private final int fromAccount;
    private final int toAccount;
    private final double amount;
    private final Currency currency;
    private final String executedAt;
    private final String status;

    public Transfer(int id, int fromAccount, int toAccount, double amount,
                    Currency currency, String executedAt, String status) {
        this.id = id; this.fromAccount = fromAccount; this.toAccount = toAccount;
        this.amount = amount; this.currency = currency;
        this.executedAt = executedAt; this.status = status;
    }
    public int getId() { return id; }
    public int getFromAccount() { return fromAccount; }
    public int getToAccount() { return toAccount; }
    public double getAmount() { return amount; }
    public Currency getCurrency() { return currency; }
    public String getExecutedAt() { return executedAt; }
    public String getStatus() { return status; }

    // TODO Task 1 (collections recap): override equals + hashCode using EVERY field
    //   EXCEPT id, so a HashSet treats "same money, new receipt number" as ONE transfer.
    //   - equals: `instanceof Transfer t`, then compare the six business fields
    //     (Double.compare for the double, == for the enum, .equals for the Strings).
    //   - hashCode: Objects.hash(...) over the SAME six fields.
    //   Without these, Java compares by identity and the dedupe silently keeps every
    //   re-send (1005 stays 1005) — the worst kind of bug, because nothing errors.

    @Override public String toString() {
        return "#" + id + " " + fromAccount + "->" + toAccount + " "
                + amount + " " + currency + " " + status;
    }
}

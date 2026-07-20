# Exercise 1 — Recap: exceptions & collections (the Friday catch-up)

*Decks 15 Exceptions · 13 Collections · ~45 min · grader `RecapCheck`*

> [Morning index](../TODO.md) · [Theory + cheatsheet](../index.html) · Next: [Exercise 2 →](02-junit-test-first.md)

### Why we're doing this

Two Friday behaviours are stubbed out in the baseline, each marked `// TODO Task 1`. You'll
put them back — not from scratch, the classes and fields are all there, just the key
methods are hollow. This is a recap, not a rebuild: fast, exact, and it hands you the very
code Exercise 2 will test.

Skills you're re-anchoring:
* The exception family: **checked** vs **unchecked**, and *throwing* instead of warning.
* `equals`/`hashCode` as a contract — and why a `HashSet` needs both to spot a duplicate.
* A `Map` grouping built with `computeIfAbsent`, no streams.

### What you're producing

`RecapCheck` green (`./mvnw test -Dtest=RecapCheck`). Five failing checks turn green; the
three already-green ones stay green.

### Step-by-step

**Part A — exceptions (`com.fx.core`, deck 15).**

1. **`Account.withdraw` grows up.** Open `Account.java`. It currently *warns* and returns.
   Make it `throw new InsufficientFundsException(amount, balance)` when `amount > balance`.
   (The `throws` clause is already on the signature — leave it.) The exception's message is
   **frozen**: it already names both the requested and the available amount, and Exercise 2
   asserts exactly that — so don't touch `InsufficientFundsException`, just throw it.
2. **`CurrencyConverter` guards its rate.** Both constructors/overloads have a
   `// TODO Task 1`. A non-positive rate is a *bug*, not an everyday event — reject it with
   the **unchecked** `InvalidRateException` (`if (rate <= 0) throw new InvalidRateException(rate);`).

**Part B — collections (`com.fx.analyzer`, deck 13).**

3. **Teach `Transfer` what "same" means.** Two payments are the *same* transfer when every
   business field matches — **except `id`** (a re-send gets a new receipt number). Override
   `equals` (`instanceof Transfer t`, then compare the six fields — `Double.compare` for the
   double, `==` for the enum, `.equals` for the Strings) and `hashCode`
   (`Objects.hash(...)` over the **same** six fields). Re-add `import java.util.Objects;`.
   Disagree between the two and a `HashSet` keeps duplicates *silently* — the worst way for
   anything to break.
4. **Dedupe and group in `Analyzer.main`.** After the `Loaded: 1005` line:
   - `Set<Transfer> unique = new LinkedHashSet<>(transfers);` → print
     `After dedupe: 1000 (removed 5)`.
   - Group the deduped list by currency into a `Map<Currency, List<Transfer>>` with
     `computeIfAbsent(t.getCurrency(), k -> new ArrayList<>()).add(t)`, print each count,
     and name the busiest lane → `Busiest currency: SGD (154)`.

**Checkpoint:** `./mvnw test -Dtest=RecapCheck` — **all 8 green** (dedupe 1005→1000,
busiest SGD 154, `withdraw` throws, rate guarded). Commit: `feat: finish Friday — exceptions throw + dedupe/grouping`.

<details>
<summary>Stuck?</summary>

`equals` and `hashCode` must list the **same** fields or a `HashSet` misbehaves —
`Objects.hash(fromAccount, toAccount, amount, currency, executedAt, status)`. Include `id`
by mistake and every row looks unique (you'll see 1005 survive). For the busiest currency,
track a running `max` and `busiest` while you print each lane's count. The sort/top-5,
`removeIf(<100)→998`, filters and `Map.merge` from Friday are **optional** — they live in
*Extra challenges* in `index.html`, not in the grader.
</details>

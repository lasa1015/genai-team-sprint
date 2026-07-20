# Exercise 2 — JUnit, test-first: `FxRate`, then the converter & the exception you just wrote

*Deck: JUnit 5 · ~50 min · checkpoint: FxRate test-first green + ≥7 converter/account tests*

> [Morning index](../TODO.md) · [Theory + cheatsheet](../index.html) · [← Exercise 1](01-recap-collections-exceptions.md) · Next: [Exercise 3 →](03-mockito-fake-the-feed.md)

### Why we're doing this

Your first real contact with automated testing — and you start the way professionals do,
**test-first**. Week 1 left exactly one Day-3 table without a Java twin: `fx_rate`. You'll
build that twin, `FxRate`, by writing the test *before* the class — red → green → refactor —
and it becomes the row your REST API serves tomorrow (EUR/USD **1.0818** on 2026-01-12,
straight from `fxdb-seed.sql`). Then you turn the same discipline on `CurrencyConverter`
and — the important one — on the `withdraw` you just made throw in Exercise 1. The unhappy path
matters more than the happy path in a money system; `withdraw` throwing only *earns* that
name once a test provably fires when the balance is short.

Skills you're building:
* Red → green → refactor: the test is the spec; write the minimum that makes it pass.
* `@Test`, `@BeforeEach`, `@DisplayName` — a fixture that resets between tests.
* `assertEquals(expected, actual, delta)` — why doubles need a tolerance, and how small.
* `assertThrows` — capturing the exception and asserting its message + fields.
* `@ParameterizedTest` + `@CsvSource` — one method, many rows.
* Behaviour-driven test names: `withdraw_moreThanBalance_throws`, not `testWithdraw`.

### What you're producing

- **`FxRate` built test-first** — `FxRateTest` first (it won't even compile: that's the
  point, the test is the spec), then the minimal `FxRate` that turns it green, then a
  refactor with the tests standing guard.
- **A `CurrencyConverterTest` + `Account` suite** — **at least seven green executions**,
  enough that flipping any one behaviour in production code makes at least one go red.

### Step-by-step

1. **Red.** Create `src/test/java/com/fx/core/FxRateTest.java` with a `@BeforeEach` fixture
   building `new FxRate(Currency.EUR, Currency.USD, 1.0818, "2026-01-12")` and a test:
   `assertEquals(108.18, rate.convert(100), 1e-4)`. It won't compile — `FxRate` doesn't
   exist yet. **That red is the spec.**
2. **Green.** Write the minimal `FxRate` in `src/main/java/com/fx/core/`: four fields
   (`Currency base`, `Currency quote`, `double rate`, `String rateDate`), a constructor,
   getters, `convert(amount)`. Suite green.
3. **Red again.** Add `inverted()` round-trips (EUR/USD → USD/EUR at `1/rate`), and
   `assertThrows(InvalidRateException.class, () -> new FxRate(EUR, USD, -1, …))` — the same
   unchecked guard you restored in Exercise 1, now on `FxRate`. Make them pass.
4. **Refactor.** Give `FxRate` a `toString()` → `EUR/USD 1.0818 (2026-01-12)` and a test
   pinning it. Tests stay green: you changed shape, not behaviour.
5. **The converter suite** (`CurrencyConverterTest`): two known-conversion `@Test`s with
   `assertEquals(expected, actual, 1e-9)`, a zero-amount case, and a `toString` check.
6. **Account happy + unhappy paths.** Deposit-then-read; then the important one —
   `withdraw_moreThanBalance_throws` via
   `assertThrows(InsufficientFundsException.class, () -> …)`, asserting **both** amounts on
   the captured exception (`getRequested`, `getAvailable`) and that the message contains the
   requested amount. *This is Exercise 1's `withdraw`, now under a machine's eye.*
7. **One parameterized test.** `conversionTable(double amount, double rate, double expected)`
   driven by `@CsvSource({"100,1.10,110.0", …})` over ≥3 rows.
8. **Name every test after its behaviour**, then **run the suite** — all green.

**Checkpoint:** `FxRate` built test-first (green), plus **≥7 green converter/account tests
including one parameterized set** (`./mvnw test`).

<details>
<summary>Stuck?</summary>

Test-first means the compile error is expected — the test names the class into existence.
`FxRate`'s guard throws `InvalidRateException` (the unchecked one). Parameterized shape:
`@CsvSource({"100,1.10,110.0", "0.01,1.10,0.011", ...})` with matching method parameters —
types auto-convert. Assert the exception message via the object returned by `assertThrows`.
If IntelliJ can't find `@Test`, you're importing `org.junit`, not `org.junit.jupiter.api`.
</details>

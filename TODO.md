# sha luo branch  111  66666

# fx-w2d1-m — Friday's recap, then tests first (morning)

*Decks: Collections 13 · Exceptions 15 · JUnit 5 · Mockito · Test Driven Development v2 · GenAI for tests · morning, ~3.5h hands-on*

> Open [`index.html`](index.html) for a scrollable overview, cheatsheet and extra
> challenges. Each task is a sheet in [`exercises/`](exercises/) — struggle first; your
> instructor holds the worked solutions and shares them once you've had a proper go.
> **This is the morning session only.** The afternoon is a separate subject.

---

## The story so far

Friday ran out of clock. You got most of the way through Core Java, but two things
never got finished: the **exception family** (making `withdraw` *throw* instead of
mumble a warning) and the **collections** work (teaching a `Set` to spot a duplicate
transfer). They matter too much to skip — a money system that can't refuse a withdrawal,
or that silently double-counts a re-sent payment, isn't a money system. So this morning
opens by closing Friday, fast.

Then the week's real theme begins. Week 1 ended with you writing Java that behaved right
*for one person on one Friday afternoon*: you ran `Main`, you eyeballed the output, you
nodded. That doesn't scale. This week the exchange goes to production, and "trust me, I
checked" stops counting. From today on, a machine has to be able to prove — in seconds,
on demand, in front of a room — that every behaviour still holds. Today is the day tests
stop being an afterthought and become the way you write code.

There's a nice symmetry to it: **the code you rebuild in Exercise 1 is the first code you'll
write tests for in Exercise 2.** Friday's `withdraw` that throws is exactly the unhappy path
your JUnit suite must pin down. You'll finish the behaviour, then prove it.

Two newcomers appeared in `com.fx.core` overnight (given, both already in `fx-app/`):
`RateFeed`, an interface for a live market rate source that **nobody has built yet**, and
`ConversionService`, which depends on it. You're not going to wait for the feed — you'll
fake it with Mockito. Nothing new gets installed: `spring-boot-starter-test` has been
quietly bundling JUnit 5, Mockito and AssertJ into your classpath for a week.

## Start your project first (do this before Exercise 1)

```bash
./mvnw test -Dtest=RecapCheck    # RED — 8 tests, 5 failing. That's Exercise 1.
git log --oneline -3             # you are where you left off
```

Unlike a normal test run, this one starts **red on purpose**. `RecapCheck` is a *given*
grader — read it (`src/test/java/com/fx/recap/RecapCheck.java`) before you write a line:
it's your Exercise 1 target **and** your first worked example of a JUnit test. Five checks
fail because Friday's work is gutted out of the baseline. Make them green, and Friday is
finished.

## The exercises

Work them in order — each hands the next its material. Full step-by-step, checkpoints and
"stuck?" hints are in each sheet.

| # | Sheet | Time | Checkpoint |
|---|-------|------|-----------|
| 1 | [Recap: exceptions & collections](exercises/01-recap-collections-exceptions.md) | ~45 min | `RecapCheck` all 8 green (dedupe 1005→1000, busiest **SGD 154**, `withdraw` throws, rate guarded) |
| 2 | [JUnit, test-first: `FxRate` + converter/account](exercises/02-junit-test-first.md) | ~50 min | `FxRate` built test-first + **≥7** green tests incl. one parameterized set |
| 3 | [Mockito: fake the missing feed](exercises/03-mockito-fake-the-feed.md) | ~45 min | **4+** green tests; the guard-order test goes red on a reorder |
| 4 | [TDD kata: the `FeeCalculator`](exercises/04-tdd-feecalculator.md) | ~50 min | `tdd:` commits in red-green cadence; suite green; boundary in a test name |
| 5 | [AI-driven testing (closer)](exercises/05-ai-driven-testing.md) | ~30 min | `docs/genai-tests-note.md` names the assertion the code-only run missed |

*Timing is a guide, not a gate — Exercise 1 and the AI closer are the two we protect if the
clock runs short; the TDD kata's rules ⑤–⑦ can spill into spare afternoon moments.*

---

## End-of-morning: are you done?

You should be able to say yes to all of these:

- [ ] `./mvnw test` runs green in one pass — **RecapCheck (8) + the testing suites** — with
      **no failures** (one `@Disabled` example may show as skipped).
- [ ] `RecapCheck` is green: `withdraw` throws, the rate is guarded, `Transfer` dedupes
      1005→1000, the busiest currency prints **SGD (154)**.
- [ ] `src/test/java/com/fx/core/` contains:
  - [ ] `FxRateTest` — built test-first (convert, inverted, the invalid-rate throw, toString).
  - [ ] `CurrencyConverterTest` — ≥7 green tests incl. one parameterized set and the
        `withdraw_moreThanBalance_throws` unhappy path.
  - [ ] `ConversionServiceTest` — 4+ tests, incl. one that catches the guard-clause reorder.
  - [ ] `FeeCalculatorTest` — one test per rule you reached + a named boundary decision.
- [ ] `git log --oneline` shows `tdd:` commits in red-green-refactor cadence.
- [ ] `docs/genai-tests-note.md` committed with the code-vs-rules comparison.

Tomorrow the stub dies: you build the real REST API on fxdb — and `FeeCalculator` starts
charging. This suite is what will tell you tomorrow's Spring wiring is right the first time.
**Do not delete or weaken any test you wrote today.**
# 1-line change by yuki
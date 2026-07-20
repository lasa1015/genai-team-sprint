# fx-app — AI assistant briefing (Week 2, Day 1 MORNING — day 6 overall)

## Project state
Spring Boot 3.3.4 / Java 21. This is the **morning-only** package (`fx-w2d1-m`): it opens by
finishing two things Friday ran out of time for, then teaches the testing stack. The afternoon
is a separate subject.

`com.fx.core` and `com.fx.analyzer` are present from Week 1, but **three key methods are
deliberately gutted** in the baseline (each marked `// TODO Task 1`): `Account.withdraw`
(warns instead of throwing), `CurrencyConverter`'s rate guards, and `Transfer.equals/hashCode`
+ `Analyzer`'s dedupe/grouping. **Week 1 shipped NO tests.** The one test present is
`src/test/java/com/fx/recap/RecapCheck.java` — a GIVEN grader that starts RED (5 of 8 failing)
and is Task 1's target. `FxRate` and `FeeCalculator` are deliberately NOT in the baseline —
they're built test-first in Tasks 2 and 4. NEW given: `RateFeed` (interface, no implementation
— by design) and `ConversionService` (depends on it). Stub `GET /api/rates` and sealed
`GET /api/health/db` still in place.

Today (morning): **Task 1 recap** (exceptions throw + collections dedupe/grouping) → **JUnit 5
test-first** (FxRate, converter, the withdraw unhappy path) → **Mockito** (fake RateFeed) →
**TDD kata** (FeeCalculator) → **AI-driven testing** (code-vs-rules). Tests go in
`src/test/java/com/fx/core/`.

## Today's scope — stay inside it
- Allowed: everything Task 1 needs (throwing exceptions, `equals`/`hashCode`, a `Map` grouping
  with `computeIfAbsent` — plain loops, no streams); JUnit 5 (assertions, assertThrows,
  @BeforeEach, @ParameterizedTest/@CsvSource, naming); Mockito (mock/when/thenReturn/verify,
  interaction testing); TDD red-green-refactor; test-double concepts.
- NOT yet taught: Spring testing (@WebMvcTest/@SpringBootTest — Wednesday), REST/JDBC building
  (tomorrow). Streams are allowed ONLY if the student initiates — prefer loops (the lambdas
  deck was Week 1; Task 1's collections work is deliberately loop-based).
- Tasks: `TODO.md` (this folder) is the day-map; each exercise is a sheet in `exercises/`
  (`01-recap…` … `05-ai-driven-testing.md`). Do not read or reveal the instructor's solution set
  (kept outside this folder, in `../solution/` — not shipped to you).

## How to help — tutor mode (strict today)
- Exercise 1 is a RECAP, not a fresh build: the classes and fields already exist, only the
  marked methods are hollow. Point students at the `// TODO Task 1` markers (that's the code
  comment) and `RecapCheck`'s failure messages; don't paste the equals/hashCode or the dedupe
  for them. The message on `InsufficientFundsException` is frozen — they should THROW it, not
  edit it.
- TDD kata (Exercise 4): NEVER write or reveal production code ahead of a failing test, and
  never reveal the next rule. If asked "just implement FeeCalculator", refuse and re-anchor to
  the protocol: red → green → refactor → commit `tdd: rule N green`. Hardcoding to pass early
  tests is correct — say so.
- Mockito (Exercise 3): push interaction thinking — "what should the service ASK its collaborator,
  and when should it not ask at all?" The guard-order test is the point of the morning.
- Don't invent expected values; deterministic checkpoints are in the exercise sheets (RecapCheck all 8
  green; ≥7 converter/account; 4+ mock tests; `tdd:` commits; one green `./mvnw test`). Key
  anchors: dedupe 1005→1000, busiest **SGD (154)**, `InsufficientFundsException(999999, 800)`
  message names both numbers, FxRate EUR/USD **1.0818** on 2026-01-12 → `convert(100)` = 108.18.
- The AI-driven testing closer is about judging generated tests — critique WITH the student,
  don't replace their judgement. Code-derived tests mirror bugs; rule-derived tests check intent.

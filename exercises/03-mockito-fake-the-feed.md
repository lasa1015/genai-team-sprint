# Exercise 3 — Mockito: fake the missing feed

*Deck: Mockito · ~45 min · checkpoint: 4+ green tests, the guard-order test catches a reorder*

> [Morning index](../TODO.md) · [Theory + cheatsheet](../index.html) · [← Exercise 2](02-junit-test-first.md) · Next: [Exercise 4 →](04-tdd-feecalculator.md)

### Why we're doing this

`ConversionService` needs a `RateFeed`. There is no `RateFeed` implementation, and there
won't be one until tomorrow when Spring wires it to fxdb. In real projects you meet this
constantly: your class depends on a collaborator that's slow, flaky, expensive, or — like
this one — doesn't exist yet. Waiting is the amateur move. Mocking is the pro one.

Mockito lets you build a stand-in that answers any question you script it to (stubbing:
`when(...).thenReturn(...)`) and lets you interrogate the conversation after the fact
(verification: `verify(...)`, `never()`, `verifyNoInteractions(...)`). A stubbed answer is
a *state test* — "given the feed says 1.08, does the service compute 108?". A verify is an
*interaction test* — "did the service ever call the feed at all, and in what order relative
to the guard clause?". Step 4 below is a pure interaction test, and it's the
pedagogical heart of the morning.

Skills you're building:
* `mock(RateFeed.class)` / `@Mock` — a stand-in that does nothing until you script it.
* `when(...).thenReturn(...)` / `.thenThrow(...)` — teach the stub to answer.
* `verify(mock).method(...)`, `verify(mock, never())`, `verifyNoInteractions(mock)`.
* State-vs-interaction testing as two distinct tools.

### What you're producing

A new test class `ConversionServiceTest` with at least four green tests, one of which will
provably break if you reorder the guard clause in `ConversionService.convert`.

### Step-by-step

1. **Create `ConversionServiceTest.java`.** Annotate with
   `@ExtendWith(MockitoExtension.class)` and declare `@Mock RateFeed feed;`.
2. **State test — happy path.** `when(feed.rateFor("EUR/USD")).thenReturn(1.0818);`,
   `new ConversionService(feed)`, call `convert(...)`, assert the result, then
   `verify(feed).rateFor("EUR/USD")`. (The `RateFeed` interface takes the pair as one
   `"BASE/QUOTE"` string — check `com.fx.core.RateFeed`.)
3. **State test — unknown pair.**
   `when(feed.rateFor("XXX/YYY")).thenThrow(new IllegalArgumentException("unknown pair"))`,
   assert the service bubbles it up with `assertThrows`.
4. **The interaction test — the important one.** When the amount is invalid (zero or
   negative), `convert` throws **and the feed is never consulted**:
   `verify(feed, never()).rateFor(anyString())` or `verifyNoInteractions(feed)`. This is the
   *guard clause runs first* contract.
5. **Now break it.** Reorder `ConversionService.convert` so the guard fires *after* the feed
   call. Rerun `./mvnw test`. Watch the interaction test go red — the test catching a bug
   the state tests can't see. Restore the correct order. Do not commit the broken version.
6. **A fourth test of your choice** — a second stubbed pair, a `verify(feed, times(1))`, or
   an `ArgumentCaptor` peek.

**Checkpoint:** **4+ green tests**; step-4's test **fails if you reorder the guard clause** —
try it, watch it catch you, restore it.

<details>
<summary>Stuck?</summary>

`verify(feed, never()).rateFor(anyString())` after a call that fails validation. A stub
answers questions; a verify asks "did the conversation happen as designed?". If Mockito
complains about "strict stubbing" on an unused `when`, delete the stub — you added it before
you needed it.
</details>

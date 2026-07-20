# Exercise 4 — TDD kata: the FeeCalculator

*Deck: Test Driven Development v2 · ~50 min · checkpoint: `tdd:` commits + green suite + named boundary*

> [Morning index](../TODO.md) · [Theory + cheatsheet](../index.html) · [← Exercise 3](03-mockito-fake-the-feed.md) · Next: [Exercise 5 →](05-ai-driven-testing.md)

### Why we're doing this

TDD is not a testing technique. It's a *design* technique that happens to leave a test suite
behind. Writing the test first forces you to state, in executable form, what "done" means
for this rule *before* you write a line of code for it — and it means you literally cannot
ship production code that has no test.

The rhythm is **RED → GREEN → REFACTOR**. RED is a failing test that fails for the *right*
reason (a compile error is not a red — fix the compile first). GREEN is the dumbest possible
code that makes the red go away; hardcoding the return on rule 1 is not a shortcut, it's
*orthodox*. REFACTOR is the license you earn by being green.

The rules for `FeeCalculator` arrive one at a time. **Do not read ahead.** The `git log`
shape at the end (commits alternating red and green) matters more than the final code.

### What you're producing

`FeeCalculator` in `src/main/java/com/fx/core/` + `FeeCalculatorTest`. A git log with
`tdd: rule N green` commits. A boundary decision visible in a test name.

### Step-by-step — rules arrive in order; finish each (red, green, refactor, commit) before the next

**Rule ①** — Retail, amount **< 1,000** → **1.0%** fee.
1. Smallest failing test: `retailSmallAmountPaysOnePercent`, `feeFor(500, false)` returns
   `5.00` (`1e-9` delta). Compile-error red isn't a real red — create the class with a stub
   returning `0.0` first. Then green with the dumbest thing: `return amount * 0.01;`. Commit
   `tdd: rule 1 green`.

**Rule ②** — Retail, **1,000 ≤ amount < 10,000** → **0.5%**.
1. `retailMidTierPaysHalfPercent`: `feeFor(5_000, false)` → `25.00`. Add the branch. Consider
   extracting a `tierPct(amount)` helper. Commit.

**Rule ③** — Retail, amount **≥ 10,000** → **0.25%**.
1. `retailLargePaysQuarterPercent`: `feeFor(50_000, false)` → `125.00`. Commit.

**Rule ④** — Retail **minimum fee 1.00** (a 50-unit conversion still pays 1.00).
1. `minimumFeeKicksIn`: `feeFor(50, false)` → `1.00`. Green with `Math.max(fee, 1.00)`. Commit.

> **If the morning is tight, ①–④ + the boundary (below) are the required core.** Rules ⑤–⑦
> continue the exact same rhythm — do them as time allows or in the afternoon's spare moments.

**Rule ⑤** — Business: flat **0.25%**, minimum **5.00**. Cover both branches (a small amount
that hits the floor, a large one that doesn't). Commit.

**Rule ⑥** — A **negative amount** throws `IllegalArgumentException`. `assertThrows`, then
guard at the top. Commit.

**Rule ⑦ — boundary** — what happens at *exactly* 1,000 and *exactly* 10,000? **You decide**,
then encode the decision in the test name (`boundaryExactly1000PaysMidTier`). The reference
puts 1,000 in the mid tier and 10,000 in the large tier — you may differ if your name says so.

**Final refactor pass** with the tests as your safety net: `tierPct` extracted, negative-guard
first, min-fee applied last.

**Checkpoint:** `git log --oneline` shows **≥4 `tdd:` commits** (≥6 if you reached rule ⑦);
suite green; your boundary decision is in a test name.

<details>
<summary>Stuck?</summary>

If rule 1 is "&lt;1,000 → 1.0%", then `return amount * 0.01;` is a *correct* green — resist
generality until a test demands it. When you refactor, run the tests after every small edit;
the moment they go red under refactor, undo — refactoring under red is gambling, not
refactoring.
</details>

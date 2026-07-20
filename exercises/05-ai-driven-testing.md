# Exercise 5 (closer) — AI-driven testing: the machine writes tests, you write judgement

*Deck: GenAI for tests · ~30 min · checkpoint: `docs/genai-tests-note.md` naming the code-only gap*

> [Morning index](../TODO.md) · [Theory + cheatsheet](../index.html) · [← Exercise 4](04-tdd-feecalculator.md)

### Why we're doing this

An AI assistant will happily generate a hundred JUnit tests for any class you paste at it.
The problem: if you give it *code*, the tests mirror the code — including its bugs. Every
test passes; the suite is worthless as a specification because it agrees with whatever the
code happens to do. If instead you give it the *rules* the code was supposed to implement,
the tests check *intent* — and can catch the implementation when it's wrong.

You'll run both experiments on the `FeeCalculator` you just built, and write down the
difference in your own words. The note is a testable claim you'll refer back to every time
an assistant offers to write tests for you. **This is the "trust but verify" habit, applied
to the tool that will write the most code for you this course.**

### What you're producing

`docs/genai-tests-note.md`, five lines minimum, comparing the two runs. It must name at
least one assertion the code-only run got wrong or missed, and identify which of the two
suites caught the minimum-fee edge (rule 4). Committed to the repo.

### Step-by-step

1. **Round 1 — from the code.** Give your assistant `FeeCalculator.java`'s source. Say,
   verbatim: *"Generate JUnit 5 tests for this class."* Do not give it the rules. Save to a
   scratch file (`FeeCalculatorTest_fromCode.java` — you won't keep it), run it. Note which
   of your seven rules it happened to cover, and which it merely *mirrored*.
2. **Round 2 — from the rules.** Fresh session. Give it *just the seven rules*, exactly as
   written in Exercise 4. Say: *"Generate JUnit 5 tests from these rules."* Save as
   `FeeCalculatorTest_fromRules.java`, run it against your `FeeCalculator`. What broke? What
   did it check that the code-only run didn't?
3. **The comparison note** (`docs/genai-tests-note.md`, ≥5 lines): one assertion the
   code-only run got wrong or missed; which suite caught the rule-4 minimum-fee edge; one
   sentence on why the difference showed up.
4. **Then the senior-teammate question.** Copy just your *own* `FeeCalculatorTest` and ask,
   verbatim: *"Here is my FeeCalculatorTest suite: `<paste>`. What behaviour is still
   untested?"* Reject aggressively — one sentence per rejection in the commit message. If
   exactly one suggestion survives, implement it **red first**, then green:
   `tdd: rule 8 (genai) green`. Zero survivors is a valid outcome — own the "no".
5. **Clean up.** Delete the two scratch test files (no competing `FeeCalculatorTest` classes
   ship). Commit: `genai: code-vs-rules test note + reviewed suggestions`.

**Checkpoint:** the note names ≥1 assertion the code-only run missed, and which suite caught
rule 4's minimum-fee edge; any accepted suggestion was implemented red-first.

<details>
<summary>Stuck?</summary>

If both runs pass every test, you're not looking hard enough — the rules-only run should be
surprised by something (the exact 1,000 boundary, a rounding choice). If neither run touches
rule 4's minimum, that's *itself* the finding: neither prompt hinted at it, so the model
didn't invent it. Say so.
</details>

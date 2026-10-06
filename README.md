<div align="center">

# Fragile Inputs

**A field guide to the inputs that break software — built for QA engineers and SDETs.**

486 annotated edge-case values across 21 industries plus accessibility. Every
value says what it tests, what it breaks, and what correct handling looks like.

[**Browse the interactive guide »**](https://gencdeveloper.github.io/fragile-inputs/) &nbsp;·&nbsp;
[npm](https://www.npmjs.com/package/fragile-inputs) &nbsp;·&nbsp;
[Java (JitPack)](https://jitpack.io/#gencdeveloper/fragile-inputs) &nbsp;·&nbsp;
[☕ Buy me a coffee](https://buymeacoffee.com/yunusemreozudogru)

[![npm](https://img.shields.io/npm/v/fragile-inputs?color=2648C7&label=npm)](https://www.npmjs.com/package/fragile-inputs)
[![JitPack](https://jitpack.io/v/gencdeveloper/fragile-inputs.svg)](https://jitpack.io/#gencdeveloper/fragile-inputs)
![inputs](https://img.shields.io/badge/inputs-486-5A6878)
![license](https://img.shields.io/badge/license-MIT-1F7A4D)

</div>

---

## Why this exists

Every tester keeps a mental list of strings that tend to break things: the
apostrophe in `O'Brien`, the emoji that blows past a length limit, the ZIP code
that loses its leading zero. Those lists live in people's heads and in scattered
gists, and the best-known public one — *Big List of Naughty Strings* — is a flat
wall of text with no explanation of what each line is for.

**Fragile Inputs is that knowledge, written down and explained.** For every
value you get three things a raw list never gives you:

- **Tests** — what the input actually exercises.
- **Breaks** — the concrete failure when it's mishandled (wrong charge, lost
  record, silent truncation, XSS).
- **Expected** — what correct behavior looks like, so a failing case turns
  straight into a filed bug instead of a shrug.

I built it for people who do my job — **SDETs and QA engineers** — so that
"test the edge cases" stops meaning "remember the edge cases."

## How to use it

Pick your language, install it once, and feed the values straight into your
tests — one loop covers a whole category of edge cases.

| You want to… | Use |
|---|---|
| Explore, search and copy values by hand, with WCAG tags and byte metrics | the **[web guide](https://gencdeveloper.github.io/fragile-inputs/)** |
| Drive automated tests in JS/TS (Playwright, Cypress, Jest, Vitest) | the **npm package** |
| Drive automated tests in Python (pytest, unittest) | the **Python package** (install from GitHub) |
| Drive automated tests in Java (Selenium, JUnit 5, TestNG) | the **Java package** (via JitPack) |
| Pull the data into your own tooling | **`data/fragile-inputs.json`** |

### npm

```bash
npm install fragile-inputs
```

```js
const { test, expect } = require('@playwright/test');
const { getValues } = require('fragile-inputs');

for (const value of getValues({ category: 'fin' })) {   // fintech amounts, cards, IBANs…
  test(`amount field survives ${JSON.stringify(value)}`, async ({ page }) => {
    await page.goto('/checkout');
    await page.getByLabel('Amount').fill(value);
    await page.getByRole('button', { name: 'Pay' }).click();
    await expect(page.locator('.server-error')).toHaveCount(0);
  });
}
```

### Python

Not on PyPI yet — install straight from GitHub (pip handles the rest):

```bash
pip install "git+https://github.com/gencdeveloper/fragile-inputs.git#subdirectory=packages/pip"
```

```python
import pytest
from fragile_inputs import get_values

@pytest.mark.parametrize("value", get_values(category="name"))
def test_name_field(value, client):
    r = client.post("/signup", {"full_name": value})
    assert r.status_code in (201, 422)   # saved, or cleanly rejected — never 500
```

### Java (Selenium + JUnit 5)

Add the JitPack repository, then the dependency (no Sonatype account needed):

```xml
<repositories>
  <repository><id>jitpack.io</id><url>https://jitpack.io</url></repository>
</repositories>
<dependency>
  <groupId>com.github.gencdeveloper</groupId>
  <artifactId>fragile-inputs</artifactId>
  <version>v1.0.1</version>
</dependency>
```

```java
import io.github.gencdeveloper.fragileinputs.FragileInputs;

@ParameterizedTest
@MethodSource("fintechAmounts")
void amountFieldIsRobust(String value) {
    driver.findElement(By.id("amount")).sendKeys(value);
    driver.findElement(By.id("pay")).click();
    assertTrue(driver.findElements(By.cssSelector(".server-error")).isEmpty());
}

static List<String> fintechAmounts() {
    return FragileInputs.getValues("fin");
}
```

The Java package has **no runtime dependencies** (it ships a tiny JSON reader),
so it won't collide with the Jackson/Gson versions already on your test
classpath. Requires Java 8+. See [packages/java](packages/java).

### Just the data

```js
const data = require('fragile-inputs/data.json');
```
```python
import json, importlib.resources as r
data = json.loads(r.files("fragile_inputs").joinpath("data.json").read_text("utf-8"))
```
Or read `data/fragile-inputs.json` straight from the repo. It validates against
[`schema/fragile-inputs.schema.json`](schema/fragile-inputs.schema.json).

## What's inside

Three groups, 42 categories, 486 inputs.

**General (198)** — the edge cases every text field, form and API should survive:
Length & whitespace · Names · Addresses & phone · Email · Numbers & currency ·
Dates & time · Case & locale · Invisible characters · Lookalikes & normalization ·
Emoji & graphemes · Scripts & direction · Reserved words · File names · Passwords ·
Security.

**Industry (235)** — inputs specific to regulated and transactional products:
Fintech & payments · AML & sanctions screening · Gambling & lottery · Healthcare ·
E-commerce · Travel & aviation · HR & payroll · Automotive & mobility · Telecom ·
Logistics & shipping · Insurance · Government & public services · Energy &
utilities · Real estate & property · Legal & compliance · Education · Pharma &
life sciences · Manufacturing · Construction & engineering · Media & content ·
Cybersecurity & networking.

**Accessibility (53)** — text, settings and console snippets for testing with
assistive tech, each mapped to a WCAG 2.2 success criterion: Screen reader
content · Zoom, reflow & spacing · Keyboard & focus · Forms & errors · Color,
motion & display · Images & language.

### The shape of one input

```json
{
  "id": "fi-029",
  "group": "gen",
  "category": "addr",
  "title": "ZIP code with leading zero",
  "value": "02134",
  "tests": "Boston-area ZIPs start with 0.",
  "breaks": "Stored as a number it becomes 2134; spreadsheets drop the zero too.",
  "expected": "Stored and exported as text."
}
```

Accessibility inputs add a `"wcag"` field. Date-relative inputs (for example
*“a card expiring this month”*) carry a `"dynamic"` rule and `resolve()`
recomputes them for today, so the dataset never goes stale.

## Data-driven testing patterns

**Cypress**
```js
import { getValues } from 'fragile-inputs';
getValues({ category: 'email' }).forEach((value) => {
  it(`handles email ${value}`, () => {
    cy.visit('/signup');
    cy.get('#email').type(value, { parseSpecialCharSequences: false });
    cy.get('form').submit();
    cy.get('.stacktrace').should('not.exist');
  });
});
```

**unittest (Python)**
```python
from fragile_inputs import get_values

class SearchTests(unittest.TestCase):
    def test_search_never_crashes(self):
        for value in get_values(group="gen"):
            with self.subTest(value=value):
                self.assertNotEqual(search(value).status, 500)
```

**Target a product area.** Testing a sanctions-screening screen like the one
this project started from? `get_values(category="aml")`. A checkout?
`category=["fin", "shop"]`. An a11y audit? `get_inputs(group="a11y")`, then work
through each WCAG criterion by hand — these are the checks scanners miss.

## A note on the Security and AML inputs

The **Security** category contains basic probes (SQL, XSS, SSRF, template
injection) for checking whether input is ever interpreted as code. Use them only
on systems you are authorized to test. The **AML** names are fictional; pair
them with matching entries in your own test watchlist. Tax and reporting
thresholds (W-2G, CTR, FLSA, etc.) vary by jurisdiction and change over time —
confirm them against your own requirements.

## Contributing

New edge cases are very welcome — especially ones you've seen break something
real. Add a row to [`data/fragile-inputs.json`](data/fragile-inputs.json), run
`node scripts/build.mjs`, and open a PR. See
[CONTRIBUTING.md](CONTRIBUTING.md).

## Build

`data/fragile-inputs.json` is the single source of truth. The build inlines it
into the web page and copies it into all three packages:

```bash
node scripts/build.mjs
```

## Support

Fragile Inputs is free and MIT-licensed. If it caught a bug before your users
did, [buy me a coffee](https://buymeacoffee.com/yunusemreozudogru) ☕ — it keeps
the list growing.

## License

MIT © Yunus Emre Ozudogru

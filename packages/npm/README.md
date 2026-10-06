# fragile-inputs (Node)

Edge-case input strings for QA/SDET testing — 486 annotated values that break
forms, search, databases, exports and assistive tech, across 21 industries plus
accessibility. Each value comes with what it tests, what it breaks, and the
correct behavior. Ships with TypeScript types.

```bash
npm install fragile-inputs
```

## Use it in Playwright

```js
const { test, expect } = require('@playwright/test');
const { getValues } = require('fragile-inputs');

for (const value of getValues({ category: 'fin' })) {
  test(`amount field survives: ${JSON.stringify(value)}`, async ({ page }) => {
    await page.goto('/checkout');
    await page.getByLabel('Amount').fill(value);
    await page.getByRole('button', { name: 'Pay' }).click();
    // handled or cleanly rejected — never a 500 or a silent wrong charge
    await expect(page.locator('.server-error')).toHaveCount(0);
  });
}
```

## API

```js
const fi = require('fragile-inputs');          // or: import * as fi from 'fragile-inputs'

fi.getInputs({ category: 'aml' });              // FragileInput[] for one category
fi.getInputs({ group: 'a11y', wcag: true });    // a11y inputs that cite a WCAG criterion
fi.getInputs({ hasInvisible: true });           // values with invisible characters
fi.getValues({ category: ['fin', 'num'] });     // just the strings, ready for test.each
fi.getById('fi-135');                           // a single input
fi.resolve(fi.getById('fi-135'));               // date-relative values computed for today

fi.INPUTS; fi.CATEGORIES; fi.GROUPS; fi.META;   // raw data
```

Each input has: `id, group, category, title, value, tests, breaks, expected,
wcag?, hasInvisible?, dynamic?`.

Groups: `gen` (general), `ind` (industry), `a11y` (accessibility).

Raw JSON is also importable: `require('fragile-inputs/data.json')`.

## Why

Most "naughty strings" lists are a flat wall of text. This one tells you, for
every value, what it exercises and what correct handling looks like — so a
failing case is a filed bug, not a shrug.

Part of the **Fragile Inputs** project: <https://github.com/gencdeveloper/fragile-inputs>

If it saves you a production incident, you can
[buy me a coffee](https://buymeacoffee.com/yunusemreozudogru). ☕

MIT

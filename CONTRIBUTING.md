# Contributing to Fragile Inputs

Thanks for helping the list grow. The best contributions are edge cases you've
actually seen break something.

## Add an input

1. Open `data/fragile-inputs.json` — the single source of truth.
2. Add an object to the `inputs` array:

   ```json
   {
     "id": "fi-487",
     "group": "gen",
     "category": "email",
     "title": "Short, specific name of the case",
     "value": "the exact string to paste",
     "tests": "What this input exercises.",
     "breaks": "The concrete failure when it is mishandled.",
     "expected": "What correct behavior looks like."
   }
   ```

   - `id` is `fi-` plus the next number.
   - `group` is `gen`, `ind` or `a11y`. `category` must be an existing
     category id (see the `categories` array). To propose a new category, add
     it there too.
   - For accessibility inputs, add `"wcag": "1.4.3"` (the related 2.2 criterion).
   - If the value contains invisible characters, add `"hasInvisible": true`.
     Write them as real characters in the JSON — the build escapes them for the
     web page automatically.

3. Rebuild and sanity-check:

   ```bash
   node scripts/build.mjs
   ```

4. Open a pull request.

## What makes a good entry

- **Real.** It has broken, or clearly would break, a real system.
- **Specific.** "Tests" and "breaks" name a concrete mechanism and failure, not
  "might cause issues."
- **Actionable.** "Expected" tells a developer what correct handling is, so the
  entry turns into a bug report.

## Guidelines

- Keep Security probes to standard, well-known checks. This project helps teams
  test their own systems; it is not an exploit collection.
- AML and screening names must be fictional.
- Don't assert legal or regulatory thresholds as universal — note that they vary
  by jurisdiction.

## Local checks

```bash
node scripts/build.mjs                         # rebuild web + package data
cd packages/pip && PYTHONPATH=src python -m pytest -q
```

MIT © contributors

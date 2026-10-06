# fragile-inputs (Python)

Edge-case input strings for QA/SDET testing — 486 annotated values that break
forms, search, databases, exports and assistive tech, across 21 industries plus
accessibility. Each value comes with what it tests, what it breaks, and the
correct behavior.

Not on PyPI yet — install straight from GitHub:

```bash
pip install "git+https://github.com/gencdeveloper/fragile-inputs.git#subdirectory=packages/pip"
```

## Use it in pytest

```python
import pytest
from fragile_inputs import get_values

@pytest.mark.parametrize("value", get_values(category="fin"))
def test_amount_field_is_robust(value, client):
    resp = client.post("/charge", {"amount": value})
    assert resp.status_code in (200, 422)   # handled or cleanly rejected, never 500
```

## API

```python
from fragile_inputs import (
    INPUTS, CATEGORIES, GROUPS, META,
    get_inputs, get_values, get_by_id, get_category, get_group, resolve,
)

get_inputs(category="aml")             # list[Input] for one category
get_inputs(group="a11y", wcag=True)    # accessibility inputs that cite a WCAG criterion
get_inputs(has_invisible=True)         # values containing invisible characters
get_values(category=["fin", "num"])    # just the strings, ready to parametrize
get_by_id("fi-135")                    # a single Input
resolve(get_by_id("fi-135"))           # date-relative values computed for today
```

Each `Input` has: `id, group, category, title, value, tests, breaks, expected,
wcag, has_invisible, dynamic`.

Groups: `gen` (general), `ind` (industry), `a11y` (accessibility).

## Why

Most "naughty strings" lists are a flat wall of text. This one tells you, for
every value, what it exercises and what correct handling looks like — so a
failing case is a filed bug, not a shrug.

Part of the **Fragile Inputs** project: <https://github.com/gencdeveloper/fragile-inputs>

If it saves you a production incident, you can
[buy me a coffee](https://buymeacoffee.com/yunusemreozudogru). ☕

MIT

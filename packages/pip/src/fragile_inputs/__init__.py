"""fragile-inputs — edge-case input strings for QA/SDET testing.

A curated dataset of values that break forms, search, databases, exports and
assistive technology, each annotated with what it tests, what it breaks and
the correct behavior. Data-only, with a small query/resolve API.

    from fragile_inputs import get_values, get_inputs, INPUTS

    import pytest

    @pytest.mark.parametrize("value", get_values(category="fin"))
    def test_amount_field(value):
        ...
"""
from __future__ import annotations

import json
from dataclasses import dataclass
from datetime import date
from importlib import resources
from typing import Iterable, List, Optional, Sequence, Union

__all__ = [
    "INPUTS", "CATEGORIES", "GROUPS", "META",
    "get_inputs", "get_values", "get_by_id", "get_category", "get_group",
    "resolve", "Input", "Category", "Group",
]

__version__ = "1.0.0"


def _load() -> dict:
    with resources.files(__package__).joinpath("data.json").open(encoding="utf-8") as fh:
        return json.load(fh)


_DATA = _load()


@dataclass(frozen=True)
class Input:
    id: str
    group: str
    category: str
    title: str
    value: str
    tests: str
    breaks: str
    expected: str
    wcag: Optional[str] = None
    has_invisible: bool = False
    spaces_visible: bool = False
    dynamic: Optional[dict] = None
    value_is_snapshot: bool = False

    @classmethod
    def _from(cls, d: dict) -> "Input":
        return cls(
            id=d["id"], group=d["group"], category=d["category"], title=d["title"],
            value=d["value"], tests=d["tests"], breaks=d["breaks"], expected=d["expected"],
            wcag=d.get("wcag"), has_invisible=d.get("hasInvisible", False),
            spaces_visible=d.get("spacesVisible", False),
            dynamic=d.get("dynamic"), value_is_snapshot=d.get("valueIsSnapshot", False),
        )


@dataclass(frozen=True)
class Category:
    id: str
    group: str
    name: str
    description: str = ""
    warning: Optional[str] = None


@dataclass(frozen=True)
class Group:
    id: str
    name: str
    description: str = ""


INPUTS: List[Input] = [Input._from(d) for d in _DATA["inputs"]]
CATEGORIES: List[Category] = [
    Category(id=c["id"], group=c["group"], name=c["name"],
             description=c.get("description", ""), warning=c.get("warning"))
    for c in _DATA["categories"]
]
GROUPS: List[Group] = [
    Group(id=g["id"], name=g["name"], description=g.get("description", ""))
    for g in _DATA["groups"]
]
META = _DATA.get("counts", {}) | {
    "name": _DATA["name"], "version": _DATA["version"],
    "license": _DATA.get("license"), "funding": _DATA.get("funding"),
    "updated": _DATA.get("updated"),
}


def _as_set(v: Union[None, str, Iterable[str]]):
    if v is None:
        return None
    if isinstance(v, str):
        return {v}
    return set(v)


def resolve(inp: Input, today: Optional[date] = None) -> str:
    """Return the value for a given day. Inputs without a ``dynamic`` rule are
    returned unchanged."""
    dyn = inp.dynamic
    if not dyn:
        return inp.value
    base = today or date.today()
    if dyn["kind"] == "card-expiry":
        m = base.month - 1 + int(dyn.get("monthOffset", 0))
        y = base.year + m // 12
        return f"{m % 12 + 1:02d}/{y % 100:02d}"
    # offset-date
    y = base.year + int(dyn.get("years", 0))
    total = (base.month - 1) + int(dyn.get("months", 0))
    y += total // 12
    month = total % 12 + 1
    # clamp day for month length (e.g. offsetting onto a shorter month)
    import calendar
    day = min(base.day, calendar.monthrange(y, month)[1])
    d = date(y, month, day) + _timedelta_days(int(dyn.get("days", 0)))
    return d.isoformat()


def _timedelta_days(n: int):
    from datetime import timedelta
    return timedelta(days=n)


def get_inputs(
    group: Union[None, str, Iterable[str]] = None,
    category: Union[None, str, Iterable[str]] = None,
    wcag: bool = False,
    has_invisible: bool = False,
) -> List[Input]:
    """Filter the dataset by group, category, WCAG presence or invisibility."""
    g = _as_set(group)
    c = _as_set(category)
    out = []
    for x in INPUTS:
        if g is not None and x.group not in g:
            continue
        if c is not None and x.category not in c:
            continue
        if wcag and not x.wcag:
            continue
        if has_invisible and not x.has_invisible:
            continue
        out.append(x)
    return out


def get_values(today: Optional[date] = None, **filters) -> List[str]:
    """Just the values, with dynamic ones resolved to today (or ``today=``).

    Ideal for ``pytest.mark.parametrize``. Accepts the same filters as
    :func:`get_inputs`."""
    return [resolve(x, today) for x in get_inputs(**filters)]


def get_by_id(input_id: str) -> Optional[Input]:
    return next((x for x in INPUTS if x.id == input_id), None)


def get_category(category_id: str) -> Optional[Category]:
    return next((c for c in CATEGORIES if c.id == category_id), None)


def get_group(group_id: str) -> Optional[Group]:
    return next((g for g in GROUPS if g.id == group_id), None)

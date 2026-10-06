from datetime import date
import fragile_inputs as fi


def test_dataset_loads():
    assert len(fi.INPUTS) >= 480
    assert {g.id for g in fi.GROUPS} == {"gen", "ind", "a11y"}


def test_filter_by_category():
    fin = fi.get_inputs(category="fin")
    assert fin and all(x.category == "fin" for x in fin)


def test_values_are_strings():
    vals = fi.get_values(group="gen")
    assert vals and all(isinstance(v, str) for v in vals)


def test_wcag_filter():
    a11y = fi.get_inputs(wcag=True)
    assert a11y and all(x.wcag for x in a11y)


def test_dynamic_resolves_to_today():
    card = fi.get_by_id("fi-135")  # "Card expiring this month"
    assert card.dynamic
    today = date(2026, 10, 6)
    assert fi.resolve(card, today) == "10/26"


def test_ids_unique():
    ids = [x.id for x in fi.INPUTS]
    assert len(ids) == len(set(ids))

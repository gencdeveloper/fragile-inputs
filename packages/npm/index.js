'use strict';
/**
 * fragile-inputs — edge-case input strings for QA/SDET testing.
 * Data-only package: a curated dataset plus a small query/resolve API.
 */
const data = require('./data.json');

const INPUTS = data.inputs;
const CATEGORIES = data.categories;
const GROUPS = data.groups;
const META = {
  name: data.name, version: data.version, description: data.description,
  homepage: data.homepage, license: data.license, funding: data.funding,
  updated: data.updated, counts: data.counts
};

function pad2(n) { return String(n).padStart(2, '0'); }
function ymd(d) { return d.getFullYear() + '-' + pad2(d.getMonth() + 1) + '-' + pad2(d.getDate()); }

/**
 * Resolve a date-relative value for a given day. For inputs without a
 * `dynamic` rule the stored value is returned unchanged.
 * @param {object} input
 * @param {Date} [today]
 * @returns {string}
 */
function resolve(input, today) {
  const dyn = input && input.dynamic;
  if (!dyn) return input ? input.value : undefined;
  const base = today ? new Date(today.getFullYear(), today.getMonth(), today.getDate()) : new Date();
  if (dyn.kind === 'card-expiry') {
    const d = new Date(base.getFullYear(), base.getMonth() + (dyn.monthOffset || 0), 1);
    return pad2(d.getMonth() + 1) + '/' + String(d.getFullYear()).slice(2);
  }
  // offset-date
  const d = new Date(base.getFullYear(), base.getMonth(), base.getDate());
  if (dyn.years) d.setFullYear(d.getFullYear() + dyn.years);
  if (dyn.months) d.setMonth(d.getMonth() + dyn.months);
  if (dyn.days) d.setDate(d.getDate() + dyn.days);
  return ymd(d);
}

/**
 * Filter the dataset.
 * @param {object} [opts]
 * @param {string|string[]} [opts.group]      group id(s): gen | ind | a11y
 * @param {string|string[]} [opts.category]   category id(s), e.g. "fin", "aml"
 * @param {boolean} [opts.wcag]               only inputs that reference a WCAG criterion
 * @param {boolean} [opts.hasInvisible]       only inputs whose value has invisible characters
 * @returns {object[]}
 */
function getInputs(opts) {
  opts = opts || {};
  const inGroup = toSet(opts.group);
  const inCat = toSet(opts.category);
  return INPUTS.filter(x => {
    if (inGroup && !inGroup.has(x.group)) return false;
    if (inCat && !inCat.has(x.category)) return false;
    if (opts.wcag === true && !x.wcag) return false;
    if (opts.hasInvisible === true && !x.hasInvisible) return false;
    return true;
  });
}

/**
 * Just the values, ready for data-driven tests. Dynamic values are resolved
 * to today (or `opts.today`).
 * @param {object} [opts] same filters as getInputs, plus { today?: Date }
 * @returns {string[]}
 */
function getValues(opts) {
  opts = opts || {};
  return getInputs(opts).map(x => resolve(x, opts.today));
}

/** Look up a single input by id, e.g. "fi-001". */
function getById(id) { return INPUTS.find(x => x.id === id); }
/** Look up a category definition by id. */
function getCategory(id) { return CATEGORIES.find(c => c.id === id); }
/** Look up a group definition by id. */
function getGroup(id) { return GROUPS.find(g => g.id === id); }

function toSet(v) {
  if (v == null) return null;
  return new Set(Array.isArray(v) ? v : [v]);
}

module.exports = {
  INPUTS, CATEGORIES, GROUPS, META,
  getInputs, getValues, getById, getCategory, getGroup, resolve
};

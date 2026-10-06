export interface DynamicRule {
  kind: "offset-date" | "card-expiry";
  years?: number;
  months?: number;
  days?: number;
  monthOffset?: number;
  format?: string;
}

export interface FragileInput {
  /** Stable id, e.g. "fi-001". */
  id: string;
  /** Group id: "gen" | "ind" | "a11y". */
  group: string;
  /** Category id, e.g. "fin", "aml", "kbd". */
  category: string;
  /** Short name of the case. */
  title: string;
  /** The value to paste. May contain invisible characters. */
  value: string;
  /** What this input exercises. */
  tests: string;
  /** What goes wrong when it is mishandled. */
  breaks: string;
  /** Correct behavior. */
  expected: string;
  /** Related WCAG 2.2 success criterion, for accessibility inputs. */
  wcag?: string;
  /** True when the value contains characters that do not render visibly. */
  hasInvisible?: boolean;
  spacesVisible?: boolean;
  /** Present when the value is date-relative; use resolve() to recompute. */
  dynamic?: DynamicRule;
  valueIsSnapshot?: boolean;
}

export interface Category {
  id: string;
  group: string;
  name: string;
  description?: string;
  warning?: string;
}

export interface Group {
  id: string;
  name: string;
  description?: string;
}

export interface Meta {
  name: string;
  version: string;
  description: string;
  homepage: string;
  license: string;
  funding: string;
  updated: string;
  counts: { inputs: number; categories: number; groups: number };
}

export interface Filter {
  group?: string | string[];
  category?: string | string[];
  wcag?: boolean;
  hasInvisible?: boolean;
}

export const INPUTS: FragileInput[];
export const CATEGORIES: Category[];
export const GROUPS: Group[];
export const META: Meta;

export function getInputs(opts?: Filter): FragileInput[];
export function getValues(opts?: Filter & { today?: Date }): string[];
export function getById(id: string): FragileInput | undefined;
export function getCategory(id: string): Category | undefined;
export function getGroup(id: string): Group | undefined;
export function resolve(input: FragileInput, today?: Date): string;

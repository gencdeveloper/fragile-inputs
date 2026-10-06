package io.github.gencdeveloper.fragileinputs;

/** One edge-case input, with what it tests, what it breaks and the expected behavior. */
public final class Input {
    /** Stable id, e.g. "fi-001". */
    public String id;
    /** Group id: "gen", "ind" or "a11y". */
    public String group;
    /** Category id, e.g. "fin", "aml", "kbd". */
    public String category;
    /** Short name of the case. */
    public String title;
    /** The value to paste. May contain invisible characters. */
    public String value;
    /** What this input exercises. */
    public String tests;
    /** What goes wrong when it is mishandled. */
    public String breaks;
    /** Correct behavior. */
    public String expected;
    /** Related WCAG 2.2 success criterion, for accessibility inputs (may be null). */
    public String wcag;
    /** True when the value contains characters that do not render visibly. */
    public boolean hasInvisible;
    public boolean spacesVisible;
    /** Present when the value is date-relative; use FragileInputs.resolve() to recompute. */
    public DynamicRule dynamic;
    public boolean valueIsSnapshot;

    @Override
    public String toString() {
        return "Input{" + id + " [" + category + "] " + title + "}";
    }
}

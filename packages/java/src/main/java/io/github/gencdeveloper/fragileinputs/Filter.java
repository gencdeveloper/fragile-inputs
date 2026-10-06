package io.github.gencdeveloper.fragileinputs;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A fluent filter for {@link FragileInputs#getInputs(Filter)} and
 * {@link FragileInputs#getValues(Filter)}.
 *
 * <pre>{@code
 * FragileInputs.getValues(Filter.of().category("fin", "num"));
 * FragileInputs.getInputs(Filter.of().group("a11y").wcag());
 * }</pre>
 */
public final class Filter {
    Set<String> groups;
    Set<String> categories;
    boolean wcagOnly;
    boolean invisibleOnly;

    public static Filter of() {
        return new Filter();
    }

    public Filter group(String... ids) {
        this.groups = new HashSet<>(Arrays.asList(ids));
        return this;
    }

    public Filter category(String... ids) {
        this.categories = new HashSet<>(Arrays.asList(ids));
        return this;
    }

    /** Keep only inputs that reference a WCAG success criterion. */
    public Filter wcag() {
        this.wcagOnly = true;
        return this;
    }

    /** Keep only inputs whose value contains invisible characters. */
    public Filter hasInvisible() {
        this.invisibleOnly = true;
        return this;
    }

    boolean matches(Input x) {
        if (groups != null && !groups.contains(x.group)) return false;
        if (categories != null && !categories.contains(x.category)) return false;
        if (wcagOnly && (x.wcag == null || x.wcag.isEmpty())) return false;
        if (invisibleOnly && !x.hasInvisible) return false;
        return true;
    }
}

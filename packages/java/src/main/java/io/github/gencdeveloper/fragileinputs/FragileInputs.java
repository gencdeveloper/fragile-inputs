package io.github.gencdeveloper.fragileinputs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Edge-case input strings for QA/SDET testing.
 *
 * <p>A data-only library with no runtime dependencies: a curated dataset plus a
 * small query/resolve API, ideal for JUnit {@code @ParameterizedTest} and
 * TestNG {@code @DataProvider}.
 *
 * <pre>{@code
 * import io.github.gencdeveloper.fragileinputs.FragileInputs;
 *
 * @ParameterizedTest
 * @MethodSource("amounts")
 * void amountFieldSurvives(String value) { ... }
 *
 * static List<String> amounts() { return FragileInputs.getValues("fin"); }
 * }</pre>
 */
public final class FragileInputs {

    public static final List<Input> INPUTS;
    public static final List<Category> CATEGORIES;
    public static final List<Group> GROUPS;

    static {
        Object root = Json.parse(readResource("/fragile-inputs.json"));
        @SuppressWarnings("unchecked")
        Map<String, Object> obj = (Map<String, Object>) root;
        INPUTS = Collections.unmodifiableList(mapInputs(asList(obj.get("inputs"))));
        CATEGORIES = Collections.unmodifiableList(mapCategories(asList(obj.get("categories"))));
        GROUPS = Collections.unmodifiableList(mapGroups(asList(obj.get("groups"))));
    }

    private FragileInputs() {
    }

    // ---- loading ----

    private static String readResource(String path) {
        try (InputStream in = FragileInputs.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("Resource not found on classpath: " + path);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) != -1) out.write(buf, 0, n);
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load fragile-inputs dataset", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static List<Object> asList(Object o) {
        return (List<Object>) o;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> asMap(Object o) {
        return (Map<String, Object>) o;
    }

    private static String str(Map<String, Object> m, String k) {
        Object v = m.get(k);
        return v == null ? null : (String) v;
    }

    private static boolean bool(Map<String, Object> m, String k) {
        Object v = m.get(k);
        return v instanceof Boolean && (Boolean) v;
    }

    private static int intOr0(Map<String, Object> m, String k) {
        Object v = m.get(k);
        return v == null ? 0 : (int) Math.round((Double) v);
    }

    private static List<Input> mapInputs(List<Object> arr) {
        List<Input> out = new ArrayList<>(arr.size());
        for (Object o : arr) {
            Map<String, Object> m = asMap(o);
            Input x = new Input();
            x.id = str(m, "id");
            x.group = str(m, "group");
            x.category = str(m, "category");
            x.title = str(m, "title");
            x.value = str(m, "value");
            x.tests = str(m, "tests");
            x.breaks = str(m, "breaks");
            x.expected = str(m, "expected");
            x.wcag = str(m, "wcag");
            x.hasInvisible = bool(m, "hasInvisible");
            x.spacesVisible = bool(m, "spacesVisible");
            x.valueIsSnapshot = bool(m, "valueIsSnapshot");
            Object dyn = m.get("dynamic");
            if (dyn != null) {
                Map<String, Object> d = asMap(dyn);
                DynamicRule r = new DynamicRule();
                r.kind = str(d, "kind");
                r.years = intOr0(d, "years");
                r.months = intOr0(d, "months");
                r.days = intOr0(d, "days");
                r.monthOffset = intOr0(d, "monthOffset");
                r.format = str(d, "format");
                x.dynamic = r;
            }
            out.add(x);
        }
        return out;
    }

    private static List<Category> mapCategories(List<Object> arr) {
        List<Category> out = new ArrayList<>(arr.size());
        for (Object o : arr) {
            Map<String, Object> m = asMap(o);
            Category c = new Category();
            c.id = str(m, "id");
            c.group = str(m, "group");
            c.name = str(m, "name");
            c.description = str(m, "description");
            c.warning = str(m, "warning");
            out.add(c);
        }
        return out;
    }

    private static List<Group> mapGroups(List<Object> arr) {
        List<Group> out = new ArrayList<>(arr.size());
        for (Object o : arr) {
            Map<String, Object> m = asMap(o);
            Group g = new Group();
            g.id = str(m, "id");
            g.name = str(m, "name");
            g.description = str(m, "description");
            out.add(g);
        }
        return out;
    }

    // ---- queries ----

    /** All inputs. */
    public static List<Input> getInputs() {
        return INPUTS;
    }

    /** Inputs matching a filter. */
    public static List<Input> getInputs(Filter filter) {
        if (filter == null) return INPUTS;
        return INPUTS.stream().filter(filter::matches).collect(Collectors.toList());
    }

    /** Inputs in a single category, type-safe. */
    public static List<Input> getInputs(Cat category) {
        return getInputsByCategory(category.id());
    }

    /** Values in a single category, type-safe, resolved to today. */
    public static List<String> getValues(Cat category) {
        return resolveAll(getInputsByCategory(category.id()), null);
    }

    /** Inputs in a single category, e.g. {@code getInputsByCategory("aml")}. */
    public static List<Input> getInputsByCategory(String category) {
        return getInputs(Filter.of().category(category));
    }

    /** All values, with date-relative ones resolved to today. */
    public static List<String> getValues() {
        return resolveAll(INPUTS, null);
    }

    /** Values for a single category, resolved to today. */
    public static List<String> getValues(String category) {
        return resolveAll(getInputsByCategory(category), null);
    }

    /** Values matching a filter, resolved to today. */
    public static List<String> getValues(Filter filter) {
        return resolveAll(getInputs(filter), null);
    }

    /** Values matching a filter, resolved to a given day. */
    public static List<String> getValues(Filter filter, LocalDate today) {
        return resolveAll(getInputs(filter), today);
    }

    private static List<String> resolveAll(List<Input> inputs, LocalDate today) {
        return inputs.stream().map(x -> resolve(x, today)).collect(Collectors.toList());
    }

    public static Optional<Input> getById(String id) {
        return INPUTS.stream().filter(x -> x.id.equals(id)).findFirst();
    }

    public static Optional<Category> getCategory(String id) {
        return CATEGORIES.stream().filter(c -> c.id.equals(id)).findFirst();
    }

    public static Optional<Group> getGroup(String id) {
        return GROUPS.stream().filter(g -> g.id.equals(id)).findFirst();
    }

    // ---- dynamic values ----

    /** Resolve a value for today. Inputs without a dynamic rule are unchanged. */
    public static String resolve(Input input) {
        return resolve(input, null);
    }

    /** Resolve a value for a given day. Inputs without a dynamic rule are unchanged. */
    public static String resolve(Input input, LocalDate today) {
        DynamicRule dyn = input.dynamic;
        if (dyn == null) return input.value;
        LocalDate base = (today != null) ? today : LocalDate.now();
        if ("card-expiry".equals(dyn.kind)) {
            LocalDate d = base.withDayOfMonth(1).plusMonths(dyn.monthOffset);
            return String.format("%02d/%02d", d.getMonthValue(), d.getYear() % 100);
        }
        // offset-date
        LocalDate d = base.plusYears(dyn.years).plusMonths(dyn.months).plusDays(dyn.days);
        return d.toString(); // ISO-8601 yyyy-MM-dd
    }
}

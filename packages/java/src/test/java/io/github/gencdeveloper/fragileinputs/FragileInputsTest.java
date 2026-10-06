package io.github.gencdeveloper.fragileinputs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FragileInputsTest {

    @Test
    void datasetLoads() {
        assertTrue(FragileInputs.INPUTS.size() >= 480);
        assertEquals(42, FragileInputs.CATEGORIES.size());
        Set<String> groups = new HashSet<>();
        FragileInputs.GROUPS.forEach(g -> groups.add(g.id));
        assertEquals(new HashSet<>(java.util.Arrays.asList("gen", "ind", "a11y")), groups);
    }

    @Test
    void filterByCategory() {
        List<Input> fin = FragileInputs.getInputsByCategory("fin");
        assertFalse(fin.isEmpty());
        assertTrue(fin.stream().allMatch(x -> x.category.equals("fin")));
    }

    @Test
    void wcagFilter() {
        List<Input> a11y = FragileInputs.getInputs(Filter.of().wcag());
        assertFalse(a11y.isEmpty());
        assertTrue(a11y.stream().allMatch(x -> x.wcag != null && !x.wcag.isEmpty()));
    }

    @Test
    void idsAreUnique() {
        long distinct = FragileInputs.INPUTS.stream().map(x -> x.id).distinct().count();
        assertEquals(FragileInputs.INPUTS.size(), distinct);
    }

    @Test
    void dynamicResolvesToGivenDay() {
        Input card = FragileInputs.getById("fi-135").orElseThrow(AssertionError::new);
        assertNotNull(card.dynamic);
        assertEquals("10/26", FragileInputs.resolve(card, LocalDate.of(2026, 10, 6)));
    }

    // Example of the intended usage: data-driven test fed by the dataset.
    @ParameterizedTest(name = "amount field survives: {0}")
    @MethodSource("fintechAmounts")
    void amountFieldIsRobust(String value) {
        // Here you would drive Selenium / RestAssured with `value` and assert
        // the system handles it or rejects it cleanly (never a 500).
        assertNotNull(value);
    }

    static List<String> fintechAmounts() {
        return FragileInputs.getValues("fin");
    }
}

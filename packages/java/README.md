# fragile-inputs (Java)

Edge-case input strings for QA/SDET testing — 486 annotated values that break
forms, search, databases, exports and assistive tech, across 21 industries plus
accessibility. Each value comes with what it tests, what it breaks, and the
correct behavior. Built for Selenium + JUnit 5 / TestNG.

## Install (via JitPack)

No Sonatype account needed — JitPack builds the package straight from GitHub.

**Maven** — add the JitPack repository, then the dependency:

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependency>
  <groupId>com.github.gencdeveloper</groupId>
  <artifactId>fragile-inputs</artifactId>
  <version>v1.0.2</version>
</dependency>
```

**Gradle**

```groovy
repositories { maven { url 'https://jitpack.io' } }
dependencies { testImplementation 'com.github.gencdeveloper:fragile-inputs:v1.0.2' }
```

## Use it (JUnit 5 + Selenium)

```java
import io.github.gencdeveloper.fragileinputs.FragileInputs;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.List;

class CheckoutTest {

    @ParameterizedTest(name = "amount field survives: {0}")
    @MethodSource("fintechAmounts")
    void amountFieldIsRobust(String value) {
        driver.get(baseUrl + "/checkout");
        driver.findElement(By.id("amount")).sendKeys(value);
        driver.findElement(By.id("pay")).click();
        // handled or cleanly rejected — never a 500 or a silent wrong charge
        assertTrue(driver.findElements(By.cssSelector(".server-error")).isEmpty());
    }

    static List<String> fintechAmounts() {
        return FragileInputs.getValues("fin");
    }
}
```

## API

```java
import io.github.gencdeveloper.fragileinputs.FragileInputs;
import io.github.gencdeveloper.fragileinputs.Filter;

FragileInputs.getInputsByCategory("aml");              // by string id
FragileInputs.getValues(Cat.AML);                      // or type-safe enum (autocompletes)
FragileInputs.getInputs(Filter.of().group("a11y").wcag());   // a11y inputs citing a WCAG criterion
FragileInputs.getInputs(Filter.of().hasInvisible());   // values with invisible characters
FragileInputs.getValues("fin");                        // List<String>, ready for @MethodSource
FragileInputs.getValues(Filter.of().category("fin", "num"));
FragileInputs.getById("fi-135");                       // Optional<Input>
FragileInputs.resolve(input);                          // date-relative values computed for today

FragileInputs.INPUTS;  FragileInputs.CATEGORIES;  FragileInputs.GROUPS;   // raw data
```

Each `Input` has: `id, group, category, title, value, tests, breaks, expected,
wcag, hasInvisible, dynamic`.

Groups: `gen` (general), `ind` (industry), `a11y` (accessibility).

Requires Java 8+. Depends only on Jackson for loading the bundled dataset.

## Why

Most "naughty strings" lists are a flat wall of text. This one tells you, for
every value, what it exercises and what correct handling looks like — so a
failing case is a filed bug, not a shrug.

Part of the **Fragile Inputs** project: <https://github.com/gencdeveloper/fragile-inputs>

If it saves you a production incident, you can
[buy me a coffee](https://buymeacoffee.com/yunusemreozudogru). ☕

MIT

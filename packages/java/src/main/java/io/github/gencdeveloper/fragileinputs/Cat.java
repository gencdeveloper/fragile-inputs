package io.github.gencdeveloper.fragileinputs;

/**
 * Type-safe category keys, so you get IDE autocompletion instead of guessing
 * string ids. Use with {@link FragileInputs#getInputs(Cat)} and
 * {@link FragileInputs#getValues(Cat)}.
 *
 * <pre>{@code
 * FragileInputs.getValues(Cat.AML);   // same as getValues("aml")
 * }</pre>
 */
public enum Cat {
    LENGTH("len", "gen", "Length & whitespace"),
    NAMES("name", "gen", "Names"),
    ADDRESS_PHONE("addr", "gen", "Addresses & phone"),
    EMAIL("email", "gen", "Email"),
    NUMBERS("num", "gen", "Numbers & currency"),
    DATES("date", "gen", "Dates & time"),
    CASE_LOCALE("case", "gen", "Case & locale"),
    INVISIBLE("inv", "gen", "Invisible characters"),
    LOOKALIKES("norm", "gen", "Lookalikes & normalization"),
    EMOJI("emo", "gen", "Emoji & graphemes"),
    SCRIPTS("bidi", "gen", "Scripts & direction"),
    RESERVED_WORDS("magic", "gen", "Reserved words"),
    FILE_NAMES("file", "gen", "File names"),
    PASSWORDS("pass", "gen", "Passwords"),
    SECURITY("sec", "gen", "Security"),
    FINTECH("fin", "ind", "Fintech & payments"),
    AML("aml", "ind", "AML & sanctions screening"),
    GAMBLING("gamb", "ind", "Gambling & lottery"),
    HEALTHCARE("health", "ind", "Healthcare"),
    ECOMMERCE("shop", "ind", "E-commerce"),
    TRAVEL("travel", "ind", "Travel & aviation"),
    HR_PAYROLL("hr", "ind", "HR & payroll"),
    AUTOMOTIVE("auto", "ind", "Automotive & mobility"),
    TELECOM("tel", "ind", "Telecom"),
    LOGISTICS("log", "ind", "Logistics & shipping"),
    INSURANCE("ins", "ind", "Insurance"),
    GOVERNMENT("gov", "ind", "Government & public services"),
    UTILITIES("util", "ind", "Energy & utilities"),
    REAL_ESTATE("re", "ind", "Real estate & property"),
    LEGAL("legal", "ind", "Legal & compliance"),
    EDUCATION("edu", "ind", "Education"),
    PHARMA("pharma", "ind", "Pharma & life sciences"),
    MANUFACTURING("mfg", "ind", "Manufacturing"),
    CONSTRUCTION("cons", "ind", "Construction & engineering"),
    MEDIA("media", "ind", "Media & content"),
    CYBERSECURITY("cyber", "ind", "Cybersecurity & networking"),
    SCREEN_READER("sr", "a11y", "Screen reader content"),
    ZOOM_REFLOW("zoom", "a11y", "Zoom, reflow & spacing"),
    KEYBOARD("kbd", "a11y", "Keyboard & focus"),
    FORMS_ERRORS("form", "a11y", "Forms & errors"),
    COLOR_MOTION("vis", "a11y", "Color, motion & display"),
    IMAGES_LANGUAGE("img", "a11y", "Images & language");

    private final String id;
    private final String group;
    private final String displayName;

    Cat(String id, String group, String displayName) {
        this.id = id;
        this.group = group;
        this.displayName = displayName;
    }

    /** The string id used in the dataset, e.g. "aml". */
    public String id() { return id; }

    /** The group this category belongs to: "gen", "ind" or "a11y". */
    public String group() { return group; }

    /** Human-readable name, e.g. "AML & sanctions screening". */
    public String displayName() { return displayName; }

    /** Look up by dataset id, or null if unknown. */
    public static Cat fromId(String id) {
        for (Cat c : values()) {
            if (c.id.equals(id)) return c;
        }
        return null;
    }
}

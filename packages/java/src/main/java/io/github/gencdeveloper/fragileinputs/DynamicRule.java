package io.github.gencdeveloper.fragileinputs;

/** Rule for recomputing a date-relative value for the current day. */
public final class DynamicRule {
    public String kind;
    public int years;
    public int months;
    public int days;
    public int monthOffset;
    public String format;
}

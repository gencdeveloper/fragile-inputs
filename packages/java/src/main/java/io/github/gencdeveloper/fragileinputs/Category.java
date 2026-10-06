package io.github.gencdeveloper.fragileinputs;

/** A category definition. */
public final class Category {
    public String id;
    public String group;
    public String name;
    public String description;
    /** Optional safety note (e.g. for the Security and AML categories). */
    public String warning;

    @Override
    public String toString() { return "Category{" + id + " " + name + "}"; }
}

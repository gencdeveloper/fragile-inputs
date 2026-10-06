package io.github.gencdeveloper.fragileinputs;

/** A top-level group: general, industry or accessibility. */
public final class Group {
    public String id;
    public String name;
    public String description;

    @Override
    public String toString() { return "Group{" + id + " " + name + "}"; }
}

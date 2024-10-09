package com.sunofdawn.semvertools.model;

import java.util.Arrays;
import java.util.List;

public class Constant {

    public static final String STABLE_STRING = "stable";

    public static final List<String> UNSTABLE_STRINGS = Arrays.asList("dev", "alpha", "beta", "rc");

    public static final List<String> BLACK_REQUIRED_CHARS = Arrays.asList("-");

    public static final List<String> UN_PARSEABLE_CHARS = Arrays.asList("=", "<", ">", "~", "^", "[", "]", "(", ")", "|", ",", "_", "/", "@");
}

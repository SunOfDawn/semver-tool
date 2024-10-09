package com.sunofdawn.semvertools.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SemverType {
    NORMAL(""),
    CARGO("cargo"),
    COMPOSER("composer"),
    MAVEN("maven"),
    NPM("npm"),
    PIP("pip");

    private final String name;
}

package com.sunofdawn.semvertools;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class Semver implements Comparable<Semver> {
    private String originVersion;
    private long major;
    private long minor;
    private long patch;
    private String[] preRelease;
    private String build;
    private boolean stable;
    private boolean parseAble;

    public Semver(String originVersion) {
        this(originVersion, 0, 0, 0);
    }

    public Semver(String originVersion, long major, long minor, long patch) {
        this.originVersion = originVersion;
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.stable = true;
        this.parseAble = true;
    }

    public Semver nextMajor(int inc) {
        return new Semver(originVersion, major + inc, 0, 0);
    }

    public Semver nextMinor(int inc) {
        return new Semver(originVersion, major, minor + inc, 0);
    }

    public Semver nextPatch(int inc) {
        return new Semver(originVersion, major, minor, patch + inc);
    }

    public Semver currentMajor() {
        return new Semver(originVersion, major, 0, 0);
    }

    public Semver currentMinor() {
        return new Semver(originVersion, major, minor, 0);
    }

    public Semver currentPatch() {
        return new Semver(originVersion, major, minor, patch);
    }

    public boolean isValid() {
        return this.parseAble && major >= 0 && minor >= 0 && patch >= 0;
    }

    public String getVersion() {
        return String.format("%s.%s.%s", major, minor, patch);
    }

    public String getFullVersion() {
        StringBuilder sb = new StringBuilder();
        sb.append(major).append(".").append(minor).append(".").append(patch);

        if (preRelease != null) {
            boolean first = true;
            for (String s : preRelease) {
                if (first) {
                    sb.append('-');
                    first = false;
                } else {
                    sb.append('.');
                }
                sb.append(s);
            }
        }

        if (build != null && !build.isEmpty()) {
            sb.append("+").append(build);
        }

        return sb.toString();
    }

    public boolean isGreaterThan(Semver other) {
        // 仅比较major、minor、patch
        if (this.major != other.major) {
            return this.major > other.major;
        }
        if (this.minor != other.minor) {
            return this.minor > other.minor;
        }
        if (this.patch != other.patch) {
            return this.patch > other.patch;
        }
        return false;
    }

    public boolean isLetterThan(Semver other) {
        // 仅比较major、minor、patch
        if (this.major != other.major) {
            return this.major < other.major;
        }
        if (this.minor != other.minor) {
            return this.minor < other.minor;
        }
        if (this.patch != other.patch) {
            return this.patch < other.patch;
        }
        return false;
    }

    public boolean isEquipTo(Semver other) {
        return this.major == other.major && this.minor == other.minor && this.patch == other.patch;
    }

    @Override
    public int compareTo(@NonNull Semver other) {
        if (isGreaterThan(other)) {
            return 1;
        } else {
            return isLetterThan(other) ? -1 : 0;
        }
    }
}

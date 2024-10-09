package com.sunofdawn.semvertools;

import com.sunofdawn.semvertools.model.Constant;
import com.sunofdawn.semvertools.util.StringUtils;

public class SemverParser {

    public static Semver parse(String originVersion) {
        String prettyVersion = originVersion.trim().toLowerCase().replaceFirst("v", "");

        Semver semver = new Semver(originVersion);
        if (StringUtils.isBlank(prettyVersion)) {
            return semver;
        }

        char firstChar = prettyVersion.charAt(0);
        if (!StringUtils.isDigit(firstChar) && !StringUtils.isAlphaChar(firstChar)) {
            semver.setParseAble(false);
            return semver;
        }

        String[] parts;
        String[] tmp;
        if (hasPreRelease(prettyVersion)) {
            parts = prettyVersion.split("-", 2);
            if (parts.length == 1) {
                // 1.2.6alpha+build1
                if (parts[0].endsWith("+")) {
                    semver.setBuild("");
                    parseBaseVersion(semver, parts[0].substring(0, parts[0].length() - 1));
                } else {
                    tmp = parts[0].split("\\+");
                    parseBaseVersion(semver, tmp[0]);
                    if (tmp.length == 2) {
                        semver.setBuild(tmp[1]);
                    }
                }
            } else {
                // 1.2.6-alpha+build1
                parseBaseVersion(semver, parts[0]);
                tmp = parts[1].split("\\+", 2);
                semver.setPreRelease(tmp[0].split("\\."));
                if (tmp.length == 2) {
                    semver.setBuild(tmp[1]);
                }
            }
        } else {
            parseBaseVersion(semver, prettyVersion);
        }
        parseStability(semver);
        return semver;
    }

    private static void parseBaseVersion(Semver semver, String prettyVersion) {
        if (Constant.UN_PARSEABLE_CHARS.stream().anyMatch(prettyVersion::contains)) {
            semver.setParseAble(false);
            return;
        }

        // example: 48f5a47ff12e19265d437d8f74022e53431b97f5
        if (isGitCommit(prettyVersion)) {
            semver.setParseAble(false);
            return;
        }

        String[] parts = prettyVersion.split("\\.");
        switch (parts.length) {
            case 1:
                semver.setMajor(parseDigit(parts[0]));
                return;
            case 2:
                semver.setMajor(parseDigit(parts[0]));
                semver.setMinor(parseDigit(parts[1]));
                return;
            case 3:
            default:
                semver.setMajor(parseDigit(parts[0]));
                semver.setMinor(parseDigit(parts[1]));
                semver.setPatch(parseDigit(parts[2]));
        }
    }

    private static long parseDigit(String value) {
        if (StringUtils.isBlank(value)) {
            return 0;
        }

        char ch;
        long result = 0;
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length && i < 17; i++) {
            ch = chars[i];
            if (StringUtils.isDigit(ch)) {
                result *= 10;
                result += Character.getNumericValue(ch);
                continue;
            } else if (StringUtils.isAlphaChar(ch)) {
                break;
            }
            return -1;
        }
        return result;
    }

    private static boolean hasPreRelease(String version) {
        int firstIndexOfPlus = version.indexOf("+");
        int firstIndexOfHyphen = version.indexOf("-");
        if (firstIndexOfHyphen == -1) {
            return false;
        } else {
            return firstIndexOfPlus == -1 || firstIndexOfHyphen < firstIndexOfPlus;
        }
    }

    private static boolean isGitCommit(String version) {
        if (version.length() != 40) {
            return false;
        }

        boolean isHasDigit = false;
        boolean isHasAlpha = false;
        char[] chars = version.toCharArray();
        for (char ch : chars) {
            if (StringUtils.isDigit(ch)) {
                isHasDigit = true;
            } else if (StringUtils.isAlphaChar(ch)) {
                isHasAlpha = true;
            } else {
                return false;
            }
        }
        return isHasDigit && isHasAlpha;
    }

    private static void parseStability(Semver semver) {
        String originVersion = semver.getOriginVersion();
        semver.setStable(originVersion.contains(Constant.STABLE_STRING) || Constant.UNSTABLE_STRINGS.stream().noneMatch(originVersion::contains));
    }
}

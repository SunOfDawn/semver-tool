package com.sunofdawn;

import com.sunofdawn.semvertools.Requirement;
import com.sunofdawn.semvertools.RequirementParser;
import com.sunofdawn.semvertools.Semver;
import com.sunofdawn.semvertools.SemverParser;
import com.sunofdawn.semvertools.model.SemverType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        System.out.println("Never mind!");

        // 解析版本号
        Semver semver = SemverParser.parse("12.34.46");
        if (semver.isValid()) {
            System.out.println(semver.getVersion());
            System.out.println(semver.isGreaterThan(SemverParser.parse("12.0.0")));
        }

        // 分析表达式
        RequirementParser parser = RequirementParser.newInstance();
        Requirement requirement = parser.parse(SemverType.NPM, "~= 3.14.2");

        // 1、候选版本semver化
        List<Semver> versions = Arrays.asList(
                SemverParser.parse("3.14.5"),
                SemverParser.parse("3.10.3"),
                SemverParser.parse("3.14.1-release"),
                SemverParser.parse("3.15-dev"),
                SemverParser.parse("3.14.2"),
                SemverParser.parse("3.14")
                );
        // 使用前记得过滤有效值
        versions = versions.stream().filter(Semver::isValid).collect(Collectors.toList());

        // 2、调用版本选择器，期间候选版本列表会自动根据大小排序
        Semver result = requirement.choiceVersion(versions, true);
        if (result != null) {
            System.out.println(result.getVersion());
        } else {
            // 若无法解析到一个可用的版本号，给排序后的最大版本号
            System.out.println(versions.get(versions.size() - 1));
        }
    }
}
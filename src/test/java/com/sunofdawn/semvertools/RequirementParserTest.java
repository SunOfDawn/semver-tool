package com.sunofdawn.semvertools;

import com.sunofdawn.semvertools.model.SemverType;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class RequirementParserTest {

    @Test
    public void test123() {
        List<Semver> semverList = Arrays.asList(
//                SemverParser.parse("2.8.6"),
                SemverParser.parse("2.8.7")
//                SemverParser.parse("2.8.8"),
//                SemverParser.parse("2.8.9")
        );

        RequirementParser parser = RequirementParser.newInstance();
        Requirement requirement;

        requirement = parser.parse(SemverType.COMPOSER, "2.8.8");
        Semver semver = requirement.choiceVersion(semverList, true);
        System.out.println("1");
    }

    @Test
    public void testParseNpm() {
        List<Semver> semverList = Arrays.asList(
                SemverParser.parse("0.0.4"),
                SemverParser.parse("0.0.5"),
                SemverParser.parse("0.0.5-release+build1"),
                SemverParser.parse("0.0.5-release+build2"),
                SemverParser.parse("0.0.6"),
                SemverParser.parse("0.0.8"),
                SemverParser.parse("0.0.9-alpha"),
                SemverParser.parse("0.4.1"),
                SemverParser.parse("0.5.1"),
                SemverParser.parse("0.5.4"),
                SemverParser.parse("0.5.7-rc1"),
                SemverParser.parse("0.6.1"),
                SemverParser.parse("0.6.4"),
                SemverParser.parse("0.7.1-beta"),
                SemverParser.parse("1.3.4"),
                SemverParser.parse("2.3.4"),
                SemverParser.parse("3.3.4"),
                SemverParser.parse("4"),
                SemverParser.parse("4.4"),
                SemverParser.parse("4.3.2"),
                SemverParser.parse("4.3.3"),
                SemverParser.parse("4.3.12"),
                SemverParser.parse("4.3.8"),
                SemverParser.parse("4.3.21-dev"),
                SemverParser.parse("4.5.4"),
                SemverParser.parse("4.13.1"),
                SemverParser.parse("4.13.7"),
                SemverParser.parse("5.5.2"),
                SemverParser.parse("14.5.2"),
                SemverParser.parse("17.3.4")
        );

        RequirementParser parser = RequirementParser.newInstance();
        Requirement requirement;

        requirement = parser.parse(SemverType.NPM, ">=4.3.2");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "17.3.4");
        requirement = parser.parse(SemverType.NPM, ">=4.3.2,<4.3.3");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.3.2");
        requirement = parser.parse(SemverType.NPM, ">=4.3.2,<4.4");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.3.12");
        requirement = parser.parse(SemverType.NPM, ">= 4.3.2 , < 4.4");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.3.12");
        requirement = parser.parse(SemverType.NPM, ">= 4.3.2||< 2.4");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "17.3.4");
        requirement = parser.parse(SemverType.NPM, ">= 4.3.2 |< 2.4");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "17.3.4");
        requirement = parser.parse(SemverType.NPM, "4.3.31 | < 0.0.1 | > 4 , < 4.5");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.4.0");
        requirement = parser.parse(SemverType.NPM, ">4 , <3");
        Assert.assertNull(requirement.choiceVersion(semverList, true));
        requirement = parser.parse(SemverType.NPM, ">4.3.2,<4.3.3");
        Assert.assertNull(requirement.choiceVersion(semverList, true));

        requirement = parser.parse(SemverType.NPM, "~=4.3.2");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.3.12");
        requirement = parser.parse(SemverType.NPM, "~= 4.3.2");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.3.12");
        requirement = parser.parse(SemverType.NPM, "~=4.3");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.13.7");
        requirement = parser.parse(SemverType.NPM, " ~4.3 ");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.13.7");
        requirement = parser.parse(SemverType.NPM, "~=4");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "17.3.4");
        requirement = parser.parse(SemverType.NPM, "~ 4");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "17.3.4");

        requirement = parser.parse(SemverType.NPM, "^=0");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "0.6.4");
        requirement = parser.parse(SemverType.NPM, "^=0.0");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "0.0.8");
        requirement = parser.parse(SemverType.NPM, "^=0.5");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "0.5.4");
        requirement = parser.parse(SemverType.NPM, "^=0.0.5");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getFullVersion(), "0.0.5-release+build2");
    }

    @Test
    public void testParseComposer() {
        List<Semver> semverList = Arrays.asList(
                SemverParser.parse("0.0.4"),
                SemverParser.parse("0.0.5"),
                SemverParser.parse("0.0.5-release+build1"),
                SemverParser.parse("0.0.5-release+build2"),
                SemverParser.parse("0.0.6"),
                SemverParser.parse("0.0.8"),
                SemverParser.parse("0.0.9-alpha"),
                SemverParser.parse("0.4.1"),
                SemverParser.parse("0.5.1"),
                SemverParser.parse("0.5.4"),
                SemverParser.parse("0.5.7-rc1"),
                SemverParser.parse("0.6.1"),
                SemverParser.parse("0.6.4"),
                SemverParser.parse("0.7.1-beta"),
                SemverParser.parse("1.3.4"),
                SemverParser.parse("2.3.4"),
                SemverParser.parse("3.3.4"),
                SemverParser.parse("4"),
                SemverParser.parse("4.4"),
                SemverParser.parse("4.3.2"),
                SemverParser.parse("4.3.3"),
                SemverParser.parse("4.3.12"),
                SemverParser.parse("4.3.8"),
                SemverParser.parse("4.3.21-dev"),
                SemverParser.parse("4.5.4"),
                SemverParser.parse("4.13.1"),
                SemverParser.parse("4.13.7"),
                SemverParser.parse("5.5.2"),
                SemverParser.parse("14.5.2"),
                SemverParser.parse("17.3.4")
        );

        RequirementParser parser = RequirementParser.newInstance();
        Requirement requirement;

        requirement = parser.parse(SemverType.COMPOSER, ">=4.3.2");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "17.3.4");
        requirement = parser.parse(SemverType.COMPOSER, ">=4.3.2,<4.3.3");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.3.2");
        requirement = parser.parse(SemverType.COMPOSER, ">=4.3.2,<4.4");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.3.12");
        requirement = parser.parse(SemverType.COMPOSER, ">= 4.3.2 , < 4.4");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.3.12");
        requirement = parser.parse(SemverType.COMPOSER, ">= 4.3.2||< 2.4");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "17.3.4");
        requirement = parser.parse(SemverType.COMPOSER, ">= 4.3.2 |< 2.4");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "17.3.4");
        requirement = parser.parse(SemverType.COMPOSER, "4.3.31 | < 0.0.1 | > 4 , < 4.5");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.4.0");
        requirement = parser.parse(SemverType.COMPOSER, ">4 , <3");
        Assert.assertNull(requirement.choiceVersion(semverList, true));
        requirement = parser.parse(SemverType.COMPOSER, ">4.3.2,<4.3.3");
        Assert.assertNull(requirement.choiceVersion(semverList, true));

        requirement = parser.parse(SemverType.COMPOSER, "~=4.3.2");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.3.12");
        requirement = parser.parse(SemverType.COMPOSER, "~= 4.3.2");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.3.12");
        requirement = parser.parse(SemverType.COMPOSER, "~=4.3");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.13.7");
        requirement = parser.parse(SemverType.COMPOSER, " ~4.3 ");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.13.7");
        requirement = parser.parse(SemverType.COMPOSER, "~=4");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "17.3.4");
        requirement = parser.parse(SemverType.COMPOSER, "~ 4");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "17.3.4");

        requirement = parser.parse(SemverType.COMPOSER, "^=0");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "0.0.8");
        requirement = parser.parse(SemverType.COMPOSER, "^=0.0");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "0.0.8");
        requirement = parser.parse(SemverType.COMPOSER, "^=0.5");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "0.5.4");
        requirement = parser.parse(SemverType.COMPOSER, "^=0.0.5");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getFullVersion(), "0.0.8");
    }

    @Test
    public void testParsePip() {
        List<Semver> semverList = Arrays.asList(
                SemverParser.parse("0.0.4"),
                SemverParser.parse("0.0.5"),
                SemverParser.parse("0.0.5-release+build1"),
                SemverParser.parse("0.0.5-release+build2"),
                SemverParser.parse("0.0.6"),
                SemverParser.parse("0.0.8"),
                SemverParser.parse("0.0.9-alpha"),
                SemverParser.parse("0.4.1"),
                SemverParser.parse("0.5.1"),
                SemverParser.parse("0.5.4"),
                SemverParser.parse("0.5.7-rc1"),
                SemverParser.parse("0.6.1"),
                SemverParser.parse("0.6.4"),
                SemverParser.parse("0.7.1-beta"),
                SemverParser.parse("1.3.4"),
                SemverParser.parse("2.3.4"),
                SemverParser.parse("3.3.4"),
                SemverParser.parse("4"),
                SemverParser.parse("4.4"),
                SemverParser.parse("4.3.2"),
                SemverParser.parse("4.3.3"),
                SemverParser.parse("4.3.12"),
                SemverParser.parse("4.3.8"),
                SemverParser.parse("4.3.21-dev"),
                SemverParser.parse("4.5.4"),
                SemverParser.parse("4.13.1"),
                SemverParser.parse("4.13.7"),
                SemverParser.parse("5.5.2"),
                SemverParser.parse("14.5.2"),
                SemverParser.parse("17.3.4")
        );

        RequirementParser parser = RequirementParser.newInstance();
        Requirement requirement;

        requirement = parser.parse(SemverType.PIP, ">=4.3.2");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "17.3.4");
        requirement = parser.parse(SemverType.PIP, ">=4.3.2,<4.3.3");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.3.2");
        requirement = parser.parse(SemverType.PIP, ">=4.3.2,<4.4");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.3.12");
        requirement = parser.parse(SemverType.PIP, ">= 4.3.2 , < 4.4");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.3.12");
        requirement = parser.parse(SemverType.PIP, ">= 4.3.2||< 2.4");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "17.3.4");
        requirement = parser.parse(SemverType.PIP, ">= 4.3.2 |< 2.4");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "17.3.4");
        requirement = parser.parse(SemverType.PIP, "4.3.31 | < 0.0.1 | > 4 , < 4.5");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.4.0");
        requirement = parser.parse(SemverType.PIP, ">4 , <3");
        Assert.assertNull(requirement.choiceVersion(semverList, true));
        requirement = parser.parse(SemverType.PIP, ">4.3.2,<4.3.3");
        Assert.assertNull(requirement.choiceVersion(semverList, true));

        requirement = parser.parse(SemverType.PIP, "~=4.3.2");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.3.12");
        requirement = parser.parse(SemverType.PIP, "~= 4.3.2");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.3.12");
        requirement = parser.parse(SemverType.PIP, "~=4.3");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.13.7");
        requirement = parser.parse(SemverType.PIP, " ~4.3 ");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.13.7");
        requirement = parser.parse(SemverType.PIP, "~=4");
        Assert.assertNull(requirement.choiceVersion(semverList, true));
        requirement = parser.parse(SemverType.PIP, "~ 4");
        Assert.assertNull(requirement.choiceVersion(semverList, true));

        requirement = parser.parse(SemverType.PIP, "^=0");
        Assert.assertNull(requirement.choiceVersion(semverList, true));
    }

    @Test
    public void testParseCargo() {
        List<Semver> semverList = Arrays.asList(
                SemverParser.parse("0.0.4"),
                SemverParser.parse("0.0.5"),
                SemverParser.parse("0.0.5-release+build1"),
                SemverParser.parse("0.0.5-release+build2"),
                SemverParser.parse("0.0.6"),
                SemverParser.parse("0.0.8"),
                SemverParser.parse("0.0.9-alpha"),
                SemverParser.parse("0.4.1"),
                SemverParser.parse("0.5.1"),
                SemverParser.parse("0.5.4"),
                SemverParser.parse("0.5.7-rc1"),
                SemverParser.parse("0.6.1"),
                SemverParser.parse("0.6.4"),
                SemverParser.parse("0.7.1-beta"),
                SemverParser.parse("1.3.4"),
                SemverParser.parse("2.3.4"),
                SemverParser.parse("3.3.4"),
                SemverParser.parse("4"),
                SemverParser.parse("4.4"),
                SemverParser.parse("4.3.2"),
                SemverParser.parse("4.3.3"),
                SemverParser.parse("4.3.12"),
                SemverParser.parse("4.3.8"),
                SemverParser.parse("4.3.21-dev"),
                SemverParser.parse("4.5.4"),
                SemverParser.parse("4.13.1"),
                SemverParser.parse("4.13.7"),
                SemverParser.parse("5.5.2"),
                SemverParser.parse("14.5.2"),
                SemverParser.parse("17.3.4")
        );

        RequirementParser parser = RequirementParser.newInstance();
        Requirement requirement;

        requirement = parser.parse(SemverType.CARGO, ">=4.3.2");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "17.3.4");
        requirement = parser.parse(SemverType.CARGO, ">=4.3.2,<4.3.3");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.3.2");
        requirement = parser.parse(SemverType.CARGO, ">=4.3.2,<4.4");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.3.12");
        requirement = parser.parse(SemverType.CARGO, ">= 4.3.2 , < 4.4");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.3.12");
        requirement = parser.parse(SemverType.CARGO, ">= 4.3.2||< 2.4");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "17.3.4");
        requirement = parser.parse(SemverType.CARGO, ">= 4.3.2 |< 2.4");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "17.3.4");
        requirement = parser.parse(SemverType.CARGO, "4.3.31 | < 0.0.1 | > 4 , < 4.5");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.13.7");
        requirement = parser.parse(SemverType.CARGO, ">4 , <3");
        Assert.assertNull(requirement.choiceVersion(semverList, true));
        requirement = parser.parse(SemverType.CARGO, ">4.3.2,<4.3.3");
        Assert.assertNull(requirement.choiceVersion(semverList, true));

        requirement = parser.parse(SemverType.CARGO, "~=4.3.2");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.3.12");
        requirement = parser.parse(SemverType.CARGO, "~= 4.3.2");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.3.12");
        requirement = parser.parse(SemverType.CARGO, "~=4.3");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.3.12");
        requirement = parser.parse(SemverType.CARGO, " ~4.3 ");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.3.12");
        requirement = parser.parse(SemverType.CARGO, "~=4");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.13.7");
        requirement = parser.parse(SemverType.CARGO, "~ 4");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "4.13.7");

        requirement = parser.parse(SemverType.CARGO, "^=0");
        Assert.assertNull(requirement.choiceVersion(semverList, true));
        requirement = parser.parse(SemverType.CARGO, "^=0.0");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "0.6.4");
        requirement = parser.parse(SemverType.CARGO, "^=0.5");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getVersion(), "0.6.4");
        requirement = parser.parse(SemverType.CARGO, "^=0.0.5");
        Assert.assertEquals(requirement.choiceVersion(semverList, true).getFullVersion(), "0.0.8");
    }
}

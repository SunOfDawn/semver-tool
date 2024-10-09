package com.sunofdawn.semvertools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

public class SemverParserTest {

    @Test
    public void testParseSuccess() {
        Semver semver;
        semver = SemverParser.parse("12.46.32");
        Assert.assertTrue(semver.isValid());
        Assert.assertEquals(semver.getMajor(), 12);
        Assert.assertEquals(semver.getMinor(), 46);
        Assert.assertEquals(semver.getPatch(), 32);
        Assert.assertEquals(semver.getVersion(), "12.46.32");

        semver = SemverParser.parse("12a.46j.32L");
        Assert.assertTrue(semver.isValid());
        Assert.assertEquals(semver.getMajor(), 12);
        Assert.assertEquals(semver.getMinor(), 46);
        Assert.assertEquals(semver.getPatch(), 32);
        Assert.assertEquals(semver.getVersion(), "12.46.32");

        semver = SemverParser.parse("12a.46j.32L-beta1+build2");
        Assert.assertTrue(semver.isValid());
        Assert.assertEquals(semver.getMajor(), 12);
        Assert.assertEquals(semver.getMinor(), 46);
        Assert.assertEquals(semver.getPatch(), 32);
        Assert.assertEquals(semver.getPreRelease(), new String[]{"beta1"});
        Assert.assertEquals(semver.getBuild(), "build2");
        Assert.assertEquals(semver.getVersion(), "12.46.32");
        Assert.assertEquals(semver.getFullVersion(), "12.46.32-beta1+build2");

        semver = SemverParser.parse("555555555555555555555555555.4444444444444444444444444444444444444444.333333333333333333333333333333333333333");
        Assert.assertTrue(semver.isValid());
        Assert.assertEquals(semver.getMajor(), 55555555555555555L);
        Assert.assertEquals(semver.getMinor(), 44444444444444444L);
        Assert.assertEquals(semver.getPatch(), 33333333333333333L);

        semver = SemverParser.parse("2021.02.24");
        Assert.assertTrue(semver.isValid());
        Assert.assertEquals(semver.getMajor(), 2021);
        Assert.assertEquals(semver.getMinor(), 2);
        Assert.assertEquals(semver.getPatch(), 24);
        Assert.assertEquals(semver.getVersion(), "2021.2.24");

        semver = SemverParser.parse("v34.24.1");
        Assert.assertTrue(semver.isValid());
        Assert.assertEquals(semver.getMajor(), 34);
        Assert.assertEquals(semver.getMinor(), 24);
        Assert.assertEquals(semver.getPatch(), 1);
        Assert.assertEquals(semver.getVersion(), "34.24.1");
    }

    @Test
    public void testParseFailed() {
        Assert.assertFalse(SemverParser.parse("12. 1").isValid());
        Assert.assertFalse(SemverParser.parse("12 .1").isValid());
        Assert.assertFalse(SemverParser.parse("1 2.1").isValid());
        Assert.assertFalse(SemverParser.parse("-12dev").isValid());
        Assert.assertFalse(SemverParser.parse("~=12").isValid());
        Assert.assertFalse(SemverParser.parse("~= 12").isValid());
        Assert.assertFalse(SemverParser.parse("1.*").isValid());
        Assert.assertFalse(SemverParser.parse("*").isValid());
        Assert.assertFalse(SemverParser.parse("2321.233>2").isValid());
        Assert.assertFalse(SemverParser.parse("12*.034.0046").isValid());
        Assert.assertFalse(SemverParser.parse("48f5a47ff12e19265d437d8f74022e53431b97f5").isValid());
    }

    @Test
    public void testParseFuzz() {
        List<String> fuzzStrings = FileUtil.readLines(new File(new ClassPathResource("version.txt").getAbsolutePath()), Charset.defaultCharset());
        long startTime = System.currentTimeMillis();
        for (int i = 0; i <= 100; i++) {
            for (String s : fuzzStrings) {
                SemverParser.parse(s);
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("parse 100 * 5000 time, running time:" + (endTime - startTime) + " ms");
    }
}

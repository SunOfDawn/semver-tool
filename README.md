# semver-tool
A tool for parsing version that conform to the semver specification

### simple usage

parse version string by semver format
```java
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
```

try lock a version
```java
List<Semver> semverList = Arrays.asList(
      SemverParser.parse("0.0.4"),
      SemverParser.parse("0.0.5"),
      SemverParser.parse("0.0.6"),
      SemverParser.parse("0.0.8")
);

RequirementParser parser = RequirementParser.newInstance();
Requirement requirement = parser.parse(SemverType.NPM, ">0.0.5");
String version = requirement.choiceVersion(semverList, true).getVersion();
System.out.println(version);
// 0.0.6
```
For version range expressions, the specific interpretation varies depending on the language. Please refer to the corresponding official documentation for details. 


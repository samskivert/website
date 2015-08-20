Build systems are the devil.

Compilers should not take command line switches, they should take a config file (or include the
configuration in a source file, so you can have type checking and use language
meta-programming/compile-time code execution for fancy stuff).

Otherwise you end up in situations like this, where the GWT compiler takes a zillion arguments:

```
Google Web Toolkit 2.8.0-SNAPSHOT
Compiler [-logLevel (ERROR|WARN|INFO|TRACE|DEBUG|SPAM|ALL)] [-workDir dir] [-X[no]closureFormattedOutput] [-[no]compileReport] [-X[
no]checkCasts] [-X[no]classMetadata] [-[no]draftCompile] [-[no]checkAssertions] [-X[no]closureCompiler] [-XfragmentCount numFragments] [-Xf
ragmentMerge numFragments] [-gen dir] [-XjsInteropMode (NONE|JS)] [-XmethodNameDisplayMode (NONE|ONLY_METHOD_NAME|ABBREVIATED|FULL)] [-Xnam
espace (NONE|PACKAGE)] [-optimize level] [-[no]saveSource] [-setProperty name=value,value...] [-style (DETAILED|OBFUSCATED|PRETTY)] [-[no]f
ailOnError] [-[no]validateOnly] [-sourceLevel [auto, 1.7, 1.8]] [-localWorkers count] [-[no]incremental] [-war dir] [-deploy dir] [-extra d
ir] [-saveSourceOutput dir] module[s]

where
  -logLevel                     The level of logging detail: ERROR, WARN, INFO, TRACE, DEBUG, SPAM or ALL (defaults to INFO)
  -workDir                      The compiler's working directory for internal use (must be writeable; defaults to a system temp dir
)
  -X[no]closureFormattedOutput  EXPERIMENTAL: Enables Javascript output suitable for post-compilation by Closure Compiler (defaults
 to OFF)
   -[no]compileReport            Compile a report that tells the "Story of Your Compile". (defaults to OFF)
   -X[no]checkCasts              EXPERIMENTAL: Insert run-time checking of cast operations. (defaults to ON)
   -X[no]classMetadata           EXPERIMENTAL: Include metadata for some java.lang.Class methods (e.g. getName()). (defaults to ON)
   -[no]draftCompile             Compile quickly with minimal optimizations. (defaults to ON)
   -[no]checkAssertions          Include assert statements in compiled output. (defaults to OFF)
   -X[no]closureCompiler         EXPERIMENTAL: Compile output Javascript with the Closure compiler for even further optimizations. (
 defaults to ON)
   -XfragmentCount               EXPERIMENTAL: Limits of number of fragments using a code splitter that merges split points.
   -XfragmentMerge               DEPRECATED (use -XfragmentCount instead): Enables Fragment merging code splitter.
   -gen                          Debugging: causes normally-transient generated types to be saved in the specified directory
   -XjsInteropMode               EXPERIMENTAL: Specifies JsInterop mode: NONE or JS (defaults to NONE)
   -XmethodNameDisplayMode       EXPERIMENTAL: Specifies method display name mode for chrome devtools: NONE, ONLY_METHOD_NAME, ABBREVIATED or FULL (defaults to NONE)
   -Xnamespace                   Puts most JavaScript globals into namespaces. Default: PACKAGE for -draftCompile, otherwise NONE
   -optimize                     Sets the optimization level used by the compiler.  0=none 9=maximum.
   -[no]saveSource               Enables saving source code needed by debuggers. Also see -debugDir. (defaults to OFF)
   -setProperty                  Set the values of a property in the form of propertyName=value1[,value2...].
   -style                        Script output style: DETAILED, OBFUSCATED or PRETTY (defaults to OBFUSCATED)
   -[no]failOnError              Fail compilation if any input file contains an error. (defaults to ON)
   -[no]validateOnly             Validate all source code, but do not compile. (defaults to OFF)
   -sourceLevel                  Specifies Java source level (defaults to 1.7)
   -localWorkers                 The number of local workers to use when compiling permutations
   -[no]incremental              Compiles faster by reusing data from the previous compile. (defaults to OFF)
   -war                          The directory into which deployable output files will be written (defaults to 'war')
   -deploy                       The directory into which deployable but not servable output files will be written (defaults to 'WEB-INF/deploy' under the -war directory/jar, and may be the same as the -extra directory/jar)
   -extra                        The directory into which extra files, not intended for deployment, will be written
   -saveSourceOutput             Overrides where source files useful to debuggers will be written. Default: saved with extras.
 and
   module[s]                     Specifies the name(s) of the module(s) to compile
```

and gwt-maven-plugin (and every other goddamned Java build system which embeds GWT support)
dutifully replicates those arguments in its own configuration system:

```xml
<configuration>
  <checkAssertions default-value="false"/>
  <closureCompiler default-value="false">true</closureCompiler>
  <clusterFunctions default-value="true">false</clusterFunctions>
  <compileReport default-value="false">${gwt.compiler.compileReport}</compileReport>
  <compilerMetrics default-value="false">true</compilerMetrics>
  <deploy>/Users/mdb/ops/gwt/maven-plugin/target/it-tests/compile/target/deploy</deploy>
  <detailedSoyc default-value="false">${gwt.compiler.soycDetailed}</detailedSoyc>
  <disableCastChecking default-value="false">${gwt.disableCastChecking}</disableCastChecking>
  <disableClassMetadata default-value="false">${gwt.disableClassMetadata}</disableClassMetadata>
  <disableRunAsync default-value="false">${gwt.disableRunAsync}</disableRunAsync>
  <draftCompile default-value="false">true</draftCompile>
  <enableJsonSoyc default-value="false"/>
  <enforceStrictResources default-value="false">true</enforceStrictResources>
  <extra default-value="${project.build.directory}/extra"/>
  <extraJvmArgs default-value="-Xmx512m">${gwt.extraJvmArgs}</extraJvmArgs>
  <extraParam default-value="false">true</extraParam>
  <failOnError default-value="false">true</failOnError>
  <force default-value="false">${gwt.compiler.force}</force>
  <fragmentCount default-value="-1">2</fragmentCount>
  <gen default-value="${project.build.directory}/.generated">${gwt.gen}</gen>
  <genParam default-value="true">false</genParam>
  <generateDirectory default-value="${project.build.directory}/generated-sources/gwt"/>
  <gwtSdkFirstInClasspath default-value="false">${gwt.gwtSdkFirstInClasspath}</gwtSdkFirstInClasspath>
  <incremental default-value="false">${gwt.compiler.incremental}</incremental>
  <inlineLiteralParameters default-value="true">false</inlineLiteralParameters>
  <inplace default-value="false">true</inplace>
  <jsInteropMode default-value="NONE">JS</jsInteropMode>
  <jvm>${gwt.jvm}</jvm>
  <localRepository default-value="${localRepository}"/>
  <localWorkers>${gwt.compiler.localWorkers}</localWorkers>
  <logLevel default-value="INFO">INFO</logLevel>
  <methodNameDisplayMode default-value="NONE">FULL</methodNameDisplayMode>
  <module>${gwt.module}</module>
  <modulePathPrefix>${gwt.modulePathPrefix}</modulePathPrefix>
  <namespace>NONE</namespace>
  <optimizationLevel default-value="-1">1</optimizationLevel>
  <optimizeDataflow default-value="true">false</optimizeDataflow>
  <ordinalizeEnums default-value="true">false</ordinalizeEnums>
  <overlappingSourceWarnings default-value="false"/>
  <persistentunitcache>true</persistentunitcache>
  <persistentunitcachedir>/Users/mdb/ops/gwt/maven-plugin/target/it-tests/compile/target/persistentunitcache</persistentunitcachedir>
  <pluginArtifactMap default-value="${plugin.artifactMap}"/>
  <project default-value="${project}"/>
  <remoteRepositories default-value="${project.pluginArtifactRepositories}"/>
  <removeDuplicateFunctions default-value="true">false</removeDuplicateFunctions>
  <saveSource default-value="false">true</saveSource>
  <saveSourceOutput>/Users/mdb/ops/gwt/maven-plugin/target/it-tests/compile/target/savedSources</saveSourceOutput>
  <skip default-value="false">${gwt.compiler.skip}</skip>
  <sourceLevel default-value="auto">${maven.compiler.source}</sourceLevel>
  <style default-value="OBF">OBF</style>
  <validateOnly default-value="false">${gwt.validateOnly}</validateOnly>
  <warSourceDirectory default-value="${basedir}/src/main/webapp"/>
  <webappDirectory default-value="${project.build.directory}/${project.build.finalName}">${gwt.war}</webappDirectory>
  <workDir>/Users/mdb/ops/gwt/maven-plugin/target/it-tests/compile/target/workDir</workDir>
</configuration>
```

and not only does this yield a bunch of pointless busywork for everyone whenever an argument is
added or deleted, but it makes development fragile. I was trying to compile the latest snapshot
gwt-maven-plugin against the latest gwt and of course things broke because some argument change was
made to one and not the other:

```
[ERROR] Unknown argument: -XenforceStrictResources
```

There are a myriad of reasons why build systems suck, and the effort a language invests to
eliminate the need for a build system, or minimize the amount of work it does pays dividends in
thousands of programmer hours saved. And hours spent fucking with the build system are the worst
programmer hours of all. Good riddance to them.

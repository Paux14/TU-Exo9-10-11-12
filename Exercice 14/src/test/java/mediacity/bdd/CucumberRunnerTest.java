package mediacity.bdd;

import org.junit.platform.suite.api.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = "cucumber.plugin", value = "pretty, json:target/cucumber-report.json")
@ConfigurationParameter(key = "cucumber.glue", value = "mediacity.bdd.steps")
public class CucumberRunnerTest {}

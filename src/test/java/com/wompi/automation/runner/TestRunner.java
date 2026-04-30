package com.wompi.automation.runner;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * TestRunner: punto de entrada para ejecutar las pruebas Cucumber.
 * Usa JUnit Platform Suite + Cucumber Engine (compatible con Maven Surefire).
 *
 * Ejecución por tags:
 *   mvn test -Dcucumber.filter.tags="@exitoso"
 *   mvn test -Dcucumber.filter.tags="@error"
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
        value = "pretty, json:target/cucumber-reports/cucumber.json, html:target/cucumber-reports/report.html")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME,
        value = "com.wompi.automation.stepdefinitions")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME,
        value = "not @ignorar")
public class TestRunner {
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.tests.appium;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.remote.DesiredCapabilities;
/**
 *
 * @author shannah
 */
@SuppressWarnings("deprecation")
public class CN1AppiumIOSTest {
    private IOSDriver<WebElement> driver;
    private long startTime = 0;

    @Before
    public void setUp() throws Exception {
        
        System.out.println("Codename One Appium Unit Tester:");
        System.out.println("---------------------------------");
        System.out.println("The following properties are required:");
        System.out.println("  -Dapp=/path/to/MyApp.app  : The path to your app to test");
        System.out.println("The following properties are optional:");
        
        System.out.println("  -DdeviceName=[Device Name to Run On] : Default iPhone 6s");
        System.out.println("  -DplatformVersion=[iOS version] : Default platform default");
        System.out.println("Example (Running on Emulator): ");
        System.out.println("  $ mvn test -Dapp=./MyApp.app\n");
        System.out.println("Example (Running on Device):");
        System.out.println("  $ mvn test -Dapp=./MyApp.ipa\n");
        
        String appiumUrl = System.getProperty("appium.url", "http://127.0.0.1:4723/wd/hub");
        DesiredCapabilities capabilities = new DesiredCapabilities();
        String appPath = System.getProperty("app", null);
        if (appPath != null) {
            capabilities.setCapability("app", new File(appPath).getAbsolutePath());
            //capabilities.setCapability("platformName", "iOS");
            capabilities.setCapability("deviceName", System.getProperty("deviceName", "iPhone 6s"));
            capabilities.setCapability("automationName", "XCUITest");
            if (System.getProperty("deviceName") != null) {
                capabilities.setCapability("deviceName", System.getProperty("deviceName", "Android Emulator"));
            }
            if (System.getProperty("platformVersion") != null) {
                capabilities.setCapability("platformVersion", System.getProperty("platformVersion", "9.3"));
            }
        }
        
        startTime = System.currentTimeMillis();
        driver = new IOSDriver<WebElement>(new URL(appiumUrl), capabilities);
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
    }
    
    private LogEntries getLogEntries(String logType) {
        try {
            return driver.manage().logs().get(logType);
        } catch (Throwable t) {
            return null;
        }
    }
    
    @Test
    public void runTest() throws Exception {
        System.out.println("Running tests...");
        Pattern failedPattern = Pattern.compile(".*Passed: (\\d+) tests\\. Failed: (\\d+) tests\\..*");
                                                            //Total 1 tests passed
        Pattern allPassedPattern = Pattern.compile(".*Total (\\d+) tests passed.*");
        //Pattern pidRegex = Pattern.compile("\\b"+Pattern.quote(pid)+"\\b");
        boolean testCompleted = false;
        boolean testPassed = true;
        int passedTests = 0;
        int failedTests = 0;
        Set<String> availableLogTypes = new HashSet<String>(driver.manage().logs().getAvailableLogTypes());
        StringBuilder sb = new StringBuilder();
        outer: while (true) {
            Set<String> logTypes = new HashSet<String>(availableLogTypes);
            for (String logType : logTypes) {
                LogEntries entries = getLogEntries(logType);
                if (entries == null) {
                    availableLogTypes.remove(logType);
                    continue;
                }
                Iterator<LogEntry> it = entries.iterator();
                while (it.hasNext()) {
                    LogEntry e = it.next();
                    if (e.getTimestamp() < startTime) {
                        continue;
                    }
                    //System.out.println(e.getMessage());
                    sb.append(e.getMessage()).append("\n");
                    if (e.getMessage().contains("-----FINISHED TESTS-----")) {
                        break outer;
                    }
                    String line = e.getMessage().trim();
                    Matcher m = failedPattern.matcher(line);
                    Matcher m2 = allPassedPattern.matcher(line);
                    if (m.find()) {
                        testCompleted = true;
                        String numFailedStr = m.group(2);
                        if (Integer.parseInt(numFailedStr) > 0) {
                            testPassed = false;
                        }
                        passedTests += Integer.parseInt(m.group(1));
                        failedTests += Integer.parseInt(m.group(2));
                        System.out.println(line);
                    } else if (m2.find()) {
                        testCompleted = true;
                        passedTests += Integer.parseInt(m2.group(1));
                    }
                }
            }
        }
        Assert.assertTrue("Tests not completed."+"\n"+sb.toString(), testCompleted);
        Assert.assertEquals("PASSED: "+passedTests+" FAILED: "+failedTests+"\n"+sb.toString(), 0, failedTests);
        
    }
}

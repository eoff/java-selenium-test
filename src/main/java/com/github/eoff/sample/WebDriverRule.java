package com.github.eoff.sample;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.proxy.CaptureType;
import org.apache.log4j.Logger;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WebDriverRule extends TestWatcher {
    private static final Logger LOG = Logger.getLogger(WebDriverRule.class);

    private BrowserMobProxy proxyServer;
    private DesiredCapabilities caps;
    private WebDriver driver;

    public WebDriverRule(DesiredCapabilities caps) {
        this.caps = caps;
    }

    @Override
    protected void starting(Description description) {
        try {
            LOG.info("Starting proxy server");
            proxyServer = new BrowserMobProxyServer();
            proxyServer.start();
            LOG.info("Started proxy server on port " + proxyServer.getPort());
            proxyServer.enableHarCaptureTypes(CaptureType.REQUEST_HEADERS);
            prepareCapabilities();
            this.driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), this.caps);
        } catch (Exception e) {
            throw new RuntimeException("Failed to start proxy server. " + e.getMessage());
        }
    }

    @Override
    protected void finished(Description description) {
        try {
            LOG.info("Closing driver");
            driver.quit();
            LOG.info("Closed driver");
        } catch (Exception e) {
            LOG.error("Failed to close driver.", e);
        }
        try {
            LOG.info("Stopping proxy server");
            proxyServer.stop();
            LOG.info("Stopped proxy server");
        } catch (Exception e) {
            LOG.error("Failed to stop proxy server.", e);
        }
    }

    private void prepareCapabilities() {
        Proxy proxy = ClientUtil.createSeleniumProxy(proxyServer);
        LOG.info("Setup " + caps.getBrowserName() + " to use selenium proxy at " + proxy.getHttpProxy());
        if (caps.getBrowserName().contains("chrome")) {
            List<String> switches = (List<String>) caps.getCapability("chrome.switches");
            if (switches == null) {
                switches = new ArrayList<>();
            }
            switches.add("--proxy-server=" + proxy.getHttpProxy());
            caps.setCapability("chrome.switches", switches);
        }
        caps.setCapability(CapabilityType.PROXY, proxy);
    }

    public DesiredCapabilities getCapabilities() {
        return caps;
    }

    public WebDriver getDriver() {
        return driver;
    }

    public BrowserMobProxy getProxyServer() {
        return proxyServer;
    }
}

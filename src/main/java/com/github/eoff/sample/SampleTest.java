package com.github.eoff.sample;

import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;

/**
 * Created by eoff on 31.01.17.
 */
public class SampleTest {

    private static final Logger LOG = Logger.getLogger(SampleTest.class);
    private static final String TOUCH_USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 8_1_1 like Mac OS X) " +
            "AppleWebKit/600.1.4 (KHTML, like Gecko) Version/8.0 Mobile/12B436 Safari/600.1.4";

    @Rule
    public WebDriverRule rule = new WebDriverRule(DesiredCapabilities.chrome());


    @Test
    public void test() throws MalformedURLException {
        WebDriver driver = rule.getDriver();

        rule.getProxyServer().addHeader("User-Agent", TOUCH_USER_AGENT);

        driver.get("https://www.yandex.ru");
        System.out.println(driver.getPageSource());
    }
}

package book.web.page;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Detail {

    @Drone
    private WebDriver browser;


    public String getImageURL() {
        return browser.findElement(By.id("game-cover"))
                .getAttribute("src");
    }
}

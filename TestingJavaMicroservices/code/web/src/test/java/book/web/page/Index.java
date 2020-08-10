package book.web.page;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Represents the index page
 */
@Location("/") // <1>
public class Index {

    @Drone
    private WebDriver browser; // <2>

    @FindBy(id = "tms-search") // <3>
    private WebElement search;

    @FindBy(id = "tms-button")
    private WebElement button;

    @FindBy(className = "col-sm-3")
    private List list; // <4>

    // <5>
    public void navigateTo(String url) {
        browser.manage().window().maximize();
        browser.get(url);
    }

    public List searchFor(String text) {
        search.sendKeys(text); // <6>

        Graphene.guardAjax(button).click(); // <7>

        return list; // <8>
    }

    public String getSearchText() {
        return search.getAttribute("value");
    }
}

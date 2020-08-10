package book.web.page;

import org.jboss.arquillian.graphene.Graphene;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents the search result list
 */
public class List {

    @FindBy(className = "list-group")
    private WebElement list;

    @FindBy(id = "detail-view")
    private Detail detail; // <1>

    public Collection<String> getResults() { // <2>

        ArrayList<String> results = new ArrayList<>();

        if (null != list) {
            java.util.List<WebElement> elements = list.findElements
                    (By.cssSelector("a > p"));

            for (WebElement element : elements) {
                results.add(element.getText());
            }
        }

        return results;
    }

    public Detail getDetail(int index) {

        java.util.List<WebElement> elements = list.findElements(By
                .cssSelector("a > p"));

        if (!elements.isEmpty()) {
            Graphene.guardAjax(elements.get(index)).click(); // <3>
        }


        return detail; // <4>
    }
}

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Itest {
    public static void main(String[] args) {

        WebDriver driver = new ChromeDriver();
        driver.get("http://www.itest.info");

        String title = driver.getTitle();
        System.out.println(title);

        driver.close();
    }
}

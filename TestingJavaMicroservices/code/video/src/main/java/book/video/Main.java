// tag::app[]
package book.video;

import org.springframework.boot.actuate.system
        .ApplicationPidFileWriter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.HashMap;
import java.util.Map;

// <1>
@SpringBootApplication
@EnableAsync
public class Main {

    // <2>
    public static void main(final String[] args) {

        final Map<String, Object> map = new HashMap<>();
        map.put("endpoints.shutdown.enabled", true);
        map.put("endpoints.shutdown.sensitive", false);

        new SpringApplicationBuilder(Main.class).listeners(new
                ApplicationPidFileWriter("./video.pid"))
                .logStartupInfo(true).properties(map).run(args);
    }
}
// tag::app[]

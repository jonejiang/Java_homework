package book.video;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent
        .ThreadPoolTaskExecutor;

@Configuration
public class ThreadExecutorConfiguration {

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new
                ThreadPoolTaskExecutor();
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }
}

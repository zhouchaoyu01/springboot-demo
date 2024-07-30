# 定时任务

在Spring Boot中使用`@Scheduled`注解来实现定时任务，你需要引入Spring的调度模块依赖。在`pom.xml`文件中添加以下依赖：

```
xmlCopy code<dependencies>
    <!-- 其他依赖 -->

    <!-- 引入 Spring 的调度模块依赖 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
</dependencies>
```

`spring-boot-starter`依赖将会自动包含Spring的调度模块，并且还会包含其他必要的Spring Boot依赖。



**启用Scheduling**：在你的主应用程序类中添加@EnableScheduling注解，以启用定时任务调度。

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ScheduledTaskApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScheduledTaskApplication.class, args);
    }
}

```

**创建定时任务**：创建一个类来包含定时任务方法，并在方法上使用`@Scheduled`注解。

```java
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MyScheduledTasks {

    @Scheduled(fixedRate = 5000) // 每隔5秒执行一次
    public void performTask() {
        // 定时任务逻辑
        System.out.println("定时任务执行了。");
    }
}

```

在这个例子中，`performTask`方法将会每隔5秒执行一次。你可以根据需要调整`fixedRate`属性。



**运行应用程序**：运行你的Spring Boot应用程序。定时任务将会在后台自动执行。

这个简单的例子演示了如何使用Spring Boot的`@Scheduled`注解来创建和执行定时任务。你可以根据需要进行更复杂的配置，比如使用`fixedDelay`、`initialDelay`、`cron`等来满足不同的调度需求。

确保你的定时任务方法是线程安全的，并且不会阻塞线程太长时间，以免影响其他任务的调度。



如何用cron来设置定时任务，用上面的例子讲解

@Scheduled(cron = "0/10 * * * * ?") // 每隔10秒执行一次



`0/10 * * * * ?`表示在每分钟的每秒的第0秒开始，每隔10秒执行一次。你可以根据需要修改cron表达式来实现更精确的调度。

```
秒 分 时 日 月 周
```



- 秒（0-59）
- 分（0-59）
- 时（0-23）
- 日（1-31）
- 月（1-12或JAN-DEC）
- 周（0-6或SUN-SAT）

另外，`*`表示每个字段的每个值，`?`用于不指定值，而`/`用于指定步长。

http://cron.ciding.cc/



# Quartz



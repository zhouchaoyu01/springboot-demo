# Sprinngboot3整合Redis



## 1.引入依赖



```xml
<!-- redis -->
<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- mybatis -->
 <dependency>
     <groupId>org.mybatis.spring.boot</groupId>
     <artifactId>mybatis-spring-boot-starter-test</artifactId>
     <version>3.0.2</version>
     <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>3.0.2</version>
</dependency>

<!-- mysql -->
 <dependency>
     <groupId>mysql</groupId>
     <artifactId>mysql-connector-java</artifactId>
     <version>8.0.30</version>
</dependency>
```

## 2.配置文件

```yml
server:
  port: 8001
spring:
  application:
    name: spring3-redis
  datasource:
    url: jdbc:mysql://localhost:3306/test_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root
    password: root123
  data:
    redis:
      # Redis数据库索引（默认为0）
      database: 1
      # Redis服务器地址
      host: localhost
      # Redis服务器连接端口
      port: 6379
      # Redis服务器连接密码（默认为空）
      password: 123
      # 连接超时时间（毫秒）
      connect-timeout: 5000
      lettuce:
        pool:
          # 连接池最大连接数（使用负值表示没有限制）
          max-active: 8
          # 连接池最大阻塞等待时间（使用负值表示没有限制）
          max-wait: -1
          # 连接池中的最大空闲连接
          max-idle: 8


mybatis:
  mapper-locations: classpath:mapper/*.xml #配置 mapper.xml 路径
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl #打印sql
    map-underscore-to-camel-case: true #自动驼峰转化

```

## 3.建立开发所需类

entity, mapper,service,controller



## 4.问题和解决

### Problem1：

```
ERROR 11292 --- [io-8001-exec-10] o.a.c.c.C.[.[.[/].[dispatcherServlet]    : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed: org.springframework.data.redis.serializer.SerializationException: Cannot serialize] with root cause

java.lang.IllegalArgumentException: DefaultSerializer requires a Serializable payload but received an object of type [com.coding.redis.entity.SysUser]
```

 要缓存的 Java 对象必须实现 Serializable 接口，因为 Spring 会将对象先序列化再存入 Redis

```java
@Autowired
    private RedisTemplate redisTemplate;

@Override
    public void setUserToRedisByUserId(String userId) {
        SysUser user = sysUserMapper.getUserById(userId);
        redisTemplate.opsForValue().set("userId:info", user);
    }
```

### Solution1:

```java
public class SysUser implements Serializable {
}
```



http://localhost:8001/setUserToRedis/1



### Problem2：

redis中是乱码

```json
\xac\xed\x00\x05sr\x00\x1fcom.coding.redis.entity.SysUser\x0a\xc2\x18\xe8\x85{\xee\x98\x02\x00\x03L\x00\x08passwordt\x00\x12Ljava/lang/String;L\x00\x06userIdq\x00~\x00\x01L\x00\x08userNameq\x00~\x00\x01xpt\x00\x08admin123t\x00\x011t\x00\x05admin
```

### Solution2:

自定义Redis序列化方式

```java
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        // 设置key序列化方式string，RedisSerializer.string() 等价于 new StringRedisSerializer()
        redisTemplate.setKeySerializer(RedisSerializer.string());
        // 设置value的序列化方式json，使用GenericJackson2JsonRedisSerializer替换默认序列化，RedisSerializer.json() 等价于 new GenericJackson2JsonRedisSerializer()
        redisTemplate.setValueSerializer(RedisSerializer.json());
        // 设置hash的key的序列化方式
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        // 设置hash的value的序列化方式
        redisTemplate.setHashValueSerializer(RedisSerializer.json());
        // 使配置生效
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}

```





```json
{
    "@class": "com.coding.redis.entity.SysUser",
    "userId": "1",
    "userName": "admin",
    "password": "admin123"
}
```


org.apache.ibatis.binding.BindingException: Invalid bound statement (not found): com.coding.mapper.SysUserMapper.findAllUsers

https://blog.csdn.net/sundacheng1989/article/details/81630370

xml路径没告知配置yml



引入依赖

```
<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>3.0.0</version>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
    </dependencies>
```



配置文件

`application.properties`

```
spring.datasource.url=jdbc:mysql://localhost:3306/test_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root123
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

server.port=8888
```

`application.yml`

```
#配置 mapper.xml 路径
mybatis:
  mapper-locations: classpath:mapper/*.xml
```



写对应的接口和类



http://localhost:8888/users

```
[{"userId":null,"userName":null,"password":"admin123"},{"userId":null,"userName":null,"password":"guest123"}]
```

userId为null？ 未开启驼峰转换

```yml
#配置 mapper.xml 路径
mybatis:
  mapper-locations: classpath:mapper/*.xml
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl #打印sql
    map-underscore-to-camel-case: true #自动驼峰转化
```



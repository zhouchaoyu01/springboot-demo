使用JPA完成了简单的查询和按名字模糊查询

http://localhost:8080/users/search?userName=ad

[
    {
    "userId": "1",
    "userName": "admin",
    "password": "admin123"
    }
]

http://localhost:8080/users

[
    {
    "userId": "1",
    "userName": "admin",
    "password": "admin123"
    },
    {
    "userId": "2",
    "userName": "guest",
    "password": "guest123"
    }
]

# ddl-auto什么？

`spring.jpa.hibernate.ddl-auto`是Spring Boot中一个关于数据库表自动更新的配置属性。它控制着Hibernate在启动时如何管理数据库表的创建、更新和删除。这个属性有几个不同的选项，每个选项都会导致不同的行为：

1. **none：** 不自动创建、更新或删除表结构。这通常用于生产环境，以避免意外地修改数据库结构。

2. **validate：** 在应用程序启动时，Hibernate会验证实体类与数据库表结构是否匹配。如果不匹配，会抛出异常。适用于生产环境中的只读应用程序。

3. **update：** 在应用程序启动时，Hibernate会自动创建表，但不会删除或修改已存在的表结构。如果实体类发生更改，Hibernate会尝试通过更新表结构来适应这些更改，但有限制。这适用于开发和测试环境，但在生产环境中需要小心使用。

4. **create：** 在应用程序启动时，Hibernate会自动创建表，如果表已存在，会先删除它然后再创建。这会导致数据丢失，只适合开发和测试阶段。

5. **create-drop：** 类似于create，但在应用程序关闭时会删除表。适用于开发和测试，临时性的数据库。

选择正确的`ddl-auto`设置取决于你的应用程序的阶段和需求。在开发和测试阶段，通常使用`update`或`create`，以便自动管理数据库表的变化。在生产环境中，通常使用`none`或`validate`，以防止意外修改数据库结构。需要注意的是，自动更新表结构可能会带来意外的结果，因此在生产环境中要谨慎使用。


# 使用模糊查询的注意事项：

findByUserNameContainingIgnoreCase方法会返回所有user_name字段包含指定字符串的记录。
使用ContainingIgnoreCase意味着查询是不区分大小写的。
如果你需要更复杂的查询，还可以使用@Query注解来编写自定义查询语句。

# 如何让jpa打印查询的日志在控制台
你可以通过在Spring Boot应用程序的配置文件中设置特定的属性来启用JPA查询的日志输出。这将有助于你在控制台上查看生成的SQL查询语句，以便更好地理解应用程序的数据库交互。以下是在`application.properties`中启用JPA查询日志的方法：

在`src/main/resources/application.properties`文件中添加以下配置：

```properties
# 打印所有SQL语句和参数
spring.jpa.show-sql=true

# 格式化打印的SQL语句
spring.jpa.properties.hibernate.format_sql=true

# 设置日志级别为DEBUG，以显示SQL查询
logging.level.org.hibernate.SQL=DEBUG
```

这些配置将会做以下事情：

- `spring.jpa.show-sql=true`：将会在控制台上打印所有生成的SQL查询语句。
- `spring.jpa.properties.hibernate.format_sql=true`：将会格式化打印的SQL语句，使其更易读。
- `logging.level.org.hibernate.SQL=DEBUG`：设置了Hibernate的SQL查询日志级别为DEBUG，这将会显示生成的SQL查询语句。

通过这些配置，你将能够在控制台上看到应用程序中执行的SQL查询语句，以及查询参数等详细信息。这在调试和优化数据库交互时非常有用。
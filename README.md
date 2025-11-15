# ShangtingGongyu

This is the Shangting Apartment Project, my first practice project focusing solely on backend development. It took more than 20 days to finish, and I gained substantial experience from the process.
All the code is stored in the master branch.

技术栈：mysql，redis，vue，minio，springboot，mybatis-plus，maven，java，ssm，knife4j

学习总结：
1. 在面向对象编程中，子类继承父类后，更新数据库时能连带更新父类字段，核心原因在于子类实例本质上包含了父类的所有属性（字段），且 ORM（对象关系映射）框架通常会将父子类的属性映射到数据库中，并在更新时统一处理整个对象的完整数据。
   
2. 类型转换问题：
    WebDataBinder：用于将前端发送的请求参数绑定到Controller方法的参数，并实现参数类型转换。必要时需要重写新的convertor。
    TypeHandler：用于处理java中实体对象与数据库之间的数据类型转换。mybatis-plus提供了@EnumValue实现了枚举对象与code之间的转换。
    HTTPMessageConvertor：负责Controller与HTTP响应体之间参数的类型转换，即序列化与反序列化（JSON）。Jackson提供了@JsonValue实现了枚举对象与code之间的映射。

3. 多表查询问题：Collection映射多的一方，association映射一的一方。

4. left join 和 right join 区别
    Left join 保留左表所有查询结果，即使右表中没有匹配的记录；右表中未匹配的字段以 NULL 填充。
    Right join 保留右表的所有记录，即使左表中没有匹配的记录；左表中未匹配的字段以 NULL 填充。
    与内连接（INNER JOIN）的区别：内连接只保留两表中匹配成功的记录，而左 / 右连接会保留某一张表的全部记录（无论是否匹配）

5. 全局异常处理：
   通过springmvc提供的全局异常处理统一处理异常，又下面三个注解启用：
        `@ControllerAdvice`用于声明处理全局Controller方法异常的类
        `@ExceptionHandler`用于声明处理异常的方法，`value`属性用于声明该方法处理的异常类型（也可以使用自定义的异常类型）
        `@ResponseBody`表示将方法的返回值作为HTTP的响应体

6. 定时任务：用@EnableScheduling定期执行方法，来检查租约是否到期

7. JWT：我们所说的Token，通常指**JWT**（JSON Web TOKEN）。JWT是一种轻量级的安全传输方式，用于在两个实体之间传递信息，通常用于身份验证和信息传递。JWT是一个字符串，该字符串由三部分组成，三部分由`.`分隔。三个部分分别被称为
    - `header`（头部）
    - `payload`（负载）
    - `signature`（签名）
我们可以在登陆后，将用户名、密码信息存入JWT，用JWT作为标识来决定是否响应信息给前端。
我们可以配合拦截器使用，在拦截器里验证token是否有效，如果有效再进行响应。

8. THreadLocal：用来存用户信息，在解析一次token之后，我们可以直接将用户信息存入Threadlocal，这样进行第二次请求时就不需要再次解析token，直接在ThreadLocal里面拿取数据即可。

9. Mysql查询语句编写顺序：Select ... from ... where ... order by ... limit ...

10. 分页查询：使用mybatisPlus分页查询时，如果需要collection进行映射，只能使用嵌套查询，不能使用嵌套结果映射。

11. @Async：避免保存浏览历史与获取房间详细信息发生冲突。

12. 涉及到两个项目的用户登录时，要把登录记录分别地用线程池装起来，不要放在同一个线程池里面。

13. Mybatis自动映射：需要加 autoMapping = “true”，默认为false。colllection，association都要一起加。

14. 项目优化思路
        缓存优化：例如：通过id查询信息，在获取id时可以将信息存入redis，下次访问时可以先访问redis获取数据，如果没有再去访问数据库，这样可以减少数据库的访问次数，访问内存速度更快。同时，删除数据时，redis与数据库里的信息也需要同步删除。
    

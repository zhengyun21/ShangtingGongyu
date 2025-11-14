# ShangtingGongyu

This is the Shangting Apartment Project, my first practice project focusing solely on backend development. It took more than 20 days to finish, and I gained substantial experience from the process.
All the code is stored in the master branch.

这是尚庭公寓项目，是我第一个练习的纯后端项目，历时20多天，收获颇多。 
前端后端代码都放在 master 分支当中。

技术栈：mysql，redis，vue，minio，springboot，mybatis-plus，maven，java，ssm，knife4j

学习总结：
1. 在面向对象编程中，子类继承父类后，更新数据库时能连带更新父类字段，核心原因在于子类实例本质上包含了父类的所有属性（字段），且 ORM（对象关系映射）框架通常会将父子类的属性映射到数据库中，并在更新时统一处理整个对象的完整数据。
2. 类型转换问题：
    WebDataBinder：用于将前端发送的请求参数绑定到Controller方法的参数，并实现参数类型转换。
    TypeHandler：用于处理java中实体对象与数据库之间的数据类型转换。
    HTTPMessageConvertor：负责Controller与HTTP响应体之间参数的类型转换，即序列化与反序列化（JSON）。
3. 

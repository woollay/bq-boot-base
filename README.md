# bq-boot-base组件的使用说明
- 本组件基于springboot二次封装，主要是为了简化和固化常规的业务场景，当然也支持灵活的配置扩展：
- 本组件引入方法：
    ```xml
    <dependency>
        <groupId>com.biuqu</groupId>
        <artifactId>bq-boot-base</artifactId>
        <version>1.0.2</version>
    </dependency>
    ```

## 1. 为什么要写bq-boot-base组件

- `bq-boot-base`就是为了简化基于Web容器的SpringBoot服务，并同时提供更多更强大的扩展能力，如：限流、数据库数据自动加解密、统一记录接口接口调用日志等，没有必要在每个业务服务中单独写1套；
- "一千个人就有一千个哈姆雷特"，本人在从业过程中，非常注意这种实战经验的积累，因此也沉淀出了这个非常基础的仅依赖SpringBoot的代码框架,算作抱砖引玉吧，期望大家能够一起把基础框架做得更精致，以便让更多的人即学即用，如有深入研究的兴趣，还可以翻看源码和文档；

## 2. 使用bq-boot-base组件有什么好处

- 在`bq-boot-root`能力的基础上，封装了PostgreSQL的自动注入；
- 封装了基于数据库和Redis的QPS/每天最大调用量限流（可配置，默认不限流）；
- 封装了数据库数据模型的自动加解密，业务服务仅需配置注解后即可实现数据的加解密；
- 封装了sleuth/zipkin，并同时整合了logback，做到每个Web服务的Access Log和运行日志均具有链路追踪ID；

## 3. bq-boot-base最佳实践
- bq-boot-root最佳实践是配合SpringBoot一起使用，bq-boot-base中的能力在[bq-service-biz](https://github.com/woollay/bq-service-biz) 和[bq-service-auth](https://github.com/woollay/bq-service-auth) 中有更好的体现；



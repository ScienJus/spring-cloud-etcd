# Spring Cloud Etcd

[![Build Status](https://travis-ci.org/ScienJus/spring-cloud-etcd.svg?branch=master)](https://travis-ci.org/ScienJus/spring-cloud-etcd)
[![Coverage Status](https://coveralls.io/repos/github/ScienJus/spring-cloud-etcd/badge.svg)](https://coveralls.io/github/ScienJus/spring-cloud-etcd)

Etcd integration with Spring Cloud, based on etcd v3 api(jetcd).

## Feature

- Jetcd Starter Supports
- Spring Cloud Discovery Client and Service Registry(like Spring Cloud Consul)

## Example

uses with Spring Cloud Feign:

Add dependencies:

```
<dependencies>
    <dependency>
        <groupId>com.scienjus</groupId>
        <artifactId>spring-cloud-etcd-discovery</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <version>1.5.4.RELEASE</version>
    </dependency>

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-feign</artifactId>
        <version>1.3.1.RELEASE</version>
    </dependency>
</dependencies>
```

Create Spring MVC service project:

```
@RestController
@SpringBootApplication
public class App {

  @GetMapping("hello")
  public String hello() {
    return "world";
  }

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }
}
```

Configure etcd properties:

```
spring:
  cloud:
    etcd:
      endpoints: http://127.0.0.1:2379
      discovery:
        prefix: /services
```

After server started up, check service-registry by etcdctl:

```
-> % ETCDCTL_API=3 etcdctl get /services --prefix
/services/application/192.168.1.102:8080
{"serviceName":"application","address":"192.168.1.102","port":8080,"serviceId":"application"}
```

Then you can use Feign Client like Spring Cloud Consul.


# Dubbo架构搭建

## 一、编译Dubbo项目

### Github下载Dubbo项目

https://github.com/alibaba/dubbo

### Maven安装项目

```powershell
mvn install -Dmaven.test.skip=true
```

注意：需要跳过单元测试，要不然会报错。

## 二、运行Zookeeper

使用Docker直接运行Zookeeper

```powershell
docker run --name some-zookeeper --restart always -d -p 2181:2181 -p 2888:2888 -p 3888:3888 zookeeper
```

## 三、创建Maven项目

### 创建主项目

#### pom.xml文件内容

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.alibaba</groupId>
        <artifactId>dubbo-parent</artifactId>
        <version>2.5.8</version>
    </parent>
    <groupId>top.ruix.demo</groupId>
    <artifactId>dubbo-demo</artifactId>
    <packaging>pom</packaging>
    <version>1.0-SNAPSHOT</version>
    <modules>
        <module>dubbo-demo-api</module>
        <module>dubbo-demo-provider</module>
        <module>dubbo-demo-consumer</module>
    </modules>
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>com.alibaba</groupId>
                <artifactId>dubbo</artifactId>
                <version>2.5.8</version>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

### 创建公共接口项目

#### pom.xml文件内容

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>dubbo-demo</artifactId>
        <groupId>top.ruix.demo</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>
    <artifactId>dubbo-demo-api</artifactId>
</project>
```

#### 创建模型类

Movie类：

```java
package top.ruix.demo.dubbo.api.model;

import java.io.Serializable;
import java.util.Date;

public class Movie implements Serializable {

    private String movieName;
    private String movieDescribe;
    private Date releaseDate;

    public Movie(String movieName, String movieDescribe, Date releaseDate) {
        this.movieName = movieName;
        this.movieDescribe = movieDescribe;
        this.releaseDate = releaseDate;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getMovieDescribe() {
        return movieDescribe;
    }

    public void setMovieDescribe(String movieDescribe) {
        this.movieDescribe = movieDescribe;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "movieName='" + movieName + '\'' +
                ", movieDescribe='" + movieDescribe + '\'' +
                ", releaseDate=" + releaseDate +
                '}';
    }
}

```

注意：必须实现`Serializable`消费者才能正常使用

#### 创建业务接口

IMovieService接口：

```java
package top.ruix.demo.dubbo.api.service;

import top.ruix.demo.dubbo.api.model.Movie;
import java.util.List;

public interface IMovieService {
    List<Movie> getAll();
}
```

### 创建提供者项目

#### pom.xml文件内容

```xml
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.alibaba</groupId>
        <artifactId>dubbo-demo</artifactId>
        <version>2.5.8</version>
    </parent>
    <artifactId>dubbo-demo-provider</artifactId>
    <packaging>jar</packaging>
    <name>${project.artifactId}</name>
    <description>The demo provider module of dubbo project</description>
    <properties>
        <skip_maven_deploy>false</skip_maven_deploy>
    </properties>
    <dependencies>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>dubbo-demo-api</artifactId>
            <version>${project.parent.version}</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>dubbo</artifactId>
            <version>${project.parent.version}</version>
        </dependency>
        <dependency>
            <groupId>com.101tec</groupId>
            <artifactId>zkclient</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.curator</groupId>
            <artifactId>curator-framework</artifactId>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
        </dependency>
        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.4</version>
        </dependency>
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-all</artifactId>
            <version>4.0.35.Final</version>
        </dependency>
    </dependencies>
</project>
```

#### 创建dubbo提供者配置文件 

dubbo-provider.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
       http://code.alibabatech.com/schema/dubbo http://code.alibabatech.com/schema/dubbo/dubbo.xsd">

    <dubbo:application name="demo-provider"/>
    <dubbo:registry address="zookeeper://localhost:2181"/>
    <dubbo:protocol name="dubbo" port="20880"/>
    <bean id="movieService" class="top.ruix.demo.dubbo.provider.service.impl.MovieServiceImpl"/>
    <dubbo:service interface="top.ruix.demo.dubbo.api.service.IMovieService" ref="movieService"/>
</beans>
```

#### 创建业务实现类

```java
package top.ruix.demo.dubbo.provider.service.impl;

import top.ruix.demo.dubbo.api.model.Movie;
import top.ruix.demo.dubbo.api.service.IMovieService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MovieServiceImpl implements IMovieService {
    @Override
    public List<Movie> getAll() {
        List<Movie> movies = new ArrayList<Movie>();
        movies.add(new Movie("大话西游1","大话西游1的描述", new Date()));
        movies.add(new Movie("大话西游2","大话西游2的描述", new Date()));
        movies.add(new Movie("大话西游3","大话西游3的描述", new Date()));
        return movies;
    }
}
```

#### 创建提供者启动类

Provider类：

```java
package top.ruix.demo.dubbo.provider;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class Provider {

    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:dubbo-provider.xml");
        context.start();
        System.in.read();
    }
}
```

### 创建消费者项目

####pom.xml文件内容 

```xml
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.alibaba</groupId>
        <artifactId>dubbo-demo</artifactId>
        <version>2.5.8</version>
    </parent>
    <artifactId>dubbo-demo-consumer</artifactId>
    <packaging>jar</packaging>
    <name>${project.artifactId}</name>
    <description>The demo consumer module of dubbo project</description>
    <properties>
        <skip_maven_deploy>false</skip_maven_deploy>
    </properties>
    <dependencies>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>dubbo-demo-api</artifactId>
            <version>${project.parent.version}</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>dubbo</artifactId>
            <version>${project.parent.version}</version>
        </dependency>
        <dependency>
            <groupId>com.101tec</groupId>
            <artifactId>zkclient</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.curator</groupId>
            <artifactId>curator-framework</artifactId>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
        </dependency>
        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.4</version>
        </dependency>
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-all</artifactId>
            <version>4.0.35.Final</version>
        </dependency>
    </dependencies>
</project>
```

#### 创建消费者配置文件

dubbo-consumer.xml文件内容：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>dubbo-demo</artifactId>
        <groupId>top.ruix.demo</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>dubbo-demo-consumer</artifactId>

    <dependencies>
        <dependency>
            <groupId>top.ruix.demo</groupId>
            <artifactId>dubbo-demo-api</artifactId>
            <version>${project.version}</version>
        </dependency>

        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>dubbo</artifactId>
        </dependency>
        <dependency>
            <groupId>org.javassist</groupId>
            <artifactId>javassist</artifactId>
        </dependency>
        <dependency>
            <groupId>org.jboss.netty</groupId>
            <artifactId>netty</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.zookeeper</groupId>
            <artifactId>zookeeper</artifactId>
        </dependency>
        <dependency>
            <groupId>com.101tec</groupId>
            <artifactId>zkclient</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.curator</groupId>
            <artifactId>curator-framework</artifactId>
        </dependency>
        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
        </dependency>
    </dependencies>
</project>
```

#### 创建消费者启动类

Consumer类：

```java
package top.ruix.demo.dubbo.consumer;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import top.ruix.demo.dubbo.api.model.Movie;
import top.ruix.demo.dubbo.api.service.IMovieService;

import java.util.List;

public class Consumer {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:dubbo-consumer.xml");
        IMovieService movieService = context.getBean(IMovieService.class);
        List<Movie> all = movieService.getAll();
        System.out.println(all);
    }

}
```



## 四、启动测试

### 启动提供者

### 启动消费者


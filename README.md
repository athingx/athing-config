```text
      _____ _     _
  __ /__   \ |__ (_)_ __   __ _
 / _` |/ /\/ '_ \| | '_ \ / _` |
| (_| / /  | | | | | | | | (_| |
 \__,_\/   |_| |_|_|_| |_|\__, |
                          |___/

Just a Thing
```

# 远程配置

## 框架使用

### 添加仓库

```xml
<!-- pom.xml增加仓库 -->
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/athingx/athing-config</url>
    </repository>
</repositories>
```

### 构建客户端

```xml
<!-- pom.xml增加引用 -->
<dependency>
    <groupId>io.github.athingx.athing</groupId>
    <artifactId>athing-config-thing</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

```java
// 构建设备配置
final var thingConfig = new ThingConfigBuilder()
        .build(thing)
        .get();

// 获取设备配置
thingConfig.fetch(Scope.PRODUCT)
        .whenComplete((reply, cause) -> {
            // 处理后续
        });
```

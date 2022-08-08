# 环境

1. jdk : 官网下载，安装后自动配置到idea
2. mvn：官网下载解压，配置环境变量，idea中手动配置mvn目录
3. zookeeper官网下载地址：https://zookeeper.apache.org
   启动zkServer的命令：./zkServer.sh start
4. ftp服务器安装：[安装步骤](https://blog.csdn.net/qq_20042935/article/details/104492460?utm_medium=distribute.pc_feed_404.none-task-blog-2~default~BlogCommendFromBaidu~Rate-4-104492460-blog-null.pc_404_mixedpudn&depth_1-utm_source=distribute.pc_feed_404.none-task-blog-2~default~BlogCommendFromBaidu~Rate-4-104492460-blog-null.pc_404_mixedpud)

# 项目基础环境构建

- **springboot集成dubbo**

官方文档：https://github.com/alibaba/dubbo-spring-boot-starter/blob/master/README_zh.md

consumer直连provider，需要保证调用的服务在provider和consumer的相同的包目录结构下面，否则会报Not found exported service ....may be version or group mismatch

![img](https://cdn.nlark.com/yuque/0/2022/png/1817800/1656484761068-3380c214-4d52-43fa-95f6-f7f27d605958.png)

这里ServiceApi需要在provider和consumer相同的包结构下面。

- SpringBoot集成Dubbo中，消费者直连服务提供者的配置：

```java
@Component
public class QuickStartConsumer {

    @Reference(interfaceClass = ServiceApi.class)
    private ServiceApi serviceApi;

    public void sendMessage(String message) {
        
        System.out.println(serviceApi.sendMessage(message));
    }
}
```

@Component注解将一个类声明为Bean, @Reference注解需要使用Dubbo的包。

# 系统架构

- 总体架构

前端：使用Vue+NodeJs做前端，客户端访问API网关（实现服务聚合、熔断降级、身份安全），api网关调用不同的模块，包括用户模块、影院模块、订单模块等。

后端：使用Guns开源架构+Springboot+Dubbo框架

![img](https://cdn.nlark.com/yuque/0/2022/png/1817800/1659935977098-63c5b7d0-b4aa-4eb0-8650-7dc6a26c7a53.png)

- **guns-api**

为避免相同接口在provider和consumer的相同路径下重复出现（RPC调用需要相同路径），使用【**抽离业务接口】**的方法，将Provider和consumer共同需要的接口放在api模块下。



# Api网关



![img](https://cdn.nlark.com/yuque/0/2022/png/1817800/1659936127459-57d0cf8c-9a7d-46df-a637-7362d5c285a8.png)

客户端只需要和API网关交互，剩下的都交给后端实现，保证一个用户在不同的服务之间能够串通。

- api网关一般是微服务系统中的门面
- **api网关是微服务的重要组成部分**



**API网关作用**

1. **身份验证和安全（用户是否安全，上传的文件是否安全，这里的网关相当于防火墙的作用）**
2. **审查和检测（拦截器）**
3. **动态路由（Dubbo中是不要做的，自带，Cloud需要做）**
4. **压力测试（双十一、双十二压测）**
5. **负载均衡（Dubbo中也有）**
6. **静态相应处理（页面的动静分离，静态网页直接返回）**

# 业务相关

1. **guns-api模块**用于提供业务接口，以免去接口在多个模块下重复出现，方便其他模块引用，主要的作用是**抽离业务API**。
2. user模块不需要接jwt，不需要auth认证，该模块由gateway模块调用。客户端走API网关，API网关做jwt的验证，验证完之后，由网关分发到不同的服务。（client->网关->各个服务）

注：该部分的业务逻辑是，gateway会通过服务接口，调用服务提供者（User模块）的服务。这里的服务提供者是User模块。



User模块的服务实现：

![img](https://cdn.nlark.com/yuque/0/2022/png/1817800/1656658144321-88d1768e-632b-4c07-8c13-c21883ce5330.png)



Gateway模块的调用：

![img](https://cdn.nlark.com/yuque/0/2022/png/1817800/1656658319926-69a2e073-d779-4f09-911c-fed3f3edbbdb.png)



## 一、用户模块

### 1. 业务实体和数据实体

VO：UserModel、UserInfoModel

DO：MoocUserT (数据实体，和表一一对应）

### 2. Dubbo启动检查

- 解决必须先启动服务提供者，否则会报错的问题--> 启动检查

- 如果将用户模块（Provider）部署多台，消费者如何访问-->负载均衡

  

## 二、影片模块

### 1. api网关服务聚合

- 服务聚合是将多个【服务调用】进行封装
- 服务聚合可以简化前端调用方式
- 服务聚合可以提供更好的安全性、可扩展性

![img](https://cdn.nlark.com/yuque/0/2022/png/1817800/1659936195912-ea88663a-e688-42d2-84a7-daf91f018cfc.png)



### 2. Dubbo异步调用

- 异步调用利用NIO的非阻塞特性实现服务调用并行
- ![img](https://cdn.nlark.com/yuque/0/2022/png/1817800/1659936233783-64b1e7f4-95e0-421c-adb2-a12102f0e9ce.png)

```java
<dubbo:service
			async="true"
			timeout="10000"
			interface="com.syc.dubbo.ServiceApi"
			ref="quickStartService"
			retries="3"
	/>
serviceApi.sendMessage(message);
Future<String> sendFuture = RpcContext.getContext().getFuture();
System.out.println(sendFuture.get());
@EnableAsync
```



## 三、影院模块

### 1. 全局异常处理

不把后端的错误代码直接返回给前端，通过全局异常拦截器进行拦截

![img](https://cdn.nlark.com/yuque/0/2022/png/1817800/1659700214865-d102ccb2-6642-4719-8d1f-8bb81f4ac16e.png)

### 2. 结果缓存

- Dubbo提供声明式缓存，减少用户加缓存的工作量。（注解方式）
- Dubbo结果缓存与Redis等的区别（本地缓存和分布式缓存的性能）



**Dubbo的缓存类型**

- **lru 基于最近最少使用原则删除多余缓存，保持最新的数据被缓存**
- **threadlocal 当前线程缓存，比如一个页面渲染，用到很多portal，每个portal都要去查用户信息，通过线程缓存，可以减少这种多余访问**
- **jcache 与JSR107集成，可以桥接各种缓存实现（了解即可，基本不用）**



Spring配置Dubbo本地缓存

```java
<dubbo:reference interface="com.foo.BarService" cache="lru" />
```

业务场景配置Dubbo本地缓存

```java
@Reference(interfaceClass = CinemaServiceAPI.class, cache = "lru", check = false)
    private CinemaServiceAPI cinemaServiceAPI;
```

### 3. 并发和连接控制

Dubbo可以对连接和并发数量进行控制

- 并发控制：服务器并发执行（或占用线程池线程数）某个方法不能超过一定数量
- 连接控制：Provider可以控制有多少个Consumer可以连接访问

超出部分以错误形式返回



并发控制

```java
<dubbo:provider protocol="dubbo" accepts="10" />
```

连接控制

```java
<dubbo:reference interface="com.foo.BarService" connections="10" />
```



## 四、订单模块

**Dubbo特性：**

### 1. 数据库分库分表

订单表

**横向拆分**：家电订单、其他订单

**纵向拆分**：2018年、2019年



### 2. 服务分组 & 分组聚合

服务分组：当一个接口有多种实现时，可以用【group】进行分组

分组聚合：按组合并返回结果

服务分组实现：

```java
@Reference(interfaceClass = OrderServiceAPI.class,
            check = false,
            group = "order2018"
    )
    private OrderServiceAPI orderServiceAPI;

    @Reference(interfaceClass = OrderServiceAPI.class,
            check = false,
            group = "order2017"
    )
    private OrderServiceAPI orderServiceAPI2017;
```



### 3.多版本

当一个接口实现，出现不兼容升级时，可以用版本号过渡，版本号不同的服务相互间不引用。

版本迁移步骤：

（1）在低压力时间段，先升级一般提供者为新版本；

（2）再将所有消费者升级为新版本；

（3）然后将剩下的一半提供者升级为新版本。



Dubbo中的应用：

使用group和version配合，实现蓝绿发布。

### 4.服务限流

- 服务限流是系统高可用的一种手段
- Dubbo可以使用并发和连接控制进行限流（通常不用于做限流）
- 使用**漏桶法**和**令牌法**算法进行限流，令牌桶对业务峰值有一定的容忍度

```java
// 令牌法相比于漏桶法对业务更有一定的容忍度
public class TokenBucket {

    private int bucketNums=100;  // 桶的容量
    private int rate=1;          // 流入速度
    private int nowTokens;      //  当前令牌数量
    private long timestamp=getNowTime();     //  时间

    private long getNowTime(){
        return System.currentTimeMillis();
    }

    private int min(int tokens){
        if(bucketNums > tokens){
            return tokens;
        }else{
            return bucketNums;
        }
    }

    public boolean getToken(){
        // 记录来拿令牌的时间
        long nowTime = getNowTime();
        // 添加令牌【判断该有多少个令牌】
        nowTokens = nowTokens + (int)((nowTime - timestamp)*rate);
        // 添加以后的令牌数量与桶的容量那个小
        nowTokens = min(nowTokens);
        System.out.println("当前令牌数量"+nowTokens);
        // 修改拿令牌的时间
        timestamp = nowTime;
        // 判断令牌是否足够
        if(nowTokens < 1){
            return false;
        }else{
            nowTokens -= 1;
            return true;
        }
    }

}
```

### 5.Hystrix熔断降级

![img](https://cdn.nlark.com/yuque/0/2022/png/1817800/1659866127669-5e506b50-518a-4306-945d-caf7b4dec66f.png)



# 常用命令

检查zk上安装了哪些服务

- ./sh zkCli.sh -server localhost:2181
- ls /dubbo //查看注册的服务
- quit //退出当前窗口



# 排查问题的方式方法

从问题的现象开始分析，一步步分析和确定出现问题的原因。问题的出现很有可能会关联多个原因，**一定要分析出这个问题的根本原因**，切勿因为其他“丝毫”相关事情，而盲目地进行猜测与测试。这种解决问题的方法，失败的概率很大，最终造成时间、精力浪费，并且导致意识萧条。



- **切忌盲目尝试**
- **分析问题，是一个递进的过程**



**本次案例（踩坑）：**

**本次出现的问题，排查耗费较长时间：影片模块，首页测试，地址栏输入：localhost/film/getIndex，提示token验证失败。**

- **失败的尝试：**

**开启用户模块，测试能否使用token-->完全错误了，该url不需要jwt认证。**

**修改jwt中忽略列表的大小写-->大小写都能够匹配，也不是这个问题。**

- **正确的尝试：**

**因为提示token验证失败，所以一定走了jwt认证，那么就是配置的忽略列表有问题，排除忽略列表的拼写错误，那么只能是和前面列表的拼接错误。最后，查看忽略列表的校验逻辑，以“,”为分割，获取拆分后的字符串，所以就不能在“,”前后加空格。**

**分析过程：**

**提示token验证失败->走了jwt认证->忽略列表有问题->追加的url拼写无误->追加方式有误（不能加逗号）**

```java
jwt:
  header: Authorization   #http请求头所需要的字段
  secret: mySecret        #jwt秘钥
  expiration: 604800      #7天 单位:秒
  auth-path: auth         #认证请求的路径
  md5-key: randomKey      #md5加密混淆key
  ignore-url: /user/register,/user/check,/film/getIndex   #忽略列表
```



# 优化点

1. 订单模块需要的接口getSeatsByFieldId，正常应该是从影院模块提供出来的，作为RPC调用
2. 购票参数：应该加上影院ID，去掉seatName（之所以这么传是因为数据放在ftp里面，如果去查的话会很慢）

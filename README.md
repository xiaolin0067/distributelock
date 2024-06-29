[博客](https://blog.csdn.net/qq_36724501/article/details/125209393)

# 1. 分布式锁的常见实现方式
## 1.1 为什么要用
  在单体架构中，多个线程都是属于同一个进程的，所以在线程并发执行遇到资源竞争时，可以利用ReentrantLock、synchronized等技术来作为锁，来控制共享资源的使用。
  而在分布式架构中，多个线程是可能处于不同进程中的，当请求同时达到两个应用上，将导致无法锁住资源。
  ![在这里插入图片描述](https://img-blog.csdnimg.cn/ccb00d89e7734001b913f232bfe1b2bc.png)
## 1.2.分布式锁实现架构
  两个Tomcat通过第三方的组件实现跨进程的分布式锁，这就是分布式锁的解决思路。找到所有JVM可以共同访问的第三方组件，通过第三方组件实现分布式锁。
![在这里插入图片描述](https://img-blog.csdnimg.cn/57f40a1fe73a43e49648c7135c2ebf8c.png)
  常见实现分布式锁的第三方组件有：MySQL、Redis、Zookeeper
## 1.3.原生实现
### 1.3.1.使用MariaDB实现分布式锁
使用select for update访问同一条数据，会锁定数据，其他获取锁的线程只能等待。

```bash
# 关闭事务自动提交
select @@autocommit;
set @@autocommit = 0;
select @@autocommit;
# 加锁
select * from distribute_lock where business_code = 'demo' for update;
# 其他线程不可以对该条数据再次加锁，也不可以修改或删除。
update distribute_lock set business_name = 'newName' where business_code = 'demo';
delete from distribute_lock where business_code = 'demo';

# 可以被检索
select * from distribute_lock where business_code = 'demo';
# 提交事务，释放锁
commit;
```

### 1.3.2.使用Redis实现分布式锁
主要使用了Redis的setnx和执行lua脚本的原子性特点实现

加锁，创建锁资源key：

```bash
SET resource_name my_random_value NX PX 30000
```
若创建成功即加锁成功，否则失败。
其中：
  resource_name：要加锁的资源名称
  my_random_value：加锁线程生成的唯一随机值，线程通过该值来进行解锁
  NX：资源名称不存在设置成功，反之不成功，原子性操作
  PX：自动失效时间，异常情况时锁可以过期失效，单位ms

 解锁，删除锁资源key：
Redis自带lua脚本解释器，且天然原子性，判断加锁key和value一致后删除加锁资源key，lua脚本如下：

```bash
if redis.call('get',KEYS[1]) == ARGV[1] then 
  return redis.call('del',KEYS[1])
else
  return 0
end
```

### 1.3.3.使用Zookeeper实现分布式锁
基于的Zookeeper瞬时有序节点，Zookeeper的Watches观察器能检测节点的变化并产生回调。

 - 瞬时节点(Ephemeral Nodes)：临时存储，不可在有子节点，会话结束或zookeeper重启后会自动消失。
 - 有序节点(Sequence Nodes)：在父节点下有序且唯一命名的节点。

实现流程：
1、创建一个锁节点ParentLock
2、Client 1获取锁会在ParentLock目录下，创建临时顺序节点，获取锁目录下所有的子节点，然后获取比自己小的兄弟节点，如果不存在，则说明当前线程顺序号最小，获得锁
3、Client 2创建临时节点并获取所有兄弟节点，判断自己不是最小节点，设置监听(watcher)比自己次小的节点。
4、Client 1处理完，删除自己的节点，线程B监听到变更事件，判断自己是否为最小的节点，获得锁。
官网：
https://zookeeper.apache.org/doc/r3.8.0/recipes.html
![在这里插入图片描述](https://img-blog.csdnimg.cn/b50c275f54b3488abe58432dc555ec05.png)
加锁，创建瞬时有序节点：

```bash
create -s -e /lock/resource_name lockData
```
其中：
-s：即sequential，创建有序节点
-e：即ephemeral，创建瞬时节点
/lock/resource_name：要加锁的资源节点
lockData：节点放的数据，在这里不重要

```bash
# 查看当前节点列表
ls /lock
```
若创建的瞬时节点序号最小即加锁成功，否则等待watch上一个节点删除。
解锁，删除节点或结束会话：
```bash
# 删除节点
delete /lock/resource_name0000000002
# 结束会话
quit
```
## 1.4.使用开源组件
Java为什么这么流行，其生态好的应该占很大一部分原因。以下有两款实现分布式锁的开源组件。
### 1.4.1.使用Redis客户端Redisson
官网：
https://github.com/redisson/redisson/wiki/8.-distributed-locks-and-synchronizers/#81-lock
### 1.4.2.使用Zookeeper客户端Apache Curator
Apache Curator 是分布式协调服务Apache Zookeeper的 Java客户端库。它包括一个高级 API 框架和实用程序，使 Apache Zookeeper的使用更加轻松和可靠。
官网：
https://curator.apache.org/getting-started.html
https://github.com/apache/curator
## 1.5.分布式锁总结
### 1.5.1.存在的问题
#### 1.5.1.1.Redis集群模式下锁丢失
由于Redis的主从复制（replication）是异步的，这可能会出现在数据同步过程中，master宕机，slave还未同步锁数据，从而锁丢失。
具体流程如下：
(1)客户端1从Master获取了锁。
(2)Master宕机了，存储锁的key还没有来得及同步到Slave上。
(3)Slave升级为Master。
(4)客户端1的锁丢失，客户端2从新的Master获取到了对应同一个资源的锁。
官网也提到了这一点：
https://redis.io/docs/manual/scaling/
![在这里插入图片描述](https://img-blog.csdnimg.cn/726f9bfd1c754b97a7e01da10b272565.png)
redis作者提出客户端采用Redlock算法也并不能完全解决问题，见博文：
https://zhuanlan.zhihu.com/p/387934340
#### 1.5.1.2.程序中断导致锁释放
GC中断导致锁释放、网络问题导致锁释放
![在这里插入图片描述](https://img-blog.csdnimg.cn/a9c25d7565a24fb8ab52f5ae4d3a4c8c.png)
使用Redis实现也应存在此问题，GC中断导致无法进行锁续期
#### 1.5.2.trade off（权衡/折中）
阿里某部门的方案（视频上看的，不晓得是不是真的）
其实很简单就一句话：
  进行业务剥离，用一台Redis实例只用作分布式锁，尽力保证这台Redis不挂。
  采用使用物理服务器，给服务器增加冗余设备（多个电源/UPS/网卡）来保证服务器稳定。
### 1.5.3.对比总结
| 方式 | 优点| 缺点  | 
|--|--|--|
| MariaDB	| 实现简单、易于理解	            |对数据库压力大
| Redis	    | 易于理解，性能好	                |需要自己实现，不支持阻塞
| Zookeeper	| 支持阻塞	                        |需理解Zookeeper，程序复杂
| Curator	| 提供锁的方法，可阻塞，一致性好	|集群下压力较大
| Redisson	| 提供锁的方法，可阻塞，性能好	    |集群下可能导致锁丢失

对于锁阻塞：
数据库：不晓得数据库咋实现的，反正他阻塞了，哪位大佬知道的可补充下。
Redis：需要客户端自旋实现
Zookeeper：watch回调

对于锁释放：
数据库：事务提交即释放
Redis：客户端删除锁。需要设置锁过期时间，并需要看门狗不断进行锁续期
Zookeeper：会话结束/删除锁后锁释放

方式选择：
可考虑使用Redisson，对于已引入Zookeeper组件的系统，获取锁并发压力不大，对锁要求严格的情况下可考虑Curator。

# 2.分布式接口幂等性
## 2.1.为什么要做幂等
为了保证在如下异常场景下，系统执行结果正常：
用户注册、创建订单、支付等场景下的表单重复提交、接口重试、前端操作抖动。
其他场景：MQ消息的重复消费。
## 2.2.概念
幂等：f(f(x)) = f(x)，数学中的概念。对于同一操作请求一次还是多次请求，对结果数据的影响是不变的。即对同一次操作，调用一次接口还是调用多次接口对后端数据的影响是一样的。
**注意**：严格的幂等要求返回结果也需要一致，即相同的参数请求多次返回的结果也应一致，此处不讨论这种情况。

如何判断为同一次操作：
需要从业务上来进行判断，例如：创建订单时提交了多次，相同的商品列表创建了多个订单。

幂等性实现的核心思想：
通过唯一业务单号来保证幂等，如果没有，就创建一个。
## 2.3.幂等处理
并不是所有接口都要求幂等性，需要根据业务来定。
### 2.3.1.Select、Delete操作，天然幂等无需处理
### 2.3.2.对于Update操作（有唯一业务单号）
1、若set的值是固定的，则接口重试不会产生影响
2、若set的值基于现在的值，如 update ... set col = col + 1，则需要根据数据版本号来进行更新。
```bash
# 通过乐观锁，update行锁保证幂等
update set version = version + 1, xxx=${xxx} where id = xxx and version = ${version};
```
### 2.3.3.对于Insert操作
1、无唯一业务单号，给客户端发放token来保证幂等。
实现过程非常的简单：
①客户端进入操作页面时，从后台获取Token并暂存。
②提交操作时，将Token一同传入后台。
③后台使用Token获取分布式锁，获得锁则继续执行，否则执行返回上次应该返回的结果，并在返回的提示中提示重复提交。
可使用注解，和拦截器进行封装，在接口上添加一个注解即可实现幂等性。

关键代码如下：
定义一个注解@ApiIdempotent

```java
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiIdempotent {

}
```
拦截器中处理带了该注解的方法

```java
public class ApiIdempotentInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenController tokenController;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod= (HandlerMethod) handler;
        Method method=handlerMethod.getMethod();

        ApiIdempotent methodAnnotation=method.getAnnotation(ApiIdempotent.class);
        if (methodAnnotation != null) {
            // 校验通过放行，不通过抛出异常进行返回幂等结果
            tokenController.checkIdempotentToken(request);
        }
        return true;
    }

    /**
     * 请求controller之后，渲染视图之前
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }

    /**
     * 请求controller之后，渲染视图之后
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    }
}
```
发放与校验Token

```java
@RestController
@RequestMapping("token")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenController {

    private final StringRedisTemplate redisTemplate;
    private final RedissonClient redisson;

    @GetMapping
    public Result getIdempotentToken(HttpSession session) {
        String token = UUID.randomUUID().toString();
        // 这里的key看需求的粒度，若只防止用户在同一个浏览器上重复提交，在读个浏览器上算正常业务时，可使用sessionId
        redisTemplate.opsForValue().set(getTokenCacheKey(session.getId()),
                token, 600, TimeUnit.SECONDS);
        return Result.ok(token);
    }

    public void checkIdempotentToken(HttpServletRequest request) {
        String sessionId = request.getSession().getId();
        String token = request.getHeader("_idempotentToken");
        RLock lock = redisson.getLock(CacheKey.ORDER_TOKEN_LOCK.append(sessionId));
        lock.lock(5, TimeUnit.SECONDS);
        try {
            String orderTokenKey = getTokenCacheKey(sessionId);
            String cacheToken = redisTemplate.opsForValue().get(orderTokenKey);
            // TODO：注意，根据业务定义，这里直接抛出异常后应该进行拦截，并返回这个唯一业务ID上一次返回的结果
            if (StringUtils.isBlank(cacheToken) || !cacheToken.equals(token)) {
                throw new RuntimeException("重复提交");
            }
            redisTemplate.delete(orderTokenKey);
        } finally {
            lock.unlock();
        }
    }

    private String getTokenCacheKey(String sessionId) {
        return CacheKey.ORDER_TOKEN.append(sessionId);
    }

}
```
在一个接口上添加这个注解，多次请求该接口来进行测试

```java
    @ApiIdempotent
    @PostMapping("createOrder2")
    public Result createOrder2(@RequestBody OrderBO orderBo){
        doCreate(orderBo);
        return Result.ok();
    }
```

2、在部分业务场景Insert也是有唯一业务单号的，如秒杀商品一个用户只能购买一件，则可以使用用户ID加商品ID作为唯一业务单号，使用分布式锁来保证接口幂等。
### 2.3.4.对于混合操作
若有唯一业务单号则可使用分布式锁来保证幂等，否则给客户端发放token来保证幂等。

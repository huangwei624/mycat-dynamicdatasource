# 基于MyCat的动态数据源，实现读写分离

### 一、mysql 配置主从复制

master负责相应的写操作（insert，update，delete），slave负责读操作。
具体配置看链接：https://blog.csdn.net/qq_37654381/article/details/104497093

### 二、安装Mycat服务
具体参考链接：https://blog.csdn.net/qq_37654381/article/details/104510811


### 三、配置多数据源、读写分离
配置两个数据源，一个为写数据源，一个为只读数据源。
```yaml
spring:
  datasource:
    ###可读数据源
    select:
      jdbc-url: jdbc:mysql://192.168.38.134:8066/mycat_testdb
      driver-class-name: com.mysql.jdbc.Driver
      username: user
      password: 123456
    ####可写数据源
    update:
      jdbc-url: jdbc:mysql://192.168.38.134:8066/mycat_testdb
      driver-class-name: com.mysql.jdbc.Driver
      username: root
      password: 123456
    type: com.alibaba.druid.pool.DruidDataSource

```
DataSourceConfig将两个数据源注入spring容器，
```java
// 多数据源配置
@Configuration
public class DataSourceConfig {

	// 创建可读数据源
	@Bean(name = "selectDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.select") // application.properteis中对应属性的前缀
	public DataSource dataSource1() {
		return DataSourceBuilder.create().build();
	}

	// 创建可写数据源
	@Bean(name = "updateDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.update") // application.properteis中对应属性的前缀
	public DataSource dataSource2() {
		return DataSourceBuilder.create().build();
	}

}
```
定义AOP拦截器SwitchDataSourceAOP拦截所有的service方法，通过service方法的名称，
选择合适的数据源：
```java

@Aspect
@Component
@Lazy(false)
@Order(0) // Order设定AOP执行顺序 使之在数据库事务上先执行
public class SwitchDataSourceAOP {
	// 这里切到你的方法目录
	@Before("execution(* life.lovestudy.mycatdynamicdatasources.service.*.*(..))")
	public void process(JoinPoint joinPoint) {
		String methodName = joinPoint.getSignature().getName();
		if (methodName.startsWith("get") || methodName.startsWith("count") || methodName.startsWith("find")
				|| methodName.startsWith("list") || methodName.startsWith("select") || methodName.startsWith("check")) {
			// 读
			DataSourceContextHolder.setDbType("selectDataSource");
		} else {
			// 切换dataSource
			DataSourceContextHolder.setDbType("updateDataSource");
		}
	}
}
```
定义DynamicDataSource类继承AbstractRoutingDataSource:
```java
@Component
@Primary
public class DynamicDataSource extends AbstractRoutingDataSource {
	@Autowired
	@Qualifier("selectDataSource")
	private DataSource selectDataSource;

	@Autowired
	@Qualifier("updateDataSource")
	private DataSource updateDataSource;

	/**
	 * 这个是主要的方法，返回的是生效的数据源名称
	 */
	@Override
	protected Object determineCurrentLookupKey() {
		System.out.println("DataSourceContextHolder：：：" + DataSourceContextHolder.getDbType());
		return DataSourceContextHolder.getDbType();
	}

	/**
	 * 配置数据源信息
	 */
	@Override
	public void afterPropertiesSet() {
		Map<Object, Object> map = new HashMap<>();
		map.put("selectDataSource", selectDataSource);
		map.put("updateDataSource", updateDataSource);
		setTargetDataSources(map);
		setDefaultTargetDataSource(updateDataSource);
		super.afterPropertiesSet();
	}
}

```
这里面有个注意点，就是需要使用ThreadLocal来管理数据源的切换，为了防止在并发请求的情况下
出现并发问题，这个类是DataSourceContextHolder：
```java
@Component
@Lazy(false)
public class DataSourceContextHolder {
	// 采用ThreadLocal 保存本地多数据源
	private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

	// 设置数据源类型
	public static void setDbType(String dbType) {
		contextHolder.set(dbType);
	}

	public static String getDbType() {
		return contextHolder.get();
	}

	public static void clearDbType() {
		contextHolder.remove();
	}

}
```

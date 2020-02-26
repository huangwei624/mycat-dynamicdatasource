package life.lovestudy.mycatdynamicdatasources.config;
// 在Spring 2.0.1中引入了AbstractRoutingDataSource,
// 该类充当了DataSource的路由中介, 能有在运行时, 根据某种key值来动态切换到真正的DataSource上。

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

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

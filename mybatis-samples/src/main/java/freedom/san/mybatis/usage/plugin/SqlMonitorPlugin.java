package freedom.san.mybatis.usage.plugin;

import java.lang.reflect.Method;
import java.sql.Statement;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;

@Intercepts({
		@Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
		@Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
})
@Slf4j
public class SqlMonitorPlugin implements Interceptor {

	private Properties properties = new Properties();

	public Object intercept(Invocation invocation) throws Throwable {
		Long start = System.currentTimeMillis();
		StatementHandler target = (StatementHandler) invocation.getTarget();
		String sql = target.getBoundSql().getSql();

		Object[] args = invocation.getArgs();
		Method method = invocation.getMethod();
		Object proceed = invocation.proceed();
		log.info("\r\n args:{} \r\n sql:{} \r\n time:{} ms",args, sql, (System.currentTimeMillis() - start));
		return proceed;
	}

	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	public void setProperties(Properties properties) {
		this.properties.putAll(properties);
	}
}

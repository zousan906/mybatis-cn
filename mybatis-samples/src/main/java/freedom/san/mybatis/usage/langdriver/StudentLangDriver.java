package freedom.san.mybatis.usage.langdriver;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;

public class StudentLangDriver implements LanguageDriver {
	public ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
		return null;
	}

	public SqlSource createSqlSource(Configuration configuration, XNode script, Class<?> parameterType) {
		return null;
	}

	public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
		return null;
	}
}

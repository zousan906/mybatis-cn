package freedom.san.mybatis;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.PropertyConfigurator;

public abstract class MybatisApplication {
	private SqlSessionFactory sessionFactory;

	static {

	}

	public MybatisApplication() {
		Properties log4jProps = null;
		try {
			log4jProps = Resources.getResourceAsProperties("log4j.properties");
			PropertyConfigurator.configure(log4jProps);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}




	public void buildSqlSessionFactory(String mybatisConfig) {
		try {
			Reader confReader = Resources.getResourceAsReader(mybatisConfig);
			this.sessionFactory = new SqlSessionFactoryBuilder().build(confReader);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public SqlSessionFactory getSessionFactory() {
		return sessionFactory;
	}
}

package freedom.san.mybatis.usage.factory;

import java.util.List;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;

/**
 * mybatis 针对 结果集 实例化提供的一个扩展点.
 */
@Slf4j
public class UserObjectFactory extends DefaultObjectFactory {

	@Override
	public <T> T create(Class<T> type) {

		return super.create(type);
	}

	@Override
	public <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
		log.info("create class instance:[{}],[{}],[{}]", type, constructorArgTypes, constructorArgs);
		return super.create(type, constructorArgTypes, constructorArgs);
	}

	@Override
	protected Class<?> resolveInterface(Class<?> type) {
		return super.resolveInterface(type);
	}

	@Override
	public <T> boolean isCollection(Class<T> type) {
		return super.isCollection(type);
	}

	public void setProperties(Properties properties) {
		log.info("custom props :{}", properties);
	}
}

package freedom.san.mybatis.usage.factory;

import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.Reflector;

public class UserReflectorFactory extends DefaultReflectorFactory {


	public UserReflectorFactory() {
		super();
	}

	@Override
	public boolean isClassCacheEnabled() {
		return super.isClassCacheEnabled();
	}

	@Override
	public void setClassCacheEnabled(boolean classCacheEnabled) {
		super.setClassCacheEnabled(classCacheEnabled);
	}

	@Override
	public Reflector findForClass(Class<?> type) {
		return super.findForClass(type);
	}
}

package freedom.san.mybatis.usage.factory;

import java.util.Map;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.MapWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;

/**
 * 提供了在实例化对象后 属性处理 的扩展点
 * 包装对象后为其提供统一的属性操作方法
 *
 * {@url https://juejin.cn/post/6844904127684673550#heading-0}
 */
public class UserObjectWrapFactory extends DefaultObjectWrapperFactory {

	@Override
	public boolean hasWrapperFor(Object object) {
		return false;
	}

	@Override
	public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
		return new UserWrapper(metaObject, (Map<String, Object>) object);
	}


	public static class UserWrapper extends MapWrapper {

		public UserWrapper(MetaObject metaObject, Map<String, Object> map) {
			super(metaObject, map);
		}
	}
}

package freedom.san.mybatis.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

@Slf4j
public class MetaObjectUtils {


	public static void PrintMetaObject(MetaObject metaObject) {


		Object originalObject = metaObject.getOriginalObject();
		String[] getterNames = metaObject.getGetterNames();
		Class<?>[] interfaces = originalObject.getClass().getInterfaces();

		log.info("class:{}", originalObject.getClass().getName());
		log.info("interface:{}", interfaces);
		log.info("getNames:{}", getterNames);
	}


	public static Object findPluginTarget(MetaObject metaObject) {
		if (ProxyUtils.isProxy(metaObject.getOriginalObject())) {
			Object value = metaObject.getValue("h.target");
			MetaObject target = SystemMetaObject.forObject(value);
			return findPluginTarget(target);
		}
		return metaObject.getOriginalObject();
	}
}

package freedom.san.mybatis.utils;

import java.lang.reflect.Proxy;

public class ProxyUtils {


	public static boolean isProxy(Object object) {
		return Proxy.isProxyClass(object.getClass());
	}
}

/**
 *    Copyright ${license.git.copyrightYears} the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.binding;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.builder.annotation.MapperAnnotationBuilder;
import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

/**
 * @author Clinton Begin
 * @author Eduardo Macarron
 * @author Lasse Voss
 */
public class MapperRegistry {

  private final Configuration config;

  private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();

  public MapperRegistry(Configuration config) {
    this.config = config;
  }

  @SuppressWarnings("unchecked")
  public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
    final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
    if (mapperProxyFactory == null) {
      throw new BindingException("Type " + type + " is not known to the MapperRegistry.");
    }
    try {
      return mapperProxyFactory.newInstance(sqlSession);
    }
    catch (Exception e) {
      throw new BindingException("Error getting mapper instance. Cause: " + e, e);
    }
  }

  public <T> boolean hasMapper(Class<T> type) {
    return knownMappers.containsKey(type);
  }

  /**
   * 对mapper 进行代理,并且放入容器中 通过 class 全路径名称做为key.
   *  流程:
   *  <ul>
   *    <li>对映射器接口 生产代理类 </li>
   *    <li>对映射器接口进行注解解析流程 {@link MapperAnnotationBuilder#parse()} </li>
   *  <ul/>
   */
  public <T> void addMapper(Class<T> type) {
    // 只处理 接口
    if (type.isInterface()) {
      if (hasMapper(type)) {
        throw new BindingException("Type " + type + " is already known to the MapperRegistry.");
      }
      boolean loadCompleted = false;
      try {
        // 使用jdk 动态代理 对 我们的mapper 接口进行代理
        knownMappers.put(type, new MapperProxyFactory<>(type));
        // It's important that the type is added before the parser is run
        // otherwise the binding may automatically be attempted by the
        // mapper parser. If the type is already known, it won't try.
        MapperAnnotationBuilder parser = new MapperAnnotationBuilder(config, type);
        parser.parse();
        loadCompleted = true;
      }
      finally {
        if (!loadCompleted) {
          knownMappers.remove(type);
        }
      }
    }
  }

  /**
   * Gets the mappers.
   *
   * @return the mappers
   * @since 3.2.2
   */
  public Collection<Class<?>> getMappers() {
    return Collections.unmodifiableCollection(knownMappers.keySet());
  }

  /**
   * Adds the mappers.
   *
   * @param packageName
   *          the package name
   * @param superType
   *          the super type
   * @since 3.2.2
   * <br/>
   * 解析流程:
   * <ul>
   * <li>过滤出指定包名下的 所有指定父类型的子类
   * <li>根据扫描出来得class,处理是接口类型的 类
   * <li>判断当前class 是否已经被处理(已经被处理了的放入 knownMappers<K,V>({@link this#knownMappers}) key 是class,V 是对 接口生成的代理类,通过JDK 动态代理生成的)
   * <li>为还未处理的class 生成代理类: {@link MapperProxyFactory},并且放入 {@code knownMappers}
   * <li>通过 {@link MapperAnnotationBuilder#parse()} 解析注解
   * </ul>
   */
  public void addMappers(String packageName, Class<?> superType) {
    // 反射查找对应的mapper class
    ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<>();
    // IsA 是 ResolverUtil 中的条件匹配类,主要是用来判断是否是指定类的 子类 A.isAssignableFrom(Type)
    // find 遍历给定包名下的类 然后通过IsA test
    resolverUtil.find(new ResolverUtil.IsA(superType), packageName);
    Set<Class<? extends Class<?>>> mapperSet = resolverUtil.getClasses();
    // 遍历扫描出的mapper class 进行代理 放入容器中
    for (Class<?> mapperClass : mapperSet) {
      addMapper(mapperClass);
    }
  }

  /**
   * Adds the mappers.<p/>
   * 根据指定的 package 包名解析 mapper 映射文件
   * @param packageName
   *          the package name
   * @since 3.2.2
   */
  public void addMappers(String packageName) {
    addMappers(packageName, Object.class);
  }

}

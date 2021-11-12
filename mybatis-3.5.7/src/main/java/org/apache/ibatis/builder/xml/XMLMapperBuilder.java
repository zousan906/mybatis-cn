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
package org.apache.ibatis.builder.xml;

import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.builder.BaseBuilder;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.CacheRefResolver;
import org.apache.ibatis.builder.IncompleteElementException;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.builder.ResultMapResolver;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Discriminator;
import org.apache.ibatis.mapping.ParameterMap;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.apache.ibatis.type.TypeHandler;

/**
 * @author Clinton Begin
 * @author Kazuki Shimizu
 */
public class XMLMapperBuilder extends BaseBuilder {

  private final XPathParser parser;

  private final MapperBuilderAssistant builderAssistant;

  // 为 全局Configuration 中的 sqlFragments 的引用
  private final Map<String, XNode> sqlFragments;

  // 对应的资源对象名称
  private final String resource;

  @Deprecated
  public XMLMapperBuilder(Reader reader, Configuration configuration, String resource, Map<String, XNode> sqlFragments, String namespace) {
    this(reader, configuration, resource, sqlFragments);
    this.builderAssistant.setCurrentNamespace(namespace);
  }

  @Deprecated
  public XMLMapperBuilder(Reader reader, Configuration configuration, String resource, Map<String, XNode> sqlFragments) {
    this(new XPathParser(reader, true, configuration.getVariables(), new XMLMapperEntityResolver()),
      configuration, resource, sqlFragments);
  }

  public XMLMapperBuilder(InputStream inputStream, Configuration configuration, String resource, Map<String, XNode> sqlFragments, String namespace) {
    this(inputStream, configuration, resource, sqlFragments);
    this.builderAssistant.setCurrentNamespace(namespace);
  }

  public XMLMapperBuilder(InputStream inputStream, Configuration configuration, String resource, Map<String, XNode> sqlFragments) {
    this(new XPathParser(inputStream, true, configuration.getVariables(), new XMLMapperEntityResolver()),
      configuration, resource, sqlFragments);
  }

  private XMLMapperBuilder(XPathParser parser, Configuration configuration, String resource, Map<String, XNode> sqlFragments) {
    super(configuration);
    this.builderAssistant = new MapperBuilderAssistant(configuration, resource);
    this.parser = parser;
    this.sqlFragments = sqlFragments;
    this.resource = resource;
  }

  /**
   * XML 映射器配置解析流程
   * <ul>
   *   <li>先判断资源是否加载,这里的资源名:<b>xx.xxx.xxx.xml</b></li>
   *   <li>xpath 获取 /mapper 直接解析 mapper 节点</li>
   *   <ul>
   *     <li>解析Cache</li>
   *     <li>解析ParameterMap</li>
   *     <li>解析ResultMap</li>
   *     <li>解析SqlFragment</li>
   *     <li>解析Statement</li>
   *   </ul>
   *   <li>根据namespace 解析映射器接口. 如果namespace 是配置的映射器接口,则会走映射器接口解析
   *   {@link org.apache.ibatis.binding.MapperRegistry#addMapper(Class)}</li>
   *
   *  <li>处理在整个解析流程中异常中断的 RM,CACHE 和 Statement</li>
   * </ul>
   */
  public void parse() {
    // 正式解析 xml mapper 文档
    //1. 先判断配置是否被加载过
    if (!configuration.isResourceLoaded(resource)) {
      // 如果未加载 则解析mapper 节点 同时标记 资源被解析
      configurationElement(parser.evalNode("/mapper"));
      configuration.addLoadedResource(resource);
      // 解析namespace  通过namespace 生成对应的映射器接口 代理类,并且注册映射器接口
      bindMapperForNamespace();
    }
    // 处理 解析异常的 结果集映射
    parsePendingResultMaps();
    // 处理 解析异常的 缓存引用
    parsePendingCacheRefs();
    // 处理 解析异常的 查询块
    parsePendingStatements();
  }

  public XNode getSqlFragment(String refid) {
    return sqlFragments.get(refid);
  }

  /**
   * 解析mapper 节点:
   * 1. 解析mapper 属性:namespace , 一般都是mapper 对应的 mapperInterface
   * @param context
   */
  private void configurationElement(XNode context) {
    try {
      String namespace = context.getStringAttribute("namespace");
      if (namespace == null || namespace.isEmpty()) {
        throw new BuilderException("Mapper's namespace cannot be empty");
      }
      // 给当前解析助手设置 namespace
      builderAssistant.setCurrentNamespace(namespace);
      // 解析 二级缓存 cache 配置信息
      cacheRefElement(context.evalNode("cache-ref"));
      cacheElement(context.evalNode("cache"));
      // 解析所有的 参数映射集
      parameterMapElement(context.evalNodes("/mapper/parameterMap"));
      // 解析所有的 结果集映射
      resultMapElements(context.evalNodes("/mapper/resultMap"));
      // 解析所有的SQL片段
      sqlElement(context.evalNodes("/mapper/sql"));
      // 解析所有的 statement 节点
      buildStatementFromContext(context.evalNodes("select|insert|update|delete"));
    }
    catch (Exception e) {
      throw new BuilderException("Error parsing Mapper XML. The XML location is '" + resource + "'. Cause: " + e, e);
    }
  }

  private void buildStatementFromContext(List<XNode> list) {
    if (configuration.getDatabaseId() != null) {
      buildStatementFromContext(list, configuration.getDatabaseId());
    }
    buildStatementFromContext(list, null);
  }

  private void buildStatementFromContext(List<XNode> list, String requiredDatabaseId) {
    for (XNode context : list) {
      final XMLStatementBuilder statementParser = new XMLStatementBuilder(configuration, builderAssistant, context, requiredDatabaseId);
      try {
        statementParser.parseStatementNode();
      }
      catch (IncompleteElementException e) {
        configuration.addIncompleteStatement(statementParser);
      }
    }
  }

  private void parsePendingResultMaps() {
    Collection<ResultMapResolver> incompleteResultMaps = configuration.getIncompleteResultMaps();
    synchronized (incompleteResultMaps) {
      Iterator<ResultMapResolver> iter = incompleteResultMaps.iterator();
      while (iter.hasNext()) {
        try {
          iter.next().resolve();
          iter.remove();
        }
        catch (IncompleteElementException e) {
          // ResultMap is still missing a resource...
        }
      }
    }
  }

  private void parsePendingCacheRefs() {
    Collection<CacheRefResolver> incompleteCacheRefs = configuration.getIncompleteCacheRefs();
    synchronized (incompleteCacheRefs) {
      Iterator<CacheRefResolver> iter = incompleteCacheRefs.iterator();
      while (iter.hasNext()) {
        try {
          iter.next().resolveCacheRef();
          iter.remove();
        }
        catch (IncompleteElementException e) {
          // Cache ref is still missing a resource...
        }
      }
    }
  }

  private void parsePendingStatements() {
    Collection<XMLStatementBuilder> incompleteStatements = configuration.getIncompleteStatements();
    synchronized (incompleteStatements) {
      Iterator<XMLStatementBuilder> iter = incompleteStatements.iterator();
      while (iter.hasNext()) {
        try {
          iter.next().parseStatementNode();
          iter.remove();
        }
        catch (IncompleteElementException e) {
          // Statement is still missing a resource...
        }
      }
    }
  }

  /**
   * 定义二级缓存引用, 如果是引用,则两个对象引用的缓存是同一个对象<br/>
   * cache-ref : namespace <br/>
   * 缓存引用,应用其他 namespace 的缓存配置 <br/>
   * @param context
   */
  private void cacheRefElement(XNode context) {
    if (context != null) {
      // 标记 缓存引用
      configuration.addCacheRef(builderAssistant.getCurrentNamespace(), context.getStringAttribute("namespace"));
      CacheRefResolver cacheRefResolver = new CacheRefResolver(builderAssistant, context.getStringAttribute("namespace"));
      // 从缓存集合中获取引用实例 设置到具体的mapper 中
      try {
        cacheRefResolver.resolveCacheRef();
      }
      catch (IncompleteElementException e) {
        // 如果因为找不到缓存引用对象,则放入未完成列表,秋后算账
        configuration.addIncompleteCacheRef(cacheRefResolver);
      }
    }
  }

  /**
   * <b>定义二级缓存 配置 <b/><br/>
   * 解析缓存配置<p>
   * 属性列表:
   * <ul>
   *  <li>type: 缓存类型:默认缓存类型 perpetual {@link org.apache.ibatis.cache.impl.PerpetualCache}</li>
   *  <li>eviction: 缓存的定时回收策略:常见的 FIFO,LRU</li>
   *  <li>flushInterval: 配置一定时间自动刷新缓存</li>
   *  <li>size: hashMap 缓存的数量</li>
   *  <li>readOnly: 是否只读，若配置可读写，则需要对应的实体类能够序列化</li>
   *  <li>blocking: 若缓存中找不到对应的key，是否会一直blocking，直到有对应的数据进入缓存</li>
   * </ul>
   * 元素:
   * property
   * @param context
   */
  private void cacheElement(XNode context) {
    if (context != null) {
      String type = context.getStringAttribute("type", "PERPETUAL");
      Class<? extends Cache> typeClass = typeAliasRegistry.resolveAlias(type);
      String eviction = context.getStringAttribute("eviction", "LRU");
      Class<? extends Cache> evictionClass = typeAliasRegistry.resolveAlias(eviction);
      Long flushInterval = context.getLongAttribute("flushInterval");
      Integer size = context.getIntAttribute("size");
      boolean readWrite = !context.getBooleanAttribute("readOnly", false);
      boolean blocking = context.getBooleanAttribute("blocking", false);
      Properties props = context.getChildrenAsProperties();
      builderAssistant.useNewCache(typeClass, evictionClass, flushInterval, size, readWrite, blocking, props);
    }
  }

  /**
   *
   * xml DTD 定义
   * ELEMENT parameter EMPTY <br>
   * ATTLIST parameter
   * property CDATA #REQUIRED         参数用于指定参数的名称,为必填项 <br>
   * javaType CDATA #IMPLIED          参数的java 类型 <br>
   * jdbcType CDATA #IMPLIED          参数的jdbc 类型 <br>
   * mode (IN | OUT | INOUT) #IMPLIED 用来配置参数的类型，其中IN表示入参，OUT表示出参,INOUT表示即为出参也为入参。 <br>
   * resultMap CDATA #IMPLIED         来映射参数结果集。<br>
   * scale CDATA #IMPLIED             参数用来指定参数小数保留位数，比较有趣的是，虽然DTD中定义的是scale，但是Mybatis实际解析的却是numericScale. <br>
   * typeHandler CDATA #IMPLIED       用来指定处理参数在java类型和jdbc类型之间互相转换的转换器类型 <br>
   *
   * @note: parameterMap 已经被废弃
   * @param list
   */
  private void parameterMapElement(List<XNode> list) {
    for (XNode parameterMapNode : list) {
      String id = parameterMapNode.getStringAttribute("id");
      String type = parameterMapNode.getStringAttribute("type");
      Class<?> parameterClass = resolveClass(type);
      List<XNode> parameterNodes = parameterMapNode.evalNodes("parameter");
      List<ParameterMapping> parameterMappings = new ArrayList<>();
      for (XNode parameterNode : parameterNodes) {
        String property = parameterNode.getStringAttribute("property");
        String javaType = parameterNode.getStringAttribute("javaType");
        String jdbcType = parameterNode.getStringAttribute("jdbcType");
        String resultMap = parameterNode.getStringAttribute("resultMap");
        String mode = parameterNode.getStringAttribute("mode");
        String typeHandler = parameterNode.getStringAttribute("typeHandler");
        Integer numericScale = parameterNode.getIntAttribute("numericScale");
        // 节点参数读取完成后: 对参数对象进行解析

        ParameterMode modeEnum = resolveParameterMode(mode);
        /**
         *  将java类型转为 对应的class 对象
         *  @see TypeAliasRegistry
         */
        Class<?> javaTypeClass = resolveClass(javaType);
        /**
         * 返回一个jdbc 类型的枚举对象
         * @see JdbcType
         */
        JdbcType jdbcTypeEnum = resolveJdbcType(jdbcType);
        // 解析typeHandler 依旧是 TypeAliasRegistry 解析,先查找基本集合,如果没有,则尝试加载 class 对象并且返回
        Class<? extends TypeHandler<?>> typeHandlerClass = resolveClass(typeHandler);
        //构造一个属性映射 对象
        ParameterMapping parameterMapping = builderAssistant.buildParameterMapping(parameterClass, property, javaTypeClass, jdbcTypeEnum, resultMap, modeEnum, typeHandlerClass, numericScale);
        parameterMappings.add(parameterMapping);
      }
      /**
       * 注册 属性映射对象 到 {@link Configuration#parameterMaps}中
       */
      builderAssistant.addParameterMap(id, parameterClass, parameterMappings);
    }
  }

  private void resultMapElements(List<XNode> list) {
    for (XNode resultMapNode : list) {
      try {
        resultMapElement(resultMapNode);
      }
      catch (IncompleteElementException e) {
        // ignore, it will be retried
      }
    }
  }

  private ResultMap resultMapElement(XNode resultMapNode) {
    return resultMapElement(resultMapNode, Collections.emptyList(), null);
  }

  /**
   * 解析 结果集映射
   * <ul>
   * <li>&lt resultMap   id,type,extends,autoMapping &gt </li>
   * <li>  &lt constructor &gt    配置构造参数,如果没有无参构造,且定义了带参构造方法,则需要定义构造函数 </li>
   * <li>    &lt idArg/ &gt </li>
   * <li>    &lt arg/ &gt </li>
   * <li>  &lt /constructor &gt </li>
   * <li>  &lt id/ &gt        主键 </li>
   * <li>  &lt result/ &gt    属性映射集合 </li>
   * <li>  &lt association property=""/ &gt  级联 解决1对1 关系 </li>
   * <li>  &lt collection property=""/ &gt   级联 解决1对多 关系 </li>
   * <li>  &lt discriminator javaType="" &gt 级联 鉴别器 </li>
   * <li>   &lt case value = "" &gt &gt /case &gt </li>
   * <li>  &lt /discriminator &gt </li>
   * <li> &lt /resultMap &gt </li>
   *</ul>
   * @NOTE: 级联两种实用方式:
   *  <br/>1. 配置 select 子查询的方式(不推荐 查询N+1 问题, 每个结果集查询都要单独去查询一次, 要么就要配合lazy load 使用)
   *  <br/>2. 通过sql 写好 连接,只通过级联配置属性映射
   */

  private ResultMap resultMapElement(XNode resultMapNode, List<ResultMapping> additionalResultMappings, Class<?> enclosingType) {
    ErrorContext.instance().activity("processing " + resultMapNode.getValueBasedIdentifier());
    //ofType ,resultType,javaType  主要是 级联中配置的
    String type = resultMapNode.getStringAttribute("type",
      resultMapNode.getStringAttribute("ofType",
        resultMapNode.getStringAttribute("resultType",
          resultMapNode.getStringAttribute("javaType"))));
    // 加载ResultMap 映射的实例对象类  (会查找已经注册的 类型或者 加载对应的class 对象)
    Class<?> typeClass = resolveClass(type);
    if (typeClass == null) {
      // 检查内嵌 RM 配置: association  或者 case 的情况
      typeClass = inheritEnclosingType(resultMapNode, enclosingType);
    }
    Discriminator discriminator = null;
    List<ResultMapping> resultMappings = new ArrayList<>(additionalResultMappings);
    List<XNode> resultChildren = resultMapNode.getChildren();
    for (XNode resultChild : resultChildren) {
      //1. 解析构造函数
      if ("constructor".equals(resultChild.getName())) {
        processConstructorElement(resultChild, typeClass, resultMappings);
      }
      // 2. 解析级联 鉴定器
      else if ("discriminator".equals(resultChild.getName())) {
        discriminator = processDiscriminatorElement(resultChild, typeClass, resultMappings);
      }
      else {
        // 其他属性解析
        List<ResultFlag> flags = new ArrayList<>();
        if ("id".equals(resultChild.getName())) {
          flags.add(ResultFlag.ID);
        }
        resultMappings.add(buildResultMappingFromContext(resultChild, typeClass, flags));
      }
    }
    String id = resultMapNode.getStringAttribute("id",
      resultMapNode.getValueBasedIdentifier());
    String extend = resultMapNode.getStringAttribute("extends");
    Boolean autoMapping = resultMapNode.getBooleanAttribute("autoMapping");
    ResultMapResolver resultMapResolver = new ResultMapResolver(builderAssistant, id, typeClass, extend, discriminator, resultMappings, autoMapping);
    try {
      return resultMapResolver.resolve();
    }
    catch (IncompleteElementException e) {
      configuration.addIncompleteResultMap(resultMapResolver);
      throw e;
    }
  }

  protected Class<?> inheritEnclosingType(XNode resultMapNode, Class<?> enclosingType) {
    if ("association".equals(resultMapNode.getName()) && resultMapNode.getStringAttribute("resultMap") == null) {
      String property = resultMapNode.getStringAttribute("property");
      if (property != null && enclosingType != null) {
        MetaClass metaResultType = MetaClass.forClass(enclosingType, configuration.getReflectorFactory());
        return metaResultType.getSetterType(property);
      }
    }
    else if ("case".equals(resultMapNode.getName()) && resultMapNode.getStringAttribute("resultMap") == null) {
      return enclosingType;
    }
    return null;
  }

  private void processConstructorElement(XNode resultChild, Class<?> resultType, List<ResultMapping> resultMappings) {
    List<XNode> argChildren = resultChild.getChildren();
    for (XNode argChild : argChildren) {
      List<ResultFlag> flags = new ArrayList<>();
      flags.add(ResultFlag.CONSTRUCTOR);
      if ("idArg".equals(argChild.getName())) {
        flags.add(ResultFlag.ID);
      }
      resultMappings.add(buildResultMappingFromContext(argChild, resultType, flags));
    }
  }

  private Discriminator processDiscriminatorElement(XNode context, Class<?> resultType, List<ResultMapping> resultMappings) {
    String column = context.getStringAttribute("column");
    String javaType = context.getStringAttribute("javaType");
    String jdbcType = context.getStringAttribute("jdbcType");
    String typeHandler = context.getStringAttribute("typeHandler");
    Class<?> javaTypeClass = resolveClass(javaType);
    Class<? extends TypeHandler<?>> typeHandlerClass = resolveClass(typeHandler);
    JdbcType jdbcTypeEnum = resolveJdbcType(jdbcType);
    Map<String, String> discriminatorMap = new HashMap<>();
    for (XNode caseChild : context.getChildren()) {
      String value = caseChild.getStringAttribute("value");
      String resultMap = caseChild.getStringAttribute("resultMap", processNestedResultMappings(caseChild, resultMappings, resultType));
      discriminatorMap.put(value, resultMap);
    }
    return builderAssistant.buildDiscriminator(resultType, column, javaTypeClass, jdbcTypeEnum, typeHandlerClass, discriminatorMap);
  }

  private void sqlElement(List<XNode> list) {
    if (configuration.getDatabaseId() != null) {
      sqlElement(list, configuration.getDatabaseId());
    }
    sqlElement(list, null);
  }

  private void sqlElement(List<XNode> list, String requiredDatabaseId) {
    for (XNode context : list) {
      String databaseId = context.getStringAttribute("databaseId");
      String id = context.getStringAttribute("id");
      id = builderAssistant.applyCurrentNamespace(id, false);
      if (databaseIdMatchesCurrent(id, databaseId, requiredDatabaseId)) {
        sqlFragments.put(id, context);
      }
    }
  }

  private boolean databaseIdMatchesCurrent(String id, String databaseId, String requiredDatabaseId) {
    if (requiredDatabaseId != null) {
      return requiredDatabaseId.equals(databaseId);
    }
    if (databaseId != null) {
      return false;
    }
    if (!this.sqlFragments.containsKey(id)) {
      return true;
    }
    // skip this fragment if there is a previous one with a not null databaseId
    XNode context = this.sqlFragments.get(id);
    return context.getStringAttribute("databaseId") == null;
  }

  /**
   * 解析RM 属性配置 @param context 配置节点 @param resultType RM 返回类型 @param flags 存放Tag 的集合
   * @return
   */
  private ResultMapping buildResultMappingFromContext(XNode context, Class<?> resultType, List<ResultFlag> flags) {
    String property;
    /**
     * 如果是通过 构造函数解析 调用过来的 则解析name 属性
     * @see this#processConstructorElement flg = {@link ResultFlag#CONSTRUCTOR}
     *
     * 如果是通过 解析RM 属性节点过来的,则解析 property 节点
     * {@see this#resultMapElement} flg = {@link ResultFlag#ID}
     */
    if (flags.contains(ResultFlag.CONSTRUCTOR)) {
      property = context.getStringAttribute("name");
    }
    else {
      property = context.getStringAttribute("property");
    }
    String column = context.getStringAttribute("column");
    String javaType = context.getStringAttribute("javaType");
    String jdbcType = context.getStringAttribute("jdbcType");

    // 解析内嵌 查询,一般在级联中存在
    String nestedSelect = context.getStringAttribute("select");
    // 解析内嵌 RM ,一般在级联中存在
    String nestedResultMap = context.getStringAttribute("resultMap", () ->
      processNestedResultMappings(context, Collections.emptyList(), resultType));

    String notNullColumn = context.getStringAttribute("notNullColumn");
    String columnPrefix = context.getStringAttribute("columnPrefix");
    String typeHandler = context.getStringAttribute("typeHandler");
    String resultSet = context.getStringAttribute("resultSet");
    String foreignColumn = context.getStringAttribute("foreignColumn");
    boolean lazy = "lazy".equals(context.getStringAttribute("fetchType", configuration.isLazyLoadingEnabled() ? "lazy" : "eager"));
    Class<?> javaTypeClass = resolveClass(javaType);
    Class<? extends TypeHandler<?>> typeHandlerClass = resolveClass(typeHandler);
    JdbcType jdbcTypeEnum = resolveJdbcType(jdbcType);
    return builderAssistant.buildResultMapping(resultType, property, column, javaTypeClass, jdbcTypeEnum, nestedSelect, nestedResultMap, notNullColumn, columnPrefix, typeHandlerClass, flags, resultSet, foreignColumn, lazy);
  }

  private String processNestedResultMappings(XNode context, List<ResultMapping> resultMappings, Class<?> enclosingType) {
    if (Arrays.asList("association", "collection", "case").contains(context.getName())
      && context.getStringAttribute("select") == null) {
      validateCollection(context, enclosingType);
      ResultMap resultMap = resultMapElement(context, resultMappings, enclosingType);
      return resultMap.getId();
    }
    return null;
  }

  protected void validateCollection(XNode context, Class<?> enclosingType) {
    if ("collection".equals(context.getName()) && context.getStringAttribute("resultMap") == null
      && context.getStringAttribute("javaType") == null) {
      MetaClass metaResultType = MetaClass.forClass(enclosingType, configuration.getReflectorFactory());
      String property = context.getStringAttribute("property");
      if (!metaResultType.hasSetter(property)) {
        throw new BuilderException(
          "Ambiguous collection type for property '" + property + "'. You must specify 'javaType' or 'resultMap'.");
      }
    }
  }

  /**
   * 尝试通过 namespace 解析为class, 一般情况下,namespace 为 映射器接口,所以能正常加载
   * 但是也有用其他唯一标记作为namespace的,比如不是通过 dao接口的方式
   *
   * 处理: 1.如果能正确load 绑定类型,则 在 配置中心 使用namespace 进行标记:  namespace:+ namespace
   *      2.添加 映射器接口 到Mapper 中,并且生成对应的接口代理类并且注册
   */
  private void bindMapperForNamespace() {
    String namespace = builderAssistant.getCurrentNamespace();
    if (namespace != null) {
      Class<?> boundType = null;
      try {
        boundType = Resources.classForName(namespace);
      }
      catch (ClassNotFoundException e) {
        // ignore, bound type is not required
      }
      if (boundType != null && !configuration.hasMapper(boundType)) {
        // Spring may not know the real resource name so we set a flag
        // to prevent loading again this resource from the mapper interface
        // look at MapperAnnotationBuilder#loadXmlResource
        configuration.addLoadedResource("namespace:" + namespace);
        configuration.addMapper(boundType);
      }
    }
  }

}

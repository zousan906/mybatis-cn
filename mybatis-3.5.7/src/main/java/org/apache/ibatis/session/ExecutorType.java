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
package org.apache.ibatis.session;

/**
 * @author Clinton Begin
 *  提供三种 执行器
 * <pre>
 *  1. Simple  默认的 执行器: 每次执行query ,update 就开启一个新的statement ,用完后就立即关闭statement 对象
 *  2. resuse  重用执行器 : 每次执行完 query ,update  statement 缓存在 executor 中,reuse 主要是复用 statement 对象
 *  3. batch   批处理执行器: 只处理 update, jdbc 不处理批量查询, 批量update ,addBatch 都是缓存在executor中
 * </pre>
 */
public enum ExecutorType {
  SIMPLE, REUSE, BATCH
}

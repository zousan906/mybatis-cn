package freedom.san.mybatis.usage.basic;

import java.util.List;

import freedom.san.mybatis.MybatisApplication;
import freedom.san.mybatis.usage.basic.entity.Card;
import freedom.san.mybatis.usage.basic.entity.Student;
import freedom.san.mybatis.usage.basic.mapper.CardMapper;
import freedom.san.mybatis.usage.basic.mapper.ClassesMapper;
import freedom.san.mybatis.usage.basic.model.CardStudentDO;
import freedom.san.mybatis.usage.basic.model.ClassStudentDO;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 级联使用
 */
@FixMethodOrder(value = MethodSorters.JVM)
public class TypeHandlerUsages extends MybatisApplication {
	static final Logger log = LoggerFactory.getLogger(TypeHandlerUsages.class);

	static SqlSession sqlSession;

	static CardMapper mapper;

	@BeforeClass
	public static void init() {
		log.info("before class");
		buildSqlSessionFactory("freedom/san/mybatis/usage/basic/mybatis-basic.xml");
		sqlSession = getSessionFactory().openSession();
		mapper = sqlSession.getMapper(CardMapper.class);
	}

	@Before
	public void before() {
	}


	@Test
	public void select() {
		Card card = mapper.selectById(1);
		log.info("card:{}", card);
	}

	@Test
	public void selectCardStu() {
		CardStudentDO card = mapper.selectCardStudent(1);
		log.info("card:{}", card);
	}

	@After
	public void commit() {
		sqlSession.commit();
		log.info("commit");
	}

	@AfterClass
	public static void afterClass() {
		sqlSession.close();
		log.info("close");
	}

}

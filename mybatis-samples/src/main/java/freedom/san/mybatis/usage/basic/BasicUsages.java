package freedom.san.mybatis.usage.basic;

import java.util.List;

import freedom.san.mybatis.MybatisApplication;
import freedom.san.mybatis.usage.basic.entity.Classes;
import freedom.san.mybatis.usage.basic.mapper.ClassesMapper;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FixMethodOrder(value = MethodSorters.JVM)
public class BasicUsages extends MybatisApplication {
	static final Logger log = LoggerFactory.getLogger(BasicUsages.class);

	ClassesMapper mapper;

	SqlSession sqlSession;

	@BeforeClass
	public static void init() {
		buildSqlSessionFactory("freedom/san/mybatis/usage/basic/mybatis-basic.xml");
	}

	@Before
	public void before() {
		sqlSession = getSessionFactory().openSession();
		mapper = sqlSession.getMapper(ClassesMapper.class);
		log.info("before");
	}

	@Test
	public void insert() {
		Classes classes = new Classes();
		classes.setName("class one");
		mapper.insert(classes);
		sqlSession.commit();
		log.info("insert class{}", classes);
	}

	@After
	public void commit() {
		sqlSession.close();
		log.info("commit");
	}

	@Test
	public void selectAll() {
		List<Classes> classes = mapper.selectAll();
		for (Classes aClass : classes) {
			log.info(aClass.toString());
		}
	}

}

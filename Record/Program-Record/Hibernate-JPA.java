
hibernate-release-4.1.0.Final/documentation/quickstart/en-US/html_single/files/hibernate-tutorials.zip 
 有hibernate.cfg.xml,x.hbm.xml,x.java 复制
hibernate-release-4.1.0.Final/documentation/manual/en-US/html_single/index.html  有.dtd示例
 
hibernate-release-4.1.0.Final\project\etc\hibernate.properties 文件中有可以配置的key
Environment,AvailableSettings 类中有所有的hibernate.开头的属性

hibernate.connection.isolation 同 JDBC 的隔离级别

<!DOCTYPE hibernate-configuration PUBLIC "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
		"http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
	<session-factory>
		<!-- Hibernate 的配置不能有 value属性,而JPA必须有value属性-->
		<property name="hibernate.connection.driver_class">org.h2.Driver</property>
		<property name="hibernate.connection.url">jdbc:h2:tcp://localhost/~/test</property> 
		<property name="hibernate.connection.username">sa</property>
		<property name="hibernate.connection.password"></property>
		<property name="hibernate.dialect">org.hibernate.dialect.H2Dialect</property>
		
		<property name="hibernate.show_sql">true</property>
		<property name="hibernate.hbm2ddl.auto">update</property><!-- update, create, create-drop ,validate-->
		<property name="hibernate.use_sql_comments">true</property>
		<property name="hibernate.format_sql">true</property>
		<property name="hibernate.max_fetch_depth">3</property>	
		<!--  
		<property name="hibernate.archive.autodetection" value="class, hbm" />
		默认是class, hbm,如项目中有.hbm.xml日志中显示Detect class: true; detect hbm: true ,可修改为false
		-->
		<property name="hibernate.archive.autodetection">false</property>
		<!--
		<property name="hibernate.ejb.cfgfile" value="hibernate.cfg.xml" /> 
			日志文件中是有的,最好打开,并<hibernate-mapping auto-import="false">,
		-->
		<property name="hibernate.current_session_context_class">thread</property> <!-- thread 表示使用ThreadLocal,jta 可以使用sessionFactory.getCurrentSession(); -->
		
		<!--
		<property name="hibernate.transaction.factory_class">org.hibernate.engine.transaction.internal.jta.JtaTransactionFactory</property>
		<property name="jta.UserTransaction">jndi/xx</property>
		 -->
		<property name="hibernate.transaction.factory_class">org.hibernate.engine.transaction.internal.jdbc.JdbcTransactionFactory</property>
		<property name="hibernate.connection.provider_class">org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider</property>
		<property name="hibernate.c3p0.max_size">15</property>
		<property name="hibernate.c3p0.min_size">1</property>
		<property name="hibernate.c3p0.timeout">1800</property>
		<property name="hibernate.c3p0.max_statements">1800</property>
		<property name="hibernate.c3p0.idle_test_period">3000</property>
		<property name="hibernate.c3p0.acquire_increment">1</property>
		
		<property name="hibernate.generate_statistics">true</property> <!--可用 sessionFactory.getStatistics();可得到一级二级缓存信息 --> 
		<!-- 二级缓存 EnCache ,ehcache.xml  hibernate-release-4.3.1.Final\project\hibernate-ehcache\src\test\resources 复制 -->
		<property name="hibernate.cache.use_second_level_cache">true</property>
		<property name="hibernate.cache.region.factory_class">org.hibernate.cache.ehcache.EhCacheRegionFactory</property>
 		<property name="hibernate.cache.query_cache_factory">true</property> <!-- 命令中率低,HQL做key,所有结果的id集合做value,还要 query.setCacheable(true); -->
		
		
		
		<mapping resource="hibernate_hbm_school/Student.hbm.xml"/>
		<mapping class="org.hibernate.tutorial.annotations.Event"/>
		
		<class-cache usage="read-only" class="hibernate_hbm_school.Clazz"/><!-- 二级缓存 方式一,不能有blob列-->
		
	</session-factory>
</hibernate-configuration>

<!DOCTYPE hibernate-mapping PUBLIC "-//Hibernate/Hibernate Mapping DTD 3.0//EN" 
		"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd">
<hibernate-mapping package="hibernate_hbm_school">
	<!-- 如是Oracle DB ,`user` 来使用关键字 -->
	<class name="Student" table="HBM_STUDENT"><!-- 子类方式一 ,三   discriminator-value="C"--> <!-- 子类方式四   abstract="true" -->
        <id name="id" type="int">
            <column name="ID" />
            <generator class="assigned"/> <!-- assigned,native,sequence,identity,foreign,increment(select max不推荐),hilo(会建表HIBERNATE_UNIQUE_KEY) -->
        </id>
        <!-- 子类方式一,三
        <discriminator column="type" type="string"/>
        -->
        <property name="name" type="java.lang.String" index="IDX_student_name"><!-- 加索引 -->
            <column name="NAME" />
        </property>
        <property name="birthday" type="date"  column="BIRTHDAY" update="false" insert="true"/>
        <property name="age" type="int"  column="AGE" update="false" insert="false" formula="floor((current_date()-birthday)/365)"/>
        <!--floor((sysdate-birthday)/365) 可Oracle和H2,如H2函数要加(),可是sysdate()或者current_date())  
        	floor(MONTHS_BETWEEN(sysdate,birthday)/12) 只Oracle -->
			
		<!-- Blob大字段,lazy没用的,如要修改查询必须加锁LockOptions.UPGRADE才行,H2数据库不行的,Oracle OK  -->
        <property name="photo" type="blob" lazy="true"  column="PHOTO"/> 
        <many-to-one name="teacher" class="Teacher"  foreign-key="FK_HBM_STUDENT_TEACHER"  > 
        	<!-- fetch="join" 会在查子时join 父,是立即加载, fetch="select" 是延迟加载,多一条select
        	lazy="proxy"  not-null="true" -->
            <column name="TEACHER_ID" />
        </many-to-one>
		
		<!-- 默认会关联查询,如Student <one-to-one name="addr" 中加 fetch="select" lazy="proxy" ,查Student后没有stu.getAddress()也会多一个select address,即不能延迟,对foreign的主外键,cascade="save-update"为保存 -->
        <!-- 一对一  方式一 使用一列做主外键  
        <one-to-one name="addr" class="Address" cascade="save-update"  fetch="select" lazy="proxy"/>  
		-->
		<!-- 一对一 方式二  使用两列做主外键  -->
		<one-to-one name="addr" class="Address" property-ref="stu" cascade="save-update"  fetch="select" lazy="proxy" />

		<!-- cascade可取none,save-update,all,delete,save, delete-orpha -->
        <!-- 多对多 方式一 ,没有Score实体类,没有其它字段 ,如多对多是双向关联,两端都是inverse="false",就会报错,因多对多,是向表中插记录,双向关联就会插两次,就会复合主键 重复错误
        <set name="courses" table="HBM_SCORE" lazy="true"  inverse="false" cascade="save-update" > 
        	<key column="stu_id" not-null="false"/> 
			<many-to-many class="Course" column="cour_id" foreign-key="FK_HBM_SCORE_COURSE"/> 
        </set>
         -->
  		<!-- 多对多 方式二  两个一对多,有Sore实体类及映射,多score字段-->
  		<set name="scores"   inverse="true" cascade="all" lazy="true"> 
            <key>
                <column name="STUDENT_ID" />
            </key>
           <one-to-many   class="Score"  />
        </set>
		
		<!-- 放在 STUDENT 表中  -->
		<component name="fav" class="Favorite"> 
			<property name="loveAnimal" type="string" column="LOVE_ANIMAL" />
			<property name="loveColor" type="string" column="LOVE_COLOR" />
			<property name="loveFood" type="string" column="LOVE_FOOD" />
		</component>
		 <!-- 子类方式一 单表
		<subclass discriminator-value="L" name="LeaderStudent">
			<property name="jobPosition" column="JOB_POSITION" type="string" />
		</subclass>
		<subclass discriminator-value="G"  name="GoodStudent">
			<property name="joinDate" column="JOIN_DATE" type="date"/>
		</subclass>
		-->
		<!-- 子类方式二 多表 	 -->
		<joined-subclass name="LeaderStudent" table="HBM_LEADER_STU">
			<key column="teacher_id"   foreign-key="FK_HBM_LEADER_STU" />
			<property name="jobPosition" column="JOB_POSITION" type="string" />
		</joined-subclass>        
		<joined-subclass name="GoodStudent" table="HBM_GOOD_STU">
			<key column="teacher_id" foreign-key="FK_HBM_GOOD_STU" />
			<property name="joinDate"  column="JOIN_DATE" type="date"/>
		</joined-subclass>
	
		<!-- 子类方式三 混合
		<subclass discriminator-value="L" name="LeaderStudent">
			<property name="jobPosition" column="JOB_POSITION" type="string" />
		</subclass>
		<subclass discriminator-value="G" name="GoodStudent">
			<join table="HBM_GOOD_STU" >
				<key column="teacher_id" foreign-key="FK_HBM_GOOD_STU" />
				<property name="joinDate" column="JOIN_DATE" type="date" />
			</join>
		</subclass>
		 -->
		<!-- 子类方式四,父类映射可abstract="true"就没有表,但java代码中不能单独使用父类,子类表没有外键,并有父类列,父子表之前的ID不能相同,不能使用native,可使用hilo,查询使用了union all
		<union-subclass name="LeaderStudent" table="LEADER_STU">
			<property name="jobPosition" column="JOB_POSITION" type="string" />
		</union-subclass>
		<union-subclass name="GoodStudent"  table="GOOD_STU">
			<property name="joinDate" column="JOIN_DATE" type="date" />
		</union-subclass>
		-->
		
		<!-- <query可放在<class> 外面,也可在里面,但要session.getNamedQuery参数要全名,例session.getNamedQuery("hibernate_hbm_school.Student.getStudentByTeacher") -->
		<query name="getStudentByTeacher">
			<![CDATA[	from Student s where s.teacher.id=:teacher_id	]]>
		</query>
		<sql-query name="getStudentById">
			<![CDATA[	select * from student where id=:student_id		]]>
		</sql-query>
	</class>
	
	
	<class name="Teacher" table="HBM_TEACHER"> 
		<cache usage="read-only"/><!--二级缓存 方式二,不能放在有blob列上 -->
        <id name="id" type="int">
            <column name="ID" />
            <generator class="assigned" />
        </id>
        <version name="ver" /> <!-- 乐观锁 -->
        <property name="name" type="java.lang.String">
            <column name="NAME" />
        </property>
		 <!--如果多方的数据量很大,性能会很低,要使用HQL或者Criteria的 setMaxResults
		     如果teacher在student后save会有update,如在<set中没有  inverse="true"也会有update-->
        <set name="students"   inverse="true" cascade="all" lazy="true"> 
            <key>
                <column name="TEACHER_ID" />
            </key>
           <one-to-many   class="Student" />
        </set>
    </class>
	
	<class name="Address" table="HBM_ADDRESS">
        <id name="id" type="int" column="ID">
            <generator class="foreign"><!-- 使用主外键 -->
            	<param name="property">stu</param>
            </generator>
        </id>
		<!-- 查从,不会关联主,返回null,修改 fetch无效?????? -->
		<!-- 一对一  方式一, 使用一列做主外键
        <one-to-one name="stu" class="Student"  constrained="true" foreign-key="FK_HBM_ADDRESS_STUDENT" />
        -->
        <!-- 一对一 方式二, 使用两列做主外键-->
        <many-to-one name="stu" class="Student" column="student_id" unique="true" not-null="true" foreign-key="FK_HBM_ADDRESS_STUDENT"/>
    </class>
		
	<class name="Course" table="HBM_COURSE">
        <id name="id" type="int">
            <column name="ID" />
            <generator class="assigned" />
        </id>
        <property name="name" type="java.lang.String">
            <column name="NAME" />
        </property>
       <!-- 多对多 方式一 ,没有Score实体类,没有其它字段
        <set name="students" table="HBM_SCORE" inverse="true" lazy="true" cascade="save-update">
             <key column="cour_id" not-null="false"></key> 
			<many-to-many class="Student" column="stu_id" foreign-key="FK_HBM_SCORE_STUDENT" /> 
        </set>
         -->
        <!-- 多对多 方式二  两个一对多,有Sore实体类及映射,多score字段-->
  		<set name="scores"   inverse="true" cascade="all" lazy="true"> 
            <key>
                <column name="COURSE_ID" />
            </key>
           <one-to-many   class="Score"  />
        </set>
    </class>
	
	<class name="Clazz" table="HBM_CLAZZ">
        <id name="id" type="int">
            <column name="ID" />
            <generator class="assigned"/>
        </id>
        <property name="name" type="java.lang.String">
            <column name="NAME" />
        </property>
      <!-- 使用list,如使用<list-index 不能加inverse="true",如加inverse,clazz不维护desks的值,也就不更新position
        <list name="desks" cascade="all" lazy="true"> 
            <key>
                <column name="CLAZZ_ID" />
            </key>
           <list-index column="position" /> 
           <one-to-many   class="Desk" />
        </list>
        -->
       <!-- 使用bag,VO要使用List,没有顺序,可有重复
       <bag name="desks" cascade="all" inverse="true" lazy="true">  
            <key>
                <column name="CLAZZ_ID" />
            </key>
           <one-to-many   class="Desk" />
        </bag>
        -->
        <array name="desks" cascade="all"   >   <!--不能加inverse="true" <index 表示数组 ,数组不能创建代理,不能lazy -->
            <key>
                <column name="CLAZZ_ID" />
            </key>
            <index column="position"/>
           <one-to-many   class="Desk" />
        </array>
        <!-- 
        <map name="desks" cascade="all" lazy="true" inverse="true">
            <key>
                <column name="CLAZZ_ID" />
            </key>
            <map-key type="string" column="name" />  
            <one-to-many   class="Desk" />
        </map>
         -->
        <!-- 没有inverse属性 ,没有one-to-many,只可一个<element 应该可以自定义UserType ,有table,集合中的对象没有.hbm.xml-->
         <idbag name="news" cascade="all" lazy="true" table="NEWS" > 
  			 <collection-id type="long" column="ID">
        		<generator class="increment"/>
        	</collection-id>
            <key foreign-key="FK_HBM_NEWS_CLAZZ" column="CLAZZ_ID"/>
            <element type="date" column="WHEN" />
        </idbag>
    </class>
	<!-- 多对多 方式二  两个一对多,有Sore实体类及映射,多score字段-->
    <class name="Score" table="HBM_SCORE">
        <id name="id" type="int">
            <column name="ID" />
            <generator class="assigned"/>
        </id>
        <!-- COURSE_ID,STUDENT_ID不能做复合主键,因外键不能引用
        <composite-id>
        	<key-many-to-one name="student" />
        	<key-many-to-one name="course" />
        </composite-id>
      	-->
        <property name="score" type="float" column="SCORE"/>
        <many-to-one name="student" class="Student" foreign-key="FK_HBM_SCORE_STUDENT" column="STUDENT_ID" not-null="true" />
        <many-to-one name="course"  class="Course"  foreign-key="FK_HBM_SCORE_COURSE"  column="COURSE_ID" not-null="true" />
    </class>
</hibernate-mapping>

Configuration hibConfiguration = new Configuration() 
	//.addResource("hibernate_hbm_school/Student.hbm.xml")
	//.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
	.configure("hibernate_hbm_school/hibernate.cfg.xml");         
ServiceRegistry serviceRegistry = new ServiceRegistryBuilder()
										.applySettings(hibConfiguration.getProperties())
										.buildServiceRegistry();
SessionFactory sessionFactory = hibConfiguration.buildSessionFactory(serviceRegistry);//org.hibernate.service.jndi.JndiException: Error parsing JNDI name []
//SessionFactory sessionFactory = hibConfiguration.buildSessionFactory();//hibernate 3老版本
//SessionFactory是线程安全的,Session是线程不安全的
//Session session = factory.openSession();
//Session session = sessionFactory.withOptions().openSession();
//StatelessSession session=sessionFactory.openStatelessSession();//不与一级,二级缓存交互,也不做listner,直接与数据库交互
Session session = sessionFactory.getCurrentSession();//对应hibernate.current_session_context_class 为thread或jta
session.beginTransaction();

query=session.getNamedQuery("getStudentByTeacher");

//---Blob
Student stu=(Student)session.load(Student.class,2001,LockOptions.UPGRADE);//如要修改必须加锁
Blob imgBlob=stu.getPhoto();
OutputStream writeStream=imgBlob.setBinaryStream(0);
InputStream readStream =imgBlob.getBinaryStream();
InputStream input =SchoolMainApp.class.getResourceAsStream("/hibernate_hbm_school/Water.jpg");

//---二级缓存信息
Statistics state= sessionFactory.getStatistics();//对应于二级缓存的 hibernate.generate_statistics
System.out.println("hit次数:"+state.getSecondLevelCacheHitCount());
System.out.println("miss次数:"+state.getSecondLevelCacheMissCount());
System.out.println("put次数:"+state.getSecondLevelCachePutCount());
sessionFactory.getCache().evictEntity(Teacher.class, 1001);//清一,二级缓存
query.setCacheable(true);//命令中率低,HQL做key,所有结果的id集合做value,对应于 hibernate.cache.query_cache_factory

if(session.isOpen())
{
	session.getTransaction().commit(); //如使用 sessionFactory.getCurrentSession();在commit时就已经close();
	//session.close();
}

自己的VO的要求
1.要有一个无参构造方法
2.类是不能是final的 ,因要用lazy,要继承这个类的
3.集合类要用接口声明,如List,Set,使用代理

persistent持久		: session和DB中都有
transient临时		: session和DB中都没有,刚 new 的;
detached脱管,游离	: session中没有,DB中有,使用evict()后

session.evit(x)//清session缓存 

session.get()//不存在,返回null
session.load()//不存在异常,lazy来做,返回proxy,不可能是null 

//session.persistent()//cascade="save-update" 没有事务不会保存
session.save();//cascade="persist" ,JSR-220.没有事务不会保存

session.merge();//保存,对象还是脱管的(脱离Session的管理)

<id unsaved-value="-1" //saveOrUpdate来判断对象是临时,还是持久,游离,saveOrUpdate方法时

Query query=session.createQuery("from Teacher t where t.name=:t_name");//字串参数
query.setString("t_name" , "teacher1");
//如已经在一级缓存中,但HQL不使用,直接查,如过多可能内存溢出,清用session.clear(),evict(x)
Query.uniqueResult();
query.setString(0, "lisi");//Hibernate从0  开始,JDBC从1开始

SQLQuery sqlQuery=session.createSQLQuery("");

使用oracle <class name="User" table="`user`" //使用``来防oracle 关键字

query.setFirstResult(2) ; 
query.setMaxResults(20); //PreparedStatement 有void setMaxRows(int max)

Criteria ceriteria =session.createCriteria(Student.class);
ceriteria.add(Restrictions.eq("name", "lisi"));
ceriteria.setFirstResult(2);
ceriteria.setMaxResults(20);
List list=ceriteria.list();	


//动态查询
DetachedCriteria detach=DetachedCriteria.forClass(Student.class);
if(true)//构造动态参数,没有关联Session
	detach.add(Restrictions.eq("name", "lisi"));
Criteria dynamicCriteria=detach.getExecutableCriteria(session);
List allResult=dynamicCriteria.list();

query=session.createQuery("from Student where id>100");
Iterator iter=query.iterate();//如没有缓存,这个方法会生成N+1条select,1条是查所有的ID,性能很低

<property name="username" unique="true"  //唯一约束
						   not-null="true"

lazy="proxy" fetch="select" 是默认值
如 lazy="proxy" fetch="join" 则lazy值无效
-----------Interceptor
//hibConfiguration.setInterceptor(new MyInterceptor());//SessionFactory级别的
//Session session = sessionFactory.withOptions().interceptor(new MyInterceptor()).openSession();//Session级别的
public class MyInterceptor implements Interceptor{
private Set updates=new HashSet();
	private Set inserts=new HashSet();
	public boolean onFlushDirty(Object entity, Serializable id,
			Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) throws CallbackException {
		 if(entity instanceof MyAudit)
		 {
			 System.out.println("Interceptor__MyAudit__onFlushDirty");
			 updates.add(entity);
		 }
		return false;
	}
	public boolean onSave(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) throws CallbackException {
		if(entity instanceof MyAudit)
		 {
			 updates.add(entity);
			 System.out.println("Interceptor__MyAudit__onSave");
		 }
		return false;
	}
	public void postFlush(Iterator entities) throws CallbackException {
		 System.out.println("Interceptor__MyAudit__postFlush");
		Iterator iterator=updates.iterator();
		 while(iterator.hasNext())
			 iterator.next();//做存DB的update记录
		 iterator=inserts.iterator();
		 while(iterator.hasNext())
			 iterator.next();//做存DB的insert记录
	}
	public String onPrepareStatement(String sql) {
		 System.out.println("Interceptor__MyAudit__onPrepareStatement");
		return sql;
	}
}
-----------user type
<property name="addr" type="hibernate_hbm_school.type.MyAddrType">
	<column name="PROVINCE"  />
	<column name="CITY" />
	<column name="STREE" />
	<column name="NO" />
</property>
 //implements CompositeUserType{//可HQL
public class MyAddrType implements UserType{

	public Class returnedClass() {
		return MyAddr.class;
	}
	private static final int []SQL_TYPES={Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR};//与.hbm配置顺序
	public int[] sqlTypes() {//对应returnedClass返回类的类型
		return SQL_TYPES;
	}
	public Object deepCopy(Object obj) throws HibernateException {
		return obj;//因没有集合类
	}
	public boolean equals(Object one, Object two) throws HibernateException {
		return one.equals(two);//对象重写equals方法
	}
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
		if(rs.wasNull())
			return null;
		MyAddr addr=new MyAddr();
		addr.setProvince(rs.getString(names[0]));//与.hbm配置顺序
		addr.setCity(rs.getString(names[1]));
		addr.setStreet(rs.getString(names[2]));
		addr.setNo(rs.getString(names[3]));
		return addr;
	}
	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
		if(value!=null)
		{
			MyAddr addr=(MyAddr)value;
			st.setString(index, addr.getProvince());//与.hbm配置顺序
			st.setString(index+1, addr.getCity());
			st.setString(index+2, addr.getStreet());
			st.setString(index+3, addr.getNo());
		}else
		{
			st.setNull(index, Types.VARCHAR);
			st.setNull(index+1,Types.VARCHAR);
			st.setNull(index+2,Types.VARCHAR);
			st.setNull(index+3,Types.VARCHAR);
		}
	}
}
-----------Listener






//Employee自己的字段depart_id值 是department的主键
<class name="Employee"
	<many-to-one   name="department" column="depart_id" //column  可省
	 property-ref="dept_id" //dept_id是 Department的非主键， 默认是主键
	not-null="true" //就必须先存Department,再存Employee ,也必须setDepartment(xx);
	/> 

Hibernate.initialize(emp.getDepartment());//就可以在session.close();后使用emp.getDepartment()
//和emp.getDepartment()一样,但别人看了这段代码是像是没有意义的

Department端
<set name="employees" lazy="true"> inverse="true" //如dep.setEmployees(all);多几次(set的大小)  update两句
	<key column="department_id"></key>
	<one-to-many class="Employee" />
</set>.

Employee端
<many-to-one not-null="false" name="department" class="Department" fetch="join" >
	<column name="department_ID" />
</many-to-one>

//主外键, 能过主对象来连子对象  会使用一条 left outer join ,子对像连父两条SQL
<id name="id">
	<generator class="foreign"> //另个可是 native
		<param name="property">person</param> //自己的属性person的主键
	</generator>
</id>
<one-to-one name="person"  constrained="true" /> 数据库加约束
	



//或者用单个外键,子对像连父三条SQL
	IDCard外中<many-to-one name="person" column="person_id" unique="true"/>
	Person主中 <one-to-one name="idcard"   property-ref="person"/>

//多对多 ,
Student.hbm.xml
	 <set name="courses" table="score" inverse="false" lazy="true" cascade="save-update">
           <key column="stu_id" not-null="false"></key> 
			<many-to-many class="org.zhaojin.school.Course" column="cour_id" ></many-to-many> 
     </set>	
Course.hbm.xml
	 <set name="students" table="score" inverse="true" lazy="true" cascade="save-update">
             <key column="cour_id" not-null="false"></key> 
			<many-to-many class="org.zhaojin.school.Student" column="stu_id" ></many-to-many> 
      </set>



--组件
<component name="" class=""> 

自定义一个类何存放数据中 ,如java类的firstname,lastname 放在数据库的一个字段
	UserType 接口,CompositeUseType ????????????


 
<set>
	<composite-element class=""></composite-element>
		<element></element>
	




List,Set,Map 都是线程不安全的，Hashtable是安全的,用 Collections.synchronize....()

 <list name="employees" >
		 	<key column="department_id"></key>
		 	<list-index column="`index`"></list-index> //记录顺序,取成List时用

数组用<array 也有顺序和<list　一样的
	
<bag 可以不记录并使用List类 ,不能是Set

<map name="employees">
			<key column="department_id"></key>
			<map-key type="string" column="`key`"></map-key>  //如key 是其它类型???????????????????????????????



放数据库中是HashSet 出来不是HashSet是Persistent实现了Set，lazy有关的，所以属性定义只能接口Set



一对多的<set name="employees"  cascade="save-update"	//(save-update,delete或者all)保存/修改　department时也把employees保存/修改
				//先insert department,再insert employee,再update employee set dep_id=?
	
			inverse="true" 　还要先保存主，可在从配置not-null="true" ,就没有update了
			不会再dep.setEmployees(all);这句不生成update,
			如inverse="true" 用在<list><array>中不会管<list-index 
	
	//orphan  孤儿,  

多对多　只可一端配置inverse="true"　，就可以在Java中双向关联了

-----继承
1.单表
<class name="Employee" descriminator-value="0">		//值默认是 包.类 名
	<discriminator column="type" type="int">		//默认是string类型
	<subclass name="Skiller" descriminator-value="1">
	//以下属性必须可以为 null,只对较少字段，不经常改表结构

2.每子类一张表，多表的主外键
<joined-subclass name="org.zhaojin.vo.hbm.sub.Skiller"  table="skiller_hbm" >
	<key column="emp_id"></key>
	.....
</joined-subclass>
	//查询父类　会关联所有的子表，效率低


3.单表　和　每子类一张表　结合使用
<subclass name="org.zhaojin.vo.hbm.sub.Sale" discriminator-value="1">
	<join table="sale_hbm">
		<key column="emp_id"></key>
		...
	//如报type列找到，是因为employee表不能删除， 因有sales外键，可以drop database,

4.子类表含父类表字段,
	<union-subclass name="org.zhaojin.vo.hbm.sub.Skiller" table="skiller_hbm">
		<property name="professional"></property>

	//多个表的id不能相同,父类(多态)给一个id,从多张表中查只能返回一个,主键生成方式不能为native,可以为hilo,多一张hibernate_unique_key表,next_hi存主键
		
所以不要用多态来查

---
load方法,是lazy="proxy"的.类不能是final,因能proxy来继承
Hibernate.initialize(...);

默认hibernate 对所有的关联都使用延迟加载,即lazy="proxy", 但只一对一查主时默认是关联使用一条left join
	一对一的从中 <one-to-one fetch="join"  constrained="true" //加数据库约束
		查从对象时,则不会关联
		fetch="join"就不会lazy,只一条select,fetch 默认是"select" ,
					

一对一 查主,因不知道是否有子,也就不能给proxy,只能查DB
一对多 可以lazy,可以没有子


可以对属性进lazy ,如CLOB,BLOB大字段

---
一级Session 缓存范围小,其它Session拿不到,很快就关闭,不会管理缓存大小

session.evict(xx);//清session的缓存对象
session.clear() ;

配置二级缓存
	以下可在 包中etc/hibernate.properties中复制
	<property name="hibernate.cache.use_second_level_cache">true</property> //默认是true
	<property name="hibernate.cache.region.factory_class">org.hibernate.cache.ehcache.EhCacheRegionFactory</property>
	
	<class-cache class="org.zhaojin.vo.hbm.Student" usage="read-only"/>   //二级缓存什么类
														"read-write",
														"transactional"//OScache没有这种功能

	包中etc/oscache.properties 配置文件放在和hiberante.cfg.xml相同的classpath中
	可以缓存在硬盘的目录中 cache.path=

也可以配置在映射文件 <class>
						<cache usage="read-only"/>

跨多Session的缓存

<property name="hibernate.generate_statistics">true</property>//打开统计
getSessionFactory().getStatistics().toString();//可以看很多信息

Statistics s=factory.getStatistics();

s.getSecondLevelCachePutCount();
s.getSecondLevelCacheHitCount();//命中数
s.getSecondLevelCacheMissCount();//第一次没有就是miss

//save时主键用native不会放缓存中，hilo的save会放缓冲中



getSessionFactory().evict(User.class);//清二级缓存，可传id

默认session.createQuery不能二级缓存  ,可打开
	<property name="hibernate.cache.use_query_cache">true</property>
		query.setCacheable(true);
		//如果where name=? 是不行的，多条select,除非传入相同的值

	二级缓存也叫　SessionFactory的缓存



多机器负载，如一端update缓存，其它机器的缓存无效，要通知其它机器，建议不要使用修改缓存


JTATransactionManager  跨多数据库事务，J2EE容器才行
javax.transaction.UserTransaction tx=context.lookup("jndiname")
tx.begin();
//多个数据库的session操作

tx.commit();
catch()
{
	tx.rollback();
}

ThreadLocal 变量的作用域在一个线程内有效，里放Session 就可以跨多个dao做事务,Filter 中做事务　
	get();
	set(xxx);
	//session一级缓存变大，事务加锁
sessionFactory.getCurrentSession();//要配置current_session_context_class=thread   (jta)没有?????????????????????


乐观锁，select时读一个version ，改时在where 中加 version=
	<class>
		<version name="ver"　/>  //VO类中也必须有int 型ver的字段才行
		<timestamp name="xxx"/>// 不建议用时间，可能每毫秒执行两次
	
悲观锁，在select　for update某一条,就加锁
	 session.load(User.class, new Long(1), LockMode.UPGRADE);//hibernate4是LockOptions.UPGRADE
	 Query setLockMode(String alias, LockMode lockMode)  //FROM clause.
	 
	  b.使用select...for update语句查询时，锁定级别为LockMode.Update；
	  c.使用select...for update nowait语句查询时，锁定级别为LockMode.Update_NOWAIT；

数据源如c3p0,proxool,JNDI容器提供的,可从hibernate.properties文件中复制
proxool还要一个配置文件

hibernate.format_sql true
hibernate.default_schema test
hibernate.default_catalog test
hibernate.max_fetch_depth 1
hibernate.default_batch_fetch_size 8
 batch 最好关闭 use_second_level_cache , session.flush(); session.clear();
 

<class name="" multable="true"  //类是不能修改 
		dynamic-update//如果属性没有修改，不会update
		where="enable=true"


id生成器  
1.increment　hibernate自己加
2.identity
3.sequence
4.hilo 高位从数据库得,低位hibernate 加,
5.foreign
6.native
7.assigned 手工指定的

<property update="true"
		  unique="true" 加建表唯一约束
		  not-null="true"
		 index="index_name"
		 unique_key="xx" //与其它列的唯一


一个Session只能放在一个线程中使用,要立即释放,不能放在httpSession中

一级缓存可能会内存溢出,session.flush,session.clear;

StatelessSession 不会和一级缓存,和二级缓存,和JDBC的connection类似了
			sessionFactory.openStatelessSession();
 

query.executeUpdate()//会清一级,二级缓存,但速度快,不会更新version的版本(update ....)


session.createQuery("From xx where id=?") //默认不会查缓存 query.setCacheable(true);,要用get()

  


<generator class="hilo">
	<param name="table" >hi_vale</param>
	<param name="column" >nextval</param>
	<param name="max_lo" >1000</param>
</generator>


一个表有两个主键，类要实现Serializable接口；
要复写equals 和 hashcode要用第三方工具comming-lang-1.0.jar
EqualsBuiler 对象的append(..).appned(..).isEquals()方法 即两个类的每个属性是相等的

HashCodeBuilder.append(属性).append(...).toHashCode()  myeclipse 会自动生成

<composite-id >
	<key-property name="" cloumn='' type='String'/>
	<key-property name="" cloumn='' type='String'/>
</composite-id>
<property name="" type="" column="" length=""/>


把多个主键设一个类在这个类中重写方法equals   和hasCode
<composite-id name='' class=''> 这是新加的
	<key-property name="" cloumn='' type='String'/>
	<key-property name="" cloumn='' type='String'/>
</composite-id>


Restrictions
	Criterion sqlRestriction(String sql)  
		sql="{alias}.id=?"   (hibernate 的Type) Hibbernate.YES_NO


session.createCriteria(Department.class).createCriteria("employees")
																		或用.create Alias("employees","e")以后要加e.xxxx;没有创建对象
																		


Criterian setResultTransformer (ResultTransformer)
				最后加CriteriaSpecification.ALIAS_TO_ENTITY_MAP（返回一个ResultTransformer）
				返回的Map 的key 是前面定义的别名，（CriteriaSpecifiaction.ROOT_ALIAS）可得到没有别名的根对象
				
criterian包下的Property.forName("属性名").eq("值")返回一个 Criterian
																				.in(Collection或数组)
																				.desc()排序
Example.create(实体)把实体中的属性作为条件。Example excludeZeroes()  .ignoreCase（）。excludeNone不排除0和null



criteria.setProjection()
			Projections. projectionList().add(Projections.avg("")).add(Projections.groupProerty("")) ;Projections.property("propertyName")
			返回一Object[]
DetachedCriteria.forClass(Class clazz) 
		.add(Criteran)
		.add(Property.forName("").gt())
		
		Criteria getExecutableCriteria(Session session)  
		作子查询Property 的方法中Criterion gt(DetachedCriteria subselect) 如果DetachedCriteria 中有多个列，那么只取最后的一列
		
		
HQL　连接查询和条件用with ,相当于SQL 的on 或where 
		frome Object  如接查询返回一个Object[]
		不可select *  ,可类.集合属性.xxx
		
		select new List(属性名,..,)from ..
		返回的每一行是一个list 都是　String　类型的,也其它类型是构造的参数，注意参数类型
		
	每个字段加别名，可以new Map() as 必须有,则key 是别名
	
		在<set>中fetch="join"，用左外连接把集合与类一起查询出来

---Hibernate 4

Configuration hibConfiguration = new Configuration() //.addResource("test/vo/table.hbm.xml")
									.configure("hibernate.cfg.xml");       
ServiceRegistry serviceRegistry = new ServiceRegistryBuilder()
										.applySettings(hibConfiguration.getProperties())
										.buildServiceRegistry();
SessionFactory sessionFactory = hibConfiguration.buildSessionFactory(serviceRegistry);
//Session session = sessionFactory.openSession();
Session session = sessionFactory.withOptions().openSession();
		
session.beginTransaction();
...
session.getTransaction().commit();
session.close();

Spring配置时BaseDAO注入sessionFactory,再用getSessionFactory().getCurrentSession();



------------------------------Annotation-------------------------------------------
  //单个类的属性一定不能new，只有Set才new ,@的配置要不全在getXX前，要不全在属性前


org.hibernate.cfg.AnnotationConfiguration类。该类在hibernate-annotation.jar，

 <hibernate-configuration>
          <session-factory>
            <mapping package="test.animals"/>
            <mapping class="test.Flight"/>
			<mapping resource="test/animals/orm.xml"/>



 new AnnotationConfiguration()
                    .addPackage("test.animals") 
                    .addAnnotatedClass(Flight.class)


@Entity
@Table(name="USER_T")
class ....... 类前

@Id
@GeneratedValue(strategy=GenerationType.AUTO)  //AUTO   数据库本身生成 ,TABLE数据库生成表 
getId()  方法前


@Version //版本控制

复合主键类一
@IdClass(FootballerPk.class) //这个类有getFirstname(),getLastname(),hashCode,equals的实现
public class Footballer {
@Id 
public String getLastname() {..}
@Id 
public String getFirstname() {..}
}
复合主键二
@Embeddable
public class FootballerPk {}

public class Footballer {
    @EmbeddedId
	public FootballerPk id;
}


@Table(name="tbl_sky",
		    uniqueConstraints = {@UniqueConstraint(columnNames={"month", "day"})}
		)

@Basic(fetch = FetchType.LAZY)  //默认是EAGER
@Enumerated(STRING)  //放在 enum 定义类型前，默认是序列值
@Transient           //不被保存在数据库中
@Temporal(TemporalType.TIMESTAMP) //时间的 ，在数据库只要DATA则是TemporalType.DATA,日期,时间都要就是TIMESTAMP
@Lob  根据返回类型是 Blob Clob


@Column(updatable = false,insertable=true, name = "flight_name", nullable = false, length=50)

//==<component的映射OK
1.@Embeddable 放在类前,表示其它类的属性是这种类型的
2.@Embedded 放在方法,属性前,对该类的对象属性,定入覆盖,映射
@AttributeOverrides( {
            @AttributeOverride(name="iso2", column = @Column(name="bornIso2") ),
            @AttributeOverride(name="name", column = @Column(name="bornCountryName") )
    } )



@Entity
@javax.persistence.SequenceGenerator(
    name="SEQ_STORE",
    sequenceName="my_sequence"
)
public class Store implements Serializable {
    private Long id;

    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_STORE")
    public Long getId() { return id; }
}
         











父类中
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="planetype",
    discriminatorType=DiscriminatorType.STRING
)
@DiscriminatorValue("Plane")
public class Plane { ... }

子类中
@Entity
@DiscriminatorValue("A320")
public class A320 extends Plane { ... }
    
	    

你可以通过 @AttributeOverride注解覆盖实体父类中的定义的列. 这个注解只能在继承层次结构的顶端使用.




@Transient 不会持久化
 

一对一  (单向的,双向的)
 @Entity
public class Body {
    @Id
    public Long getId() { return id; }

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    public Heart getHeart() {
        return heart;
    }
   
}


 一对一// OK  
@Entity
public class Customer implements Serializable {
    @OneToOne(cascade = CascadeType.ALL)//ManyToOne也可
	@JoinColumn(name="passport_fk")
    )
	public Passport getPassport()
	{
     
    }
}
@Entity
public class Passport implements Serializable {
    @OneToOne(mappedBy = "passport")
    public Customer getOwner() {}
   
}
 



/////Software中包括了以codeName作为 key和以Version作为值的Map.

@Entity
public class Software {
    @OneToMany(mappedBy="software")
    @MapKey(name="codeName")
    public Map<String, Version> getVersions() {
        return versions;
    }
...
}

@Entity
@Table(name="tbl_version")
public class Version {
    public String getCodeName() {...}

    @ManyToOne
    public Software getSoftware() { ... }
...
}







//===一对多 OK
class Student
{
	@ManyToOne(cascade ={ CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinColumn(name = "class_id_FK" ,nullable=false)
	public Clazz getClazz()
	{}
}
class Clazz
{	@OneToMany( mappedBy="clazz",fetch=FetchType.LAZY,cascade=CascadeType.PERSIST)
	public Set<Student> getStudent()
	{}
}	
//==



//多对多 OK
@Entity
public class Employer implements Serializable {
    @ManyToMany(
        targetEntity=org.hibernate.test.metadata.manytomany.Employee.class,
        cascade={CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable( //可无，可一端有，也可两端全有
        name="EMPLOYER_EMPLOYEE",
        joinColumns={@JoinColumn(name="EMPER_ID")},
        inverseJoinColumns={@JoinColumn(name="EMPEE_ID")}
    )
    public Collection getEmployees() {
        return employees;
    }
    ...
}
@Entity
public class Employee implements Serializable {
    @ManyToMany(
        cascade={CascadeType.PERSIST, CascadeType.MERGE},
        mappedBy="employees"　//只可任何的一端有mappedBy，和targetEntity有关的,
							//也可没有mappedBy(inverse="true),但Java只能有一级关系
        targetEntity=Employer.class
    )
    public Collection getEmployers() {
        return employers;
    }
}









getGenericSuperclass()方法首先会判断是否有泛型信息，有那么返回泛型的Type，没有则返回Class
Class<T> persistentClass=(Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

this.entityType = getClass().getClassLoader().loadClass(entityType);













要新的jar包
@PersistenceContext   //Spring集成时
public   void setEntityManager ( EntityManager em ) {}
    




org.springframework.transaction.annotation.Transactional ;
类前加 @Transactional 表示做事务



===============JPA=========================
JPA 没有加index的功能,没有缓存

都在javax.persitence. 包下

META-INF/persitence.xml 文件
<persistence xmlns="http://java.sun.com/xml/ns/persistence"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd"
        version="2.0">
	<persistence-unit name="MyJPA" transaction-type="RESOURCE_LOCAL"> <!-- JTA,RESOURCE_LOCAL -->
	    <provider>org.hibernate.ejb.HibernatePersistence</provider>
	    <!-- 
	    <jta-data-source>jndi/myjta</jta-data-source>
	    <mapping-file>META-INF/orm.xml</mapping-file>
	    <exclude-unlisted-classes/>
	   -->
	    <class>jpa.one2one.IdCard</class>
	    <class>jpa.one2one.Person</class>
	    <class>jpa.single.UserBean</class>
		<properties>
			<!-- Hibernate 的配置不能有 value属性,而JPA必须有value属性-->
			<!-- 
 			<property name="hibernate.dialect" value="org.hibernate.dialect.Oracle10gDialect"/>
 			<property name="hibernate.connection.driver_class" value="oracle.jdbc.driver.OracleDriver" />
			<property name="hibernate.connection.url" value="jdbc:oracle:thin:@localhost:1521:XE" />
			<property name="hibernate.connection.username" value="hr" />
 			<property name="hibernate.connection.password" value="hr" />
			 -->
			<property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/>
			<property name="javax.persistence.jdbc.driver" value="org.h2.Driver" />
            <property name="javax.persistence.jdbc.url" value="jdbc:h2:tcp://localhost/~/test" />
            <property name="javax.persistence.jdbc.user" value="sa" />
            <property name="javax.persistence.jdbc.password" value="" />
			
			<property name="hibernate.show_sql" value="true"/>
			<property name="hibernate.hbm2ddl.auto" value="update"/> <!-- update, create, create-drop ,validate-->
			<property name="hibernate.format_sql" value="true"/>
			<property name="hibernate.max_fetch_depth" value="3" />	
			<property name="hibernate.use_sql_comments" value="true"/>
			<!--  
			<property name="hibernate.archive.autodetection" value="class, hbm" />
			默认是class, hbm,如项目中有.hbm.xml日志中显示Detect class: true; detect hbm: true ,可修改为false
			-->
			<property name="hibernate.archive.autodetection" value="false" />
			<!--
			<property name="hibernate.ejb.cfgfile" value="hibernate.cfg.xml" /> 
				日志文件中是有的,最好打开,并<hibernate-mapping auto-import="false">,
			-->
			<property name="hibernate.transaction.factory_class" value="org.hibernate.engine.transaction.internal.jdbc.JdbcTransactionFactory"/>
			<property name="hibernate.connection.provider_class" value="org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider"/>
			<property name="hibernate.c3p0.max_size" value="15"/>
			<property name="hibernate.c3p0.min_size" value="1"/>
			<property name="hibernate.c3p0.timeout" value="1800"/>
			<property name="hibernate.c3p0.max_statements" value="1800"/>
			<property name="hibernate.c3p0.idle_test_period" value="3000"/>
			<property name="hibernate.c3p0.acquire_increment" value="1"/>
 		</properties>
	</persistence-unit>
</persistence>

//如没有配置<provider>会查所有classpath下(在hibernate-entitymanger-4.1.jar中)的META-INF/services/javaxpersitence.spi.PersistenceProvider文件,有写Provider实现类,可复制
		
Map configOverrides = new HashMap();
//configOverrides.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect ");
configOverrides.put("hibernate.ejb.cfgfile", "hibernate.cfg.xml");//日志文件 中有hibernate.cfg.xml

EntityManagerFactory factory=persitence.createEntityManagerFactory("MyJPA",configOverrides)//MyJPA是persitence.xml中配置的
EntityManager em=factory.createEntityManager();
EntityTransaction transaction=em.getTrasaction();
trasaction.begin();

em.persitence(user);//保存对象
em.find(UserBean.class,1);
em.merge(user);
em.clear();
em.refresh(user);//重新从DB取新记录,CascadeType.REFRESH


Student lisi= em.find(Student.class, 1); //相当于Hibernate的get
System.out.println("使用JAP find的学生:"+lisi.getName());

Student wang= em.getReference(Student.class, 2);//相当于Hibernate的load 
System.out.println("使用JAP getReference的学生:"+wang.getName());
		
//Query query=em.createQuery("from Student s where s.name=?",Student.class);//JPSqL
//query.setParameter(1, "lisi");//JPA都是setParameter,JPA和JDBC一样是1开始的,而Hibernate是0开始的,也可在?后加1,?1表示这个位置就是1

Query query=em.createQuery("from Student s where s.name=:myname",Student.class);
query.setParameter("myname", "lisi");
query.setFirstResult(0);
query.setMaxResults(20);
List<Student> list=query.getResultList();//JPA是getResultList

Query sqlQuery=em.createNativeQuery("select count(*) from JPA_STUDENT s,JPA_TEACHER t where s.teacher_id=t.id and t.name=:t_name ");
sqlQuery.setParameter("t_name" , "teacher1");
Object count=sqlQuery.getSingleResult();//JAP是getSingleResult
sqlQuery.setFirstResult(2);
sqlQuery.setMaxResults(20);
System.out.println("JPQL Query查询teacher1老师的学生数:"+count);

sqlQuery=em.createNativeQuery("select  * from JPA_TEACHER ",Teacher.class);
list=sqlQuery.getResultList();
for (Object o :list)
{
	System.out.println("JPQL Query 老师有:"+((Teacher)o).getName());//返回Object[]
}

//批量更新可使用 update 
query=em.createQuery("update Student s set s.name=:my_name where s.id=?1");
query.setParameter("my_name" , "学生XYZ");
query.setParameter(1 ,1);//可以混合使用?1和:标签做参数 
query.executeUpdate();

//--lock
Student lisi=(Student)em.find(Student.class, 2);
em.refresh(lisi, LockModeType.PESSIMISTIC_WRITE);//悲观锁,会加select ... for upate
lisi.setXxxx
//---
em.clear();
Teacher teacher1001=(Teacher)em.find(Teacher.class, 1); //乐观锁 ,对应有@version ,会加ver=?
teacher1001.setXxx

trasaction.commit();
em.close();



import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
@Entity
@Table(name = "T_USER")
@GenericGenerator(name="myuuid",strategy="uuid")//只有Hibernate的方式
public class UserBean
{
	@Id
	@GeneratedValue(generator="myuuid")//只有Hibernate的方式
	private String id;//为uuid的String类型
	
	@Column(name = "name")
	@Index(name="IDX_user_name") //只有Hibernate的方式
	private String name;
}

//=========一对一
@Entity
@Table(name = "T_PERSON")
public class Person
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)//可以放在getXxx前,也可放在属性前 
	public Integer getId()
	{
		return id;
	}
	@Column(length = 10, nullable = false,name="FULL_NAME")
	public String getName()
	{
		return name;
	}
	
	@OneToOne(mappedBy = "person",cascade = CascadeType.ALL)//有mappedBy是主方
	@JoinColumn(name = "IDCARD_ID")
	public IdCard getIdCard()
	{
		return idCard;
	}
}
//---
@Entity
@Table(name = "T_IDCARD")
public class IdCard
{
	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH },
			  optional = false, fetch = FetchType.EAGER)//从方来加 optional = false
    @ForeignKey(name="FK_IDCARD_PERSON")//只Hibernate有
	@JoinColumn(name = "PERSON_ID")
	public Person getPerson()
	{
		return person;
	}

}


//=================================school_jpa包
//---
@NamedQuery(name="getStudentByTeacher",query="from Student s where s.teacher.id=:teacher_id")

@Entity
@Table(name = "JPA_STUDENT")

//方式一SINGLE_TABLE
//@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name="type",discriminatorType=DiscriminatorType.CHAR) //对于SINGLE_TABLE
//@DiscriminatorValue("C")//对于SINGLE_TABLE 

//方式二 JOINED
@Inheritance(strategy=InheritanceType.JOINED)

public class Student implements Serializable 
{

	@Id
	@GeneratedValue
    private int id;
	 
	@Index(name="IDX_JPA_Student_name")//只Hibernate有
	private String name;
	    
	 
    @Enumerated(EnumType.STRING)//ORDINAL 是枚举索引值,STRING是枚举名字
    @Column(length=6,nullable=false)//enum应该不能为空的
    private Gender gender=Gender.MALE;//默认值 ,是enum类型的
    
    @Temporal(TemporalType.DATE)//日期格式
    private Date birthday;
    
    @Formula("floor((current_date()-birthday)/365)")//只Hibernate有,如birthday为null,会报错,Null给age,如是Oracle使用nvl(floor((current_date-birthday)/365),0)
	//floor((sysdate-birthday)/365) 可Oracle和H2,如H2函数要加(),可是sysdate()或者current_date())  ,floor(MONTHS_BETWEEN(sysdate,birthday)/12) 只Oracle
    private int age;
    
	 //@Embedded 可以不加
    private Favorite fav;
	
    @ManyToOne(cascade={CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH},optional=true)//从方optional=true表示一个学生可以没有老师
    @ForeignKey(name="FK_JPA_STUDENT_TEACHER")//只Hibernate有
    @JoinColumn(name="TEACHER_ID")
    private  Teacher teacher;
    
    @OneToOne(mappedBy="stu",fetch=FetchType.LAZY,cascade={CascadeType.PERSIST,CascadeType.MERGE})//有mappedBy是主方
    private Address addr;
    
    //--------------多对多 方式一 ,没有Score实体类,没有其它字段 ,
//    @ManyToMany(fetch=FetchType.LAZY,targetEntity=Course.class,cascade={CascadeType.PERSIST,CascadeType.MERGE})
//    @ForeignKey(name="FK_JPA_score_student")
//    @JoinTable(name="JPA_SCORE",joinColumns=@JoinColumn(name="student_id"),
//    				 	 inverseJoinColumns=@JoinColumn(name="course_id")  )
    @Transient //不与数据库交互
    private Set<Course> courses;
    //--------------多对多 方式二  两个一对多,有Sore实体类及映射,多score字段
    @OneToMany(mappedBy="student",cascade={CascadeType.PERSIST,CascadeType.MERGE},targetEntity=Score.class)
    private Set<Score>scores;
    //--------------
    
    @Lob
    @Basic(fetch=FetchType.LAZY)//大的字段延迟加载
    private byte[] photo;
   
    @Lob
    @Basic(fetch=FetchType.LAZY)
    private String remark;
}
//---
@Embeddable //类似于<component>
public class Favorite 
{
}
//---
@Entity

//方式一SINGLE_TABLE
//@DiscriminatorValue("G")//相当于<subclass discriminator-value="G"

//方式二 JOINED
@PrimaryKeyJoinColumn(name="student_id")//相当于<joined-subclass
@Table(name="JPA_GOOD_STUDENT")

public class GoodStudent extends Student {
}
//---
@Entity
//方式一SINGLE_TABLE
//@DiscriminatorValue("L")

//方式二 JOINED
@PrimaryKeyJoinColumn(name="student_id")//相当于<joined-subclass
@Table(name="JPA_LEADER_STUDENT")
public class LeaderStudent extends Student {
}
//---
@Entity
@Table(name="JPA_TEACHER")
public class Teacher implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
	
    @OneToMany(cascade={CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH} ,
    			fetch=FetchType.LAZY,mappedBy="teacher",targetEntity=Student.class)//有mappedBy是主方
    private Set<Student> students;
    
    @Version
    private int ver;
}
//---
@Entity
@Table(name="JPA_COURSE")
public class Course implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    private String name;
    
	//-------------- 多对多 方式一 ,没有Score实体类,没有其它字段 ,
    //@ManyToMany(mappedBy="courses",fetch=FetchType.LAZY,targetEntity=Student.class,cascade={CascadeType.PERSIST,CascadeType.MERGE})
    //@ForeignKey(name="FK_JPA_score_course")
    @Transient //不与数据库交互
    private Set<Student>students;
    //--------------多对多 方式二  两个一对多,有Sore实体类及映射,多score字段
    @OneToMany(mappedBy="course",cascade={CascadeType.PERSIST,CascadeType.MERGE},targetEntity=Score.class)
    private Set<Score>scores;
}

//---
@Entity
@Table(name="JPA_ADRESS")
public class Address 
{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	@OneToOne(cascade={CascadeType.MERGE,CascadeType.PERSIST},optional=true,fetch=FetchType.EAGER)//address必须要有对应的student
	@JoinColumn(name="stu_id",nullable=false)//和optional=true类似
	@ForeignKey(name="FK_JPA_ADDRESS_STUDENT")//在从方加外键
	private Student stu;
}


//--------------多对多 方式二  两个一对多,有Sore实体类及映射,多score字段
@Entity
@Table(name="JPA_SCORE")
public class Score implements Serializable 
{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@ManyToOne(optional=false,cascade={CascadeType.PERSIST,CascadeType.MERGE})
	@ForeignKey(name="FK_JPA_score_student")
	private Student student;
	
	@ManyToOne(optional=false,cascade={CascadeType.PERSIST,CascadeType.MERGE})
	@ForeignKey(name="FK_JPA_score_course")
	private Course course;
	
	private float score;
}	

抽象类前加
@MappedSuperclass


em.createNamedQuery("HistoryEntry.findByEntity")//"JPSQL"类似HQL，对应于 @NamedQuery(name = "HistoryEntry.findByEntity", query = "
	.setParameter("xx",yy);

JPA  没有加索引 功能
	没有缓存

Hibernate 有 @Index(name="I_xx")

@Column(nullable=false,name="")

hibernate 包 @GenericGenerator(name="myuuid" strategy="uuid")


@Inheritance(strategy=Inheritancetype.SINGLE_TABLE)   //多类在一表
@DiscriminatorColumn(name="type");
@DiscriminatorValue("o");

@ManyToOne()   //表示本类和　get方法前的关系
@JoinCOlumn(name="dep_id")//可以不写的JPA默认是属性名_id ,而hibernate 只是属性名


@OneToMany (casecade={CaseCadeType.MERGE,CascadeType.PERSIT},fetch=FetchType.LAZY,mappedBy="depart")// 怎么知道集合中,每个类的哪个属性是我呢
	如类不是用<xx>范型,还要加 targetEntity=M.class



hibernate @Cache

===============上 JPA=========================

-------------lucene 集成
@Entity
@Indexed(index="indexes/essays")
public class XXXX
想为所有的Lucene索引定义一个根目录，你可以在配置文件中用属性hibernate.lucene.index_dir进行配置。
hibernate.lucene.analyzer属性进行配置的。如果没有定义，则把org.apache.lucene.analysis.standard.StandardAnalyzer作为缺省。

必须在你的实体类的标志属性上指定@Keyword(id=true)

------------------Hibernate Search

------------------Hibernate BeanValidation



=====================Middlegen-Hibernate-r5 的使用

ojdbc6.jar 放在Middlegen-Hibernate-r5\lib下
Middlegen-Hibernate-r5\config\database\oracle.xml 配置
	driver(./lib/ojdbc6.jar),
	schema(hr),username(hr),
	catalog为"",
	url,password等,可以11g

build.xml引用的数据库配置
找到
<!DOCTYPE project [
    <!ENTITY database SYSTEM "file:./config/database/hsqldb.xml">
]>
将其改为：
<!DOCTYPE project [
    <!ENTITY database SYSTEM "file:./config/database/oracle.xml">
]>


<hibernate
	destination="${build.gen-src.dir}"
	package="${name}.hibernate"      表示生成的java package包名,修改它
 
 
.hbm文件的输出目录<property name="build.gen-src.dir"              value="${build.dir}/gen-src"/>
	默认是在Middlegen-Hibernate-r5\build\gen-src
 
在Middlegen-Hibernate-r5目录执行  ant出现界面,
读用户下的所有表,可以加 <table>,找注释所在的位置加
<table name="employees"/>
<table name="departments"/>

----hbm文件生成到Java
复制commons-lang.jar
hibernate2.jar
hibernate-tools.jar
jdom.jar到Middlegen-Hibernate-r5\middlegen-lib下

ant hbm2java 会在Middlegen-Hibernate-r5\build\gen-src下也生成Java文件
 
-----middlegenide_1.3.3 是eclipse插件  
links安装无效,只能复制到eclipse目录中,eclispe-3.7.2也是有效的
 rc文件夹上面右键，选择New->Other->MiddleGen->MiddleGen Build file  选择JDBC jar,
要选择正常的driver
Oracle只填写catalog,也可catalog,schema都不写,directory应该是src,包名写自己的,

load table按钮,选择表后,只能点finish,有时会直接弹出swing界面,会在包下建立src/lib目录放入eclipse的classpath

如没有弹出swing界面,要fresh后会看到生成了middlegen-build.xml
对Oracle数据库要修改文件 database.schema 为用户名 (HR),右击middlegen-build.xml运行->ant build,
也会自动生成.java,默认是把src下的所有的.hbm.xml生成Java,如果已经有的请注意单独备份!!!,
或者修改-build.xml文件 中    <include name="**/*.hbm.xml" />

会在src目录下生成hibernate.cfg.xml, 但"-//Hibernate/Hibernate Configuration DTD//EN"应该修改为
									  "-//Hibernate/Hibernate Configuration DTD 3.0//EN"

会用到 import org.apache.commons.lang.builder.ToStringBuilder;

多对多生成时,没有中间表
-------
jBoss Tools可以从.java生成到.hbm,但要注意修改(一对多时,<set>中关联列使用外键,而不是主键)
jBoss Tools如有多对多生成时,没有中间表
=====================


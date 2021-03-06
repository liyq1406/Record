
 
==================================web.xml
<context-param>
	<param-name>life-name</param-name>
	<param-value>LifeServler的值</param-value>
</context-param>
<listener>
 	<listener-class>myservlet.listener.MyContextAttributeListener</listener-class>
</listener>
<error-page><!-- JSP中有  isErrorPage="true"  -->
	<error-code>404</error-code>
	<location>/notfound.jsp</location>
</error-page>
<error-page>
	<exception-type>java.lang.RuntimeException</exception-type>
	<location>/error.jsp</location>
</error-page>
<session-config>
    <session-timeout>20</session-timeout><!-- 20分 -->
</session-config>
 <jsp-config>
	<taglib>
		<taglib-location>/WEB-INF/tlds/customTag.tld</taglib-location>
		<taglib-uri>/mytag</taglib-uri>
	</taglib>
 </jsp-config>
 
 
<servlet>
	<servlet-name>myJsp</servlet-name>
	<jsp-file>/security/develop/index.jsp</jsp-file>
	 <run-as>
		<role-name></role-name>
	</run-as>
	<security-role-ref>
		<role-name>manager</role-name><!-- 代码中使用的  request.isUserInRole("manager")-->
		<role-link>develop</role-link><!-- 容器中使用的 -->
	</security-role-ref>
</servlet>
<servlet-mapping>
	<servlet-name>myJsp</servlet-name>
	<url-pattern>/devIndex</url-pattern>
</servlet-mapping>
<!-- <transport-guarantee> 对http是 NONE,对https是 CONFIDENTIAL,INTEGRAL ,<url-pattern> 不能对特定路径???  -->
<security-constraint>
	<web-resource-collection>
		<web-resource-name>market Area</web-resource-name>
		<url-pattern>*.html</url-pattern>
		<http-method>GET</http-method>
		<http-method>POST</http-method>
	</web-resource-collection>
	<auth-constraint>
		<role-name>market</role-name>
	</auth-constraint>
	<user-data-constraint>
		<transport-guarantee>NONE</transport-guarantee> 
	</user-data-constraint>
</security-constraint>
<security-constraint>
	<web-resource-collection>
		<web-resource-name>develop Area</web-resource-name>
		<url-pattern>*.jsp</url-pattern>
		<http-method>GET</http-method>
		<http-method>POST</http-method>
	</web-resource-collection>
	<auth-constraint>
		<role-name>develop</role-name>
	</auth-constraint>
	<user-data-constraint>
		<transport-guarantee>NONE</transport-guarantee> 
	</user-data-constraint>
</security-constraint>

<security-role>
	<role-name>market</role-name>
</security-role>
<security-role>
	<role-name>develop</role-name>
</security-role>

<!-- BASIC,FORM 只可使用一种
配置tomcat-users.xml
<role rolename="market"/>
<role rolename="develop"/>
<user username="lisi" password="123" roles="develop"/>
<user username="zhang" password="123" roles="market"/>

密码保存在数据库 tomcat/docs/realm-howto.html
create table users (
  user_name         varchar(15) not null primary key,
  user_pass         varchar(15) not null
);
create table user_roles (
  user_name         varchar(15) not null,
  role_name         varchar(15) not null,
  primary key (user_name, role_name)
);

jdbc.jar 放入tomcat/lib

<Realm className="org.apache.catalina.realm.JDBCRealm"
	  driverName="org.h2.Driver" connectionURL="jdbc:h2:tcp://localhost/~/test" connectionName="sa" connectionPassword =""
	   userTable="users" userNameCol="user_name" userCredCol="user_pass"
   userRoleTable="user_roles" roleNameCol="role_name"/>
   
insert into users(user_name,user_pass) values('lisi','123');
insert into users(user_name,user_pass) values('zhang','123');
insert into user_roles(user_name,role_name) values('lisi','develop');
insert into user_roles(user_name,role_name) values('zhang','market'); 
-->
 
<!--BASIC 方式 ,会弹出输入用户名密码 ,在<realm-name>中不能写中文??? ,没有注销方法????
<login-config>
	<auth-method>BASIC</auth-method>
	<realm-name>BASIC access.jsp use lisi to login,基本验证</realm-name>
</login-config>
-->
<!--FORM 不能使用response.encodeURL(), 规范要求action="j_security_check" name="j_username" name="j_password" -->
<login-config>
	<auth-method>FORM</auth-method>
	<form-login-config>
		<form-login-page>/security/loginForm.html</form-login-page>
		<form-error-page>/error.jsp</form-error-page>
	</form-login-config> 
</login-config>
 
==================================Servlet
HttpServlet//单实例,最好不要加属性,要用synchroized 或 final的常量
public void init(ServletConfig config) {//如不加load-on-startup,第一次请求时初始化 
	ServletContext contex=config.getServletContext();
	String contexParam=contex.getInitParameter("life-name");
}
public void destroy() {}
//service方法默认实现是来决定调用doGet还是doPost方法

//禁用Cookie,IE中tools->internet options->privacy->advanced->选中override automatic cookie handling,再选两个block
//对localhost无效,要使用本机IP
HttpSession session=request.getSession();
Object sessionObj=session.getAttribute("sessionObj");
if(sessionObj==null)
{
	MySessionUser u=new MySessionUser();
	session.setAttribute("sessionObj", u);
}
//如有禁用Cookie使用response.encodeURL("");会自动加jsessionid的参数
request.getRequestDispatcher(response.encodeURL("/ok.jsp")).forward(request, response);
<form action="<%=response.encodeURL("life")%>"   ,危险可以把链接发给其它去点
		
if("POST".equals(request.getMethod()))
	request.setCharacterEncoding("UTF-8");//对<form method="post"生效,如是get无效,

String username=request.getParameter("username");
if("GET".equals(request.getMethod()))
	username=new String(username.getBytes("iso8859-1"),"UTF-8");
//javac -encoding GBK XX.java

session.isNew();
session.getId();//jsessionid为键的值 ,<form action="abc;jsessionid=xxx"
session.getCreationTime();
session.getLastAccessedTime();
session.getMaxInactiveInterval();
session.invalidate();//注销
		
response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,"系统繁忙");//SC=Server Code
response.sendError(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION,"未授权");
response.sendError(HttpServletResponse.SC_NOT_FOUND,"找不到页");
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"出现错误");
response.sendRedirect("/ok.jsp");
request.getRequestDispatcher("/ok.jsp").forward(request, response);//后面的不会被执行
request.getRequestDispatcher("/ok.jsp").include(request, response);

PrintWriter writer =response.getWriter();
writer.println("<h2>中国</h2>");
writer.close();//如是被其它Servlet,调用request.getRequestDispatcher("/ok.jsp").include(),就不要关闭


其它的Listener
ServletContextAttributeListener
ServletRequestAttributeListener
ServletRequestListener
HttpSessionAttributeListener
HttpSessionListener

Listener->Filter->Servlet(load-on-startup)加载顺序

==================================Servlet 3.0

@WebServlet(urlPatterns={"/page1","page2"}, initParams = {@WebInitParam(name = " default_market " ,  value = " NASDAQ " )})
//@WebInitParam(name = " default_market " , value = " NASDAQ " )
public class MyServlet3 extends HttpServlet {}

<servlet-class>
	<async-supported>true</async-supported>
或者
@WebServlet( asyncSupported=true

AsyncContext ctx=request.startAsync();   //异步的
ctx.addListener(new AsyncListener()
				{
					public void onComplete(AsyncEvent event) throws IOException 
					{
						System.out.println("onComplete DONE");
					}
					public void onError(AsyncEvent event) throws IOException 
					{
					}
					public void onStartAsync(AsyncEvent event) throws IOException
					{
					}
					public void onTimeout(AsyncEvent event) throws IOException 
					{
					}  
				});
context.complete();//会调用  Listener的complete方法



@WebListener
public class MyContxtListener3 implements ServletContextListener 
{
	public void contextInitialized(ServletContextEvent event) {
		ServletContext context=event.getServletContext();
		ServletRegistration.Dynamic dynServ=context.addServlet("myServName", MyServlet3.class);
		dynServ.setInitParameter("initParam1", "initVal");
		dynServ.addMapping("/dynServ","/dynServ2");
		dynServ.setAsyncSupported(true);
		//动态注册Servlet 
		FilterRegistration.Dynamic  dynFilter=context.addFilter("myFileterName",MyFilter3.class);//动态注册Filter
		
 	}
}

@WebFilter(filterName = "myFilter", urlPatterns = { "/test","/test1" })
public class MyFilter3 implements Filter{}

文件上传的原生支持
@MultipartConfig(maxFileSize=50*1024*1024) //用于支持文件上传 enctype="multipart/form-data"

req.setCharacterEncoding("UTF-8");
Part file1=req.getPart("attache1");//是<input type=file name="attache1"
String name=file1.getName();//attache1 
String pairs=file1.getHeader("content-disposition");
String nameHeader="filename=";
String path=pairs.substring(pairs.indexOf(nameHeader)+nameHeader.length()+1,pairs.lastIndexOf("\""));
if(!"".equals(path))
{
	String filename=path.substring(path.lastIndexOf("\\")+1);//只IE是带C:\,和req.setCharacterEncoding("UTF-8")中文 OK
	file1.write("d:/temp/"+filename);//一个Part要调用一次write
}

InputStream input=file2.getInputStream();
String param=req.getParameter("username");
System.out.println(new String(param.getBytes("iso8859-1"),"UTF-8"));//中文 OK
----无web.xml
public interface WebParameter {    
    public void loadInfo(ServletContext servletContext) throws ServletException;    
}  
public class ServletParameter implements WebParameter {    
    @Override    
    public void loadInfo(ServletContext servletContext) throws ServletException {    
        ServletRegistration.Dynamic testServlet=servletContext.addServlet("test","servlet3_new.nowebxml.TestServlet");    
        testServlet.setLoadOnStartup(1);    
        testServlet.addMapping("/nowebxml");    
    }    
}
//(不能是web项目的META-INF) WEB-INF/lib/xxx.jar/META-INF/services/javax.servlet.ServletContainerInitializer 中写实现 implements ServletContainerInitializer全类名
@HandlesTypes(WebParameter.class)    
public class WebConfiguration implements ServletContainerInitializer {    
    @Override    
    public void onStartup(Set<Class<?>> webParams, ServletContext servletCtx)throws ServletException 
   { //webParams 的值为项目中所有实现  @HandlesTypes(WebParameter 的类
        if (webParams != null)
        {    
            for (Class<?> paramClass : webParams) 
            {    
                if (!paramClass.isInterface() && !Modifier.isAbstract(paramClass.getModifiers()) &&    
                        WebParameter.class.isAssignableFrom(paramClass)) //是多余的判断
                {    
                    try {    
                        ((WebParameter) paramClass.newInstance()).loadInfo(servletCtx);    
                    }    
                    catch (Throwable ex) {    
                        throw new ServletException("Failed to instantiate WebParam class", ex);    
                    }    
                }    
            } 
        } 
    } 
}


==================================JSP
内置对象
page,pageContext,request,response,session,out,exception
application 是 ServletContext
config  是  ServletConfig


String realpath=request.getSession().getServletContext().getRealPath("/");//有项目名
String project=request.getContextPath();// 只项目名/
String basePath = request.getScheme() + "://"+ request.getServerName() + ":" + request.getServerPort()+ project ;

request.getRequestDispatcher("/pages/xxx.jsp").forward(request,response);

out.clear();
out = pageContext.pushBody();
可以防止 java.lang.IllegalStateException: getOutputStream() has already been called for this response 

//验证码的生成
BufferedImage image=new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
Graphics g = image.getGraphics();
//...
g.drawImage(new ImageIcon("x.gif").getImage(),0,0,width,height,null);//可以放大,缩小图片
g.dispose();
response.setContentType("image/jpeg");
ImageIO.write(image, "JPEG", response.getOutputStream());

	
<%@include file="one.jsp" %> <!--指令, 先引入文件再编译,可能有变量名重定义的错误 -->
<jsp:include page="one.jsp"></jsp:include><!--动作 , 引入文件的执行后的静态结果 -->


<jsp:useBean id="my" class="myservlet.MySessionUser" scope="request" >
</jsp:useBean>
<jsp:setProperty property="*" name="my"/> <!-- * 表示请求的参数的名与Bean的属性名对应 ,自动传入-->
<jsp:setProperty property="name" name="my" value="lisi"/> 
name的值是:<jsp:getProperty property="name" name="my"/>

--jspx其实就是以xml语法来书写jsp的文件
<?xml version="1.0" encoding="utf-8"?>
<jsp:root version="2.0" 
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:c="http://java.sun.com/jsp/jstl/core" >
	
	<jsp:directive.page import="javax.portlet.PortletPreferences"/>
	<jsp:directive.page import="java.util.Map"/>
	<jsp:declaration>
		int i;
	</jsp:declaration>
	
	<jsp:directive.include file="common.jsp"/>   会有变量重定义 
	<jsp:include page="menu.jsp" />
	
	<jsp:scriptlet>
	  String greeting =  "greeting";
	</jsp:scriptlet>
	 
	<jsp:expression>greeting</jsp:expression>
	
	<jsp:text>这里是内部文本 &#x20; 是空格 </jsp:text> <!-- 可不使用jsp:text -->
  	这里是外部文本
	
	jspx中使用<!-- -->就可注释服务端标签,因是XML
	不能使用泛型,//的java注释也不行
	 
</jsp:root>
==================================EL 表达式
+,-,*,/或div,%或mod
${xx.id} 或 ${xx["id"]}
(== 或 eq),(!= 或 ne),(< 或 lt),(> 或 gt),(<= 或 lt),(>= 或 gt),(&& 或 and),(|| 或 or) ,(! 或 not)
${xx eq null} 
${empty xx}   ${not empty  xx}
${A?B:C} //如A为true返回B,否则返回C
${23*(5-2)} 

EL中隐含对象,pageContext,pageScope,requestScope,sessionScope,applicationScope,param,paramValues,header,headerValues,cookie,initParam
${pageContext.servletContext.serverInfo}
${pageContext.request.requestURL}
${pageContext.response.characterEncoding}
${pageContext.session.createTime}
${header["User-Agent"]}//包含一些特殊字符,一定要使用[ ]

${sessionScope.user[data]}中data 是一个变量
${param.name}		//request.getParameter("name");
${paramValues.name}	//request.getParameterValues("name")
${header.name)		//request.getHeader(name)。
${headerValues.name} //request.getHeaderValues(name)。
${cookie.name.value} 
initParam		//ServletContext.getInitparameter(String name)

${user.username}//会按page->request->session->application 顺序查找
instanceof  在EL中是键字

String referer=request.getHeader("referer")
if(referer==null ||   !referer.startsWith("http://localhost:8080/J_JavaEE"))//防盗链,本次请求是人哪个URL过来的
{
	 response.sendRedirect("steal.html");  
	 return;  
}

---可自定义EL表达式函数,类的方法要是 public static 的
TLD文件中
<function>
	<name>toGBK</name>
	<function-class>myservlet.tag.MyELFunc</function-class>
	<function-signature>java.lang.String toGBK(java.lang.String,java.lang.String)</function-signature>
</function>
自定义的EL表达式函数toGBK:${you:toGBK("你好abc123","ISO8859-1")}<br/>

==================================JSTL中
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
-----------c标签库
<c:forTokens items="1,2,3,4,5,6,7,8,9" delims="," var="num">
	<option value="${num }">${num }</option>
</c:forTokens>

<!-- <c:forEach   varStatus的值是 LoopTagStatus  
		status.index,status.count,status.first,status.last, 
		status.begin,status.end, status.current-->
<c:forEach items="${messages}" var="item" begin="0" end="9" step="1" varStatus="var">
		<c:if test="${var.index % 2 == 0}">
		*
		</c:if>
	${item}<br>
</c:forEach>

Map map = new HashMap();
map.put("a","12345"); 
map.put("b","abcde");
<c:forEach items="${map}" var="mymap" > //可以对MAP
		<c:out value="${mymap.key}" /> <c:out value="${mymap.value}" />
</c:forEach>
<c:forEach items="${dataSet}" var="row" varStatus="status"  >
	<tr>
		<td>${status.index}</td> 
		<td>${row['username']}</td><!-- 对Map -->
		<td>${row.password}</td>
		<td><c:out value="${row.age}"/></td>
	</tr>
</c:forEach>
	
<c:if test="${x==6}">
</c:if>

<c:choose>
	<c:when test="${user.role == 'member'}">x</c:when>
	<c:otherwise>y</c:otherwise>
</c:choose>
 
 
<c:set target="${myMap}" property="lisi" value="李四"/> <!-- 可以对Map -->
<c:out value="${myMap.lisi}"></c:out>
<c:remove var="myMap" scope="page"/>
<c:catch var="ex">
	<%
		int i=5;
	int j=0;
	int k=0;
	k=j/k;
	%>
</c:catch>
<br/>异常信息为:
<c:out value="${ex.message}"></c:out> 

<div style="border-style:solid;border-width:5pt; border-color:blue" >
	<c:import url="sessionForm.jsp"></c:import>
</div>
<c:url value="life" var="myUrl"><!--,如以/开头,自动加项目名, 会话跟踪 不行的????esponse.encodeURL(-->
	<c:param name="username" value="李"></c:param>
</c:url>

<c:redirect url="/myhttp.action" context="/J_Struts2"> <!--Tomcat的 <Context 加 crossContext="true" -->
	<c:param name="myParam" value="test123" />  
</c:redirect> <!-- 后面的不会被执行 -->
<%
	System.out.println("JPS的尾部");
%>


-----------fmt标签库
<fmt:setBundle basename="message" scope="session" var="mybindle"/>
${mybindle.locale}

<fmt:setLocale value="zh_CN"/>
<fmt:setLocale value="en_US"/>

<fmt:message bundle="${mybindle}" key="CanNotEmpty">
	<fmt:param>
		<fmt:message bundle="${mybindle}" key="username"/>
	</fmt:param>
</fmt:message>

<fmt:requestEncoding value="GBK"/>
<fmt:setTimeZone value="GMT+8:00"/> <br/>

<fmt:formatNumber value="12.3" pattern=".000" /> <!-- DecimalFormat -->   <br/>
<fmt:formatNumber value="123456.789" pattern="#,#00.0#"/> <!--整数最少两位,小数最少1位 -->
 
number:<fmt:parseNumber value="456.78" integerOnly="true" type="number"></fmt:parseNumber>   <br/>
currency:<fmt:parseNumber value="$456.78" type="currency" parseLocale="en_US"></fmt:parseNumber><br/>
percent:<fmt:parseNumber value="75%" type="percent" ></fmt:parseNumber>  <br/> 

<fmt:formatDate value="<%=new java.util.Date() %>" pattern="yyyy-MM-dd HH:mm:ss"/> <br/>
<fmt:parseDate value="2012-11-22 12:33:22" pattern="yyyy-MM-dd HH:mm:ss" var="myDate"></fmt:parseDate> <br/>
 
-----------sql 标签库
 
<sql:setDataSource  url="jdbc:h2:tcp://localhost/~/test" driver="org.h2.Driver"  user="sa" password="" var="myDataSource"/>
<c:catch var="sqlEx">
	<sql:update sql="drop  table student" dataSource="${myDataSource}" />
</c:catch>
<sql:update sql="create table student (id int,name varchar2(20),age int,birthday date)" dataSource="${myDataSource}" />

<c:forEach begin="1" end="5" step="1" var="i">
	<sql:update sql="insert into  student (id,name,age ,birthday )values(?,?,?,?)" dataSource="${myDataSource}"  >
		<sql:param value="${i}"></sql:param>
		<sql:param value="lisi__${i}"></sql:param>
		<sql:param value="${22+i}"></sql:param>
		<sql:dateParam value="${myDate}"></sql:dateParam>
	</sql:update>
</c:forEach>

<!-- javax.servlet.jsp.jstl.sql.Result -->
<sql:query sql="select * from student where id>? or birthday <?" dataSource="${myDataSource}" maxRows="20" var="students">
	<sql:param value="1"></sql:param>
	<sql:dateParam  value="${myDate}"></sql:dateParam>
</sql:query>

<c:forEach var="row" items="${students.rows}"> <!--rows, rowsByIndex -->
	<c:out value="${row.name}"/> ,
	<c:out value="${row.age}"/> ,
	<c:out value="${row.birthday}"/> <br/>
</c:forEach>
<sql:transaction dataSource="${myDataSource}" isolation="serializable">
	<sql:update sql="update student set age=age+1 where id=1"/>
	<sql:update sql="update student set age=age-1 where id=2"/>
</sql:transaction>
 
 
-----------xml 标签库
<c:import url="student.xml" var="xmlDoc"></c:import>
<x:parse  doc="${xmlDoc}" var="studentDoc"></x:parse>
<x:out select="$studentDoc/students/student[@id>1]/teacher"/> <br/>
<x:out select="$studentDoc//*[name()='teacher'][1]" escapeXml="false"/> <!-- 1开始 -->
 
<br/> set:
<x:set var="teacher" select="$studentDoc//teacher"/>
<x:out select="$teacher"/>

<br/> if:
<x:if select="$studentDoc//student[@name='lisi']">
	<x:out select="$studentDoc/students/student/@name"/>
</x:if>

<br/> choose:
<x:choose>
	<x:when select="$studentDoc//student[@name='lisi']">lisi</x:when>
	<x:when select="$studentDoc//student[@name='wang']">wang</x:when>
	<x:otherwise>其它的</x:otherwise>
</x:choose>

<br/> forEach:
<x:forEach select="$studentDoc//student" var="student" varStatus="status">
	${status.index} ,<x:out select="$student/@name"/> <br/>
</x:forEach> 

<br/>transform:
<c:set var="xmltext">
  <books>
    <book>
      <name>Padam History</name>
      <author>ZARA</author>
      <price>100</price>
    </book>
    <book>
      <name>Great Mistry</name>
      <author>NUHA</author>
      <price>2000</price>
    </book>
  </books>
</c:set>

<c:import url="book.xsl" var="xslt"/>
<x:transform xml="${xmltext}" xslt="${xslt}"/>
<hr/>
<c:import url="book.xml" var="xml"/>
<x:transform doc="${xml}" xslt="${xslt}"/> <!-- doc=和xml=是一样的 -->
-----------fn标签库
contains:${fn:contains("zhangsan","san")} <br/>
containsIgnoreCase:${fn:containsIgnoreCase("zhangsan","SAN")}<br/>
startsWith :${fn:startsWith("zhangsan","zhan")} <br/>
endsWith :${fn:endsWith("zhangsan","san")} <br/>
indexOf :${fn:indexOf("zhangsan","san")} <br/>
replace :${fn:replace("zhangsan","san","123")} <br/>
substring :${fn:substring("zhangsan",7,-1)} <br/>
substringBefore :${fn:substringBefore("zhangsan","sa")} <br/>
substringAfter :${fn:substringAfter("zhangsan","ang")} <br/>
split :<c:set value='${fn:split("zhang;li",";")}' var="names" />
	<c:forEach items="${names}" var="item">
		${item}<br/>
	</c:forEach> <br/>
<%
	String[] welcome=new String[]{"wlcome","you","to","china"};
request.setAttribute("array",welcome);
%>
join :${fn:join(array," ")} <br/> 	
toLowerCase :${fn:toLowerCase("Good")} <br/> 	
toUpperCase :${fn:toUpperCase("Good")} <br/>
trim :${fn:trim(" do it ")} <br/>  
escapeXml :${fn:escapeXml("<br/>")} <br/>  
length :${fn:length("zhang")} <br/> 
length :${fn:length(array)} <br/>  <!-- 也可是List -->
	
	
==================================自定义标签
//类必须在包下
public class MyEmptyTag implements Tag
{
	private PageContext pageContext;
	private Tag parent;
	public void setPageContext(PageContext page) {
		pageContext=page;
	}
	public void setParent(Tag tag) {//如没父标签设置为null
		parent=tag;
	}
	public Tag getParent() {
		return parent;
	}
	public int doStartTag() throws JspException {
		return Tag.SKIP_BODY;
	}
	public int doEndTag() throws JspException {
		JspWriter writer=pageContext.getOut();
		try {
			writer.println("我的自定义标签的输出");
		} catch (IOException e) {
			e.printStackTrace();
		}
		//不要writer.close();
		return Tag.EVAL_PAGE;//JSP的剩余部分继续执行
	}
	public void release() {
	}
}

public class MyBodyTag extends BodyTagSupport
{
	public int doStartTag() throws JspException {
		return EVAL_BODY_BUFFERED; //必须是EVAL_BODY_BUFFERED才可用bodyContent对象
		//Servlet容器会缓存实例
	}
	public int doEndTag() throws JspException {
		JspWriter writer=bodyContent.getEnclosingWriter();//TagSupport的成员 
	}
}

MyIteratorTag  extends TagSupport{

public class MySimpleTagSupport  extends SimpleTagSupport{//JSP2.0,Servlet容器不会缓存
	private JspFragment body;
	private String name;
	public void setName(String name) {
		this.name = name;
	}
	public void setParent(JspTag parent) {//如没有父不会被调用
	}
	public void setJspBody(JspFragment jspBody) {//如没有体不会被调用
		this.body=jspBody;
	}
	public void doTag() throws JspException, IOException {
		JspContext context=getJspContext();//SimpleTagSupport中的方法
		JspWriter writer=context.getOut();
		writer.println(name);
		writer.println(",");
		body.invoke(null);//null是当前输出流
	}
}
public class MyMaxSimpleTagSupport  extends SimpleTagSupport implements DynamicAttributes
				
customTag.tld 会自动搜索.war/WEB-INF/的所有*.tld 和 .jar/META-INF/所有的*.tld
<?xml version="1.0" encoding="UTF-8" ?>
<taglib xmlns="http://java.sun.com/xml/ns/j2ee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-jsptaglibrary_2_0.xsd"
	version="2.0">
	<display-name>My Custom Tag</display-name>
	<tlib-version>1.0</tlib-version>
	<short-name>my</short-name>
	<uri>http://zhaojin.org/tags</uri> 
	<tag>
		<name>hello</name>
		<tag-class>myservlet.tag.MyEmptyTag</tag-class>
		<body-content>empty</body-content> <!-- empty 空标签,JSP   -->
	</tag>
	<tag>
		<name>iterator</name>
		<tag-class>myservlet.tag.MyIteratorTag</tag-class>
		<body-content>JSP</body-content>
		<attribute>
			<name>var</name>
			<required>true</required>
			<rtexprvalue>false</rtexprvalue> <!-- false -->
		</attribute>
		<attribute>
			<name>items</name>
			<required>true</required>
			<rtexprvalue>true</rtexprvalue>
		</attribute>
		<variable> <!-- 迭代的中间变量 -->
			<name-from-attribute>var</name-from-attribute>
			<variable-class>myservlet.tag.ValueLabelBean</variable-class>
			<scope>NESTED</scope><!--NESTED  -->
		</variable>
	</tag>
	<tag>
		<name>welcom</name>
		<tag-class>myservlet.tag.simple.MySimpleTagSupport</tag-class>
		<body-content>tagdependent</body-content> <!--  不能是JSP,因 setJspBody(JspFragment), tagdependent  -->
		<attribute>
			<name>name</name>
			<required>true</required>
			<rtexprvalue>true</rtexprvalue>
		</attribute>
	</tag>
	<tag>
		<name>max_ex</name>
		<tag-class>myservlet.tag.simple.MyMaxSimpleTagSupport</tag-class>
		<body-content>empty</body-content>
		<dynamic-attributes>true</dynamic-attributes>
		<variable>
			<name-given>max</name-given> <!--对应于 context.setAttribute("max" -->
			<variable-class>Integer</variable-class>
			<declare>true</declare>
			<scope>AT_END</scope><!--AT_END ,AT_BEGIN -->
		</variable>
	</tag>
</taglib>

<%@taglib  prefix="my" uri="/WEB-INF/tlds/customTag.tld" %>
<my:iterator var="item" items="${allBean}">
	${item["label"]}  ==  <jsp:getProperty property="value" name="item"/>  <br/>
</my:iterator>

 extends  TagSupport //实现了IterationTag,Tag
	pageContext.getOut();//TagSupport的成员 
==================================标签文件
<%@taglib tagdir="/WEB-INF/tags/" prefix="tg" %>
扩展名是*.tag,*.tagx(使用XML语法),.war会自动搜索/WEB-INF/tags的子目录,.jar是/META-INF/tags/所有*.tag,*.tagx
.tag中有jspContext隐含对象,是PageContext的基类,没有page,pageContext,exception其它同JSP

hello:<tg:hello/> <!-- hello是hello.tag文件名的前部分 -->

welcome:
<tg:welcome >
	<jsp:attribute name="user">李四</jsp:attribute>
	<jsp:body>欢迎</jsp:body>
</tg:welcome> <br/>

escapeXml: 
<tg:toHTML escapeHtml="true">
	<font color="red" >这是true</font>
</tg:toHTML><br/>
<tg:toHTML escapeHtml="false">
	<font color="red" >这是false</font>
</tg:toHTML><br/>

variable 测试:
 <tg:my_var num1="100" num2="2002" num3="303" >
    <jsp:attribute name="great">
        <font color="red">SUM：${sum}</font>
    </jsp:attribute>
    <jsp:attribute name="less">
        <font color="blue">SUM：${sum} </font>
    </jsp:attribute>
</tg:my_var>
--hello.tag
<%@tag pageEncoding="UTF-8" %>
这是第一个tag的测试

--welcome.tag
<%@attribute name="user" required="true" fragment="true" %> 
<!-- 如 fragment="true" ,那么rtexprvalue="true" type="javax.servlet.jsp.tagext.JspFragment" 的值是固定的 ,不能被指定-->
<jsp:invoke fragment="user" /> ,<jsp:doBody/><!-- 只可在.tag文件中使用,都可指定 var或 varReader(只可一个)来保存结果,scope-->
---toHTML.tag
<c:choose>
	<c:when test="${escapeHtml}"> <!-- 这里可以直接得到 @attribute,在jspContext中 -->
		<jsp:doBody var="content"/>
		 <%  out.println(toHtml((String)jspContext.getAttribute("content")));    %>
	</c:when>
	<c:otherwise>
		<jsp:doBody/>
	</c:otherwise>
</c:choose>
 ---my_var.tag
<%@tag pageEncoding="UTF-8" body-content="scriptless" dynamic-attributes="numColumn"%>
<%@ attribute name="great" fragment="true" %>
<%@ attribute name="less" fragment="true" %>
<%@ variable name-given="sum" variable-class="java.lang.Integer" %>
<c:if test="${not empty numColumn}">
	<c:forEach items="${numColumn}" var="num">
		<c:set var="sum" value="${num.value + sum}" />
	</c:forEach>
	<c:if test="${sum >= 1000}" >
		<jsp:invoke fragment="great" />
	</c:if>
	<c:if test="${sum < 1000}" >
		<jsp:invoke fragment="less" />
	</c:if>  
</c:if>
==================================HTTP 断点继传 (下载)
服务器返回的HTTP头有 Accept-Ranges=bytes
浏览器请求的HTTP头有 RANGE: bytes=2000070-
服务器返回的HTTP头 是206 ,Content-Range=bytes 2000070-106786027/106786028

HttpURLConnection  httpConnection.setRequestProperty("RANGE","bytes=2000070");
RandomAccessFile  seek(2020) ,read,write
--
下载文件设置HTTP头,文件名
response.reset();
response.setContentType("application/msexcel");//tomcat的conf/web.xml中
response.setHeader("Content-disposition","inline;filename=workbook.xls");//inline显示在浏览器中

response.setContentType("application/x-msdownload");
response.addHeader("Content-Disposition", "attachment;filename="+ new String(filename.getBytes("GBK"), "ISO-8859-1"));//attachment会提示下载
ServletOutputStream output=response.getOutputStream();
output.close();//可能不要关闭
	
==================================JNDI
Context对象的createSubContext(Name name)
		createSubContext(String name)
		bind("name",Object)
		rebind("anme",Object)
		unbind("name")或Name
		close()

要把C:\bea\weblogic90\server\lib下的weblogic.jar放classpath
Hashtable env=new Hashtable();
env.put(Context.INITIAL_CONTEXT_FACTORY,"weblogic.jndi.WLInitialContextFactory");
env.put(Context.PROVIDER_URL,"t3://localhost:7001");
env.put(Context.SECURITY_PRINCIPAL,"username");
env.put(Context.SECURITY_CREDENTIALS,"password");
Context ctx=new InitialContext(env);//如果在容器中就不用传参数了，只对main方法中


web.xml中
	<env-entry>
		<env-entry-name>MyCompany</env-entry-name>
		
		<env-entry-type>java.lang.String</env-entry-type>//或是基本类型的封闭类
		<env-entry-value>aa</env-entry-value>
	</env-entry>

Context ctx=new InitialContext();
String c=(String)ctx.lookup("java:comp/env/MyCompany");


context.lookup("java:comp/env/jdbc/Modeling")

对应一个JNDIname  weblogic中的  数据源变时，只改weblogic，不用改，web.xml
和java 代码，在容器是配置时要ref这个name ?????????


实现数据分离
如果web应用由Servlet容器管理的某个JNDI Resource，
必须在web.xml中声明对这个JNDI Resource的引用。

web.xml
<resource-ref>
	<res-ref-name>jdbc/Modeling</res-ref-name> 
	<res-type>javax.sql.DataSource</res-type>
	<res-auth>Container</res-auth>
</resource-ref>

weblogic.xml
<resource-description>
	<res-ref-name>jdbc/Modeling</res-ref-name>
	<jndi-name>jdbc/Modeling</jndi-name>
</resource-description>


---web.xml
<resource-env-ref>
	<resource-env-ref-name>jms/StockQueue</resource-env-ref-name>
	<resource-env-ref-type>javax.jms.Queue</resource-env-ref-type>
</resource-env-ref>

=================================java mail
发送邮件服务器
	SMTP:Simple Mail Transfer Protocol
接收邮件服务器 
	POP3 :  Post Office Protocol 3，不存放在服务器上，不可在线阅读邮件服务器上的邮件
	IMAP :Internet Message Access Protocol   可在线阅读邮件服务器上的邮件，存储在服务器上

	POP3默认端口110 SMTP默认端口25 IMAP默认端口143
	
	
cmd命令  telent smtp.163.com 25 
	以下不能输错,不能贴粘.
	
	HELO smtp.163.com 	命令 
	250 hz-b-163smtp1.163.com 9561591f-d7ff-4bd5-876a-9fefcf7846e5  返回250表示OK 
	auth login 			命令 
	334 VXNlcm5hbWU6 9561591f-d7ff-4bd5-876a-9fefcf7846e5 
	USER emhhbzFqaW40	 命令 是用户名的Base64 3
	PASS xxxxxx 		命令 是密码的Base64,这里就过不去了!!!!
	后面还有　 
	MAILFROM:XXX@163.COM 
	RCPTTO:XXX@163.COM 
	DATA 
	354 End data with .
	QUIT
	--------另一套
	 EHLO zhaopinpc
	 250-hz-b-163smtp2.163.com
	 250-mail
	 250-PIPELINING
	 250-8BITMIME
	 250-AUTH LOGIN PLAIN
	 250-AUTH=LOGIN PLAIN
	 250 SARTTLS 1ad55c7f-d850-4a65-8d61-3ba8f024113f
	 AUTH LOGIN

	 

javaMail中的URLName(String)
	格式为：   协议名称：//用户名：密码@邮件服务器/
pop3://username:password@163.com/
	 

	 
javax.mail.Session类 getStore("imap")//imaps,pop3,pop3s
下为抽象类
javax.mail.Store 有一个static getFolder()
		.connect(host,usrname,password)
javax.mail.Folder 
	getFolder("xx")
	open(Folder.READ_WRITE)

	getMessageCount() 共有邮件数量
	getUnreadMessageCount()//未读邮件

javax.mail.Message 的子类MimeMessage(Session ) 如mes
		setRecipients(Message.RecipientsType.TO,IternetAddress[] xx)	
				Message.RecipientsType.CC(抄送) BCC密送
		setFrom()
		setText()
		setSubject()
	Transport.send(mes)


javax.mail.Address 的子类在internet包下的 InternetAddress
javax.mail.Transport 发送 static send(Message)


BodyPart 的.getDispostion 如返回是Part.ATTACHMENT或是Part.INNLINE 说明是附件,如是null是正文


javax.mail.Authenticator 抽象类只有getPasswordAuthentication()方法可被 重写
	protected javax.mail.PasswordAuthentication getPasswordAuthentication()
	{ 
		return new javax.mail.PasswordAuthentication(user,password); 
	}


javax.mail.Session 的getDefalutInstance(Properties,Autenticatior)
			
			getnstance(Properties,Autenticatior)

MimeMessge构造方法(Session)
InternetAddress.parse(String 用逗号分隔（comma）如第二参数为true则可以用空格分隔) 返回一个InternetAddresss[]
MimeMessage的setRecipients(Message.RecipientType.TO,Address[]) Address的子类是InternetAddress
	     setRecipients(Message.RecipientType.TO,String address)
		setSubject("")
		setFrom(Address)
		setText("  ")



javax.mail.Transport是抽象类有一个static send(Message子类MimeMessage)

MimeBodyPart
可以通过Session对象的getTransport
java.util.ResourceBundle.getBundle("basename",Locale.SIMPLIFIED_CHINESE)


Javax.mail.internet.MimeMessage
	InternetAddress的.getType()返回在jar包下的javamail.default.address.map文件中的key  (值是协议)
	MerakMail软件
	
	  Properties  props = System.getProperties();
  props.put("mail.transport.protocol", "smtp");
  props.put("mail.store.protocol", "imap");
  props.put("mail.smtp.class", "com.sun.mail.smtp.SMTPTransport");
  props.put("mail.imap.class", "com.sun.mail.imap.IMAPStore");
  props.put("mail.smtp.host", hostname);

  
  
示例
	static Properties prop=	new Properties();
	static{
		try {
			prop.load(new FileInputStream("C:/temp/mail.properties"));
		} catch ( Exception e) {
			e.printStackTrace();
		}  
	}
	public static final String username=prop.getProperty("username");  // 是@sina.com 前面的部分
	public static final String password=prop.getProperty("password"); 
	public static final String mailTo =prop.getProperty("mailTo");   // 带@的
	public static final String filterFromMailAddr=prop.getProperty("filterFrom");//xx@sina.com
	public static final String addrHost="163.com";
	public static final String pop3Host="pop3.163.com";
	public static final String smtpHost="smtp.163.com";
	public static final String imapHost="imap.163.com";
	
	public static void receivePop3Mail() throws Exception
	{
		Properties props = new Properties();
		Session recesession = Session.getInstance(props, null);
		recesession.setDebug(true);
		Store store = recesession.getStore("pop3");
		store.connect(pop3Host, username, password);
		
		Folder inbox = store.getFolder("INBOX");
		inbox.open(Folder.READ_ONLY);
		int count = inbox.getMessageCount();

	 
		// 设置过滤规则，对接收的e-mail进行过滤，
		SearchTerm st = new OrTerm(new SubjectTerm("笔记"), new FromStringTerm(filterFromMailAddr));//OrTerm 是或的原则
		Message[] filtermsg = inbox.search(st);
		// 对被过滤出的e-mail设置删除标志
		for (int i = 0; i < filtermsg.length; i++) 
		{
			Message msg = filtermsg[i];
			if (msg != null) 
			{
				// 得到被过滤出的e-mail的标题
				String filterTitle = msg.getSubject();
				System.out.println("被过滤的邮件:" + filterTitle);
				
				// 设置删除标记
				msg.setFlag(Flags.Flag.DELETED, true);//修改进入[已删除]列表,但不是在邮箱中是已删除???
			}
		}
		System.out.println("收件箱中总共有" + (count - filtermsg.length) + "封e-mail");
		 
		
		//列表显示出来
	    for(int j=1;j<=count;j++)
	    {
		      Message message=inbox.getMessage(j);
		      //如果不是待删除的e-mail就显示出来
		      if(message.isSet(Flags.Flag.DELETED))
		    	  continue;
		      
	         String title=message.getSubject();
	         System.out.println("---------邮件标题:"+title);
	         //------------邮件细节
	         Address[] address=message.getFrom();//javax.mail.internet.InternetAddress
	         if(address!=null)
	        	  for(int i=0;i<address.length;i++)
	            	 System.out.print("发件人:"+((InternetAddress)address[i]).getAddress());
	         
	         Date sentdate=message.getSentDate();
	         if(sentdate!=null)
	        	 System.out.print("发出的时间:"+sentdate.toString());
	         
	         address=message.getRecipients(Message.RecipientType.TO);
	         if(address!=null)
	            for(int i=0;i<address.length;i++)
	            	 System.out.print("收件人:"+address[i]);
	         
	         address=message.getRecipients(Message.RecipientType.CC);
	         if(address!=null)
	            for(int i=0;i<address.length;i++) 
	            	System.out.print("抄送人:"+address[i]);
	         
	        //如果是一个多部分内容的e-mail
	        if(message.isMimeType("multipart/*"))
	        {
	           //获得代表该e-mail的多部分内容的Multipart对象
	           Multipart multipart = (Multipart)message.getContent();
	           //依次获取Multipart对象的每个部分
	           for(int i = 0;i < multipart.getCount();i++)
	           {
	             //得到每个部分的属性
	             Part p = multipart.getBodyPart(i);
	             String disposition = p.getDisposition();
	             
	             //如果该部分中是附件内容，则输出该附件的下载链接
	             if ((disposition != null) &&(disposition.equals(Part.ATTACHMENT) || disposition.equals(Part.INLINE)))
	             {
	                String filename=p.getFileName();
	                filename=javax.mail.internet.MimeUtility.decodeText(filename);//中文OK
	                System.out.println("符件:"+filename+",mime:"+p.getContentType());
	   	         
	                int num=message.getMessageNumber();
	                System.out.println("邮件索引:"+num);

	                InputStream input=p.getInputStream();//下载附件
	              }else if(disposition==null)
                 {
                    if(p.isMimeType("text/plain"))
                    {
                    	  System.out.print("只处理的文本:"+p.getContent());
                    }else   if(p.isMimeType("text/html"))
                    {   
                    	System.out.println("=====HTMLcontent:"+p.getContent());
                    }else//如type:multipart/alternative
                    {
                    	System.out.println("=====not text/plain or text/html ===type:"+p.getContentType()+"\n----content:"+p.getContent());
                    }
                 }
               }
	        }else if(message.isMimeType("text/plain"))  //如果是普通文本形式的e-mail，则显示其详细内容
            {
            	System.out.print("邮件文本:"+message.getContent());
            }else//几乎没很少有这种情况
            {
            	System.out.print("========other ContentType:"+message.getContentType());
            }
         } 
 		inbox.close(true);
 		store.close();
	}
	public static void receiveIMAPMail() throws MessagingException 
	{
		Properties props = System.getProperties();
		Session sess = Session.getInstance(props, null);
		sess.setDebug(true);
		Store st = sess.getStore("imap");//还可是 imaps
		st.connect(imapHost, username, password);
		
		Folder fol = st.getFolder("INBOX");
		if (fol.exists())
		{
			for (Folder f : fol.list()) 
			{
				System.out.printf("-----box:%s", f.getName());//只有一个INBOX
			}
			fol.open(Folder.READ_ONLY);
			Message[] msgs=fol.getMessages();
			for (Message m : msgs) 
			{
				System.out.printf("-----/n来自%s /n标题%s/n大小%d/n",convertAddress2String(m.getFrom()), m.getSubject(), m.getSize());
			}
			fol.close(false);
		} 
		st.close();
	}
	public static void sendHTMLAttachmentMail() throws MessagingException // TestOK
	{
		String subject = "subject from javamail 标题";
		String bodyText = "hello !,this is java mail test body 正文.";
		String attachment = "c:/temp/图片.jpg";
		boolean isSendAttach=true;

		
		Properties props = new Properties();
		Session sendsession = Session.getInstance(props, null);
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.auth", "true");// 设置SMTP服务器需要权限认证
		sendsession.setDebug(true);
		Message message = new MimeMessage(sendsession);
		message.addHeader("Content-type", "text/html");//对HTML格式的邮件
		
		message.setFrom(new InternetAddress(username + "@" + addrHost));
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(mailTo));
		message.setSubject(subject);
		message.setSentDate(new Date());

		if (isSendAttach) 
		{
			// 建立第一部分：文本正文
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent("<meta http-equiv=Content-Type content=text/html; charset=UTF-8><div style='color:red;font-size:50px'>" + bodyText+"</div>", "text/html;charset=UTF-8");//对HTML格式的邮件
			// 建立多个部分Multipart实例
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			
			
			
			// 建立第二部分：附件
			messageBodyPart = new MimeBodyPart();
			// 获得附件
			DataSource source = new FileDataSource(attachment);
			// 设置附件的数据处理器
			messageBodyPart.setDataHandler(new DataHandler(source));
			// 设置附件文件名
			
			String fileName=source.getName(); 
			try {
				fileName=MimeUtility.encodeText(fileName);//在收件箱中的中文OK
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			messageBodyPart.setFileName(fileName); 
			
			
			// 加入第二部分
			multipart.addBodyPart(messageBodyPart);
			// 将多部分内容放到e-mail中
			message.setContent(multipart);
		}  
		
		message.saveChanges();
		Transport transport = sendsession.getTransport("smtp");
		transport.connect(smtpHost, username, password);
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
	}

================================JMS 
SoupUI 可JMS


多台weblogic域 JMS通信 要域"密码"一样,这里的密码是在console界面中的第一项即"域名"->security标签->下方的Advanced->Credential:处的密码

Queue 和 Topic 都继承Destination
TopicSubscriber 和 QueueReceiver 继承自 MessageConsumer
QueueSender 和 TopicPublisher 继承自 MessageProducer

//weblogic JMS 通用部分
String url = "t3://localhost:7001";
String jndiConnectionFactory = "jms/myFactory";
String jndiQueue = "jms/myQueue";
String jndiTopic = "jms/myTopic";
boolean transacted = false;
Properties properties = new Properties();
properties.put(Context.INITIAL_CONTEXT_FACTORY,"weblogic.jndi.WLInitialContextFactory");
properties.put(Context.PROVIDER_URL,url);
Context context = new InitialContext(properties);
Object lookupFactory = context.lookup(jndiConnectionFactory);

//===MyQueueSender.java  OK
//---父类通用的
ConnectionFactory factory =(ConnectionFactory)lookupFactory;
Queue queue = (Queue)context.lookup(jndiQueue);
Connection connection =factory.createConnection();
connection.start();
Session session = connection.createSession(transacted,  Session.AUTO_ACKNOWLEDGE);
MessageProducer producer  = session.createProducer(queue);

TextMessage textMessage = session.createTextMessage();
textMessage.clearBody();
textMessage.setText("MessageProducer's  Message");
producer.send(textMessage);//OK,weblogic监视Messages Current列+1
if (transacted)
{
	session.commit();
}
producer.close();
session.close();
connection.close();
//--子类
QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory) lookupFactory;
Queue queue = (Queue)context.lookup(jndiQueue);
QueueConnection queueConnection = queueConnectionFactory.createQueueConnection();
queueConnection.start();
QueueSession queueSession = queueConnection.createQueueSession(transacted, Session.AUTO_ACKNOWLEDGE);
QueueSender queueSender = queueSession.createSender(queue);
TextMessage textMessage = queueSession.createTextMessage();
textMessage.clearBody();
textMessage.setText("QueueSender's Message");
queueSender.send(textMessage);//OK ,weblogic监视Messages Current列+1
if (transacted)
{
	queueSession.commit();
}
queueSender.close();
queueSession.close();
queueConnection.close();

//===MyQueueReceiver.java OK
//---父类通用的
Object obj = context.lookup(jndiQueue);
Queue queue = (Queue) obj;
ConnectionFactory factory =(ConnectionFactory)lookupFactory;
Connection connection =factory.createConnection();
connection.start();
Session session = connection.createSession(transacted,  Session.AUTO_ACKNOWLEDGE);
MessageConsumer consumer  = session.createConsumer(queue);

//---
//Message tmpMsg=consumer.receiveNoWait();//OK
//System.out.println("MessageConsumer get is:"+ tmpMsg);
//---
consumer.setMessageListener(new MessageListener(){		
	public void onMessage(Message message) {
		if (message instanceof TextMessage)
		{
			TextMessage textMessage = (TextMessage) message;
			try
			{
				System.out.println("MessageListener get is:"+ textMessage.getText());
			}catch (JMSException e)
			{
				e.printStackTrace();
			}
		}
	}});
MyQueueReceiver msgRcvr = new MyQueueReceiver();
synchronized(msgRcvr){ msgRcvr.wait(100000);}  
//------

if (transacted)
{
	session.commit();
}
consumer.close();
session.close();
connection.close();

//--子类
QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory) lookupFactory;
Object obj = context.lookup(jndiQueue);
Queue queue = (Queue) obj;
QueueConnection queueConnection = queueConnectionFactory.createQueueConnection();
queueConnection.start();
QueueSession queueSession = queueConnection.createQueueSession(transacted,  Session.AUTO_ACKNOWLEDGE);
QueueReceiver queueReceiver = queueSession.createReceiver(queue);

QueueBrowser browser = queueSession.createBrowser(queue);//只看不取 OK
Enumeration msgs = browser.getEnumeration();
while (msgs.hasMoreElements()) 
{
 TextMessage msg = (TextMessage)msgs.nextElement();
System.out.println("QueueBrowser get is: " + msg.getText());
}
//--------
//			TextMessage textMessage=(TextMessage)queueReceiver.receive();//会阻塞 ,只读一个继续,可while,OK
//			System.out.println("QueueReceiver get is:"+ textMessage.getText());
//--------
 queueReceiver.setMessageListener(new MessageListener(){		//异步 OK, 会读所有的
	public void onMessage(Message message) {
		if (message instanceof TextMessage)
		{
			TextMessage textMessage = (TextMessage) message;
			try
			{
				System.out.println("MessageListener get is:"+ textMessage.getText());
			}catch (JMSException e)
			{
				e.printStackTrace();
			}
		}
	}});
MyQueueReceiver msgRcvr = new MyQueueReceiver();
synchronized(msgRcvr){ msgRcvr.wait(100000);}  
//------
queueReceiver.close();     
queueSession.close();     
queueConnection.close();  


//==============MyTopicSubscriber.java  weblogic 有示例的 
//离线topic的要求一定要配置一个JMS store
//---parent  离线/在线 OK
ConnectionFactory connectionFactory = (ConnectionFactory) lookupFactory;
Topic topic = (Topic)context.lookup(jndiTopic);

Connection connection = connectionFactory.createConnection();
connection.setClientID("client-name-1"); 
Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE); 
TopicSubscriber  consumer = session.createDurableSubscriber(topic, "my-sub-name-1"); 
connection.start();
Message msg=consumer.receive();
System.out.println("parent get is:"+msg);
//---child  离线/在线 OK
TopicConnectionFactory topicConnectionFactory = (TopicConnectionFactory) lookupFactory;
Topic topic = (Topic)context.lookup(jndiTopic);
TopicConnection topicConnection = topicConnectionFactory.createTopicConnection();

topicConnection.setClientID("client-name");
TopicSession topicSession = topicConnection.createTopicSession(transacted, Session.AUTO_ACKNOWLEDGE);
TopicSubscriber topicSubscriber=topicSession.createDurableSubscriber(topic, "my-sub-name"); 
//会在weblogic的Monitor->Durable Subscribers下建立的,离线也可取消息,之后connection.start();
//TopicSubscriber topicSubscriber= topicSession.createSubscriber(topic);//必须在线可取消息
topicConnection.start();
topicSubscriber.setMessageListener(new MessageListener() 
{
	public void onMessage(Message msg)
	{
		if(msg instanceof TextMessage)
		{
			TextMessage t=(TextMessage)msg;
			try
			{
				System.out.println("Topic get is:"+t.getText());
			} catch (JMSException e)
			{
				e.printStackTrace();
			}
		}
	}});

MyTopicSubscriber my=new MyTopicSubscriber();
synchronized(my){my.wait(100000);}    
topicSubscriber.close();	 
topicSession.close();     
topicConnection.close();     
//==============MyTopicPublisher.java 
//---parent  离线/在线 OK
ConnectionFactory connectionFactory = (ConnectionFactory) lookupFactory;
Topic topic = (Topic)context.lookup(jndiTopic);
Connection connection = connectionFactory.createConnection();
Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE); 

MessageProducer producer = session.createProducer(topic); 
producer.setDeliveryMode(DeliveryMode.PERSISTENT); //设置保存消息 ,这个可以不写的，如先subscript是durable的，这里就放到durable里
connection.start(); //设置完了后，才连接  

TextMessage msg=session.createTextMessage();
msg.clearBody();
msg.setText("Test Message!!!");
producer.send(msg); 
//---child  离线/在线 OK
TopicConnectionFactory topicConnectionFactory = (TopicConnectionFactory) lookupFactory;
Topic topic = (Topic)context.lookup(jndiTopic);
TopicConnection topicConnection = topicConnectionFactory.createTopicConnection();
TopicSession topicSession = topicConnection.createTopicSession(transacted, Session.AUTO_ACKNOWLEDGE);
TopicPublisher topicPublisher= topicSession.createPublisher(topic);
topicPublisher.setDeliveryMode(DeliveryMode.PERSISTENT);//设置保存消息 
topicConnection.start();

TextMessage textMessage=topicSession.createTextMessage();
textMessage.setText("topicPublisher's Message");
topicPublisher.publish(textMessage);

topicPublisher.close();
topicSession.close();     
topicConnection.close();     

如果有webloigc 中有	Durable的Topic,那么只有Durable的Subscriber可以收到，

=============================JTA
Context ctx=new InitialContext();
UserTransaction ut=(UserTransaction)ctx.looup("javax.transaction.UserTransaction");
ut.begin();
...
ut.commit();

@Inject
UserTransaction userTransaction;

在类前加
@Transactional(value = Transactional.TxType.MANDATORY) //表示调用类的方法必须在 ut.begin();以后
@Transactional(value = Transactional.TxType.NEVER)//表示调用类的方法不能在 ut.begin();以后
@Transactional(value = Transactional.TxType.NOT_SUPPORTED)//有没有 ut.begin();都可
@Transactional(value = Transactional.TxType.REQUIRED)//有没有 ut.begin();都可
@Transactional(value = Transactional.TxType.REQUIRES_NEW)//有没有 ut.begin();都可,会自动建立事务 @Inject UserTransaction userTransaction;
@Transactional(value = Transactional.TxType.SUPPORTS)//有没有 ut.begin();都可

@javax.transaction.TransactionScoped  //类前,表示类的方法要在事务中被调用,同一事务中类的所有对象都相同,不同事务类的对象不同

@javax.annotation.PreDestroy 放在方法前

--weblogic中使用JTA,要在Oracle数据库服务器上启用 XA 
@$ORACLE_HOME/rdbms/admin/xaview.sql   (以sys用户执行)
grant select on v$xatrans$ to public (or <user>);
grant select on pending_trans$ to public;
grant select on dba_2pc_pending to public;
grant select on dba_pending_transactions to public;
grant execute on dbms_system to <user>;


import javax.sql.PooledConnection;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

public static XADataSource newOracleXADataSource() throws Exception//OK
{
	OracleXADataSource oracleXADS=new OracleXADataSource();//oracle.jdbc.xa.client.
	oracleXADS.setURL("jdbc:oracle:thin:@127.0.0.1:1521/XE");
	oracleXADS.setUser("hr");
	oracleXADS.setPassword("hr");
	oracleXADS.setPortNumber(1521);
	oracleXADS.setDatabaseName("XE");
	oracleXADS.setServerName("127.0.0.1");
	return oracleXADS;
}
	
public static XADataSource newH2XADataSource()//OK
{
	JdbcDataSource h2XADS=new JdbcDataSource();//org.h2.jdbcx.
	h2XADS.setURL("jdbc:h2:tcp://localhost:9092/test");
	h2XADS.setUser("sa");
	h2XADS.setPassword("sa");
	return h2XADS;
}
	
	
public XADataSource newMySQLXADataSource()
{
	MysqlXADataSource mysqlXADS=new MysqlXADataSource(); //com.mysql.jdbc.jdbc2.optional.
	mysqlXADS.setUser("root");
	mysqlXADS.setPassword("root");
	String url="jdbc:mysql://localhost:3306/test?useUnicode=true&amp;characterEncoding=UTF-8";
	mysqlXADS.setUrl(url);
	mysqlXADS.setURL(url);
	mysqlXADS.setPort(3306);
	mysqlXADS.setPortNumber(port);
	mysqlXADS.setServerName("127.0.0.1");
	mysqlXADS.setDatabaseName("test");
	return mysqlXADS;
}
public void executeInJndiConnection(Connection conn) throws SQLException
{
   PreparedStatement pstmt = conn.prepareStatement("update oracle_score set score=score-1 where id=1"); 
   //PreparedStatement pstmt = conn.prepareStatement("insert into oracle_score(id,score)values(1,88)");//使用主键重复的错误测试
	pstmt.executeUpdate();//如有错误,这里就抛出,无论是否设置setAutoCommit(false)
	pstmt.close();
}
public void executeInNewConnection(Connection conn ) throws SQLException
{
	PreparedStatement pstmt = conn.prepareStatement("update mysql_score set score=score+1 where id=1"); 
	pstmt.executeUpdate();
	pstmt.close();
}
 
//.class要在weblogic的容器内才行
TransactionManager trans = null;
Connection jndiConn = null;
Connection newConn = null;
try {
	Properties properties = new Properties();
	properties.put(Context.INITIAL_CONTEXT_FACTORY,"weblogic.jndi.WLInitialContextFactory");
	properties.put(Context.PROVIDER_URL, "t3://127.0.0.1:7001");
	Context context = new InitialContext(properties);
	
	Object jndiObj= context.lookup("jdbc/oracleXA"); 
	//weblogic配置时一定要选择XA JDBC driver,driver自动配置为oracle.jdbc.xa.client.OracleXADataSource
	DataSource jndiDataSource = (DataSource)jndiObj;//不可强转到XADataSource
	jndiConn = jndiDataSource.getConnection();
	//jndiConn.setAutoCommit(false); //可以不加的
	trans = (TransactionManager)context.lookup("javax.transaction.UserTransaction");//返回的也是UserTransaction,
	trans.begin();  
	
	XADataSource xaDataSource = newMySQLXADataSource();//这个数据源是通过JDBC配置的
	XAConnection xaConn = xaDataSource.getXAConnection();
	XAResource xaRes = xaConn.getXAResource();   
	trans.getTransaction().enlistResource(xaRes);//需要主动的加入到当前的事务中      
	//如.class不在weblogic中则报错You may enlist a resource only on a server.
	//就是说weblogic的JTA TransactionManager只能在代码(war或者ear)发布到weblogic的server上才能使用.
	newConn = xaConn.getConnection();
	//newConn.setAutoCommit(false);//可以不加的
	executeInNewConnection(newConn); 
	executeInJndiConnection(jndiConn);//通过在weblogic上配置的DataSource,会自动加入到当前的事务中. 
	trans.commit();
	System.out.println("OK!transaction manager commited!");
} catch (Exception e)
{    
	 e.printStackTrace();
	try {
		trans.rollback();
		System.out.println("Exception!rollback transactions managed by traMgr.");
	} catch (Exception e1) {
		e1.printStackTrace();
	} 
}finally{
	try {
		jndiConn.close();
		newConn.close();
	} catch (SQLException e) {e.printStackTrace();}     
}



//-----.class不在容器中,但只一个连接????
//是用“两步提交协议”来提交一个事务分支：
 public void  test()throws Exception
{
	XADataSource xaDS;
	XAConnection xaCon;
	XAResource xaRes;
	Xid xid;
	Connection con;
	Statement stmt;
	int ret;
	xaDS = newOracleXADataSource();
	xaCon = xaDS.getXAConnection();
	xaRes = xaCon.getXAResource();
	con = xaCon.getConnection();
	stmt = con.createStatement();
	xid = new MyXid(100, new byte[]{0x01}, new byte[]{0x02});//实现Xid接口的类, 类用来标识事务
	try 
	{
		xaRes.start(xid, XAResource.TMNOFLAGS);
		stmt.executeUpdate("insert into  h2_score(id,score) values(1,70)");
		xaRes.end(xid, XAResource.TMSUCCESS);
		ret = xaRes.prepare(xid);
		if (ret == XAResource.XA_OK) 
			xaRes.commit(xid, false);
	}
	catch (XAException e) 
	{
		e.printStackTrace();
	}
	finally
	{
		stmt.close();
		con.close();
		xaCon.close();
	}
}




=============================Portlet
portlet 1 规范JSR-168
portlet 2 规范JSR-286

---web.xml

<filter>
	<filter-name>servletFilter</filter-name>
	<filter-class>pluto_portlet.ServletFilter</filter-class>
</filter>
<filter-mapping>
	<filter-name>servletFilter</filter-name>
	<url-pattern>/portal/*</url-pattern>  */<!-- 路径只能是/portal/ 开头 -->
</filter-mapping>


为 pluto
<servlet>
	<servlet-name>changeCaseServ</servlet-name>
	<servlet-class>org.apache.pluto.container.driver.PortletServlet</servlet-class>
	<init-param>
		<param-name>portlet-name</param-name>
		<param-value>ChangeCasePortlet</param-value> <!-- 这里除了是portlet.xml中的值,还要与  <url-pattern>/PlutoInvoker/的值一样才行 -->
	</init-param>
	<load-on-startup>1</load-on-startup>
</servlet>
<servlet-mapping>
	<servlet-name>changeCaseServ</servlet-name>
	<url-pattern>/PlutoInvoker/ChangeCasePortlet</url-pattern><!-- 路径只能是/PlutoInvoker/ 开头 -->
</servlet-mapping>



---WEB-INF/portlet.xml
<?xml version="1.0" encoding="UTF-8"?>
<portlet-app
    xmlns="http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd"
    version="1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd
                        http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd">
	<portlet>
      <portlet-name>ChangeCasePortlet</portlet-name>
      <display-name>Change Case Portlet</display-name>
      <portlet-class>pluto_portlet.ChangeCasePortlet</portlet-class>
      <supports>
         <mime-type>text/html</mime-type>
         <portlet-mode>view</portlet-mode>
      </supports>
	  <resource-bundle>pluto_portlet.changeCase-message</resource-bundle> <!-- 每个portlet单独文件国际化,<title>中的值 -->
      <portlet-info>
         <title>ChangeCasePortlet</title>
      </portlet-info>
	  <portlet-preferences>
			<preference>
				<name>my-pref-name</name>
				<value>my-pref-value</value>
			</preference>
			<preference>       
				<name>company</name>
				<value>abc_firm</value>
			</preference> 
		</portlet-preferences>
   </portlet>

	<filter>
		<filter-name>MyAllFilter</filter-name>
		<filter-class>pluto_portlet.filter.MyAllFilter</filter-class>
		<lifecycle>ACTION_PHASE</lifecycle>
		<lifecycle>RENDER_PHASE</lifecycle>
		<lifecycle>EVENT_PHASE</lifecycle>
		<lifecycle>RESOURCE_PHASE</lifecycle>
	</filter>
	<filter-mapping>    
		<filter-name>MyAllFilter</filter-name>
		<portlet-name>my*</portlet-name>
	</filter-mapping>
   
   	<!-- 
   <default-namespace>http://company.com</default-namespace> 可有可无
    -->
classpath下的pluto_portlet/changeCase-message_en_US.properties,但在pluto中,中文乱码????
javax.portlet.title=my javax changeCase portlet title
javax.portlet.title.<display-name>=  优先级高
<portlet-info><title>做国际化key = 优先级低


动作 Action 阶段和呈现Render阶段.
在单个请求的整个处理过程中,动作阶段只会被执行一次,而显示阶段可能会被执行多次

GenericPortlet实现了Portlet,PortletConfig,EventPortlet,ResourceServingPortlet

MyPortlet implements Portlet 

import javax.portlet.PortletPreferences;
MyGenericPortlet extends GenericPortlet
{	//视图模式时调用 doView() 方法
	protected void doView(RenderRequest request, RenderResponse response)
			throws PortletException, IOException{
		//-----------
		PortletSession  session=  request.getPortletSession();
		session.setAttribute("portletSessionScopeTest", "portletSessionScopeTestValue" );
		PortletContext context_1= session.getPortletContext();
		//-----------
		PortletContext context = getPortletConfig().getPortletContext();
		context.getRequestDispatcher(VIEW_JSP).include(request, response);
	}
		//点提交时调用 processAction() 方法
	public void processAction(ActionRequest actionRequest, ActionResponse actionResponse)
			throws PortletException, java.io.IOException {
		//-------
		PortletPreferences prefs = actionRequest.getPreferences();
		prefs.setValue("greeting", "hello in javacode");
		prefs.store();//store is not allowed during RENDER phase.
		String  greet=(String)prefs.getValue("greeting", "Hello! this is default value");
		String my_pref_name = prefs.getValue("my-pref-name", null);//配置在portlet.xml
		Map<String, String[]> mapData=prefs.getMap(); //是只从portlet.xml中取的结果
		
		System.out.println("greet="+greet);
		System.out.println("my_pref_name="+my_pref_name);
		System.out.println("mapData="+mapData);
		//-------
		
	    String newCase = actionRequest.getParameter("case");//得到参数
		
		actionResponse.setRenderParameter("textBox", textBox);//设置传参数
	}
}	


<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects/>表示可以在JSP中使用 
renderRequest
resourceRequest
actionRequest
eventRequest

renderResponse
resourceResponse
actionResponse
eventResponse

portletConfig
portletSession
portletSessionScope			${portletSessionScope.xx}
portletPreferences
portletPreferencesValues  是 Map<String, String[]> 类型 , 同 portletPreferences.getMap()
 -->

<br/>
EL portletPreferences 1 = ${portletPreferences.getValue("greeting","default value")}  <br/>
EL portletPreferences 2 = ${portletPreferences.map["company"][0]} <br/>
EL portletPreferences 3 = ${portletPreferencesValues["company"][0]} <br/>
 
portletSessionScope=<c:out value="${portletSessionScope.portletSessionScopeTest}"/><br /> 
portletSession=<%=portletSession.getAttribute("portletSessionScopeTest")%> <br/>
 
<%
	String textBox = renderRequest.getParameter("textBox");
%>
<FORM  action="<portlet:actionURL/>"> 表示调用GenericPortlet的processAction方法

ResourceServingPortlet 不建议直接仿问资源,如href="/portlet/myResource.jsp",而没有Portlet的功能,<portlet:resourceURL/>触发

<a href="<portlet:resourceURL/>">Click me to request Resource URL</a><!-- 触发 ResourceServingPortlet 的 serveResource 方法 -->

PortletContext contex=portletConfig.getPortletContext();
portletConfig.getPortletName();//<portlet-name>的值
PortletRequestDispatcher portletRequestDispatcher = contex.getRequestDispatcher("/portlet/eventSender.jsp");
portletRequestDispatcher.include(renderRequest, renderResponse);
String myParam=portletConfig.getInitParameter("myParam");
<portlet>中
	<init-param>
	  <name>myParam</name>
	  <value>myValue in init-param</value>
	</init-param>

事件
发送方 actionResponse.setEvent("myComplexEvent",myEvent );//OK
	   actionResponse.setEvent(new QName("http://zhaojin.org","myComplexEvent","my"),myEvent );//OK
接收方 EventReceiverPortlet  implements Portlet,EventPortlet 有方法 processEvent
		Event event = eventRequest.getEvent();//接收并处理事件
发送方<portlet>中
	<supported-publishing-event>
		<qname xmlns:my="http://zhaojin.org">my:myComplexEvent</qname>
		<!--<name>myComplexEvent</name>-->
	</supported-publishing-event>
接收方<portlet>中
	<supported-processing-event>
 		<qname xmlns:my="http://zhaojin.org">my:myComplexEvent</qname>
		<!--<name>myComplexEvent</name>-->
	</supported-processing-event>
根下
	<event-definition>   
		<qname xmlns:my="http://zhaojin.org">my:myComplexEvent</qname>
		<!--<name>myComplexEvent</name>-->
		<value-type>pluto_portlet.MyComplexEvent</value-type>
	</event-definition>
	
公共参数
String mapCity=actionRequest.getParameter("mapCity");//如是中文是经过&#99999;编码的,中文OK
actionResponse.setRenderParameter("mapCity", mapCity+"_One");//是Render

<portlet>中
	<supported-public-render-parameter>mapCity</supported-public-render-parameter>
根下
<public-render-parameter>
	<identifier>mapCity</identifier><!-- 参数名 -->
</public-render-parameter>



首先进行 Servlet 过滤，其次是 Portlet 过滤

四种类型的过滤器
EventFilter 	拦截 processEvent 方法
ActionFilter 	拦截 processAction 方法
RenderFilter	拦截 render 方法
ResourceFilter 	拦截 serveResource 方法

String id=actionRequest.getWindowID();
//在 Portal 容器中布局同一个 Portlet 多次的情况下，windowID 可以用来区分同一个 Portlet 的不同窗口

actionResponse.setPortletMode(PortletMode.VIEW);//转换视图调用对应的GenericPortlet 的 doEdit,doView 方法

<a href='<portlet:renderURL portletMode="view" windowState="normal"/>' > -Home-</a>
windowState 还有 MAXIMIZED 和 MINIMIZED

<supports>
	<mime-type>text/html</mime-type>
	<portlet-mode>view</portlet-mode>
	<portlet-mode>edit</portlet-mode>
</supports>

-----<portlet:namespace/> ???

-----文件上传
portlet 只要使用 org.apache.commons.fileupload.portlet.PortletFileUpload; 其它的不变
pluot中文问题??? &#19981

-----文件下载
public void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws PortletException, IOException {
		//----下载
		//ResourceURL downloadURL =resourceResponse.createResourceURL();
		byte[] content="this is a download test__这是一个下载测试".getBytes("UTF-8")
		resourceResponse.reset();
		resourceResponse.setContentType("application/x-msdownload");
		resourceResponse.setContentLength(content.length);
		//文件名如何设置 ????
		resourceResponse.getPortletOutputStream().write(content);
//		PrintWriter writer =resourceResponse.getWriter();
//		PortletRequestDispatcher portletRequestDispatcher=resourceRequest.getPortletSession().getPortletContext().getRequestDispatcher(HELP_JSP);
//	    portletRequestDispatcher.include(resourceRequest, resourceResponse);//也是.getWriter()不能和getPortletOutputStream()一起使用
	
	}
<a href="<portlet:resourceURL/>">download a file </a>
resourceResponse.setContentType("application/x-msdownload");//OK

Servlet 中使用 response.setContentType("text/xml; charset=UTF-8");

-----问题
pluto jsp页面中不能中文????

my.setMyname("中国");//Event Sender到Receiver后中文乱码???
actionResponse.setEvent(new QName("http://zhaojin.org","myComplexEvent"),my );//<qname>无效????
<supported-publishing-event>
	<!-- <qname>无效????
	<qname xmlns:key="http://zhaojin.org">myComplexEvent</qname>
	 -->
</supported-publishing-event>



=============================Batch

JavaEE带的示例

.war\WEB-INF\classes\META-INF\batch-jobs\PayrollJob.xml

<job id="payroll" xmlns="http://xmlns.jcp.org/xml/ns/javaee" version="1.0">
    <step id="process">
        <chunk item-count="3">
            <reader ref="SimpleItemReader"></reader>   <!-- 对应 @Named("SimpleItemReader"), 类要 extends AbstractItemReader  从哪读要做的记录 -->
            <processor ref="SimpleItemProcessor"></processor> <!-- implements ItemProcessor  处理读到的记录 -->
            <writer ref="SimpleItemWriter"></writer> <!-- extends AbstractItemWriter  存储处理结果-->
        </chunk>
		<partition>
            <mapper ref="PayrollPartitionMapper"/> <!-- implements PartitionMapper  -->
        </partition>
    </step>
</job>

@Inject
private JobContext jobContext; //EJB 容器实现

Properties jobParameters = BatchRuntime.getJobOperator().getParameters(jobContext.getExecutionId());

JobOperator jobOperator = BatchRuntime.getJobOperator();
long executionID = jobOperator.start("PayrollJob", props); //每一个名字是xml文件名,提交一个Job,AbstractItemReader 就被调用了

for (JobInstance jobInstance : jobOperator.getJobInstances("payroll", 0, Integer.MAX_VALUE-1))//取全部, "payroll" 对应xml文件中的 <job id="payroll"
{
	for (JobExecution jobExecution : jobOperator.getJobExecutions(jobInstance)) 
	{
	    jobExecution.getJobName()// payroll
		jobExecution.getExecutionId()
		jobExecution.getBatchStatus()//emnu 的类型 BatchStatus.COMPLETED
		jobExecution.getExitStatus()
		jobExecution.getStartTime()
		jobExecution.getEndTime()
	}
}

 return new PartitionPlanImpl()
        {
            @Override
            public int getPartitions() {
                return 5;
            }
            @Override
            public Properties[] getPartitionProperties() {
			}
		}
==================webSocket
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/myMsgWebSocket",
		decoders = {MyMessageDecoder.class}, //implements Decoder.Text<T> 请求时如@OnMessage方法的第一个参数是自定义类型,把String->T
		encoders = { MyMessageEncoder.class  }) //implements Encoder.Text<T>   T->String
public class MyMessageWebSocket { 
   private static Set<Session> peers = Collections.newSetFromMap(new ConcurrentHashMap<Session, Boolean>());
    @OnOpen
    public void onOpen(Session session) {
        peers.add(session);
    }
    @OnClose
    public void onClose(Session session) {
        peers.remove(session);
    }
    @OnMessage
    public void shapeCreated(MyMessage message, Session client) //如第一个参数是自定义类型会使用decoders和encoders,如String就不用
				throws IOException, EncodeException 
	{
		Set<Session> opendSessions=client.getOpenSessions();//所有打开的
        for (Session otherSession : peers) 
		{
            if ( otherSession.equals(client)) 
			{
                //otherSession.getBasicRemote().sendText(message);//发String消息到客户端
            	otherSession.getBasicRemote().sendObject(message);//要Encoder ,T->String
				client.getUserProperties().put("key",obj);//是个Map
				Object stored=client.getUserProperties().get("key");
            }
        }
    }
}

//编程式部署，放入其它的启动初始化方法中
ServerEndpointConfig.Builder.create(MyServerEchoEndpoint.class, "/echo").build();

---websocket java 做 client
public class MyClientEndpoint extends Endpoint  
{
	@Override
	public void onOpen(final Session session, EndpointConfig config) 
	{
		session.addMessageHandler(new MessageHandler.Whole<String>()
		{
			@Override
			public void onMessage(String message) {
				System.out.println("客户端收到消息:"+message);	
			}
		}); 
	}
}

//代码最好也运行在容器中
WebSocketContainer container = ContainerProvider.getWebSocketContainer();//实现 META-INF/services/javax.websocket.ContainerProvider 
Session session=container.connectToServer(MyClientEndpoint.class,   //收到服务端消息的回调类
		ClientEndpointConfig.Builder.create().build(),
		new URI("ws://localhost:8080/J_JavaEE/myMsgWebSocket"));
session.getBasicRemote().sendText("obj_type123:data_123客户端发送的Test消息");
//session.getBasicRemote().sendObject(new MyMessage("obj_type","123_data"));//报错 因使用的 Decoder.Text 
Thread.sleep(3*1000);
session.close();
	
=============================Concurrent
@Resource
ManagedExecutorService executor;

@Resource
ManagedScheduledExecutorService scheduledExecutor;

@Resource
ManagedThreadFactory factory;

//--
InitialContext ctx = new InitialContext();
ManagedExecutorService executor = (ManagedExecutorService) ctx.lookup("java:comp/DefaultManagedExecutorService");

===javax.ws.rs.  CXF支持
=============================JSON
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonBuilderFactory;
import javax.json.JsonStructure;
//--生成
JsonBuilderFactory bf = Json.createBuilderFactory(null);
JsonStructure struct =bf.createObjectBuilder()
            .add("firstName", "John")
            .add("age", 25)
            .add("address", bf.createObjectBuilder()
                .add("streetAddress", "21 2nd Street")
                .add("city", "New York")
			).build();
			
JsonArray array=bf.createArrayBuilder()
	.add(bf.createObjectBuilder()
		.add("type", "home")
		.add("number", "212 555-1234"))
	.add(bf.createObjectBuilder()
		.add("type", "fax")
		.add("number", "646 555-4567"))
	.build();
System.out.println(struct.toString());  //{...}
System.out.println(array.toString());//[{...},{...}]
//--解析
String inStr="[{\"type\":\"home\",\"number\":\"212 555-1234\"},{\"type\":\"fax\",\"number\":\"646 555-4567\"}]";
JsonParser parser = Json.createParser(new StringReader(inStr));
while(parser.hasNext()) 
{
	Event e = parser.next();
	if (e == Event.KEY_NAME)
	{
		if (parser.getString().equals("number")) 
		{
			parser.next();
			System.out.println(parser.getString());
		}  
	}
}


=============================Interceptor
/WEB-INF/beans.xml 

<!-- 是 JavaEE 的 interceptors 用途 -->
<beans xmlns="http://java.sun.com/xml/ns/javaee"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/beans_1_0.xsd">
    <interceptors>
     <class>javaee_intercepter.LoggingInterceptorDetail</class>
    </interceptors>           
</beans>

import javax.inject.Inject;
import javax.interceptor.InterceptorBinding;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Inherited
@InterceptorBinding
@Retention(RUNTIME)
@Target({METHOD, TYPE})
public @interface LoggingInterceptor {
}

@LoggingInterceptor
@Interceptor
public class LoggingInterceptorDetail {
    @AroundInvoke
    public Object log(InvocationContext context) throws Exception {
        System.out.println("BEFORE: " + context.getParameters());
        Object result = context.proceed();
        System.out.println("AFTER: " + context.getMethod());
        return result;
    }
}

@LoggingInterceptor
public class ShoppingCart {
	 public void addItem(String item) {
        items.add(item);
    }
	public void checkout() {
        System.out.println(items + " checking out");
    }
}

@Inject ShoppingCart cart;
cart.checkout(); //会被拦截
cart.addItem(item);


=============================Event    glassfish中的weld-osgi-bundle.jar
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.enterprise.event.Event;
//注册事件的观察
public void onPrintAndBind(@Observes @BindIt PrintEvent event) {// @BindIt (有@Qualifier)和 PrintEvent 是自定义的
	System.out.println("Printing and binding " + event.getPages() + " pages");
}
public void onPrint(@Observes @Default PrintEvent event) {
	System.out.println("Printing " + event.getPages() + " pages");
}
//--产生事件
@Inject Event<PrintEvent> printEvent;
@Inject @BindIt Event<PrintEvent> printAndBindEvent;    
public void print(int pages) 
{
	PrintEvent event = new PrintEvent(pages);
	if (pages < 10)
		printEvent.fire(event);//产生
	else
		printAndBindEvent.fire(event);
}


=============================JCA (J2EE Connector Architecture)
SPI (Service Provider Interfaces) 
CCI (Common Client Interface)
Enterprise Information Systems (EIS)

Resource Adapter Archive (RAR)文件中(JAR 文件格式)

Resource Adapter 像 JDBC driver 一般是供应商提供 ,EIS像是DBMS

---ra.xml 老版本,新版本使用@
xx.rar/META-INF/ra.xml 
xx.rar/yy.jar中放 .class
	
<!DOCTYPE connector PUBLIC '-//Sun Microsystems, Inc.//DTD Connector 1.0//EN' 'http://java.sun.com/dtd/connector_1_0.dtd'>
<connector>
    <display-name>Hello World Sample</display-name>
    <vendor-name>Willy Farrell</vendor-name>
    <spec-version>1.0</spec-version>
    <eis-type>Hello World</eis-type>
    <version>1.0</version>
    <resourceadapter>
        <managedconnectionfactory-class>com.ibm.ssya.helloworldra.HelloWorldManagedConnectionFactoryImpl</managedconnectionfactory-class>
        <connectionfactory-interface>javax.resource.cci.ConnectionFactory</connectionfactory-interface>
        <connectionfactory-impl-class>com.ibm.ssya.helloworldra.HelloWorldConnectionFactoryImpl</connectionfactory-impl-class>
        <connection-interface>javax.resource.cci.Connection</connection-interface>
        <connection-impl-class>com.ibm.ssya.helloworldra.HelloWorldConnectionImpl</connection-impl-class>
        <transaction-support>NoTransaction</transaction-support>
        <reauthentication-support>false</reauthentication-support>
    </resourceadapter>
</connector>

import javax.security.auth.Subject;
import javax.security.auth.message.callback.CallerPrincipalCallback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.Callback;
import javax.security.auth.message.callback.PasswordValidationCallback;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.resource.cci.ConnectionSpec;

import javax.resource.spi.work.SecurityContext;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkManager;
import javax.resource.spi.Activation;
import javax.resource.spi.ConfigProperty;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import javax.resource.spi.ActivationSpec;

@Connector(description = "Sample adapter using the JavaMail API", 
  displayName = "InboundResourceAdapter", 
  vendorName = "Sun Microsystems, Inc.", 
  eisType = "MAIL", 
  version = "1.0", 
  authMechanisms = { 
        @AuthenticationMechanism(authMechanism = "BasicPassword", 
          credentialInterface = AuthenticationMechanism.CredentialInterface.PasswordCredential) 
  }
  implements ResourceAdapter,Serializable
 {
	public void start(BootstrapContext ctx) //服务器启动时,或部署时调用 
	{	
		workManager = ctx.getWorkManager();
		workManager.scheduleWork(new Work()
			{
				 public void run() {
				 	endpoint = endpointFactory.createEndpoint(null);
					(samples.connectors.mailconnector.api.JavaMailMessageListener) endpoint).xx();
				 }
			}); 
	}
	public void endpointActivation(MessageEndpointFactory endpointFactory,ActivationSpec spec)//客户端激活,传入@Activation对应的类
	{//服务器启动时第二次调用
	}
 }
 
 
@Activation(messageListeners = {JavaMailMessageListener.class})
public class ActivationSpecImpl implements  ActivationSpec,Serializable  //ResourceAdapter的 endpointActivation 传入的
{
	@ConfigProperty()//可被外部EJB配置 @ActivationConfigProperty 修改,同一个接口 JavaMailMessageListener
    private String serverName = "";//不能为null
}

extends SecurityContext 
{
	public abstract void setupSecurityContext(CallbackHandler handler, Subject executionSubject,Subject serviceSubject)
	{
		executionSubject.getPrincipals().add(new PrincipalImpl(principalName));
		CallerPrincipalCallback cpc = new CallerPrincipalCallback(executionSubject, new PrincipalImpl(principalName));
		PasswordValidationCallback pvc =  new PasswordValidationCallback(executionSubject, userName, password.toCharArray());
		Callback callbackArray[] = new Callback[2];
		callbackArray[0]=cpc;
		callbackArray[1]=pvc;
		handler.handle(callbackArray);
		if(!pvc.getResult()){
			System.out.println("用户名验证失败");
		}
	}
}
class PrincipalImpl implements Principal
{
	 public String getName() {
        return name;
    }
}
implements WorkContextProvider
{
	 public List<WorkContext> getWorkContexts() {
        return workContexts;
    }
	getWorkContexts().add(SecurityContext x);
}

 implements ConnectionManager
 { 
	public Object allocateConnection(ManagedConnectionFactory mcf,ConnectionRequestInfo cxRequestInfo)//XX implements ConnectionRequestInfo
	{
		ManagedConnection mc = mcf.createManagedConnection(null, cxRequestInfo);
		return mc.getConnection(null, cxRequestInfo);
	}
}

PasswordCredential pc = new PasswordCredential(userName, password);
pc.setManagedConnectionFactory(mcf);

 PasswordCredential pc =  AccessController.doPrivileged(new PrivilegedAction<PasswordCredential>()
					{
                        public PasswordCredential run() 
						{
                            Set<PasswordCredential> creds = subject.getPrivateCredentials(PasswordCredential.class);
                            Iterator<PasswordCredential> iter = creds.iterator();
                            while (iter.hasNext()) 
							{
                                PasswordCredential temp = iter.next();
                                if (temp != null && temp.getManagedConnectionFactory() != null
												 && temp.getManagedConnectionFactory().equals(mcf)) {
                                    return temp;
                                }
                            }
                            return null;
                        }
                    });
					
import javax.mail.MailSessionDefinition;
import javax.resource.ConnectionFactoryDefinition;
import javax.resource.spi.TransactionSupport.TransactionSupportLevel;
import javax.resource.spi.ConnectionDefinition;
import javax.resource.Referenceable;

@MailSessionDefinition(
        name = "java:app/env/TheMailSession",
        storeProtocol = "IMAP", 
        transportProtocol = "SMTP", 
        host = "localhost", 
        user = "joe", 
        password = "joe", 
        from = "daphu", 
        properties = {
                "mail.imap.class=com.sun.mail.imap.IMAPStore",
                "mail.smtp.class=com.sun.mail.smtp.SMTPTransport" 
        }
)
@ConnectionFactoryDefinition(name = "java:comp/env/eis/MyConnectionFactory", 
  description = "Connection factory against mail server", 
  interfaceName = "samples.connectors.mailconnector.api.JavaMailConnectionFactory",//实现类定义在 @ConnectionDefinition
  resourceAdapter = "#mailconnector", 
  minPoolSize = 2, 
  transactionSupport = TransactionSupportLevel.NoTransaction)
@WebServlet
class xx extends HttpServlet {
	@Resource(lookup="java:comp/env/eis/MyConnectionFactory")
	JavaMailConnectionFactory  connectionFactory ;
	
	@Resource(lookup="java:app/env/TheMailSession")
    Session session;
	
}

@ConnectionDefinition(connectionFactory = samples.connectors.mailconnector.api.JavaMailConnectionFactory.class, 
					connectionFactoryImpl = samples.connectors.mailconnector.ra.outbound.JavaMailConnectionFactoryImpl.class, 
					connection = samples.connectors.mailconnector.api.JavaMailConnection.class, 
					connectionImpl = samples.connectors.mailconnector.ra.outbound.JavaMailConnectionImpl.class)
					
				JavaMailConnectionFactoryImpl implements  Referenceable 
					
=============================EJB
javax.ejb.Local;
javax.ejb.Remote;

javax.ejb.Stateful;
javax.ejb.Stateless;
javax.ejb.Singleton;

javax.ejb.EJB;
javax.ejb.LocalBean;
javax.ejb.Startup;

@LocalBean

@Local  

@EJB 放属性前,注入,已经@Singleton 或 @Stateless
@Startup 容器启动时加载

//--Session Bean 的三种状态
@Stateful
@Stateless 
@Singleton 单例类
---
@Stateless  放实现类前 ,实现接口 StatelessSessionBean (命名规范 在接口名后加Bean,也可不实现接口)
@Remote  放在接口前 (StatelessSession)
		也可放在实现前@Remote (StatelessSessionBean.class)

//客户端代码在容器内
InitialContext contex = new InitialContext();
//GlassFish容器
StatelessSession sless = (StatelessSession) contex.lookup("enterprise.hello_stateless_ejb.StatelessSession");// 全包类名
//java:global/automatic-timer-ejb/StatelessSessionBean  //java:global以/分隔,最后一个包,两者不能切换????
//接口要服务端和客户端都要有,包名相同,(服务端部署.jar ,EJB)


EJBContainer c = EJBContainer.createEJBContainer();
Context ic = c.getContext();
Object o= ic.lookup("java:global/ejb-embedded/SimpleEjb");

import javax.ejb.Timer;
import javax.ejb.Schedule;

@Schedule(second="*/3", minute="*", hour="*", info="Automatic Timer Test")
public void test_automatic_timer(Timer t)
{
	System.out.println("Canceling timer " + t.getInfo() + " at " + new Date());
	if(count++>10)
	{
		 t.cancel();
		 System.out.println("Done");
	}
}
	
eclipseEE 中在J2EE-> Enterprise Application Project会产生META-INF\application.xml

ear包中的APP-INF\lib目录放jar
ear包中的META-INF目录中的application.xml中
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE application PUBLIC "-//Sun Microsystems, Inc.//DTD J2EE Application 1.3//EN" "http://java.sun.com/dtd/application_1_3.dtd">
<application>
  <display-name>Foundation</display-name>
   <module>
    <web>								<!-- Web -->
      <web-uri>MDM.war</web-uri>
      <context-root>MDM</context-root>
    </web>
  </module>
  <module>
    <ejb>PARP-MessageHandlerEJB.jar</ejb> <!-- EJB -->
  </module>
  <module>
    <connector>mailconnector.rar</connector> <!-- JCA -->
  </module>
</application>


---老的方式 .jar/META-INF/ebj-jar.xml  
<ejb-jar xmlns="http://xmlns.jcp.org/xml/ns/javaee" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
         metadata-complete="false" 
         version="3.2" 
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/ejb-jar_3_2.xsd">
  <display-name>JavaMailMDB</display-name>
  <enterprise-beans>
    <message-driven>
      <display-name>JavaMailMDB</display-name>
      <ejb-name>JavaMailMDB</ejb-name>
      <ejb-class>samples.connectors.mailconnector.ejb.mdb.JavaMailMessageBean</ejb-class>
      <messaging-type>samples.connectors.mailconnector.api.JavaMailMessageListener</messaging-type>
      <transaction-type>Container</transaction-type>
    </message-driven>   
  </enterprise-beans>
</ejb-jar>

import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenBean;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDrivenContext;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

@MessageDriven(messageListenerInterface=JavaMailMessageListener.class,
        name="JavaMailMDB",
        activationConfig = {
			@ActivationConfigProperty(propertyName = "serverName", propertyValue = "localhost")//配置新的属性值
			}
		)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class JavaMailMessageBean implements MessageDrivenBean, JavaMailMessageListener //自定义接口
{
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void onMessage(javax.mail.Message message) {//自定义接口方法
	}
	public void ejbRemove() {
    }
	public void setMessageDrivenContext(MessageDrivenContext mdc) {
		this.mdc = mdc;
    }
}

=============================Tomcat
catalina.bat run 或 catalina.bat start
startup.bat

	
tomcat 的catalina.sh  中加入
JAVA_OPTS='-server -Xms800m -Xmx800m -XX:PermSize=64M -XX:MaxNewSize=256m -XX:MaxPermSize=128m -Djava.awt.headless=true'


tomcat 8  tomcat-users.xml 中加配置
	<role rolename="manager-gui"/>
	<role rolename="manager-script"/>
	<role rolename="manager-jmx"/>
	<role rolename="manager-status"/>
	<user username="admin" password="admin" roles="manager-gui,manager-script,manager-jmx,manager-status"/>
 
server.xml中
	<Connector maxThreads="150" port="8080" 
	<Host name="localhost"  appBase="webapps" 这个webapps可以改成其它路径

--------Tomcat/conf/web.xml中加修改listings 为true　就可以列目录了(默认是不可以的)
<init-param>
	<param-name>listings</param-name>
	<param-value>true</param-value>
</init-param>
--------	
---------配置DataSource ,jdbc.jar放在tomcat的lib目录下
方法一,要2步
	1) 在Tomcat 根目录下的conf\server.xml 配置Resource：
	<GlobalNamingResources>
		<Resource name="jdbc/mydatasource" type="javax.sql.DataSource"
		description="DB Connection"  auth="Container"
		driverClassName="org.h2.Driver" url="jdbc:h2:tcp://localhost/~/test" username="sa" password=""
		maxIdle="2" maxWait="5000" maxActive="4"/>
	</GlobalNamingResources>

	2) 在Tomcat 根目录下的conf\context.xml 配置:
	<ResourceLink name="myjdbc" global="jdbc/mydatasource" type="javax.sql.DataSourcer" />

	ctx.lookup("java:comp/env/myjdbc");
方法二
	整合在conf\context.xml 中配置:
	<Resource name="jdbc/mydatasource" type="javax.sql.DataSource"
	driverClassName="org.h2.Driver" url="jdbc:h2:tcp://localhost/~/test" username="sa" password=""
	maxIdle="2" maxWait="5000" maxActive="4" />

	ctx.lookup("java:comp/env/jdbc/mydatasource");

server.xml 中 在文件未尾的</Host>之前加
	<Context docBase="D:/program/eclipse_java_workspace/J_SpringPortlet/WebContent"  
			 path="/J_SpringPortlet" reloadable="true"  crossContext="true">
		<!-- 
		<Resource name="jdbc/mydatasource" type="javax.sql.DataSource"
		driverClassName="org.h2.Driver" url="jdbc:h2:tcp://localhost/~/test" username="sa" password=""
		maxIdle="2" maxWait="5000" maxActive="4" />
		-->
	</Context>


在tomcat-7\conf\Catalina\localhost\建立 SpringPortlet.xml文件,内容
<Context  docBase="D:/program/eclipse_java_workspace/J_SpringPortlet/WebContent"
		 path="/SpringPortlet" reloadable="true"  crossContext="true">
	<Resource name="jdbc/mydatasource" type="javax.sql.DataSource"
		driverClassName="org.h2.Driver" url="jdbc:h2:tcp://localhost/~/test" username="sa" password=""
		maxIdle="2" maxWait="5000" maxActive="4" />
</Context>


META-INF目录下加context.xml文件
<Context crossContext="true" /> 
<!--为Tomcat使用,eclipse生成的conf/server.xml  <Context 中会加上crossContext="true"  -->

部署方法 
$CATALINA_BASE/conf/[enginename]/[hostname]/context.xml		named [webappname].xml 
$CATALINA_BASE/webapps/[webappname]/META-INF/context.xml
[enginename]是Catalina,[hostname]是localhost
 
 
放jdbc.jar包
web.xml中
<resource-ref>
 <res-ref-name>jdbc/Modeling</res-ref-name>
 <res-type>javax.sql.DataSource</res-type>
 <res-auth>Container</res-auth>
</resource-ref>  

Context initCtx = new InitialContext();

Context envCtx = (Context) initCtx.lookup("java:comp/env");
DataSource dataSource = (DataSource) envCtx.lookup("jdbc/Modeling");
con = dataSource.getConnection();
//--------
//DataSource dataSource = (DataSource) initCtx.lookup("java:comp/env/jdbc/Modeling");
//con = dataSource.getConnection();
 
 
---------启用Tomcat 的HTTPS协议
keytool -genkey -alias tomcat -keyalg RSA -keystore C:/temp/.keystore
提示输入密码,可以使用Tomcat的默认值changeit,一些其它的信息也要写
修改tomcat目录下的server.xml文件，去掉以下注释
<!--
<Connector port="8443" protocol="HTTP/1.1" SSLEnabled="true"
		   maxThreads="150" scheme="https" secure="true"
		   clientAuth="false" sslProtocol="TLS" />   // Transport Layer Security安全传输层协议(TLS),Secure Sockets Layer (SSL),
-->
并加入keystoreFile="C:/temp/.keystore"
默认是读用户主目录下的.keystore文件
	
	
---tomcat路径文件名支持中文
<Connector port="8080" protocol="HTTP/1.1" 
    connectionTimeout="20000" redirectPort="8443" URIEncoding="utf-8"  />
以上URIEncoding="utf-8"是新加的


---tomcat路径不区分大小写
<Context  path="/mytest" docBase="D:\\Program\\apache-tomcat-6.0.18\\apache-tomcat-6.0.18\\webapps\\test" 
	caseSensitive="false"  reloadable="true"/>
注意这里的caseSensitive="false"

----web.xml <auth-method>FORM</auth-method> ,FORM和BASIC 的密码保存在数据库 tomcat/docs/realm-howto.html
	create table users (
	  user_name         varchar(15) not null primary key,
	  user_pass         varchar(15) not null
	);
	create table user_roles (
	  user_name         varchar(15) not null,
	  role_name         varchar(15) not null,
	  primary key (user_name, role_name)
	);
	
	jdbc.jar 放入tomcat/lib
	
	<Realm className="org.apache.catalina.realm.JDBCRealm"
	      driverName="org.h2.Driver" connectionURL="jdbc:h2:tcp://localhost/~/test" connectionName="sa" connectionPassword =""
	       userTable="users" userNameCol="user_name" userCredCol="user_pass"
	   userRoleTable="user_roles" roleNameCol="role_name"/>
	   
	insert into users(user_name,user_pass) values('lisi','123');
	insert into users(user_name,user_pass) values('zhang','123');
	insert into user_roles(user_name,role_name) values('lisi','develop');
	insert into user_roles(user_name,role_name) values('zhang','market');

	
	
Tomcat 日志文件格式
server.xml中在<Host>下有配置,并有提示文档在  /docs/config/valve.html
	   <Valve className="org.apache.catalina.valves.AccessLogValve" directory="logs"
               prefix="localhost_access_log" suffix=".txt"
               pattern="%h %l %u %t &quot;%r&quot; %s %b" />
	%h - Remote host name (or IP address if enableLookups for the connector is false)
	%l - Remote logical username from identd (always returns '-')
	%u - Remote user that was authenticated (if any), else '-'	
	%t - Date and time, in Common Log Format
	%r - First line of the request (method and request URI)
	%s - HTTP status code of the response
	%b - Bytes sent, excluding HTTP headers, or '-' if zero
	
	示例
	35.8.33.21 - - [06/Jul/2016:00:00:00 +0800] "POST /loan-acct-web/remoting/madeLoanProcessQuerySimpleFacadehession HTTP/1.0" 200 987

linux 下Tomcat会一直向catalina.out这个文件写(windows下就不会)
catalian.sh 脚本中　对 CATALINA_OUT 的说明,
手工设置　CATALINA_OUT="$CATALINA_BASE"/logs/catalina.`date +"%Y-%m-%d"`.out　在判断后加,为了CATALINA_BASE存在值 ,这样只能在重启时是新文件


=============================WebSphere full profile for Developer 8.5 版本
C:\Program Files (x86)\IBM\WebSphere\AppServer\bin\ProfileManagement下
开始 ->WebSphere Customerization Toolbox(wct.ba) 工具 ,Profile Management Tool(pmt.bat)

C:\Program Files (x86)\IBM\WebSphere\AppServer\profiles\AppSrv01    安装时有LDAP的东西

就在C:\Program Files (x86)\IBM\WebSphere\AppServer\bin\ProfileManagement\wct.bat 或 pmt.bat 有界面的
建立profile时只能选择一个特性如SCA,可在建后Augment再加其它特性如XML

启动服务
可用开始菜单,或者services.msc中的IBM Websphere
以管理员运行cmd ,AppSrv01\bin\startServer.bat server1
日志在:AppSrv01\logs\server1\startServer.log 和 stopServer.log
开始菜单中的admin console , http://localhost:9060/ibm/console   可以不输入任何东西直接登入,也可随便输入

AppSrv01\bin>serverStatus.bat -all 
也可用
serverStatus.bat -all -user admin -password admin  
显示服务器名:server1

AppSrv01\bin>stopServer.bat server1 停止服务


C:\Program Files (x86)\IBM\WebSphere\AppServer\profiles\AppSrv01\installedApps\<主机名>Node01Cell\  是ear解压目录 xx.ear


服务器->WebSphere Application Server-> server1 > 右则的 "Java 和进程管理"下的"进程定义" > 右则的 "Java 虚拟机" >右则的 "定制属性",可增加如user.dir的系统属性,也可设置Xmx等


服务器->WebSphere Application Server->点击server1进入->点击 通信下的[端口] ,查看
	BOOTSTRAP_ADDRESS 	my-PChostname  	2809  
	WC_adminhost 	*   	9060   
	WC_defaulthost 	*   	9080   

环境->共享库->选择有节点值和服务器值的那一项->新建->起名all_lib,输入 类路径 是所有.jar包所在的目录,可以有多行
应用程序->企业应用程序->进入项目->进入-> 引用下的"共享库引用" ,复择项目-> 点[引用共享库] 按钮,选择自己建立的all_lib
	

资源->JDBC->JDBC提供程序->下拉选择选带有节点值和服务器值 ->新建->数据库类型：Oracle/SQL Server/DB2,没有MySQL只能自定义,实现类名:com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource
		类路径中可以输入绝对的.jar路径 
资源->JDBC->数据源->新建-> 下拉选择选带有节点值和服务器值,JNDI名称不要带有"/"如 jndi_mysql->要选择现有的JDBC提供程序,选择刚刚建立的->到"设置安全性别名"页中有"全局 J2C 认证别名"点入->新建设置用户名密码
		[组件管理的认证的别名]  [容器管理的认证别名] 选择刚刚建立的
资源->JDBC->数据源->选建立的->其他属性中的"定制属性"->URL->输入jdbc:mysql://localhost:3306/databasename, 可以测试连接如不成功,就重启服务器

如建立数据源是Oracle的要在"定制属性"中手动增加user,password属性,测试OK


Properties prop=new Properties();
prop.put(Context.INITIAL_CONTEXT_FACTORY,"com.ibm.websphere.naming.WsnInitialContextFactory");
prop.put(Context.PROVIDER_URL, "iiop://localhost:2809/");
InitialContext ctx=new InitialContext(prop);
DataSource ds=(DataSource)ctx.lookup("jndi_mysql");//不要有/字符,不能加java:xx
Connection conn=ds.getConnection();

C:\Program Files (x86)\IBM\WebSphere\AppServer\runtimes\com.ibm.ws.admin.client_8.5.0.jar  大小50MB ,com.ibm.ws.orb_8.5.0.jar 大小2M 
要用IBM的JAVA_HOME=C:\Program Files (x86)\IBM\WebSphere\AppServer\java,是1.6.0版本


部署war包

[应用程序]->[资产]->[导入]->选择war包,怎么是上传不了?????
[应用程序]->[企业级应用程序 ]->[新建]->输入名称->[添加资产]->选择刚才建立的->下一步->下一步->下一步->下一步->改[上下文根 ]为web路径->下一步->[完成]->单击[保存]连接
[应用程序]->[企业级应用程序 ]  中可以看到部署的 ，选中->启动按钮
[WebSphere 企业应用程序]中也可以安装war包  选择安装的war项目  ->"Web 模块的上下文根",可以修改 /xx
http://localhost:9080/[上下文根 ]

没有eclipse插件,只能用 Rational Software Architect for WebSphere(RSA更全有UML) 或 Rational Application Developer for WebSphere(RAD)
RAD建立web项目要生成xxxEAR项目,才可在RAD的集成的websphere加入项目,ear包只加了war包,可以没有application.xml
---RAD自动建立war包/WEB-INF/ibm-web-ext.xml
<?xml version="1.0" encoding="UTF-8"?>
<web-ext		 xmlns="http://websphere.ibm.com/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://websphere.ibm.com/xml/ns/javaee http://websphere.ibm.com/xml/ns/javaee/ibm-web-ext_1_0.xsd" version="1.0">

	<reload-interval value="3"/>
	<enable-directory-browsing value="false"/>
	<enable-file-serving value="true"/>
	<enable-reloading value="true"/>
	<enable-serving-servlets-by-class-name value="false" />
</web-ext>
---RAD自动建立war包/WEB-INF/ibm-web-bnd.xml
<?xml version="1.0" encoding="UTF-8"?>
<web-bnd  			xmlns="http://websphere.ibm.com/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xsi:schemaLocation="http://websphere.ibm.com/xml/ns/javaee http://websphere.ibm.com/xml/ns/javaee/ibm-web-bnd_1_0.xsd" version="1.0">
	<virtual-host name="default_host" />
</web-bnd>
---
RAD的集成的websphere不能直接增加war包项目,可先在webphere中部署war包,同样的项目在RAD中修改java文件可以生效,但修改jsp,xml没有效果???

RAD集成后webpshere的目录在runtimes\base_v7\下

base_v7\profiles\AppSrv01\installedAssets,是在Business-level applications建立联时生成的,目录中的所有的.jar是解压形式(profile中要urgment加SCA特性)
base_v7\lib\ext
base_v7\profiles\AppSrv01\config\cells\<HostName>Node03Cell\resources.xml  有配置的DataSource
base_v7\profiles\AppSrv01\config\cells\<HostName>Node03Cell\nodes\<HostName>Node03\servers\server1\server.xml 中配置maximumHeapSize,如太大会导致服务启不来,要修改

Application servers > server1->Communications 中的Ports中有占用的所有的端口

---建立JMS Queue
Service integration->Buses->new一个叫 InternalJMS ,进入刚建立的 InternalJMS->Bus members ->Add 一个->单择Server->如是第一次建立要重启Server,检查建立的bus memeber中的server,进入确认为绿箭头(started)
进入刚建立的 InternalJMS ->Destinations ->new 一个,选择Queue,命名 myQueueDest

和Resources -> JMS ->Queue建立是一样的效果
Resources -> JMS -> JMS Providers -> Default messaging provider->Queues ->new 一个,Bus name选择上面建立的 InternalJMS,Queue name选择上面建立的 myQueueDest
---建立JMS QueueFactory
Resources -> JMS -> JMS Providers -> Default messaging provider->Queue connection factories->new 一个,在Bus name中选择上面建立的InternalJMS,
			Provider Endpoints中写localhost:7277:BootstrapBasicMessaging(对Factory和Activation specification) ,7277是没被占用的,格式为<IP Address of the Host Machine>:<SIB_ENDPOINT_ADDRESS >:BootstrapBasicMessaging

---Queue错误关联
在Bus中建立两个Queue 为 my_queue 和 my_queue_error
进入 my_queue,在Exception destination中单选Specify,输入 my_queue_error,修改Maximum failed deliveries per message为3
Resources->JMS -> Activation specifications ->选scope ,new->Default messaging provider ->输入Destination JNDI name为 my_queue,
		在Automatically stop endpoints on repeated message failure中复选Enable,Sequential failed message threshold 设3
---

		
=======================GlassFish 4
Oracle Enterprise Pack for Eclipse  中有 GlassFish 的插件

C:\glassfish4\glassfish\bin\asadmin.bat start-domain domain1 有日志提示4848端口 
C:\glassfish4\glassfish\domains\domain1\logs\server.log

C:\glassfish4\glassfish\bin\asadmin.bat stop-domain domain1

http://127.0.0.1:8080/ 有连接进入  http://localhost:4848/  GlassFish 控制台
http://127.0.0.1:8080/<部署的应用名>

C:/glassfish4/glassfish/docs/quickstart.html 有所有的端口

GlassFish 控制台 ->应用程序->部署->选择war包
GlassFish 控制台 ->配置->Server config->JVM设置->查看调试选项 端口为 9009,复选 "调试" 中的 "启用" ->保存->提示重启服务
右击项目->debug as -> Debug configuration ...->建一个 Java Remote Application->端口写 9009,检查项目的选择,就可Debug Web项目


建立数据源,资源->JDBC->先建立JDBC连接池,JDBC.jar放在C:\glassfish4\glassfish\domains\domain1\lib\ext,重启服务后再"试通"(如果先放mysql.jar再启建立连接池时设置的属性会变多)
	再建立JDBC资源
C:\glassfish4\glassfish\modules\有javax的其它jar包
C:\glassfish4\glassfish\modules\glassfish-naming.jar ,eclipse引入后会自动引用其它的.jar
//必须把编译后的class运行在glassfish中才行
Properties props=new Properties();
props.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.enterprise.naming.SerialInitContextFactory");
props.put(Context.PROVIDER_URL,"localhost:3700");//可加iiop://
InitialContext ctx=new InitialContext(props);
DataSource ds=(DataSource)ctx.lookup("jndi_mysql");//不用加java:xxx
Connection conn=ds.getConnection();


=========================JBOSS 7.1.1 使用  (改名为 WildFly 8.1)
LGPL 
https://docs.jboss.org/author/dashboard.action 有很的Doc,如JBoss AS 7.1 ,JBoss OSGi,Portlet Bridge
https://docs.jboss.org/author/display/JBASDOC/Home

安装JBOSS Tools 的eclipse插件时选择jBoss Web and Java EE Developement -> JBossAS Tools 就有启动停止的JBOSS AS-7.1的工具了
		jBoss Application Development -> freemarker IDE,Hibernate Tools
		jBoss Mobile Development(要jetty) -> Cordova Simulator , Mobile Browser Simulator
		jBoss Maven support
		
启动: bin/standalone.bat 			domain.bat  是对 Cluster的启动三个服务
关闭: Ctrl+C 或 jboss-cli.bat --connect --command=:shutdown 

http://127.0.0.1:8080 ->Administration Console  http://127.0.0.1:9990/console (启动日志也有提示)
提示要建立用户,使用bin/add-user.bat ,分为managent user(登录用的)和application user两种用户
 
management user->默认Realm (ManagementRealm)
建立后提示保存在
jboss-as-7.1.1.Final\standalone\configuration\mgmt-users.properties
jboss-as-7.1.1.Final\domain\configuration\mgmt-users.properties

application user->默认Realm (ApplicationRealm)  ,建立时密码不能和用户名相同
会要输入用户属于什么角色,建立的用户不能登录http://127.0.0.1:9990/console
jboss-as-7.1.1.Final\standalone\configuration\application-users.properties
jboss-as-7.1.1.Final\domain\configuration\application-users.properties


启动服务器后,双击jboss-cli.bat会连接9999端口,可以有很多的命令如connect,支持tab键提示命令,
如ls  /subsystem=deployment-scanner:read-resource(recursive=true) ,在按:和(时tab都会提示


配置debug模式
bin/standalone.conf
打开下面
#JAVA_OPTS="$JAVA_OPTS -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=n"
删 -XX:MaxPermSize(对JDK8)

部署
cp myapp.war $JBOSS_HOME/standalone/deployments/  
取消部署 
rm $JBOSS_HOME/standalone/deployments/myapp.war.deployed

重新部署删 .undeployed

x.war.pending

 

\jboss-as-7.1.1.Final\standalone\configuration\standalone.xml 可以修改监听端口
	 <socket-binding name="http" port="8080"/>  
配置deployment-scanner 只对standalone模式有效,开发时对修改的文件,每隔5秒更新

jboss-as-7.1.1.Final\standalone\configuration\standalone.xml 中 deployment-timeout="300"

部署CXF 报 Cannot publish wsdl to \standalone\data\wsdl\xx-web.war\XXService.wsdl
在JBoss-7/standalone/configuration/standalone.xml中要注释  <extension module="org.jboss.as.webservices"/> 
或者在 jboss-deployment-structure.xml 中加  <exclusions>  <module name="org.jboss.as.webservices" />

wild-fly-8.1 中报  Apache CXF library (cxf-xxx.jar) detected   jboss-deployment-structure.xml  中加 
<jboss-deployment-structure >
    <deployment>
        <exclude-subsystems>
            <subsystem name="webservices" />
        </exclude-subsystems>
    </deployment>
</jboss-deployment-structure>

JNDI 数据源
standalone.xml	修改
	<subsystem xmlns="urn:jboss:domain:datasources:1.0">
	驱动对应jboss-as-7.1.1.Final\modules\com\h2database\h2\main\module.xml
http://127.0.0.1:9990/console 界面可以看到,点右上角的Profile 后可以配置

Properties env = new Properties();
env.put(Context.INITIAL_CONTEXT_FACTORY, org.jboss.naming.remote.client.InitialContextFactory.class.getName());//jboss-as-7.1.1.Final\bin\client\jboss-client.jar  3M
env.put(Context.PROVIDER_URL, "remote://localhost:4447");
env.put(Context.SECURITY_PRINCIPAL,"user");//使用add-user.bat,Management User没用的,要Application User可以没有Role
env.put(Context.SECURITY_CREDENTIALS,"pass");
Context context=new InitialContext(env);//.class在容器外,要加参数

DataSource ds=(DataSource)context.lookup("datasources/ExampleDS");
Connection conn=ds.getConnection();
conn.close();
context.close();


java:comp - The namespace is scoped to the current component (i.e. EJB)
java:module - Scoped to the current module
java:app - Scoped to the current application
java:global - Scoped to the application server

java:jboss	服务器共享
java:/
java:jboss/exported 是jBoss 7.1, entries bound to this context are accessible over remote JNDI.

如果调用者的.class文件不在JBoss中只能用java:jboss/exported

//从standalone-full.xml中复制过来修改了java:/jboss为java:/
DataSource ds=(DataSource)context.lookup("datasources/ExampleDS");//.class容器中OK,配置是<datasource jndi-name="java:/datasources/ExampleDS" pool-name="ExampleDS">

	
JMS
复制standalone-full.xml 中的
	<extension module="org.jboss.as.messaging"/>
	...
	<subsystem xmlns="urn:jboss:domain:messaging:1.1">
	..
	<socket-binding name="messaging" port="5445"/>
	<socket-binding name="messaging-throughput" port="5455"/>
	  

ConnectionFactory factory = (ConnectionFactory)context.lookup("jms/RemoteConnectionFactory");//.class在容器外OK，对应配置java:jboss/exported/jms/RemoteConnectionFactory
Queue queue = (Queue)  context.lookup("jms/queue/test");//.class在容器外OK，对应配置java:jboss/exported/jms/queue/test

Context context=new InitialContext();//.class在容器内,不加参数
ConnectionFactory factory = (ConnectionFactory)context.lookup("ConnectionFactory");//.class在容器内OK,ConnectionFactory对应的配置是java:/ConnectionFactory
Queue queue = (Queue)  context.lookup("queue/test");//.class在容器内OK,queue/test对应的配置是 <entry name="queue/test"/>


Object str=context.lookup("java:global/mystring");//.class容器中OK
对应web.xml中
<env-entry>
	<env-entry-name>java:global/mystring</env-entry-name>
	<env-entry-type>java.lang.String</env-entry-type>
	<env-entry-value>Hello World</env-entry-value>
</env-entry>
 
WebRoot的WEB-INF 或 META-INF 下加 jboss-deployment-structure.xml
<jboss-deployment-structure>
    <deployment>
	    <dependencies>
	    	<module name="com.oracle"/> 
				<!-- 找对应jboss 的modules/com/oracle/main/module.xml 文件
					内容对应是 name="com.oracle" ,<resource-root path="properties"/>可是目录也可是jar包做classpath -->
	  	</dependencies>
        <!-- Exclusions allow you to prevent the server from automatically adding some dependencies -->
        <exclusions>
           <module name="org.apache.log4j" />
            <module name="org.slf4j" /> 
            <module name="org.slf4j.impl" />
            <module name="org.apache.cxf" />
			<module name="org.jboss.as.webservices" />
        </exclusions>
    </deployment>
</jboss-deployment-structure>


Https  服务建立

==============================weblogic使用
http://edocs.weblogicfans.net/wls/docs92/index.html   weblogic 9.2在线文档
http://www.oracle.com/technetwork/middleware/weblogic/documentation/bea-084522.html  weblogic 10.3.3在线文档
http://download.oracle.com/docs/cd/E14571_01/wls.htm

https:443,7002(weblogic)


nohup ${DOMAIN_HOME}/bin/startWebLogic.sh $* 1> console.log 2>&1 &


WebLogic 9.x / 10.x /10gR3 均起作用。
-Dweblogic.threadpool.MinPoolSize=100
-Dweblogic.threadpool.MaxPoolSize=500

修改端口7001 到 88
也可以在界面中,AdminServer中可以修改
C:\bea\user_projects\domains\base_domain\config\config.xml
<server>
    <name>AdminServer</name>
    <listen-address></listen-address>
</server>
改为
<server>
    <name>AdminServer</name>
    <ssl>
      <enabled>false</enabled>
    </ssl>
    <listen-port>88</listen-port>
    <listen-address></listen-address>
</server>

<server>中使用ssl
<ssl>
      <name>MDMAdminServer</name>
      <enabled>true</enabled>
      <hostname-verification-ignored>true</hostname-verification-ignored>
      <listen-port>6002</listen-port>
</ssl>


WEB-INF目录下一定要有一个weblogic.xml文件 ,可以通过eclipse插件建立weblogic web project来生成 
开发模式 可以eclipse集成debug jsp和java文件


使用eclipse插件时注意，不要复选 disable automatic publish to server


//自动部署
可以通过 console来部署一个目录(有WEB-INF/web.xml,war包的解压),也可以是ear包的解压(中有很多war),
配置目录后,在<域目录>/config/config.xml中,可以修改
<app-deployment>
    <name>testWS</name>
    <target>AdminServer</target>
    <module-type>war</module-type>
    <source-path>G:\testWS</source-path>
    <security-dd-model>DDOnly</security-dd-model>
</app-deployment>
<app-deployment>
    <name>MSV-test</name>
    <target>AdminServer</target>
    <module-type>ear</module-type>
    <source-path>F:\work\MDM\smp_dev\MSV-test</source-path>
    <security-dd-model>DDOnly</security-dd-model>
</app-deployment>
都是在根下



ear包中的META-INF目录中的weblogic-application.xml
<?xml version="1.0"?>
<weblogic-application xmlns="http://www.bea.com/ns/weblogic/90" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<prefer-application-packages>
		<package-name>org.apache.xerces.*</package-name>
		<package-name>org.apache.commons.*</package-name>
		<package-name>org.apache.log4j.*</package-name>
	</prefer-application-packages>
</weblogic-application>


base_domain->Enviroment->servers->AdminServer->View JNDI Tree
base_domain->Services->JDBC->Data Source 建JNDI 数据源
base_domain->Services->Message-> JMS Modules->建立一个Module (jms配置文件在domain目录下的config中)->
建立ConnectionFactory时默认选择AdminServer不要选择subdeployment

建Topic,Queue时要建Subdeployment(用来指定哪个JMS Server上)
				 JMS Servers 可指定Persistent Store即保存到文件还是JDBC,指定target,哪个ManageServer,每个多一个migratable

Monitor->show Message 中可以设置一个selector的名字

建立JMS Modules时,会保存在[域]/config/自己配置的目录名/自己配置的文件名   (配置保存在这里)
也可把配置文件(META-INF/weblogic-application.xml,My-jms.xml)打在.ear包中

Subdeployment是指定JMS Module对应 JMSServer


要为Topic配置 Subdeployment,要target中有值
加入C:\bea\weblogic92\server\lib\weblogic.jar　到classpath中

多台weblogic域 JMS通信 要域"密码"一样,这里的密码是在console界面中的第一项即"域名"->security标签->下方的Advanced->Credential:处的密码

weblogic 有一种Distributed 的queue或者topic ,
	建立Distributed Queue不要复选 Allocate Members Uniformly ,会提示选择非Distributed的Destination,使用时配置成Distributed的JNDI, 起到对多个queue或topic的分发作用,如是queue会对中的每个成员发送消息,无论使用Listener，还是receive结果是乱的，而且一次还取不完(即会把Member中的一个收完)
	建立Distributed Queue复选 Allocate Members Uniformly,Member标签里是没有成员的,Monitoring也没有数据,但是可以使用JMS代码收到消息的,使用HermesJMS工具可以看到消息
		 对cluster 环境, 复选 Allocate Members Uniformly,默认会选cluster,和cluster中的所有Manage Server成员
		 对cluster 环境, 不要复选 Allocate Members Uniformly ,会提示选择非Distributed的Destination
日志目录C:\bea\user_projects\domains\base_domain\servers\AdminServer\logs

C:\bea\weblogic92\server\lib\ojdbc14.jar   OracleJDBC驱动


eclipse插件的停止服务器
C:/bea/jrockit_150_12/bin/java -classpath C:/bea/weblogic92/server/lib/weblogic.jar weblogic.Admin -url t3://localhost:7001 -username weblogic -password weblogic FORCESHUTDOWN AdminServer 
启动

对于Admin Serve域目录下->建文件：boot.properties 就可以在product mode下不用输入密码,OK
password=123456
username=weblogic
#文件未尾不能有空格
启动后把文件复制到domains\MotiveSMP\servers\AdminServer下,并生成目录security\boot.properties, 这里的文件是加密的,就可以删除明文的了

方法二,也可放在servers/AdminServer的security目录下,没有security目录要手工建立



加用户Security Realms->myrealm->Users and Groups

在weblogic.Xml中，加入 单位是秒,可以ear目录中的web项目的其它类,
如果有是线程里的代码则无效,如果是struts1使用了<tiles>也不行的
    <container-descriptor>
       <servlet-reload-check-secs>5</servlet-reload-check-secs>
       <prefer-web-inf-classes>true</prefer-web-inf-classes>
    </container-descriptor>



//产品模式转开发模式

setDomainEnv.cmd文件中修改set PRODUCTION_MODE=true 为set PRODUCTION_MODE=,即删true
setDomainEnv.sh文件中修改PRODUCTION_MODE="true"为PRODUCTION_MODE=""
set MEM_ARGS=-Xms256m -Xmx512m  设置JVM参数 

weblogic92\common\bin\quickstart	
C:\bea\weblogic92\common\bin\wlst.bat //交互命令
cd c:\bea\wlserver_10.0\common\bin  //控制台安装
Windows: config.cmd -mode=console
UNIX: sh config.sh -mode=console
  

 //静态安装
server922_win32.exe -mode=silent -silent_xml=silent.xml
silent.xml
	<bea-installer> 
		  <input-fields>
			  <data-value name="BEAHOME"                  value="c:/bea/" />
			  <data-value name="USER_INSTALL_DIR"         value="c:/bea//weblogic92" />
			  <data-value name=" WLW_INSTALL_DIR" value="c:/bea//workshop92" />
			  <data-value name="INSTALL_MERCURY_PROFILING_TOOLS" value="false"/>

			  <data-value name="INSTALL_NODE_MANAGER_SERVICE"   value="yes"  />
			  <data-value name="NODEMGR_PORT"                   value="5556" />
			  <data-value name="COMPONENT_PATHS" value="WebLogic Server/Server|WebLogic Server/Web Server Plug-Ins" />
			  <data-value name="INSTALL_SHORTCUT_IN_ALL_USERS_FOLDER"   value="yes"/>
		 
		</input-fields> 
	</bea-installer>


server/common/bin/config_builder 图形界面,用来创建weblogic模板
可以选择基于已有模板,也可选择domain目录,会生成user_templates目录下一个.jar文件

可以 control标签中停止Admin Server

Server标签->后加点 Customize 可以显示更多的列,有更多的信息

/weblogic92/common/bin/wlst.sh
或者用
/weblogic92/server/bin/setWLSEnv.sh 后java weblogic.WLST
>connect('weblogic','weblogic','t3://127.0.0.1:7001')
>ls()
>help()  提示hel('all')

Manage Server的logging标签有 Files to retain 7
>edit()
>cd('Servers')    ###cd('..')   cd ('Servers/AdminServer')
>cd('AdminServer') 
>pwd()
>cd('Log')
>cd('AdminServer')
>startEdit() ,相当界面上Lock&Edit
>set('FileCount','4') 刷新界面发现值变了
>save()  相当于点页的save按钮
>activate()  
>disconnect()
>exit()

会修改域的/config/conig.xml文件

java weblogic.WLST batch.txt 可读文件中的命令


weblogic92/samples/domains/wl_server/startWebLogic.sh 启动示例
weblogic92/samples/server/examples/build 有示例
weblogic92/samples/server/docs/ 有文档
deploy以后可以在Target标签中修改部署的目录

weblogic 自动的示例　PointBase数据库 
/weblogic92/common/eval/pointbase/tools/startPointBase.sh
database name:demo
port: 9092
user:PBPUBLIC
pass:PBPUBLIC, 
测试表SYSTABLES

startPointBaseConsole.sh 是一个图形界面


JDBC Datasource 也可以修改Target,Monitor标签中可以Test

Manage Sever->General->Server Start子标签,可以加CLASSPATH,也可以配置启动用户名密码

Manage Sever->Protocol->Channels->协议用Http->Listen Port:8008是新端口,External Listen Port:8001是对应已经存在的端口
原来使用8001的应用,也可以同使用8008端口

点域名->复选Enable Administration Port,Administration Port:9002,
原来的http://127.0.0.1:7001/  console  就不能用了,只可用https://127.0.0.1:9002



---使用https的设置
Manage Server打开 SSL Listen port Enabled,设置端口号

生成.jks 
keytool -genkey -v -alias dwkey -keyalg RSA -keysize 512
		-dname "CN=localhost,OU=Dizzyworld Web,O=dizzyworld\,Inc.,C=US" 
		-keypass dwkeypass -validity 365 -keystore dw_identity.jks 
		-storepass dwstorepass
生成.pem 给CA
keytool -certreq -v -alias dwkey -file dw_cert_request.pem -keypass dwkeypass
		-storepass dwstorepass -keystore dw_identity.jks

Server中有一个Keystores标签,默认是使用 Demo Identity and Demo Trust,现在使用Custom Identity and Java Standard Trust

Custom Identity Keystore:		是生成的.jks文件	
Custom Identity Keystore Type:	JKS
Custom Identity keystore passphrase:是keytool　-genkey -storepass的值
Trust栏中的密码自已写

SSL标签中
Identity and Trust Locations:选择KeyStores
Private Key Alias:是keytool -genkey -alias 的值
Private Key Passphrase:keytool -genkey  -keypass的值

--
	security Realm->User Lockout
	domain->security->unlock User 解锁用户
---证书

建立一临时目录
复制C:\bea\weblogic92\server\lib\CertGenCA.der和CertGenCAKey.der文件 

keytool -noprompt -import -trustcacerts -alias CA -file CertGenCA.der -keystore myKeyStore.jks -storepass password

	生成 myKeyStore.jks

CLASSPATH=C:\bea\weblogic92\server\lib\weblogic.jar
java  -cp C:\bea\weblogic92\server\lib\weblogic.jar utils.CertGen passowrd PCATCert PCATKey export PCAT
	PCAT是Admin的主机名

	生成 
	PCATCert.der
	PCATCert.pem
	PCATKey.der
	PCATKey.pem

java utils.CertGen password pingCert pingKey export ping
java utils.der2pem CertGenCAKey.der
java utils.der2pem CertGenCA.der
copy /b PCATCert.pem + CertGenCA.pem PCATCertChain.pem
copy /b pingCert.pem + CertGenCA.pem pingCertChain.pem
keytool -import -alias PCACert -file PCATCert.pem -keypass password -keystore myKeyStore.jks -storepass password
keytool -import -alias pingCert -file pingCert.pem -keypass password -keystore myKeyStore.jks -storepass password

--fail
java utils.ImportPrivateKey myKeyStore.jks password PCATKey password PCATCertChain.pem PCATKey.pem
java utils.ImportPrivateKey myKeyStore.jks password pingKey password pingCertChain.pem pingKey.pem
java utils.ValidateCertChain -jks PCATKey myKeyStore.jds password
java utils.ValidateCertChain -jks pingKey myKeyStore.jds password
keytool -list -keystore myKeyStore.jks -v
java utils.der2pem CertGenCAKey.der


复制myKeyStore.jks 到Admin的域目录下,和Manager的common/nodemanager目录下


---------------weblogic9.2 Cluster
F5,Radware是loadBlance硬件

http://edocs.weblogicfans.net/wls/docs92/cluster/setup.html

config.sh的界面配置 
在 Do you want to customize any of the following options?标签 选 .YES ->
配置 Aministratrion Server标签,默认AdminServer,7001 用来使用console的 ->
配置 Managed Servers标签 ,可加多个自定义名的服务器,使用未被使用的端口->
配置 Cluster标签 ,输一个 Cluster名,Mulicast Address任何输,Mulicat port任何未使用的,Cluster address 不写->
指定 Servers to Cluster标签,如有一个未选入Cluster,会到 Create Http poxry  Applications->
配置 Machines标签, Name* 的默认值是new_Machine_1,Node Manager Listener port 默认是 5556 ,可选Unix机器 ->
指定 Servers to Machines标签,


每个Machine有自己的NodeManager,选择操作系统
启动manager server,在windows 下可使用start startManagedWeblogic.cmd managed_1 就可以启动
start命令是开一个新的CMD窗口

/opt/bea/weblogic92/common/nodemanager/nodemanager.log

Machin可以加Server ,但Manager Server不能运行
NodeManager是启动Machine的,必须在运行,才可以在界面中远程启动ManageServer

建立Manage Server时不要写Lisenter Address ,如写127.0.0.1 ,只监听一个端口127.0.0.1:8001,则不能远程登录

----NodeManager管理,如Machine和AdminServer不在同一台机器上
setWLSEnv.cmd初始环境变量
weblogic92/server/bin/startNodeManager.sh  <本机IP> 5556 ###来启动
启动后weblogic92/common/nodemanager 目的是目录多一些文件 
java weblogic.WLST或者wlst 
>connect() 会提示输入AdminServer的用户名，密码，url
>help('nmEnroll')
>nmEnroll(r'c:/bea/weblogic92/common/nodemanager')  
##注意有个r (NodeManager enroll),把NodeManage服务器注册到AdminServer上,本地weblogic目录c:/bea/weblogic92/common/nodemanager中多了config和security目录
>exit()
再重新启动 ./startNodeManager.sh  <本机IP> 5556 ###来启动

注意,5556端口要和Admin端配置的相同,Machine的IP要<本机IP> ,Machine->Monitoring中监视状态要是Reachable,可以不用运行nmEnroll
也可以使用weblogic92/common/bin/startManagedWebLogic.sh 但没有stop
----




如做过cluster
JDBC Data Sources >JDBC Pool名->Targets ->可选择All servers in the cluster 和 Part of the cluster 表示数据源为哪些机器使用

Test JNDI 连接
Services->JDBC->DataSource->名字->Monitoring标签->Testing 子标签->Test DataSource 按钮



Cluster->点Cluster名 ->control标签->在下面 选择一个 Manager服务器 ->start 按钮 ->等大约4分钟
Server ->点Server名->control标签->.......
等当　Sate列变成RUNNING

启动Manage Server后  域目录下/servers/中会多出一个 managerServer目录,可以看log/[名].log日志,.out文件 Windows 看不了



----重新部署ear包 ,ManageServer 要一直启动的
只AdminServer上 可以仿问/console,只有ManageServer可仿问自己的项目
	Deployment->删除老的SMP.ear
在Deployment->增加新的SMP.ear
启动 SMP.ear

域目录下/servers/S_smp01/stage　目录 ,成功会自动复制Admin的Server.ear，如失败删除
----
	

启动 startManagedWebLogic.cmd managedserver1 http://localhost:7001
停止 stopManagedWebLogic.cmd managedserver1	  t3://localhost:7001 weblogic weblogic

增加 ManagerServer的JVM  在Server->名字->Server start 标签中修改 JVM参数

多播地址是介于 224.0.0.0 和 239.255.255.255 之间的 IP 地址
java -cp weblogic.jar  utils.MulticastTest -N AdminiServer -A 192.168.0.1 -P 7001   ##检测Multicast是否正常


./startManagedWebLogic.sh  Manage_slave http://192.168.11:7001 << EOF
>weblogic
>weblogic
>EOF


在主服务器上AdminServer 配置ManageServer IP为另一台机器,
	在console中不能连接另一台NodeManager来启动  <BEA-090482> <BAD_CERTIFICATE alert was received from????????
在次服务器上启动ManageServer ,使用C:\bea\weblogic92\common\bin\startManagedWebLogic.cmd,会在当前目录生成servers目录

---proxy
使用config.sh建立proxy,proxy 是不在cluster 中的
配置proxy的ManagerServer表示这台ManageServer是代理的(和其它的一样,默认Client Cert Proxy Enabled是没有选择的)，请求指向这个IP,可以分发给其它ManageServer
启动后在AdminServer的console中多了一个BEAProxy4_<cluster_name>_<proxy_name>的应用，在域目录下的apps目录中只有web.xml和weblogic.xml,是部署在proxy的ManagerServer下,同下

自己的应用部署应用时选择All servers in the cluster 而不是proxy，也不是adminServer

\samples\server\examples\src\examples\cluster\sessionrep\inmemrep\defaultProxyApp\WEB-INF\web.xml有示例
把这个项目单独发布到一个standalon的Server上 ,可能要打开不能同的流览器,或者断开服务器

web.xml的内容如下，修改ip和端口。  
<web-app>
  <servlet>
    <servlet-name>HttpClusterServlet</servlet-name>
    <servlet-class>weblogic.servlet.proxy.HttpClusterServlet</servlet-class>
  <init-param>
    <param-name>WebLogicCluster</param-name>
    <param-value>10.130.2.108:7003|10.130.2.108:7005</param-value> 
  </init-param>
  <init-param>
      <param-name>verbose</param-name>
      <param-value>true</param-value>
   </init-param>
   <init-param>
      <param-name>DebugConfigInfo</param-name>
      <param-value>ON</param-value>
   </init-param>
  </servlet>
  <servlet-mapping>
    <servlet-name>HttpClusterServlet</servlet-name>
    <url-pattern>/</url-pattern>
  </servlet-mapping>
  <servlet-mapping>
    <servlet-name>HttpClusterServlet</servlet-name>
    <url-pattern>*.jsp</url-pattern>
  </servlet-mapping>
  <servlet-mapping>
    <servlet-name>HttpClusterServlet</servlet-name>
    <url-pattern>*.htm</url-pattern>
  </servlet-mapping>
  <servlet-mapping>
    <servlet-name>HttpClusterServlet</servlet-name>
    <url-pattern>*.html</url-pattern>
  </servlet-mapping>
</web-app>

weblogic.xml的内容为：
<weblogic-web-app>
  <context-root>/</context-root>
</weblogic-web-app>        

当verbose和DebugConfigInfo开的时候 http://proxyIP:proxyPort/aa.jsp?__WebLogicBridgeConfig 会显示参数
如看到一个IP,可能是NodeManager不通的原因

---Session复制，进入Weblogic console->所有的Manager Server写入相同的名字->cluster->Replication Group: 写入名字,	Preferred Secondary Group:写入名字
对weblogic cluster的Session 复制  Weblogic.xml 配置

--网上的
<session-descriptor>
	<persistent-store-type>replicated</persistent-store-type>
	<sharing-enabled>true</sharing-enabled>
  </session-descriptor>
<context-root>/</context-root>
---boobooke的weblgic 10   内存复制
<session-descriptor> 
	<timeout-secs>300</timeout-secs>
	<invalidation-interval-secs>60</invalidation-interval-secs>
	<persistent-store-type>replicated_if_clustered</persistent-store-type>
</session-descriptor>
部署时在Install Application Assisant步(选择cluster->all server后面)中要在Source accessibility中选择copy this application into every target for me


2.所有放入session的类都要序列化。

3.Customer customer = (Customer)session.getAttribute(KEY_CUSTOMER);  
customer.setName("funcreal");  
在单机环境下，session中的变量customer的name属性就被更改了

然而在集群环境下，仅仅这样做是不能触发session同步机制的。必须要把customer变量在重新放入session中，即：
session.setAttibute(KEY_CUSTOMER, customer);  

备份 session 状态,
	1.数据库复制（通过 JDBC ）
	2.基于文件的复制 
	3.内存中的复制 ,该服务器指定另外一台集群中的服务器作为辅助服务器来存储会话数据的复本

数据库复制(通过 JDBC)
	create table wl_servlet_sessions(
	wl_id varchar2(100) not null,
	wl_context_path varchar2(100) not null,
	wl_is_new char(1),
	wl_create_time number(20),
	wl_is_valid char(1),
	wl_session_values long raw,
	wl_access_time number(20),
	wl_max_inactive_interval integer,
	primary key (wl_id,wl_context_path)
	);

JDBC复制
<session-descriptor>  
	<timeout-secs>300</timeout-secs>
	<invalidation-interval-secs>60</invalidation-interval-secs>
	<persistent-store-type>jdbc</persistent-store-type>
	<persistent-store-pool>SessionDS</persistent-store-pool>   ###配置JDBC DataSource的名字
	<persistent-store-table>wl_servlet_sessions</persistent-store-table>
</session-descriptor>

一个连接断，会自动切换到另一个


-----------------上 weblogic9.2 Cluster

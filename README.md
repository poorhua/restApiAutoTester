#### RestApi自动化测试工具(restApiAutoTester)
    * 概述
  	* RestApi自动化测试工具 
	  * 基于数据驱动的后台服务接口自动化测试工具
	  * 程序读取Excle中的用例，根据用例数据调用待测服务接口， 记录服务返回值，并解析输出结果记录测试结果 
   
#### 1、需求描述
    * 测试用例使用Excel管理,包括输入,预期结果,输出,结果比对,结果统计
    * "Input"页用例主体,存放输入数据,包括用例名标识,主机,路径,用户信息body参数
    * "Baseline"页与用例主体对应,存放每一个用例的预期结果,用于结果比对
    * "Comparison"页里存放对比结果不完全一致的记录，
    * "Result"是一次用例执行结果报告。
    * 程序读取输入数据,调用指定服务方法,获取返回值写入 "Output"页.
    * 程序将结果与 "Baseline"数据进行比对,如结果不完全一致,记录不一致的信息,或是出错信息
    * 用例可以根据文件来配置
    
#### 2、技术架构
	TestNG				测试用例执行整体框架
	ApachePOI			读取Excel用例,写入执行结果
	Jayway rest-assured	调用远程服务接口
	Skyscreamer-JSONassert	用于返回值与预期结果比对

#### 3、项目结构
	main
		java
			com
				autotest
					restApiTest
						Application.java 
						HTTPReqGenTest.java 
				utils
						DataReader.java 
						DataWriter.java 
						HTTPReqGen.java 
						RecordHandler.java 
						SheetUtils.java 
		resources
			http_request_template.txt 
			HttpReqGenTest.xml 
			Http_Request_Workbook_Data.xlsx
		
#### 4、测试过程
	1)HttpReqGenTest.xml,一般不再改动,注意workBook配置,可配置为绝对路径
	2)Http_Request_Workbook_Data.xlsx 根据需要编写Input,Baseline中值
	3)执行com.autotest.restApiTest.Application,
		打成可执行Jar包后执行如: java -jar restApiTest.jar HttpReqGenTest.xml
	
#### 5、测试用例
	即编写Http_Request_Workbook_Data.xlsx中文件Input和Baseline页
	Input页:
		ID:			用例序号,最好是数值,用例按此正向排序执行
		TestCase:	用例名称
		call_type:	方法名称,包括GET、POST、PUT、DELETE
		host:		被测试服务的主机,包括协议、主机地址和端口号,如http://127.0.0.1:8080
		call_suff:	被测试服务的接口标识,包括服务名、接口名，以及参数,如/appName/login?user=admin&psd=123
		AuthScheme:	认证模式,可选	
		AuthCreds:	认证凭证,可选
		Accept:		发送端（客户端）希望接受的数据类型,如application/json
		Content-Type:	发送端（客户端|服务器）发送的实体数据的数据类型,如application/json
		Body:		仅当请求参数以JSON格式时填写,如{"user":"admin","psd":"123"}
		
	Baseline页:
		ID:			用例序号,最好是数值,用例按此正向排序执行
		TestCase:	用例名称
		Respone:		响应结果中的主体信息,即"Body"中的信息
	
	Output页:
		ID:			用例序号,最好是数值,用例按此正向排序执行
		TestCase:	用例名称
		Respone:		响应结果中的主体信息,即"Body"中的信息
		*注意:此页中用例已按ID升序排列,Comparison Result页相同.
		
	Comparison页:
		仅比对结果不完全一致的用例才列出
		ID:			用例序号,最好是数值,用例按此正向排序执行
		TestCase:	用例名称
		Message:		比对不一致信息
		Failure field Value:其它错误信息

参考： http://www.cnblogs.com/wade-xu/p/4229805.html 

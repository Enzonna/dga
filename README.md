# dga
数据治理考评平台

------------------------2024-08-15---------------------------------------------
数据治理考评系统
是什么？ 背景：随着数据不断增长，数据表指标的不断增长；
        痛点：规范 -> 数据信息缺失，不规范，数据孤岛问题严重；
             存储 -> 冗余
             计算 -> 效率低下 资源浪费
             质量 -> 数据不准确，不能按时产出
             安全 -> 没有安全规范
        解决： 通过了...利用了...实现了一套...达成了...有效发现...问题 经过...改善提高...
			通过一套数据治理的考评指标，利用hive hdfs ds日志中各类元数据的信息，对数据开发过程中的规范、存储、计算、质量、安全进行了有效的考虑

技术框架：
	springboot + mybatis + mybatis-plus + hive + hdfs + ds + sparkhistory
	该项目由springboot搭建，同时对接了hive、ds、hdfs、sparkhistory
 	功能步骤：整个过程由数据采集、录入辅助信息、指标考评、统计核算分数、可视化接口完成

指责：
	一个人：需求调研、设计、开发、测试（测试人员）、用时（2-3个月，持续升级优化）
	
功能：
	考评指标：18个

 难点：
 	1.递归遍历hdfs 汇总数据信息
  	2.从ds中提取sql，并利用SQL语法树对sql进行分析
    3.sparkhistory服务提取任务日志

springboot
	web服务
 	分层开发：
  		controller层：1.接收请求：入门：@RestController
								      @RequestMapping
		 							  @GetMapping
		  							  @PostMapping
		   			  2.接收参数：@PathVariable
		  					     @RequestParam
			                     @Requestbody
					  3.调用服务层：@Autowird
	   				  4.返回结果
		service层：1.处理业务
	   			  2.调用其他服务层
			      3.调用数据层
				  4.@service
	    mapper层：1.mybatis：1.管理连接和对话
	 					     2.封装sql
	 					     3.封装返回结果
		                     4.注解：	@Mapper
					   					@Select @Insert @Update @Delete
			 							@Param
		   								#{} ${}
		  		  2.mybatis-plus:单表的curd	1.方法：服务层方法+数据层方法
											2.在QueryWrapper放条件
		   									3.@TableField(exists=false)
			  	  3.动态数据源：多数据源 
							   @DS
		  		  4.数据连接池：druid
		其他注解：
  				@Component
	  			@PostContruct	让方法在启动后执行
	  			@Value		自定义参数

 工具技巧：
 		日期	DateUtils	1.转date 2.加减日期
   				DateFormatUtils		转String
	   JSON		fastjson	JSON.toJSONString
							JSON.parseObject parseArray
	   						JSONObject
   		递归	阶段一 准备：起点 访问工具 结果容器
	 			阶段二 遍历 分支节点 展开下级 再次递归 还是叶子节点 处理
	 	sql解析	ast语法树	制作节点处理器，节点处理器收集信息，提取信息进行加工，判断sql，为sql定性
   		对接ds	任务定义表
	 			任务实例表
	 	http访问工具	okhttp
   					访问sparkhistory接口
					需要yarnId
	 	hive访问工具	hivemetastoreclient
   		hdfs访问工具	fileststem
	 	BigDecimal	
   		设计模式		单态、模板、构造器
	 	多线程		异步编排工具	CompletableFuture	把任务写入异步编排，返回future，把返回结果收集起来，把futurelist中的元素逐个join，期货变现货
		  				
			   
		   				









 

<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
	Map<String,String> pMap = (Map<String,String>)application.getAttribute("pMap");
	Set<String> set = pMap.keySet();
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
	<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

<%--自动补全的插件--%>
<script type="text/javascript" src="jquery/bs_typeahead/bootstrap3-typeahead.min.js"></script>


<script type="text/javascript">

	//将map格式转换为Json格式。
	var json = {

		<%

            for(String key:set){

                String value = pMap.get(key);
        %>

		"<%=key%>" : <%=value%>,

		<%
            }

        %>

	};

	$(function (){

		//自动补全 客户名称
		$("#create-customerName").typeahead({
			//回调函数
			source: function (query, process) {
				//Ajax
				$.get(
						"workbench/transaction/getCustomerName.do",
						{ "name" : query },		//name客户填写的需要补全的名称。
						function (data) {
							//alert(data);
							/*
                                data
                                    [{客户名称1},{2},{3}]
                             */
							process(data);
						},
						"json"
				);
			},
			delay: 500		//延迟展示。
		});

		//向下显示的日历控件
		$(".time1").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "bottom-left"
		});

		//向上显示的日历控件。
		$(".time2").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "top-left"
		});

		//为阶段的下拉框，绑定选中下拉框的事件，根据选中的阶段填写可能性
		$("#create-stage").change(function () {

			//取得选中的阶段
			var stage = $("#create-stage").val();

			/*

                目标：填写可能性

                阶段有了stage
                阶段和可能性之间的对应关系pMap，但是pMap是java语言中的键值对关系（java中的map对象）
                我们首先得将pMap转换为js中的键值对关系json

                我们要做的是将pMap	,java格式，浏览器识别不了。
                    pMap.put("01资质审查",10);
                    pMap.put("02需求分析",25);
                    ...

                    转换为			,json格式，浏览器可以识别。

                    var json = {"01资质审查":10,"02需求分析":25...};

                以上我们已经将json处理好了

                接下来取可能性

             */

			//alert(stage);

			/*

                我们现在以json.key的形式不能取得value
                因为今天的stage是一个可变的变量
                如果是这样的key，那么我们就不能以传统的json.key的形式来取值
                我们要使用的取值方式为
                json[key]


             */
			var possibility = json[stage];
			//alert(possibility);

			//为可能性的文本框赋值
			$("#create-possibility").val(possibility);


		})

		//为保存按钮绑定事件，执行交易的添加操作
		$("#saveBtn").click(function (){

			//发出传统请求，提交表单
			$("#tranForm").submit();

		})

		//为取消按钮绑定事件，点击返回到index.jsp
		$("#qxBtn").click(function (){


		})

	})


</script>


</head>
<body>

	<div style="position:  relative; left: 30px;">
		<h3>创建订单</h3>
	  	<div style="position: relative; top: -40px; left: 70%;">
			<button type="button" class="btn btn-primary" id="saveBtn">保存</button>
			<button type="button" class="btn btn-default" id="qxBtn">取消</button>
		</div>
		<hr style="position: relative; top: -40px;">
	</div>
	<form action="workbench/transaction/save.do" id="tranForm" method="post" class="form-horizontal" role="form" style="position: relative; top: -30px;">
		<div class="form-group">
			<label for="create-transactionOwner" class="col-sm-2 control-label">创建订单管理员<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<select class="form-control" id="create-transactionOwner" name="owner">
					<option></option>
					<c:forEach items="${userList}" var="u">
						<option value="${u.id}" ${user.id eq u.id ? "selected" : ""}>${u.name}</option>
					</c:forEach>
				</select>
			</div>
			<label for="create-amountOfMoney" class="col-sm-2 control-label">订单金额</label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-amountOfMoney" name="money">
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-transactionName" class="col-sm-2 control-label">交易名称<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-transactionName" name="name">
			</div>
			<label for="create-expectedClosingDate" class="col-sm-2 control-label">预计发货日期<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control time1" id="create-expectedClosingDate" name="expectedDate">
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-accountName" class="col-sm-2 control-label">订单创建人<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-customerName" name="customerName" placeholder="支持自动补全，输入客户不存在则新建">
			</div>
			<label for="create-transactionStage" class="col-sm-2 control-label">物流阶段<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
			  <select class="form-control" id="create-stage" name="stage">
			  	<option></option>
				  <c:forEach items="${stageList}" var="s">
					  <option value="${s.value}">${s.text}</option>
				  </c:forEach>
			  </select>
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-transactionType" class="col-sm-2 control-label">物流类型</label>
			<div class="col-sm-10" style="width: 300px;">
				<select class="form-control" id="create-transactionType" name="type">
				  <option></option>
					<c:forEach items="${transactionTypeList}" var="t">
						<option value="${t.value}">${t.text}</option>
					</c:forEach>
				</select>
			</div>
		</div>

		<div class="form-group">
			<label for="create-describe" class="col-sm-2 control-label">订单描述</label>
			<div class="col-sm-10" style="width: 70%;">
				<textarea class="form-control" rows="3" id="create-describe" name="description"></textarea>
			</div>
		</div>
		

	</form>
</body>
</html>
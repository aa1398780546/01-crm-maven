<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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

		$("#create-stage").val("01收货");

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
			<label for="create-transactionOwner" class="col-sm-2 control-label">操作人员名称<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<select class="form-control" id="create-owner" name="owner">
					<option></option>
					<c:forEach items="${userList}" var="u">
						<option value="${u.id}" ${user.id eq u.id ? "selected" : ""}>${u.name}</option>
					</c:forEach>
				</select>
			</div>
			<label for="create-transactionName" class="col-sm-2 control-label">交易名称<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-tranName" name="tranName">
			</div>
		</div>

		<div class="form-group">
			<label for="create-accountName" class="col-sm-2 control-label">客户名称<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-fullname" name="fullname">
			</div>
			<label for="create-accountName" class="col-sm-2 control-label">客户性别</label>
			<div class="col-sm-10" style="width: 300px;">
				<select class="form-control" id="create-appellation">
					<c:forEach items="${appellationList}" var="a">
						<option value="${a.value}" >${a.text}</option>
					</c:forEach>
				</select>
			</div>
		</div>

		<div class="form-group">
			<label for="create-amountOfMoney" class="col-sm-2 control-label">订单金额</label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-money" name="money">
			</div>
			<label for="create-accountName" class="col-sm-2 control-label">客户邮箱</label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-email" name="email">
			</div>
		</div>

		<div class="form-group">
			<label for="create-accountName" class="col-sm-2 control-label">客户电话</label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-mphone" name="mphone">
			</div>
			<label for="create-accountName" class="col-sm-2 control-label">客户地址</label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-address" name="address">
			</div>
		</div>

		<div class="form-group">
			<label for="create-accountName" class="col-sm-2 control-label">收件人姓名</label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-Rfullname" name="Rfullname">
			</div>
			<label for="create-accountName" class="col-sm-2 control-label">收件人性别</label>
			<div class="col-sm-10" style="width: 300px;">
				<select class="form-control" id="create-Rappellation">
					<c:forEach items="${appellationList}" var="a">
						<option value="${a.value}">${a.text}</option>
					</c:forEach>
				</select>
			</div>
		</div>

		<div class="form-group">
			<label for="create-accountName" class="col-sm-2 control-label">收件人电话</label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-Rmphone" name="Rmphone">
			</div>
			<label for="create-accountName" class="col-sm-2 control-label">收件人地址</label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-Raddress" name="Raddress">
			</div>
		</div>

		<div class="form-group">
			<label for="create-transactionStage" class="col-sm-2 control-label">物流阶段<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<select class="form-control" id="create-stage" name="stage">
					<option></option>
					<option value="01收货">01收货</option>
				</select>
			</div>
			<label for="create-transactionType" class="col-sm-2 control-label">运输货物类型</label>
			<div class="col-sm-10" style="width: 300px;">
				<select class="form-control" id="create-type" name="type">
					<option></option>
					<c:forEach items="${transactionTypeList}" var="t">
						<option value="${t.value}">${t.text}</option>
					</c:forEach>
				</select>
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-expectedClosingDate" class="col-sm-2 control-label">预计发货日期<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control time1" id="create-expecteDate" name="expectedDate">
			</div><label for="create-expectedClosingDate" class="col-sm-2 control-label">预计到货日期<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control time1" id="create-receivedDate" name="receivedDate">
			</div>
		</div>

		<div class="form-group">
			<label for="create-describe" class="col-sm-2 control-label">订单描述</label>
			<div class="col-sm-10" style="width: 60%;">
				<textarea class="form-control" rows="3" id="create-description" name="description"></textarea>
			</div>
		</div>

	</form>
</body>
</html>
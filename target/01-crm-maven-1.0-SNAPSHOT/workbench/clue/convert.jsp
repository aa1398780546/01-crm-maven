<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
	<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>


<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

<script type="text/javascript">
	$(function(){

		//年月日的时间日历格式
		$(".time").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "bottom-left"
		});

		//点击√，打开为客户创建交易的模态窗口。
		$("#isCreateTransaction").click(function(){
			if(this.checked){
				$("#create-transaction2").show(200);
			}else{
				$("#create-transaction2").hide(200);
			}
		});

		//市场活动源的搜索框绑定键盘回车事件
		$("#openSearchModalBtn").click(function (){

			$("#searchActivityModal").modal("show");

		})

		//为搜索操作模态窗口的搜索框绑定事件，执行搜索，并展现市场活动列表的操作。
		$("#aname").keydown(function (event){
			//如果是回车事件
			if(event.keyCode==13){
				$.ajax({
					url : "workbench/clue/getActivityByName.do",
					data : {
						"aname" : $.trim($("#aname").val()),
					},
					type : "get",
					dataType : "json",
					success : function (data) {

						var html = "";

						$.each(data,function (i,n) {
							html += '<tr>';
							html += '<td><input type="radio" name="xz" value="'+n.id+'"/></td>';
							html += '<td id="'+n.id+'">'+n.name+'</td>';
							html += '<td>'+n.startDate+'</td>';
							html += '<td>'+n.endDate+'</td>';
							html += '<td>'+n.owner+'</td>';
							html += '</tr>';
						})

						$("#searchBody").html(html);
					}
				})
				//展示完列表后，将模态窗口默认的回车行为禁掉。
				return false;
			}

		})

		//为搜索操作的提交按钮绑定事件，填充市场活动源，将按钮的id值传给下方隐藏域，名称传给文本框。
		$("#submitActivityBtn").click(function (){

			//获取选中的市场活动单选框的id值，
			var $xz = $("input[name=xz]:checked");
			var id = $xz.val()

			//获取选中的市场活动单选框的名称name
			//拿到name所在的td标签，再通过html方法取里面的内容。
			var name = $("#"+id).html();

			//将以上两个信息填写到j交易表单的市场活动源中
			$("#activityId").val(id);
			$("#activityName").val(name);

			//将模态窗口关闭
			$("#searchActivityModal").modal("hide");

		})

		//为转换按钮添加鼠标单击事件,执行线索的转换操作。
		$("#convertBtn").click(function (){

			//进行判断“为客户创建交易的复选框”有没挑√，来判断是否需要创建交易。
			if($("#isCreateTransaction").prop("checked")){

				//alert("需要创建交易");
				//如果需要创建交易，除了要为后台传递clueId之外，还得为后台传递交易表单中的信息，金额，预计成交日期，交易名称，阶段，市场活动源（id）
				//window.location.href = "workbench/clue/convert.do?clueId=xxx&money=xxx&expectedDate=xxx&name=xxx&stage=xxx&activityId=xxx";

				//以上传递参数的方式很麻烦，而且表单一旦扩充，挂载的参数有可能超出浏览器地址栏的上限
				//我们想到使用提交交易表单的形式来发出本次的传统请求
				//提交表单的参数不用我们手动去挂载（表单中写name属性），提交表单能够提交post请求

				//提交表单
				$("#tranForm").submit();

			}else{
				//alert("不需要创建交易");
				//在不需要创建交易的时候，传一个clueId就可以了
				window.location.href = "workbench/clue/convert.do?clueId=${param.id}";
			}
		})




	});
</script>

</head>
<body>
	
	<!-- 搜索市场活动的模态窗口 -->
	<div class="modal fade" id="searchActivityModal" role="dialog" >
		<div class="modal-dialog" role="document" style="width: 90%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title">搜索市场活动</h4>
				</div>
				<div class="modal-body">
					<div class="btn-group" style="position: relative; top: 18%; left: 8px;">
						<form class="form-inline" role="form">
						  <div class="form-group has-feedback">
						    <input type="text" id="aname" class="form-control" style="width: 300px;" placeholder="请输入市场活动名称，支持模糊查询">
						    <span class="glyphicon glyphicon-search form-control-feedback"></span>
						  </div>
						</form>
					</div>
					<table id="activityTable" class="table table-hover" style="width: 900px; position: relative;top: 10px;">
						<thead>
							<tr style="color: #B3B3B3;">
								<td></td>
								<td>名称</td>
								<td>开始日期</td>
								<td>结束日期</td>
								<td>所有者</td>
								<td></td>
							</tr>
						</thead>
						<tbody id="searchBody">

						</tbody>
					</table>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<button id="submitActivityBtn" type="button" class="btn btn-primary">提交</button>
				</div>
			</div>
		</div>
	</div>

	<div id="title" class="page-header" style="position: relative; left: 20px;">
		<h4>普通客户转换为VIP客户 <small>${param.fullname}-${param.appellation}</small></h4>
	</div>
	<div id="create-customer" style="position: relative; left: 40px; height: 35px;">
		${param.company}
	</div>
	<div id="create-contact" style="position: relative; left: 40px; height: 35px;">
		新建VIP客户：${param.fullname}${param.appellation}
	</div>

	
	<div id="owner" style="position: relative; left: 40px; height: 35px; top: 35px;">
		操作管理员：<br>
		<b>${param.createBy}</b>
	</div>
	<div id="operation" style="position: relative; left: 40px; height: 35px; top: 100px;">
		<input class="btn btn-primary" type="button" value="转换" id="convertBtn">
		&nbsp;&nbsp;&nbsp;&nbsp;
		<input class="btn btn-default" type="button" value="取消">
	</div>
</body>
</html>
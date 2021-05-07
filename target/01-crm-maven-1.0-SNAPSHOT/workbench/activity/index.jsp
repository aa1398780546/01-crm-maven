<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">

<%--	写着datatimepicker的都是日历插件--%>
<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

<%--	分页插件--%>
	<link rel="stylesheet" type="text/css" href="jquery/bs_pagination/jquery.bs_pagination.min.css">
	<script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>
	<script type="text/javascript" src="jquery/bs_pagination/en.js"></script>

<script type="text/javascript">

	$(function(){
		
		//为创建按钮绑定事件，打开-添加-操作的模态窗口
		$("#addBtn").click(function (){

			//年月日的时间日历格式
			$(".time").datetimepicker({
				minView: "month",
				language:  'zh-CN',
				format: 'yyyy-mm-dd',
				autoclose: true,
				todayBtn: true,
				pickerPosition: "bottom-left"
			});

			//给后台发送Ajax请求，获取所有者选项内容。
			$.ajax({
				url:"workbench/activity/getUserList.do",
				dataType:"json",
				type:"get",
				//返回的data是json格式的user对象	[{用户1},{用户2},{3},...,{}]
				success:function (data){

					/*
						字符串拼接格式
						<option value="1">张三</option>
					 */
					var html= "<option></option>";

					//遍历出来的每一个n,就是每一个user对象
					$.each(data,function (i,n){
						html += "<option value='"+n.id+"'>"+n.name+"</option>";
					})
					$("#create-owner").html(html);

					//将当前登录的用户，设置为下拉框默认选项
					//	获取当前登录用户的id，使用El表达式
					//		在js中使用EL表达式，要将其套在字符串中
					var id = "${user.id}";
					$("#create-owner").val(id);

					//当所有者下拉框处理完毕后，展现模态窗口
					//操作模态窗口的jQuery对象
					$("#createActivityModal").modal("show");
				}
			})

		})

		//为点击创建后打开的模态窗口的-保存-按钮绑定事件，执行添加操作。
		$("#saveBtn").click(function (){

			//发送Ajax请求到ActivityController
			$.ajax({
				url: "workbench/activity/save.do",
				data:{
					owner : $.trim($("#create-owner").val()),
					name : $.trim($("#create-name").val()),
					startDate : $.trim($("#create-startDate").val()),
					endDate : $.trim($("#create-endDate").val()),
					cost : $.trim($("#create-cost").val()),
					description : $.trim($("#create-description").val()),
				},
				type:"post",
				dataType: "json",
				success:function (data){
					// alert(data.flag);	//undefined
					// alert(data.success);	//undefined
					if (data){
						//添加市场活动成功，刷新市场活动信息列表(局部刷新)
						alert("添加货物成功");
						//刷新市场活动列表
						//回到首页，每页展示已经设置好的记录数
						pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
						//清空模态窗口中的数据
						$("#activityAddForm")[0].reset();
						//关闭市场活动的模态窗口
						$("#createActivityModal").modal("hide");
					}else {
						alert("添加市场活动失败！")
					}
				}
			})

		})

		//为点击市场活动后的-查询-按钮绑定事件，添加查询操作。
		$("#searchBtb").click(function (){
			// 点击查询按钮的时候，我们应该将搜索框中的信息保存起来,保存到隐藏域中
			$("#hidden-name").val($.trim($("#search-name").val()));
			$("#hidden-owner").val($.trim($("#search-owner").val()));
			$("#hidden-startDate").val($.trim($("#search-startDate").val()));
			$("#hidden-endDate").val($.trim($("#search-endDate").val()));

			pageList(1,2);
		})

		//为全选的复选框绑定事件，触发全选操作
		$("#qx").click(function () {

			$("input[name=xz]").prop("checked",this.checked);

		})

		//为全选款下面的复选框，如果都选✔，全选框✔
		$("#activityBody").on("click",$("input[name=xz]"),function () {
			//如果	总个数=✔个数		全选框✔
			$("#qx").prop("checked",$("input[name=xz]").length==$("input[name=xz]:checked").length);
			//以下这种做法是不行的
			/*$("input[name=xz]").click(function () {

                alert(123);

            })*/

			//因为动态生成的元素，是不能够以普通绑定事件的形式来进行操作的
			/*

                动态生成的元素，我们要以on方法的形式来触发事件

                语法：
                    $(需要绑定元素的有效的外层元素).on(绑定事件的方式,需要绑定的元素的jquery对象,回调函数)

             */
		})

		//为删除按钮绑定事件，执行市场活动删除操作
		$("#deleteBtn").click(function () {

			//找到复选框中所有挑√的复选框的jquery对象
			var $xz = $("input[name=xz]:checked");

			if($xz.length==0){
				alert("请选择需要删除的记录");
			}else{
				//肯定选了，而且有可能是1条，有可能是多条

				if(confirm("确定删除所选中的记录吗？")){

					//param = id=xxx&	id=xxx&		id=xxx

					//拼接参数
					var param = "";

					//将$xz中的每一个dom对象遍历出来，取其value值，就相当于取得了需要删除的记录的id
					for(var i=0;i<$xz.length;i++){

						param += "id="+$($xz[i]).val();

						//如果不是最后一个元素，需要在后面追加一个&符
						if(i<$xz.length-1){
							param += "&";
						}

					}

					$.ajax({

						url : "workbench/activity/delete.do",
						data : param,
						type : "post",
						dataType : "json",
						success : function (data) {

							if(data.success){

								//删除成功后
								//回到第一页，维持每页展现的记录数
								pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

							}else{
								alert("删除市场活动失败");
							}
						}
					})
				}
			}
		})

		//为修改按钮绑定事件，打开修改操作的模态窗口
		$("#editBtn").click(function () {

			//当用户选中需要修改的列表，通过name获取到xz
			var $xz = $("input[name=xz]:checked");

			if($xz.length==0){
				alert("请选择需要修改的记录");
			}else if($xz.length>1){
				alert("只能选择一条记录进行修改");
				//肯定只选了一条
			}else{
				//获取到选中那条记录的id值
				var id = $xz.val();

				$.ajax({
					url : "workbench/activity/getUserListAndActivity.do",
					data : {
						"id" : id
					},
					type : "get",
					dataType : "json",
					success : function (data) {
						/*
							data
								用户列表 	User u		所有者需要用到
								市场活动对象 通过id获取的 select * from tbl_activity where id = ?

								{"uList":[{用户1},{2},{3}],"a":{市场活动}}

						 */

						//拼接处理所有者下拉框
						var html = "<option></option>";
						$.each(data.uList,function (i,n) {

							html += "<option value='"+n.id+"'>"+n.name+"</option>";

						})
						$("#edit-owner").html(html);


						//处理单条activity
						$("#edit-id").val(data.a.id);
						$("#edit-name").val(data.a.name);
						$("#edit-owner").val(data.a.owner);
						$("#edit-startDate").val(data.a.startDate);
						$("#edit-endDate").val(data.a.endDate);
						$("#edit-cost").val(data.a.cost);
						$("#edit-description").val(data.a.description);

						//所有的值都填写好之后，打开修改操作的模态窗口
						$("#editActivityModal").modal("show");

					}

				})

			}

		})

		//为-修改-操作的-更新-按钮绑定事件，执行市场活动的修改操作
		$("#updateBtn").click(function () {

			$.ajax({

				url : "workbench/activity/update.do",
				data : {
					"id" : $.trim($("#edit-id").val()),
					"owner" : $.trim($("#edit-owner").val()),
					"name" : $.trim($("#edit-name").val()),
					"startDate" : $.trim($("#edit-startDate").val()),
					"endDate" : $.trim($("#edit-endDate").val()),
					"cost" : $.trim($("#edit-cost").val()),
					"description" : $.trim($("#edit-description").val())
				},
				type : "post",
				dataType : "json",
				success : function (data) {
					if(data.success){
						//修改成功后
						//刷新市场活动信息列表（局部刷新）
						alert("信息修改成功")
						//pageList(1,2);
						/*
							修改操作后，应该维持在当前页，维持每页展现的记录数
						 */
						pageList($("#activityPage").bs_pagination('getOption', 'currentPage')
								,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

						//关闭修改操作的模态窗口
						$("#editActivityModal").modal("hide");

					}else{
						alert("修改市场活动失败");
					}
				}
			})

		})

		//页面加载完毕后触发pageList
		pageList(1,3);
		
	});

	//查询市场活动列表方法，		pageNo:页码		pageSize:每页展示记录数
	function pageList(pageNo,pageSize){

		//年月日的时间日历格式
		$(".time").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "bottom-left"
		});

		//将全选的复选框的√干掉
		$("#qx").prop("checked",false);

		//查询前，将隐藏域中保存的信息取出来，重新赋予到搜索框中
		$("#search-name").val($.trim($("#hidden-name").val()));
		$("#search-owner").val($.trim($("#hidden-owner").val()));
		$("#search-startDate").val($.trim($("#hidden-startDate").val()));
		$("#search-endDate").val($.trim($("#hidden-endDate").val()));

		//发送Ajax请求
		$.ajax({
			url:"workbench/activity/pageList.do",
			data:{
				"pageNo":pageNo,
				"pageSize":pageSize,
				"name":$.trim($("#search-name").val()),
				"owner":$.trim($("#search-owner").val()),
				"startDate":$.trim($("#search-startDate").val()),
				"endDate":$.trim($("#search-endDate").val())
			},
			type:"get",
			dataType:"json",
			success:function (data){
				/*
					data需要为我提供：
						1. 我们需要的：市场活动信息列表	[{市场活动1},{市场活动2},{市场活动3},...]
						2. 分页查询需要：查询出来的总记录数	{"total":100}
					所以data的输出形式为：
						data={"total":100,"dataList":[{市场活动1},{市场活动2},{市场活动3},...]}
				 */
				/*
					字符串拼接格式：
					<tr class="active">
							<td><input type="checkbox" /></td>
							<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/activity/detail.jsp';">发传单</a></td>
                            <td>zhangsan</td>
							<td>2020-10-10</td>
							<td>2020-10-20</td>
						</tr>
				 */
				var html ="";

				$.each(data.dataList,function (i,n){
					html += '<tr class="active">'
					html += '	<td><input type="checkbox" name="xz" value="'+n.id+'"/></td>';
					html += '	<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/activity/detail.do?id='+n.id+'\';">'+n.name+'</a></td>';
					html += '	<td>'+n.owner+'</td>';
					html += '	<td>'+n.startDate+'</td>';
					html += '	<td>'+n.endDate+'</td>';
					html += '</tr>';
				})

				$("#activityBody").html(html);

				//计算总页数
				var totalPages = data.total%pageSize==0?data.total/pageSize:parseInt(data.total/pageSize)+1;

				//数据处理完毕后，结合分页查询，对前端展现分页信息
				$("#activityPage").bs_pagination({
					currentPage: pageNo, // 页码
					rowsPerPage: pageSize, // 每页显示的记录条数
					maxRowsPerPage: 20, // 每页最多显示的记录条数
					totalPages: totalPages, // 总页数
					totalRows: data.total, // 总记录条数

					visiblePageLinks: 3, // 显示几个卡片

					showGoToPage: true,
					showRowsPerPage: true,
					showRowsInfo: true,
					showRowsDefaultInfo: true,

					onChangePage : function(event, data){
						pageList(data.currentPage , data.rowsPerPage);
					}
				});


			}
		})

	}


</script>
</head>
<body>

<%--	添加隐藏域--%>
	<input type="hidden" id="hidden-name"/>
	<input type="hidden" id="hidden-owner"/>
	<input type="hidden" id="hidden-startDate"/>
	<input type="hidden" id="hidden-endDate"/>

	<!-- 创建市场活动的模态窗口 -->
	<div class="modal fade" id="createActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建货物管理</h4>
				</div>
				<div class="modal-body">
				
					<form id="activityAddForm" class="form-horizontal" role="form">
					
						<div class="form-group">
								<label for="create-marketActivityOwner" class="col-sm-2 control-label">管理员名称<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-owner">
								</select>
							</div>
                            <label for="create-marketActivityName" class="col-sm-2 control-label">货物名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-name">
                            </div>
						</div>
						
						<div class="form-group">
							<label for="create-startTime" class="col-sm-2 control-label">接收货物日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-startDate" readonly>
							</div>
							<label for="create-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-endDate">
							</div>
						</div>
                        <div class="form-group">

                            <label for="create-cost" class="col-sm-2 control-label">货物价格</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-cost">
                            </div>
                        </div>
						<div class="form-group">
							<label for="create-describe" class="col-sm-2 control-label">货物描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveBtn">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 修改市场活动的模态窗口 -->
	<div class="modal fade" id="editActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel2">修改物流管理</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form">

						<input type="hidden" id="edit-id"/>
					
						<div class="form-group">
							<label for="edit-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-owner">
								</select>
							</div>
                            <label for="edit-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-name" >
                            </div>
						</div>

						<div class="form-group">
							<label for="edit-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-startDate" >
							</div>
							<label for="edit-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-endDate" >
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-cost" class="col-sm-2 control-label">成本</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-cost">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<!--
									关于文本域textarea：
										（1）一定是要以标签对的形式来呈现,正常状态下标签对要紧紧的挨着
										（2）textarea虽然是以标签对的形式来呈现的，但是它也是属于表单元素范畴
												我们所有的对于textarea的取值和赋值操作，应该统一使用val()方法（而不是html()方法）
								-->
								<textarea class="form-control" rows="3" id="edit-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="updateBtn">更新</button>
				</div>
			</div>
		</div>
	</div>
	

	
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>货物管理列表</h3>
			</div>
		</div>
	</div>
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">货物名称</div>
				      <input class="form-control" type="text" id="search-name">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="search-owner">
				    </div>
				  </div>


				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">开始日期</div>
					  <input class="form-control time" type="text" id="search-startDate"/>
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">结束日期</div>
					  <input class="form-control time" type="text" id="search-endDate">
				    </div>
				  </div>
				  
				  <button type="button" class="btn btn-default" id="searchBtb">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">
				  <button type="button" class="btn btn-primary" id="addBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editBtn"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="qx"/></td>
							<td>货物名称</td>
                            <td>所有者</td>
							<td>接收货物日期</td>
							<td>结束日期</td>
						</tr>
					</thead>
					<tbody id="activityBody">
<%--						<tr class="active">--%>
<%--							<td><input type="checkbox" /></td>--%>
<%--							<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/activity/detail.jsp';">发传单</a></td>--%>
<%--                            <td>zhangsan</td>--%>
<%--							<td>2020-10-10</td>--%>
<%--							<td>2020-10-20</td>--%>
<%--						</tr>--%>
<%--                        <tr class="active">--%>
<%--                            <td><input type="checkbox" /></td>--%>
<%--                            <td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/activity/detail.jsp';">发传单</a></td>--%>
<%--                            <td>zhangsan</td>--%>
<%--                            <td>2020-10-10</td>--%>
<%--                            <td>2020-10-20</td>--%>
<%--                        </tr>--%>
					</tbody>
				</table>
			</div>
			
			<div style="height: 50px; position: relative;top: 30px;">
				<div id="activityPage">	</div>
			</div>
			
		</div>
		
	</div>
</body>
</html>
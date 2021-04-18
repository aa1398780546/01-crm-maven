<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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

	<script>

		$(function(){

			//页面加载完毕后，让用户的文本框自动活得焦点
			$("#loginAct").focus();

			//页面加载完毕后，将用户文本框内容清空。
			$("#loginAct").val("");

			//为登录按钮绑定事件，执行登录操作。
			$("#submitBtn").click(function (){
				login();
			})

			//为当前窗口绑定敲键盘事件
			//event：这个事件可以取得我们敲的是哪一个键
			$(window).keydown(function (event){
				//回车键的ASCII码值是13
				if (event.keyCode == 13){
					login();
				}
			})
		})

		//登录验证的方法
		function login(){
			//验证账号密码不能为空
			//获取用户输入的账号和密码
			//将文本中的左右空格去掉，使用$.trim(文本)
			var loginAct = $.trim($("#loginAct").val());
			var loginPwd = $.trim($("#loginPwd").val());
			//如果还是空，那就是输入的就是空格
			if (loginAct=="" || loginPwd==""){
				$("#msg").html("账号密码不能为空");
				//如果账号密码为空，需要及时终止该方法。
				return false;
			}
			//这时去后台验证登录相关操作。
			$.ajax({
				url:"settings/user/login.do",
				data:{
					"loginAct":loginAct,
					"loginPwd":loginPwd
				},
				type:"post",
				dataType:"json",
				success:function (data){
					/*
						data
							{"success":true/false,"msg":"哪儿错了"}
					 */
					//如果登录成功
					if (data.success){
						//window.location.href("workbench/index.jsp");
						//alert("登陆成功");
						window.location.href = "workbench/index.jsp";
					}else {
						//登录失败有四种情况，账号密码错误，时间失效，锁定状态，ip不正确。
						$("#msg").html(data.msg);
					}
				}
			})

		}


	</script>

</head>
<body>
	<div style="position: absolute; top: 0px; left: 0px; width: 100%;">
		<img src="image/bg1.png" style="width: 100%; height: 90%; position: relative; top: 50px;">
	</div>
	<div id="top" style="height: 50px; background-color: #3C3C3C; width: 100%;">
		<div style="position: absolute; top: 5px; right: 0px; font-size: 25px; font-weight: 400; color: white; font-family: 'times new roman'">物流管理系统的设计与实现 &nbsp;<span style="font-size: 20px;">指导老师：卢利琼</span></div>
	</div>
	
	<div style="position: absolute; top: 120px; right: 35%;width:450px;height:400px;border:1px solid #D5D5D5">
		<div style="position: absolute; top: 0px; right: 60px;">
			<div class="page-header">
				<h3 align="center">物流管理系统</h3>
			</div>
			<form action="workbench/index.jsp" class="form-horizontal" role="form">
				<div class="form-group form-group-lg">
					<div style="width: 350px;">
						<%--自动获取用户名的焦点--%>
						<input class="form-control" type="text" placeholder="用户名" id="loginAct">
					</div>
					<div style="width: 350px; position: relative;top: 20px;">
						<input class="form-control" type="password" placeholder="密码" id="loginPwd">
					</div>
					<div class="checkbox"  style="position: relative;top: 30px; left: 10px;">
							 <%--输出错误消息的位置--%>
							<span id="msg" style="color:red">123</span>

					</div>
					<!--
						注意：按钮写在form表单中，默认行为就是提交表单
							一定要将按钮的类型设置为：button
							按钮所触发的行为应该是我们自己手写js代码来实现。
					-->
					<button type="button" id="submitBtn" class="btn btn-primary btn-lg btn-block"  style="width: 350px; position: relative;top: 45px;">登录</button>
				</div>
			</form>
		</div>
	</div>
</body>
</html>
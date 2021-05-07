<%@ page import="com.bjpowernode.crm.settings.domain.DicValue" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="com.bjpowernode.crm.workbench.domain.Tran" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";

//准备字典类型为stage的字典值列表
	List<DicValue> dvList = (List<DicValue>)application.getAttribute("stageList");

	//准备阶段和可能性之间的对应关系
	Map<String,String> pMap = (Map<String,String>)application.getAttribute("pMap");

	//根据pMap准备pMap中的key集合
	Set<String> set = pMap.keySet();

	//准备：前面正常阶段和后面丢失阶段的分界点下标
	int point = 0;
	for(int i=0;i<dvList.size();i++){

		//取得每一个字典值
		DicValue dv = dvList.get(i);

		//从dv中取得value
		String stage = dv.getValue();
		//根据stage取得possibility
		String possibility = pMap.get(stage);

		//如果可能性为0，说明找到了前面正常阶段和后面丢失阶段的分界点
		if("0".equals(possibility)){

			point = i;

			break;

		}


	}


%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
	<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />

<style type="text/css">

	.mystage{
		font-size: 20px;
		vertical-align: middle;
		cursor: pointer;
	}

	.closingDate{
		font-size : 15px;
		cursor: pointer;
		vertical-align: middle;
	}

</style>
	
<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>

<script type="text/javascript">

	// //默认情况下取消和保存按钮是隐藏的
	// var cancelAndSaveBtnDefault = true;
	
	$(function(){

		$("#remark").focus(function(){
			if(cancelAndSaveBtnDefault){
				//设置remarkDiv的高度为130px
				$("#remarkDiv").css("height","130px");
				//显示
				$("#cancelAndSaveBtn").show("2000");
				cancelAndSaveBtnDefault = false;
			}
		});
		
		$("#cancelBtn").click(function(){
			//显示
			$("#cancelAndSaveBtn").hide();
			//设置remarkDiv的高度为130px
			$("#remarkDiv").css("height","90px");
			cancelAndSaveBtnDefault = true;
		});
		
		$(".remarkDiv").mouseover(function(){
			$(this).children("div").children("div").show();
		});
		
		$(".remarkDiv").mouseout(function(){
			$(this).children("div").children("div").hide();
		});
		
		$(".myHref").mouseover(function(){
			$(this).children("span").css("color","red");
		});
		
		$(".myHref").mouseout(function(){
			$(this).children("span").css("color","#E6E6E6");
		});
		
		
		//阶段提示框
		$(".mystage").popover({
            trigger:'manual',
            placement : 'bottom',
            html: 'true',
            animation: false
        }).on("mouseenter", function () {
                    var _this = this;
                    $(this).popover("show");
                    $(this).siblings(".popover").on("mouseleave", function () {
                        $(_this).popover('hide');
                    });
                }).on("mouseleave", function () {
                    var _this = this;
                    setTimeout(function () {
                        if (!$(".popover:hover").length) {
                            $(_this).popover("hide")
                        }
                    }, 100);
                });

		// 以上均为样式信息==================================================

		//在页面加载完毕后，展示阶段历史信息。
		showHistoryList();

		//为全选的复选框绑定事件，触发全选操作
		$("#qx").click(function () {

			$("input[name=xz]").prop("checked",this.checked);

		})

		//为全选款下面的复选框，如果都选✔，全选框✔
		$("#tranHistoryBody").on("click",$("input[name=xz]"),function () {
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

						url : "workbench/transaction/delete.do",
						data : param,
						type : "post",
						dataType : "json",
						success : function (data) {

							if(data.success){

								//删除成功后
								//回到第一页，维持每页展现的记录数
								showHistoryList();

							}else{
								alert("删除市场活动失败");
							}
						}
					})
				}
			}
		})


	});

	//展现交易历史列表
	function showHistoryList(){

		//将全选的复选框的√干掉
		$("#qx").prop("checked",false);

		$.ajax({

			url:"workbench/transaction/getHistoryByTranId.do",
			data:{
				"tranId":"${t.id}"
			},
			type:"get",
			dataType:"json",
			success:function (data){
				//[{交易历史1}，{交易历史2},.。。。]
				var html="";

				$.each(data,function (i,n){
					html += '<tr>';
						html += '<td><input type="checkbox" name="xz" value="'+n.id+'"/></td>';
						html += '<td>'+(++i)+'</td>';
						html += '<td>'+n.stage+'</td>';
						html += '<td>'+n.money+'</td>';
						html += '<td>'+n.possibility+'</td>';
						html += '<td>'+n.expectedDate+'</td>';
						html += '<td>'+n.createTime+'</td>';
						html += '<td>'+n.createBy+'</td>';
					html += '</tr>';
				})

				$("#tranHistoryBody").html(html);
			}
		})


	}

	/*
    方法：改变交易阶段
    参数：
        stage：需要改变的阶段
        i：需要改变的阶段对应的下标
 */
	function changeStage(stage,i) 	{

		//alert(stage);
		//alert(i);
		$.ajax({

			url : "workbench/transaction/changeStage.do",
			data : {

				"id" : "${t.id}",
				"stage" : "${t.stage}",
				"money" : "${t.money}",		//生成交易历史用
				"expectedDate" : "${t.expectedDate}"	//生成交易历史用

			},
			type : "post",
			dataType : "json",
			success : function (data) {
				/*
					data
						{"success":true/false,"t":{交易}}
				 */
				if(data.success){

					//改变阶段成功后
					//(1)需要在详细信息页上局部刷新 刷新阶段，可能性，修改人，修改时间
					$("#stage").html(data.t.stage);
					$("#possibility").html(data.t.possibility);
					$("#editBy").html(data.t.editBy);
					$("#editTime").html(data.t.editTime);

					//改变阶段成功后
					//(2)将所有的阶段图标重新判断，重新赋予样式及颜色
					changeIcon(stage,i);
					showHistoryList();

				}else{
					alert("改变阶段失败");
				}

			}

		})


	}

	//改变阶段成功后
	//(2)将所有的阶段图标重新判断，重新赋予样式及颜色
	function changeIcon(stage,index1) {

		//当前阶段
		var currentStage = stage;

		//当前阶段可能性
		var currentPossibility = $("#possibility").html();

		//当前阶段的下标
		var index = index1;

		//前面正常阶段和后面丢失阶段的分界点下标
		var point = "<%=point%>";

		/*alert("当前阶段"+currentStage);
		alert("当前阶段可能性"+currentPossibility);
		alert("当前阶段的下标"+index);
		alert("前面正常阶段和后面丢失阶段的分界点下标"+point);*/

		//如果当前阶段的可能性为0 前7个一定是黑圈，后两个一个是红叉，一个是黑叉
		if(currentPossibility=="0"){

			//遍历前7个
			for(var i=0;i<point;i++){

				//黑圈------------------------------
				//移除掉原有的样式
				$("#"+i).removeClass();
				//添加新样式
				$("#"+i).addClass("glyphicon glyphicon-record mystage");
				//为新样式赋予颜色
				$("#"+i).css("color","#000000");

			}

			//遍历后两个
			for(var i=point;i<<%=dvList.size()%>;i++){

				//如果是当前阶段
				if(i==index){

					//红叉-----------------------------
					$("#"+i).removeClass();
					$("#"+i).addClass("glyphicon glyphicon-remove mystage");
					$("#"+i).css("color","#FF0000");

					//如果不是当前阶段
				}else{

					//黑叉----------------------------
					$("#"+i).removeClass();
					$("#"+i).addClass("glyphicon glyphicon-remove mystage");
					$("#"+i).css("color","#000000");

				}


			}



			//如果当前阶段的可能性不为0 前7个绿圈，绿色标记，黑圈，后两个一定是黑叉
		}else{

			//遍历前7个 绿圈，绿色标记，黑圈
			for(var i=0;i<point;i++){

				//如果是当前阶段
				if(i==index){

					//绿色标记--------------------------
					$("#"+i).removeClass();
					$("#"+i).addClass("glyphicon glyphicon-map-marker mystage");
					$("#"+i).css("color","#90F790");

					//如果小于当前阶段
				}else if(i<index){

					//绿圈------------------------------
					$("#"+i).removeClass();
					$("#"+i).addClass("glyphicon glyphicon-ok-circle mystage");
					$("#"+i).css("color","#90F790");

					//如果大于当前阶段
				}else{

					//黑圈-------------------------------
					$("#"+i).removeClass();
					$("#"+i).addClass("glyphicon glyphicon-record mystage");
					$("#"+i).css("color","#000000");

				}


			}

			//遍历后两个
			for(var i=point;i<<%=dvList.size()%>;i++){

				//黑叉----------------------------
				$("#"+i).removeClass();
				$("#"+i).addClass("glyphicon glyphicon-remove mystage");
				$("#"+i).css("color","#000000");
			}

		}


	}

</script>

</head>
<body>
	
	<!-- 返回按钮 -->
	<div style="position: relative; top: 35px; left: 10px;">
		<a href="javascript:void(0);" onclick="window.history.back();"><span class="glyphicon glyphicon-arrow-left" style="font-size: 20px; color: #DDDDDD"></span></a>
	</div>
	
	<!-- 大标题 -->
	<div style="position: relative; left: 40px; top: -30px;">
		<div class="page-header">
			<h3>${t.name} <small>${t.money}</small></h3>
		</div>
		<div style="position: relative; height: 50px; width: 250px;  top: -72px; left: 700px;">
			<button type="button" class="btn btn-default" onclick="window.location.href='edit.jsp';"><span class="glyphicon glyphicon-edit"></span> 编辑</button>
			<button type="button" class="btn btn-danger"><span class="glyphicon glyphicon-minus"></span> 删除</button>
		</div>
	</div>

	<!-- 阶段状态 -->
	<div style="position: relative; left: 40px; top: -50px;">
		阶段&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

		<%

			//准备当前阶段
			Tran t = (Tran)request.getAttribute("t");
			String currentStage = t.getStage();
			//准备当前阶段的可能性
			String currentPossibility = pMap.get(currentStage);
			System.out.println("当前阶段的可能性为： " + currentPossibility);

			//判断当前阶段
			//如果当前阶段的可能性为0 前7个一定是黑圈，后两个一个是红叉，一个是黑叉
			if("0".equals(currentPossibility)){

				for(int i=0;i<dvList.size();i++){

					//取得每一个遍历出来的阶段，根据每一个遍历出来的阶段取其可能性
					DicValue dv = dvList.get(i);
					String listStage = dv.getValue();
					String listPossibility = pMap.get(listStage);

					//如果遍历出来的阶段的可能性为0，说明是后两个，一个是红叉，一个是黑叉
					if("0".equals(listPossibility)){

						//如果是当前阶段
						if(listStage.equals(currentStage)){

							//红叉-----------------------------------------
							%>

							<span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')"
								  class="glyphicon glyphicon-remove mystage"
								  data-toggle="popover" data-placement="bottom"
								  data-content="<%=dv.getText()%>" style="color: #FF0000;"></span>
							-----------

							<%

		//如果不是当前的阶段
		}else{

			//黑叉-----------------------------------------
			%>

			<span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')"
				  class="glyphicon glyphicon-remove mystage"
				  data-toggle="popover" data-placement="bottom"
				  data-content="<%=dv.getText()%>" style="color: #000000;"></span>
			-----------

			<%
			}


		//如果遍历出来的阶段的可能性不为0，说明是前7个，一定是黑圈
		}else{

			//黑圈-----------------------------------------
			%>

			<span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')"
				  class="glyphicon glyphicon-record mystage"
				  data-toggle="popover" data-placement="bottom"
				  data-content="<%=dv.getText()%>" style="color: #000000;"></span>
			-----------

			<%
			}
		}


		//如果当前阶段的可能性不为0 前7个有可能性是绿圈，绿色标记，黑圈，后两个一定是黑叉
		}else{

			//准备当前阶段的下标
			int index = 0;
			for(int i=0;i<dvList.size();i++){

				DicValue dv = dvList.get(i);
				String stage = dv.getValue();
				//String possibility = pMap.get(stage);
				//如果遍历出来的阶段是当前阶段
				if(stage.equals(currentStage)){
					index = i;
					break;
				}
			}

			for(int i=0;i<dvList.size();i++) {

				//取得每一个遍历出来的阶段，根据每一个遍历出来的阶段取其可能性
				DicValue dv = dvList.get(i);
				String listStage = dv.getValue();
				String listPossibility = pMap.get(listStage);

				//如果遍历出来的阶段的可能性为0，说明是后两个阶段
				if("0".equals(listPossibility)){

					//黑叉--------------------------------------------
					%>

					<span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')"
						  class="glyphicon glyphicon-remove mystage"
						  data-toggle="popover" data-placement="bottom"
						  data-content="<%=dv.getText()%>" style="color: #000000;"></span>
					-----------

					<%

		//如果遍历出来的阶段的可能性不为0 说明是前7个阶段 绿圈，绿色标记，黑圈
		}else{

			//如果是当前阶段
			if(i==index){

				//绿色标记-----------------------------------
				%>

				<span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')"
					  class="glyphicon glyphicon-map-marker mystage"
					  data-toggle="popover" data-placement="bottom"
					  data-content="<%=dv.getText()%>" style="color: #90F790;"></span>
				-----------

				<%

			//如果小于当前阶段
		}else if(i<index){

			//绿圈----------------------------------------
			%>

			<span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')"
				  class="glyphicon glyphicon-ok-circle mystage"
				  data-toggle="popover" data-placement="bottom"
				  data-content="<%=dv.getText()%>" style="color: #90F790;"></span>
			-----------

			<%

			//如果大于当前阶段
		}else{

			//黑圈----------------------------------------
			%>

			<span id="<%=i%>" onclick="changeStage('<%=listStage%>','<%=i%>')"
				  class="glyphicon glyphicon-record mystage"
				  data-toggle="popover" data-placement="bottom"
				  data-content="<%=dv.getText()%>" style="color: #000000;"></span>
			-----------

			<%
					}
				}
			}
		}

		%>


		<span class="closingDate">${t.expectedDate}</span>
	</div>
	
	<!-- 详细信息 -->
	<div style="position: relative; top: 0px;">
		<div style="position: relative; left: 40px; height: 30px;">
			<div style="width: 300px; color: gray;">所有者</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${t.createBy}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">金额</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${t.money}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 10px;">
			<div style="width: 300px; color: gray;">名称</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${t.name}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">预计成交日期</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${t.expectedDate}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 20px;">
			<div style="width: 300px; color: gray;">客户名称</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${t.customerId}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">阶段</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b id="stage">${t.stage}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 30px;">
			<div style="width: 300px; color: gray;">类型</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${t.type}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">成交可能性</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b id="possibility">&nbsp;${t.possibility}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 40px;">
			<div style="width: 300px; color: gray;">来源</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${t.source}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">市场活动源</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${t.expectedDate}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 50px;">
			<div style="width: 300px; color: gray;">联系人名称</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${t.contactsId}</b></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 60px;">
			<div style="width: 300px; color: gray;">创建者</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${t.createBy}&nbsp;&nbsp;</b><small style="font-size: 10px; color: gray;">${t.createTime}</small></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 70px;">
			<div style="width: 300px; color: gray;">修改者</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b id="editBy">z${t.editBy}&nbsp;&nbsp;</b><small id="editTime" style="font-size: 10px; color: gray;">${t.editTime}</small></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 80px;">
			<div style="width: 300px; color: gray;">描述</div>
			<div style="width: 630px;position: relative; left: 200px; top: -20px;">
				<b>
					${t.description}
				</b>
			</div>
			<div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 90px;">
			<div style="width: 300px; color: gray;">联系纪要</div>
			<div style="width: 630px;position: relative; left: 200px; top: -20px;">
				<b>
					${t.contactSummary}
				</b>
			</div>
			<div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 100px;">
			<div style="width: 300px; color: gray;">下次联系时间</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;">
				<b>
					${t.nextContactTime}
				</b>
			</div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
	</div>

	
	<!-- 阶段历史 -->
	<div>
		<div style="position: relative; top: 100px; left: 40px;">
			<div class="page-header">
				<h4>本订单交易历史</h4>
			</div>

			<div style="position: relative;top: 0px;">
				<table id="activityTable" class="table table-hover" style="width: 900px;">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="qx"/></td>
							<td>序号</td>
							<td>订单阶段</td>
							<td>金额</td>
							<td>成交的可能性</td>
							<td>预计成交日期</td>
							<td>订单创建时间</td>
							<td>创建人</td>
						</tr>
					</thead>
					<tbody id="tranHistoryBody">

					</tbody>
				</table>
			</div>

			<div class="btn-group" style="position: relative; top: 18%;">
				<button type="button"  class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
			</div>

		</div>
	</div>
	
	<div style="height: 200px;"></div>
	
</body>
</html>
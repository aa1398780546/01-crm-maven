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

            //发送Ajax请求
            $.ajax({
                url:"workbench/activity/getUserListById.do",
                data:{
                    "pageNo":pageNo,
                    "pageSize":pageSize,
                    "id":"${user.id}"
                },
                type:"get",
                dataType:"json",
                success:function (data){

                    var html ="";

                    $.each(data.dataList,function (i,n){
                        html += '<tr class="active">'
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

<div>
    <div style="position: relative; left: 10px; top: -10px;">
        <div class="page-header">
            <h3>货物管理列表</h3>
        </div>
    </div>
</div>
<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
    <div style="width: 100%; position: absolute;top: 5px; left: 10px;">
        <div style="position: relative;top: 10px;">
            <table class="table table-hover">
                <thead>
                <tr style="color: #B3B3B3;">
                    <td>货物名称</td>
                    <td>所有者</td>
                    <td>接收货物日期</td>
                    <td>结束日期</td>
                </tr>
                </thead>
                <tbody id="activityBody">
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
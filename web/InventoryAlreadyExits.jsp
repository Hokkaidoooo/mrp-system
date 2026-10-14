<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2019/6/16
  Time: 15:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%--  鑾峰彇鍏叡澶撮儴--%>
<%@ include file="iframe/head.jsp"%>
<div class="container">

<div class="col-sm-12">
    <div class="alert alert-warning alert-dismissible" role="alert">
        <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <strong>鏂板澶辫触</strong>鏁版嵁搴撲腑宸插瓨鍦ㄥ搴旀潯鐩€?    </div>
    <div class="panel panel-default">

        <!-- Table -->
        <table class="table table-bordered table-hover">
            <tr>
                <th rowspan="2">鐗╂枡缂栫爜</th>
                <th rowspan="2">鍦ㄥ簱閲?/th>
                <th rowspan="2">淇濈暀閲?/th>
                <th colspan="13">璁″垝鏀舵枡閲?/th>
                <th rowspan="2">鎿嶄綔</th>
            </tr>
            <tr>
                <th>0</th>
                <th>1</th>
                <th>2</th>
                <th>3</th>
                <th>4</th>
                <th>5</th>
                <th>6</th>
                <th>7</th>
                <th>8</th>
                <th>9</th>
                <th>10</th>
                <th>11</th>
                <th>12</th>
            </tr>
            <s:iterator value="Inventory_list">
            <tr>
                <th><s:property value="P_No"/></th>
                <th><s:property value="OH"/></th>
                <th><s:property value="AL"/></th>
                <th><s:property value="OO0"/></th>
                <th><s:property value="OO1"/></th>
                <th><s:property value="OO2"/></th>
                <th><s:property value="OO3"/></th>
                <th><s:property value="OO4"/></th>
                <th><s:property value="OO5"/></th>
                <th><s:property value="OO6"/></th>
                <th><s:property value="OO7"/></th>
                <th><s:property value="OO8"/></th>
                <th><s:property value="OO9"/></th>
                <th><s:property value="OO10"/></th>
                <th><s:property value="OO11"/></th>
                <th><s:property value="OO12"/></th>
                <th>
                    <s:url id="url_delete" value="Inventory_delete.action">
                        <s:param name="Inventory_id" value="id"/>
                    </s:url>
                    <button type="button" class="btn btn-default">
                        <s:a href="%{url_delete}">鍒犻櫎</s:a>
                    </button>
                </th>
            <tr>
            </s:iterator>
            <form class="form-horizontal" action="${pageContext.request.contextPath}/Inventory_add.action" method="post">
                <tr>
                    <th>
                        <input type="text" name="P_No" class="form-control" id="inputP_No">
                    </th>

                    <th>
                        <input type="number" name="OH" class="form-control" id="inputOH">
                    </th>

                    <th>
                        <input type="number" name="AL" class="form-control"  id="inputAL">
                    </th>

                    <th>
                        <input type="number" name="OO0" class="form-control"  id="inputOO0">
                    </th>

                    <th>
                        <input type="number" name="OO1" class="form-control"  id="inputOO1">
                    </th>

                    <th>
                        <input type="number" name="OO2" class="form-control"  id="inputOO2">
                    </th>

                    <th>
                        <input type="number" name="OO3" class="form-control"  id="inputOO3">
                    </th>

                    <th>
                        <input type="number" name="OO4" class="form-control"  id="inputOO4">
                    </th>

                    <th>
                        <input type="number" name="OO5" class="form-control"  id="inputOO5">
                    </th>

                    <th>
                        <input type="number" name="OO6" class="form-control"  id="inputOO6">
                    </th>

                    <th>
                        <input type="number" name="OO7" class="form-control"  id="inputOO7">
                    </th>

                    <th>
                        <input type="number" name="OO8" class="form-control"  id="inputOO8">
                    </th>

                    <th>
                        <input type="number" name="OO9" class="form-control"  id="inputOO9">
                    </th>

                    <th>
                        <input type="number" name="OO10" class="form-control"  id="inputOO10">
                    </th>

                    <th>
                        <input type="number" name="OO11" class="form-control"  id="inputOO11">
                    </th>

                    <th>
                        <input type="number" name="OO12" class="form-control"  id="inputOO12">
                    </th>

                    <th>
                        <input type="submit" value="鏂板" class="form-control"  id="submit">

                    </th>
                </tr>
            </form>
        </table>
    </div>
</div>
</div>
<div class="container">
</div>
    <%--  鑾峰彇鍏叡灏鹃儴--%>
<%--    <%@ include file="iframe/foot.jsp"%>--%>

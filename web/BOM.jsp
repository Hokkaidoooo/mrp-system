
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%--  鑾峰彇鍏叡澶撮儴--%>
<%@ include file="iframe/head.jsp"%>
<div class="container">
<div class="col-sm-12">
    <div class="panel panel-default" >

        <!-- Table -->
        <table class="table table-bordered table-hover" style="background: transparent">
            <tr>
                <th>鐖朵欢缂栫爜</th>
                <th>瀛愪欢缂栫爜</th>
                <th>瀛愪欢闇€姹傛暟閲?/th>
                <th>鎿嶄綔</th>
            </tr>
            <s:iterator value="BOM_list">
            <tr>
                <th><s:property value="parent"/></th>
                <th><s:property value="comp"/></th>
                <th><s:property value="Q_P"/></th>
                <th>
                    <s:url id="url_delete" value="BOM_delete.action">
                        <s:param name="BOM_id" value="id"/>
                    </s:url>
                    <button type="button" class="btn btn-default">
                        <s:a href="%{url_delete}">鍒犻櫎</s:a>
                    </button>
                </th>
            <tr>
            </s:iterator>
            <form class="form-horizontal" action="${pageContext.request.contextPath}/BOM_add.action" method="post">
                <tr>
                    <th>
                        <input type="text" name="parent" class="form-control"  id="inputParent">
                    </th>
                    <th>
                        <input type="text" name="comp" class="form-control"  id="inputComp">

                    </th>
                    <th>
                        <input type="number" name="Q_P" class="form-control"  id="inputQ_P">

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
<%--  鑾峰彇鍏叡灏鹃儴--%>
<%--<%@ include file="iframe/foot.jsp"%>--%>

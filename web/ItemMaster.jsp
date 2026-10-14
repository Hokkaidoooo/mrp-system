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
        <div class="panel panel-default">

            <!-- Table -->
            <table class="table table-bordered table-hover">
                <tr>
                    <th>鐗╂枡缂栫爜</th>
                    <th>鎻愬墠鏈?/th>
                    <th>瀹夊叏鎻愬墠鏈?/th>
                    <th>瀹夊叏搴撳瓨</th>
                    <th>灞傜骇</th>
                    <th>鎵归噺绛栫暐</th>
                    <th>鎵归噺鍊?/th>

                    <th>鎿嶄綔</th>
                </tr>
                <s:iterator value="ItemMaster_list">
                    <tr>
                        <th><s:property value="P_No"/></th>
                        <th><s:property value="LT"/></th>
                        <th><s:property value="ST"/></th>
                        <th><s:property value="SS"/></th>
                        <th><s:property value="LLC"/></th>
                        <th><s:property value="LSR"/></th>
                        <th><s:property value="LS"/></th>
                        <th>
                            <s:url id="url_delete" value="ItemMaster_delete.action">
                                <s:param name="ItemMaster_id" value="id"/>
                            </s:url>
                            <button type="button" class="btn btn-default">
                                <s:a href="%{url_delete}">鍒犻櫎</s:a>
                            </button>
                        </th>
                    <tr>
                </s:iterator>
                <form class="form-horizontal" action="${pageContext.request.contextPath}/ItemMaster_add.action" method="post">
                    <tr>
                        <th>
                            <input type="text" name="P_No" class="form-control" id="inputP_No">
                        </th>

                        <th>
                            <input type="number" name="LT" class="form-control" id="inputLT">
                        </th>

                        <th>
                            <input type="number" name="ST" class="form-control" id="inputST">
                        </th>

                        <th>
                            <input type="number" name="SS" class="form-control" id="inputSS">
                        </th>

                        <th>
                            <input type="number" name="LLC" class="form-control" id="inputLLC">
                        </th>

                        <th>
                            <select class="form-control" id="inputLSR" name="LSR" required="required">
                                <option>璇烽€夋嫨...</option>
                                <option>FOQ</option>
                                <option>LFL</option>
                            </select>
                        </th>

                        <th>
                            <input type="number" name="LS" class="form-control" id="inputLS">
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

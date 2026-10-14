
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%--  鑾峰彇鍏叡澶撮儴--%>
<%@ include file="iframe/head.jsp"%>
<div class="container">

<div class="col-sm-12">
<s:iterator value="TPOP_list">
    <div class="panel panel-default">
        <table class="table table-bordered table-hover">
            <tr>
                <th>浜у搧:<s:property value="P_No"/></th>
                <th rowspan="2">鏈熷垵</th>
                <th colspan="2">鍦ㄥ簱閲?<s:property value="OH"/></th>
                <th colspan="2">淇濈暀閲?<s:property value="AL"/></th>
                <th colspan="2">鎻愬墠鏈?<s:property value="LT"/></th>
                <th colspan="2">瀹夊叏鎻愬墠鏈?<s:property value="ST"/></th>
                <th colspan="2">瀹夊叏搴撳瓨=<s:property value="SS"/></th>
                <th colspan="2">鎵归噺绛栫暐<s:property value="LSR"/>&nbsp&nbsp&nbsp<s:property value="LS"/></th>
            </tr>
            <tr>
                <th>鍛ㄦ暟</th>
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
            <tr>
                <th>姣涢渶姹?/th>
                <th><s:property value="GR0"/></th>
                <th><s:property value="GR1"/></th>
                <th><s:property value="GR2"/></th>
                <th><s:property value="GR3"/></th>
                <th><s:property value="GR4"/></th>
                <th><s:property value="GR5"/></th>
                <th><s:property value="GR6"/></th>
                <th><s:property value="GR7"/></th>
                <th><s:property value="GR8"/></th>
                <th><s:property value="GR9"/></th>
                <th><s:property value="GR10"/></th>
                <th><s:property value="GR11"/></th>
                <th><s:property value="GR12"/></th>
            </tr>
            <tr>
                <th>鍦ㄩ€旈噺</th>
                <th><s:property value="SR0"/></th>
                <th><s:property value="SR1"/></th>
                <th><s:property value="SR2"/></th>
                <th><s:property value="SR3"/></th>
                <th><s:property value="SR4"/></th>
                <th><s:property value="SR5"/></th>
                <th><s:property value="SR6"/></th>
                <th><s:property value="SR7"/></th>
                <th><s:property value="SR8"/></th>
                <th><s:property value="SR9"/></th>
                <th><s:property value="SR10"/></th>
                <th><s:property value="SR11"/></th>
                <th><s:property value="SR12"/></th>
            </tr>
            <tr>
                <th>棰勮鍙敤搴撳瓨</th>
                <th><s:property value="POH0"/></th>
                <th><s:property value="POH1"/></th>
                <th><s:property value="POH2"/></th>
                <th><s:property value="POH3"/></th>
                <th><s:property value="POH4"/></th>
                <th><s:property value="POH5"/></th>
                <th><s:property value="POH6"/></th>
                <th><s:property value="POH7"/></th>
                <th><s:property value="POH8"/></th>
                <th><s:property value="POH9"/></th>
                <th><s:property value="POH10"/></th>
                <th><s:property value="POH11"/></th>
                <th><s:property value="POH12"/></th>
            </tr>
            <tr>
                <th>棰勮鍙敤閲?/th>
                <th><s:property value="PAB0"/></th
                <th><s:property value="PAB1"/></th>
                <th><s:property value="PAB2"/></th>
                <th><s:property value="PAB3"/></th>
                <th><s:property value="PAB4"/></th>
                <th><s:property value="PAB5"/></th>
                <th><s:property value="PAB6"/></th>
                <th><s:property value="PAB7"/></th>
                <th><s:property value="PAB8"/></th>
                <th><s:property value="PAB9"/></th>
                <th><s:property value="PAB10"/></th>
                <th><s:property value="PAB11"/></th>
                <th><s:property value="PAB12"/></th>
            </tr>
            <tr>
                <th>鍑€闇€姹?/th>
                <th><s:property value="NR0"/></th>
                <th><s:property value="NR1"/></th>
                <th><s:property value="NR2"/></th>
                <th><s:property value="NR3"/></th>
                <th><s:property value="NR4"/></th>
                <th><s:property value="NR5"/></th>
                <th><s:property value="NR6"/></th>
                <th><s:property value="NR7"/></th>
                <th><s:property value="NR8"/></th>
                <th><s:property value="NR9"/></th>
                <th><s:property value="NR10"/></th>
                <th><s:property value="NR11"/></th>
                <th><s:property value="NR12"/></th>
            </tr>
            <tr>
                <th>璁″垝浜у嚭閲?/th>
                <th><s:property value="PORcpt0"/></th>
                <th><s:property value="PORcpt1"/></th>
                <th><s:property value="PORcpt2"/></th>
                <th><s:property value="PORcpt3"/></th>
                <th><s:property value="PORcpt4"/></th>
                <th><s:property value="PORcpt5"/></th>
                <th><s:property value="PORcpt6"/></th>
                <th><s:property value="PORcpt7"/></th>
                <th><s:property value="PORcpt8"/></th>
                <th><s:property value="PORcpt9"/></th>
                <th><s:property value="PORcpt10"/></th>
                <th><s:property value="PORcpt11"/></th>
                <th><s:property value="PORcpt12"/></th>
            </tr>
            <tr>
                <th>璁″垝鎶曞叆閲?/th>
                <th><s:property value="POR0"/></th>
                <th><s:property value="POR1"/></th>
                <th><s:property value="POR2"/></th>
                <th><s:property value="POR3"/></th>
                <th><s:property value="POR4"/></th>
                <th><s:property value="POR5"/></th>
                <th><s:property value="POR6"/></th>
                <th><s:property value="POR7"/></th>
                <th><s:property value="POR8"/></th>
                <th><s:property value="POR9"/></th>
                <th><s:property value="POR10"/></th>
                <th><s:property value="POR11"/></th>
                <th><s:property value="POR12"/></th>
            </tr>

        </table>
    </div>
</s:iterator>

</div>
</div>
<div class="container">
</div>

    <%--  鑾峰彇鍏叡灏鹃儴--%>
<%--    <%@ include file="iframe/foot.jsp"%>--%>

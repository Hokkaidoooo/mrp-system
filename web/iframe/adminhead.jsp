
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>鏍″洯浜屾墜鍥句功鍦ㄧ嚎浜ゆ槗骞冲彴</title>
    <!-- Bootstrap -->
    <link href="css/bootstrap.min.css" rel="stylesheet">
</head>
<body>



<div class="page-header" style="background:#f7f7f7">
    <nav class="navbar navbar-default">
        <div class="container">
            <!-- Brand and toggle get grouped for better mobile display -->

            <!-- Collect the nav links, forms, and other content for toggling -->
            <ul class="nav navbar-nav navbar-right">
                <li>
                    <button type="button" class="btn btn-default navbar-btn">
                        <a href="user.action">${sessionScope.admin.username}鐧诲綍</a>
                    </button>
                    <button type="button" class="btn btn-default navbar-btn">
                        <a href="logout.action">閫€鍑虹櫥褰?/a>
                    </button>
                </li>
            </ul>
            <!-- /.navbar-collapse -->
            <!-- /.container-fluid -->
            <h2>HIT<small>  鏍″洯浜屾墜鍥句功鍦ㄧ嚎浜ゆ槗骞冲彴</small></h2>
        </div>

    </nav>
    <div class="container">

        <%--涓昏彍鍗曞紑濮?-%>
        <ul class="nav nav-pills">
            <li role="presentation"><a href="index.action">棣栭〉</a></li>
            <li role="presentation"><a href="affichelist.action">鏂伴椈鍏憡</a></li>
            <li role="presentation"><a href="booklist.action">涔︾睄淇℃伅</a></li>
            <li role="presentation"><a href="booktypelist.action">鍒嗙被绱㈠紩</a></li>
            <li role="presentation"><a href="quickquery.action">蹇€熸煡璇?/a></li>
            <li role="presentation"><a href="guestbooklist.action">鐣欒█鍒嗕韩</a></li>
            <li role="presentation"><a href="user.action">鐢ㄦ埛涓績</a></li>
            <li role="presentation"><a href="adminlogin.jsp">绠＄悊涓績</a></li>

        </ul>
        <%--涓昏彍鍗曠粨鏉?-%>

    </div>
</div>
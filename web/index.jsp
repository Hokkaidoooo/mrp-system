<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%--  鑾峰彇鍏叡澶撮儴--%>
<%@ include file="iframe/head.jsp"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        body, html {
            height: 100%;
            margin: 0;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            font-family: Arial, sans-serif;
            overflow: hidden; /* Ensure no scrollbars appear */
        }
        .welcome-container {
            flex-grow: 1;
            display: flex;
            justify-content: center;
            align-items: center;
            width: 100%;
            z-index: 1; /* Ensure the message is above the canvas */
            position: relative;
        }
        .welcome-message {
            font-size: 24px;
            font-weight: bold;
            color: #fff; /* Change text color to white for better contrast */
            background: rgba(0, 0, 0, 0.5); /* Optional: Add semi-transparent background for readability */
            padding: 20px; /* Optional: Add padding for better appearance */
            border-radius: 10px; /* Optional: Add border radius for a rounded effect */
        }
        canvas {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            z-index: 0; /* Ensure canvas is behind the content */
        }
    </style>
</head>
<body>
<div class="welcome-container">
    <div class="welcome-message">
        娆㈣繋杩涘叆MRP绠楁硶瀹炵幇绯荤粺
    </div>
</div>

</body>
</html>

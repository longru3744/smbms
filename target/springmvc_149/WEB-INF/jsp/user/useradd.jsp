<%@ taglib prefix="fm" uri="http://www.springframework.org/tags/form" %>
<%--
  Created by IntelliJ IDEA.
  User: 龙汝
  Date: 2020/10/15
  Time: 15:29
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<fm:form method="post" modelAttribute="user">
    用户编码:<fm:input path="userCode"/><br/>
    用户名称:<fm:input path="userName"/><br/>
    用户密码:<fm:password path="userPassword"/><br/>

    用户生日:<fm:input path="birthday" cssClass="Wdate" readonly="true" onclick="WdatePicker();"/><br/>
    用户地址:<fm:input path="address"/><br/>
    联系电话:<fm:input path="phone"/><br/>
<%--    <div>用户角色:<fm:select path="userRole">--%>
<%--        <option value="1">管理员</option>--%>
<%--        <option value="2">经理</option>--%>
<%--        <option value="3">普通员工</option>--%>
<%--    </fm:select></div>--%>
<br/>
    用户角色：
    <fm:radiobutton path="userRole" value="1"/>系统管理员
    <fm:radiobutton path="userRole" value="2"/>经理
    <fm:radiobutton path="userRole" value="3"/>普通用户
    <br/>
    <br/>
    <input type="submit" value="保存">
</fm:form>
</body>
<script src="/statics/calendar/WdatePicker.js"></script>
</html>

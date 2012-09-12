<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

	<p>Administration for enrolling all users</p>

	<form
		action="/motech-platform-server/module/gates-ethiopia/enrollments/enrollall">
		<input type="submit" value="Enroll" />
	</form>

	<form
		action="/motech-platform-server/module/gates-ethiopia/enrollments/unenrollall">
		<input type="submit" value="Unenroll" />
	</form>

	<form
		action="/motech-platform-server/module/gates-ethiopia/enrollments/enrollallevenifcompleted">
		<input type="submit" value="Re-enroll" />
	</form>

</body>
</html>
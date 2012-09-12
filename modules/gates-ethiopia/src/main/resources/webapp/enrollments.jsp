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

	ACTIVE HEW Enrollments

	<table>
		<c:forEach var="activeenrollments" items="${activeenrollments}">
			<tr>
				<td>${activeenrollments.externalId}</td>
			</tr>
		</c:forEach>
	</table>
	
	COMPLETED HEW Enrollments
	
	<table>
		<c:forEach var="completedenrollments" items="${completedenrollments}">
			<tr>
				<td>${completedenrollments.externalId}</td>
			</tr>
		</c:forEach>
	</table>
	
	DEFAULTED HEW Enrollments
	
	<table>
		<c:forEach var="defaultedenrollments" items="${defaultedenrollments}">
			<tr>
				<td>${defaultedenrollments.externalId}</td>
			</tr>
		</c:forEach>
	</table>
	
	UNENROLLED HEW Enrollments
	
	<table>
		<c:forEach var="unenrolledenrollments" items="${unenrolledenrollments}">
			<tr>
				<td>${unenrolledenrollments.externalId}</td>
			</tr>
		</c:forEach>
	</table>
</body>
</html>
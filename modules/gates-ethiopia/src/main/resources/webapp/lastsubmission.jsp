<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>MoTeCH-CommCare demo</title>
<style type="text/css">
table {
	border-collapse: collapse;
	width: 100%;
}

table .odd {
	background: #99CCFF;
	color: #FFFFFF;
}

table th {
	border-bottom: 1px solid #222222;
	padding: 3px;
	text-align: left;
}

table td {
	border-bottom: 1px dotted #222222;
	padding: 3px;
}

table td:first-child {
	border-left: 1px solid #222222;
}

table td:last-child {
	border-right: 1px solid #222222;
}
</style>
</head>
<body>
	<p><b>MoTeCH - CommCare Interoperability Demo</b></p>
	<p>In order to use this demo, install the CommCare ethiopia-demo indicator reporting application to an Android phone. You can then enroll an HEW into a short schedule that expects forms to be submitted every four minutes for twenty minutes. Choose the correct day, and preferably a time a minute into the future for your enrollment. Select the correct time zone for the given HEW (John, Ken and Russellwest should be PDT, the others EDT). Login to the CommCare app to submit indicator reports. E-mails will be sent to MotechDemoRegion1@Gmail.com and MotechDemoRegion2@Gmail.com depending on the user. No e-mail is sent if you are on time for your report.</p>

	<table>
		<tr>
			<th>HEW</th>
			<th>Case Id</th>
			<th>Last date submitted&nbsp;&nbsp;</th>
			<th>Closed</th>
			<th>Enrolled</th>
			<th>
			</td>
			<th># Times Late</th>
		</tr>
		<c:set var="count" value="1" scope="page" />
		<c:forEach var="hews" items="${hews}">
		<c:choose>
		<c:when test="${count % 2 != 0 }">
		<tr class="odd">
		</c:when>
		<c:otherwise>
		<tr>
		</c:otherwise>
		</c:choose>
		<c:set var="count" value="${count + 1}" scope="page"/>
				<td>${hews.fieldValues.hew_name}</td>
				<td>${hews.caseId}</td>
				<c:choose>
					<c:when test="${empty hews.fieldValues.reporting_date}">
						<td>Never</td>
					</c:when>
					<c:otherwise>
						<td>${hews.fieldValues.reporting_date}</td>
					</c:otherwise>
				</c:choose>
				<td>${hews.closed}</td>
				<c:choose>
					<c:when test="${empty activeenrollments[hews.caseId]}">
						<td>No</td>
						<c:choose>
							<c:when test="${hews.closed == false}">
								<td>
									<form
										action="/motech-platform-server/module/gates-ethiopia/enrollments/enroll"">
										<input type="submit" value="Enroll" /> <input type="hidden"
											value="${hews.caseId}" name="caseId" /><select name="day">
											<option value="1">Monday</option>
											<option value="2">Tuesday</option>
											<option value="3">Wednesday</option>
											<option value="4">Thursday</option>
											<option value="5">Friday</option>
											<option value="6">Saturday</option>
											<option value="7">Sunday</option>
										</select> <select name="hour">
											<option value="0">0</option>
											<option value="1">1</option>
											<option value="2">2</option>
											<option value="3">3</option>
											<option value="4">4</option>
											<option value="5">5</option>
											<option value="6">6</option>
											<option value="7">7</option>
											<option value="8">8</option>
											<option value="9">9</option>
											<option value="10">10</option>
											<option value="11">11</option>
											<option value="12">12</option>
											<option value="13">13</option>
											<option value="14">14</option>
											<option value="15">15</option>
											<option value="16">16</option>
											<option value="17">17</option>
											<option value="18">18</option>
											<option value="19">19</option>
											<option value="20">20</option>
											<option value="21">21</option>
											<option value="22">22</option>
											<option value="23">23</option>
										</select> <select name="minute">
											<option value="0">0</option>
											<option value="1">1</option>
											<option value="2">2</option>
											<option value="3">3</option>
											<option value="4">4</option>
											<option value="5">5</option>
											<option value="6">6</option>
											<option value="7">7</option>
											<option value="8">8</option>
											<option value="9">9</option>
											<option value="10">10</option>
											<option value="11">11</option>
											<option value="12">12</option>
											<option value="13">13</option>
											<option value="14">14</option>
											<option value="15">15</option>
											<option value="16">16</option>
											<option value="17">17</option>
											<option value="18">18</option>
											<option value="19">19</option>
											<option value="20">20</option>
											<option value="21">21</option>
											<option value="22">22</option>
											<option value="23">23</option>
											<option value="24">24</option>
											<option value="25">25</option>
											<option value="26">26</option>
											<option value="27">27</option>
											<option value="28">28</option>
											<option value="29">29</option>
											<option value="30">30</option>
											<option value="31">31</option>
											<option value="32">32</option>
											<option value="33">33</option>
											<option value="34">34</option>
											<option value="35">35</option>
											<option value="36">36</option>
											<option value="37">37</option>
											<option value="38">38</option>
											<option value="39">39</option>
											<option value="40">40</option>
											<option value="41">41</option>
											<option value="42">42</option>
											<option value="43">43</option>
											<option value="44">44</option>
											<option value="45">45</option>
											<option value="46">46</option>
											<option value="47">47</option>
											<option value="48">48</option>
											<option value="49">49</option>
											<option value="50">50</option>
											<option value="51">51</option>
											<option value="52">52</option>
											<option value="53">53</option>
											<option value="54">54</option>
											<option value="55">55</option>
											<option value="56">56</option>
											<option value="57">57</option>
											<option value="58">58</option>
											<option value="59">59</option>
										</select> <select name="timezone">
											<option value="EDT">EDT</option>
											<option value="PDT">PDT</option>
										</select>
									</form>
								</td>
							</c:when>
						</c:choose>
					</c:when>
					<c:otherwise>
						<td>${activeenrollments[hews.caseId]}</td>
						<td>
							<form
								action="/motech-platform-server/module/gates-ethiopia/enrollments/unenroll">
								<input type="submit" value="Unenroll" /> <input type="hidden"
									value="${hews.caseId}" name="caseId" />
							</form>
						</td>
					</c:otherwise>
				</c:choose>
				<td>${late[hews.caseId]}</td>

			</tr>
		</c:forEach>
	</table>
</body>
</html>
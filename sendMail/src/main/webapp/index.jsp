<!DOCTYPE html>

<%@ page import="com.synchronit.sendmail.SendMail,java.util.List,java.util.Collections,java.util.Map,java.util.Set,java.util.SortedSet,java.io.*,javax.servlet.*" %>

<html xmlns="http://www.w3.org/1999/xhtml">
 
<body> 

	<% 
		String text = request.getParameter("text");
		String name = request.getParameter("name");
		String mail = request.getParameter("mail");

		SendMail sendMail = SendMail.getInstance(); 
		String result = "hola Fer!";
		// String result = sendMail.send(text, name, mail);
	%>

	  <p><%= result %></p>

</body>

</html>

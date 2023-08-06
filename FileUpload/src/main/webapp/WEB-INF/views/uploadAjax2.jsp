<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<h1>Upload with Ajax</h1>

<div class="uploadDiv">
	<input type="file" name="uploadFile" multiple accept="image/*" >
</div>
<div class="uploadResult">
	<ul>
	
	</ul>
</div>
<button id="uploadBtn">Upload</button>


<script  src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	
	$("#uploadBtn").on("click", function(e){
		var formData = new FormData();
		
		var inputFile = $("input[name='uploadFile']");
		
		var files = inputFile[0].files;
		
		//console.log(files);
		
		for(var i = 0; i < files.length; i++){
			
			formData.append("uploadFile", files[i]);
		}
		
		console.log(formData);
		
		$.ajax({
			url: "/file/uploadAjaxAction2",
			processData: false,
			contentType: false,
			data: formData,
			type: "POST",
			success: function(result){
				
				console.log(result);
			}
		});
		
	});
});
</script>
</body>
</html>
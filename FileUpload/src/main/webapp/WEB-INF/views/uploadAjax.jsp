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
	<input type="file" name="uploadFile" multiple>
</div>
<div class="uploadResult">
	<ul>
	
	</ul>
</div>
<button id="uploadBtn">Upload</button>


<script  src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	
	var uploadResult = $(".uploadResult ul");
	
	function showUploadedFile(uploadResultArr){
		
		var str = "";
//		each의 경우 반복문이랑 같음
		$(uploadResultArr).each(function(i, obj){
			
		var fileCallPath = encodeURIComponent( obj.uploadPath + "/s_"+obj.uuid+"_"+obj.fileName);
		alert(obj.uploadPath);
		alert(obj.uuid);
		alert(obj.fileName);
		str += "<li><img src='/file/display?fileName=/"+fileCallPath+ "'></li>'";
		});
		uploadResult.append(str);
		
	}
	
	// 업로드시 지정한 파일의 타입 이외에는 업로드 불가능하게 설정 --> 정규식
	var regex = new RegExp("(.*?)\.(exe|sh|zip|alz)");
	
	function checkExtension(fileName){
		
		if(regex.test(fileName)){
			alert("해당 종류의 파일은 업로드할 수 없습니다.");
			return false;
		}
		return true;
	}
	
	// 파일 업로드 버튼 클릭시 초창기 상태로 돌려놓기 위해서 초창기 상태를 복사
	var cloneObj = $(".uploadDiv").clone();
	
	
	$("#uploadBtn").on("click", function(e){
		var formData = new FormData();
		
		var inputFile = $("input[name='uploadFile']");
		
		var files = inputFile[0].files;
		
		console.log(files);
		
		for(var i = 0; i < files.length; i++){
			if(!checkExtension(files[i].name)){
				return false;
			}
			formData.append("uploadFile", files[i]);
		}
		
		$.ajax({
			url: "/file/uploadAjaxAction",
			processData: false,
			contentType: false,
			data: formData,
			type: "POST",
			dataType: "json",
			success: function(result){
				
				showUploadedFile(result);
				
				console.log(result);
				
				$(".uploadDiv").html(cloneObj.html());
			}
		});
		
	});
});
</script>
</body>
</html>
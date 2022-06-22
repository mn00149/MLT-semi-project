<%@ page contentType="text/html; charset=UTF-8" %> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html> 
<html> 
<head>
  <title>homepage</title>
  <meta charset="utf-8">
  <script type="text/javascript" src="${pageContext.request.contextPath}/ckeditor/ckeditor.js">     </script>
  <script type="text/JavaScript">
 $(function() {
	 CKEDITOR.replace('detail'); // <TEXTAREA>태그 id 값
	 });

 function checkIn(f){
     if (f.pname.value == ""){
             alert("상품명을 입력하세요");
             f.pname.focus()
             return false;
     }
     if (f.price.value == ""){
             alert("가격을 입력하세요");
             f.price.focus();
             return false;
     }
     if (CKEDITOR.instances['detail'].getData() == '') {
         window.alert('내용을 입력해 주세요.');
         CKEDITOR.instances['detail'].focus();
         return false;
     }
     if (f.stock.value == ""){
             alert("재고를 입력하세요");
             f.stock.focus();
             return false;
     }
}
 </script>
</head>
<body> 

<div class="container">
<h1 class="col-sm-offset-2 col-sm-10">상품 정보</h1>

  <input type="hidden" name="contentsno" value="${contentsno}">
  <div class="form-group">
    <label class="control-label col-sm-2" for="cateno">상품분류</label>
    <div class="col-sm-6">
      
        
        <c:if test= "${dto.cateno==1}"><div>Jean</div> </c:if>
        
        
        <c:if test= "${dto.cateno==2}"> <div>Bag</div> </c:if>
        
        
        <c:if test= "${dto.cateno==3}"> <div>Shoes</div> </c:if>
        
    
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-sm-2" for="pname">상품명</label>
    <div class="col-sm-8">
      <p>${dto.pname }</p>
    </div>
  </div>
  <div class="form-group">
    <label class="control-label col-sm-2" for="price">가격</label>
    <div class="col-sm-8">
      <p>${dto.price }</p>
    </div>
  </div>
  
  <div class="form-group">
    <label class="control-label col-sm-2" for="detail">상세정보</label>
    <div class="col-sm-8">
    <p rows="12" cols="7" id="detail" name="detail" class="form-control">${dto.detail }</p>
    </div>
  </div>
  
  <div class="form-group">
    <label class="control-label col-sm-2" for="stock">재고</label>
    <div class="col-sm-6">
      <p>${dto.stock}</p>
    </div>
  </div>
  
   <div class="form-group">
   <div class="col-sm-offset-2 col-sm-5">

    <button type="reset" class="btn" onclick="history.back()">뒤로</button>
   </div>
 </div>

</div>
</body> 
</html> 
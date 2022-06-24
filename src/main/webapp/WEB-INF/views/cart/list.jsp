<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="util" uri="/ELFunctions"%>

<!DOCTYPE html>
<html>
<head>
<title>Bootstrap Example</title>
<meta charset="utf-8">
<script type="text/javascript">

	// 상품이 없을때 구현
	$(function(){
		if('${empty list}'=='true'){
			$("tfoot").hide();   // 태그찾기
		} else {
			$("tfoot").show();
		}
		
	})

	function del(cartno){
		
		if(confirm("상품을 삭제하시겠습니까?")){
			let url = '/cart/delete/'+cartno
			location.href=url;           // url 선언 get방식
		}
	}
	
	function read(contentsno) {
		var url = "/contents/detail/" + contentsno;
		url += "?contentsno=" + contentsno;
		url += "&col=${col}";
		url += "&word=${word}";
		url += "&nowPage=${nowPage}";
		location.href = url;
	}

	function change(check) {
		if (check.checked) {

			let aa = document.querySelectorAll("#ch");
			for (let i = 0; i < aa.length; i++) {
				aa[i].checked=true;
			}
		} else {
			aa = document.querySelectorAll("#ch")
			for (let i = 0; i < aa.length; i++) {
				aa[i].checked=false;    //체크박스 한번에 묶고 풀기
			}
		}
	}
	
	function order(){
		// alert("hi");
		let cno = document.querySelectorAll("#ch");   //cno는 배열 아이디가 ch인것을 찾는다.
		let qty = document.querySelectorAll("#qty");  
		let size = document.querySelectorAll("#size");  
		
		let cnt = 0;  //체크값을 검사하는 변수 (체크가 되어있으면 +1)
		let param_cno = '';  //문자형태로 선언, 체크박스안에있는 벨류값 저장(상품 번호가 여러개 연결)
		let param_qty ='';   //수량을 여러개 연결
		let param_size = '';  //사이즈를 여러개 연결
		
		for(let i=0; i<cno.length; i++){
			if(cno[i].checked==true){
				cnt++;
				param_cno += cno[i].value + ',';
				param_qty += qty[i].value + ',';
				param_size += size[i].innerText+',';
			} 
		}
		if(cnt==0){
			alert("상품을 선택하세요");
			return;
		} else{
			//alert(param_cno);
			//alert(param_qty);
			//alert(param_size);
			
			let url="/order/create/cart/"+param_cno+"/"+param_qty+"/"+param_size;
			location.href=url;
			
		}
		
	}
	
</script>

</head>
<body>
	<div class="container">

		<h2>
			<img src="/svg/cart4.svg" style='width: 30px'> 장바구니
		</h2>

		<table class="table table-striped">
			<thead>
				<tr>
					<th><input type='checkbox' onchange="change(this)"></th>
					<th>상품이미지</th>
					<th>상품명</th>
					<th>수량</th>
					<th>가격</th>
					<th>삭제</th>
				</tr>
			</thead>
			<tbody>

				<c:choose>
					<c:when test="${empty list}">
						<tr>
							<td colspan="6">등록된 상품이 없습니다.</td>
					</c:when>
					<c:otherwise>

						<c:set var='tot' value='${0 }'></c:set>
						<c:forEach var="dto" items="${list }">
							<c:set var='tot' value='${tot + (dto.cdto.price * dto.count) }'></c:set>

							<tr>
								<td><input type='checkbox' id='ch' value="${dto.cdto.contentsno }"></td>
								<td><img src="/contents/storage/${dto.cdto.filename }"
									class="img-rounded" width="100px" height="100px"></td>
								<td><a href="javascript:read('${dto.cdto.contentsno }')">
								${dto.cdto.pname}(size : <span id='size'>${dto.size }</span>)
								</a></td>
								<td><input type='number' value="${dto.count }" min="1" max="10" id="qty"></td>
								<td>${dto.cdto.price }</td>
								<td><a href="javascript:del('${dto.cartno }')"> 
								<span class="glyphicon glyphicon-trash"></span>
								</a></td>
							</tr>
						</c:forEach>
					</c:otherwise>
				</c:choose>
			</tbody>
			<tfoot>
				<tr style="background-color: beige; font-size: large">
					<th colspan="6" style="padding: 30px;">
					주문금액 ${tot }원 + 배송비 3,000원 = 합계 ${tot + 3000 } 
						<a href="javascript:order()"> 
						<img src="/svg/bag-heart-fill.svg" title="주문하기"
							style='width: 30px; margin-left: 30px'></a> 
							<a href="/contents/mainlist/1"> 
							<img src="/svg/box2-heart.svg" 
							title="쇼핑계속" style='width: 30px; margin-left: 30px'></a>
					</th>
				</tr>

			</tfoot>
		</table>

	</div>
</body>
</html>
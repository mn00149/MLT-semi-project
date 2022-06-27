$(function () {//페이지가 로딩될때
   showList();
   showPage();
});//page loading function end

let reviewUL = $(".chat");//클래스가 chat인거 ->ul
let reviewPageFooter = $(".panel-footer");//클래스가 panel-footer인거 

function showList() {
    getList({ contentsno: contentsno, sno: sno, eno: eno }) //json객체
    .then(list => {
      let str = ""
 
      for (var i = 0; i < list.length ; i++) {
        str += "<li class='list-group-item' data-rnum='" + list[i].rnum + "'>";
        str += "<div><div class='header'><strong class='primary-font'>" + list[i].id + "</strong>";
        str += "<small class='pull-right text-muted'>" + list[i].regdate + "</small></div>";
        str += replaceAll(list[i].content, '\n', '<br>') + "</div></li>";
      }
 
      reviewUL.html(str);
    });
 
}//showList() end
 
function replaceAll(str, searchStr, replaceStr) {
  return str.split(searchStr).join(replaceStr);
}

let param = '';
    param = "nPage=" + nPage;
    param += "&contentsno=" + contentsno;

function showPage(){
    getPage(param)
    .then(paging => {
      console.log(paging);
      let str = "<div><small class='text-muted'>" + paging + "</small></div>";
 
      reviewPageFooter.html(str);
});
}

let modal = $(".modal");
let modalInputContent = modal.find("textarea[name='content']");
 
let modalModBtn = $("#modalModBtn");
let modalRemoveBtn = $("#modalRemoveBtn");
let modalRegisterBtn = $("#modalRegisterBtn");
 
$("#modalCloseBtn").on("click", function (e) {
   modal.modal('hide'); //모달숨기기
});
  
$("#addReplyBtn").on("click", function (e) {
	if(sessionId == '' || sessionId == null){
		alert("로그인 후 이용 가능합니다.");
		location.href="/member/login?rurl=/contents/detail/"+contentsno+"&"+param;
		return ;
	}
  modalInputContent.val("");
  modal.find("button[id !='modalCloseBtn']").hide();
 
  modalRegisterBtn.show();
 
  $(".modal").modal("show");
 
});
 
modalRegisterBtn.on("click", function (e) {
 
  if (modalInputContent.val() == '') {
    alert("댓글을 입력하세요")
    return;
  }
 
  let reply = {
    content: modalInputContent.val(),
    id: sessionId, 
    contentsno: contentsno
  };
  add(reply)	//비동기통신
    .then(result => {
      modal.find("input").val("");
      modal.modal("hide");
 
      showList(); //댓글목록
      showPage();//댓글페이지
 
    }); //end add
 
}); //end modalRegisterBtn.on
 
//댓글 조회 클릭 이벤트 처리 
$(".chat").on("click", "li", function (e) {
 
  let rnum = $(this).data("rnum");
  let id = $(this).data("id");
  
  get(rnum)
    .then(reply => { 
			modalInputContent.val(reply.content);
			modal.data("rnum", reply.rnum);
			modal.find("button[id !='modalCloseBtn']").hide();
		    	if(sessionId == ''){
			alert("로그인 후 이용 가능합니다.");
			location.href="/member/login?rurl=/contents/detail/"+contentsno+"&"+param;
			return ;
		}
		
    	if(sessionId == '' || sessionId == null){
			alert("로그인 후 이용 가능합니다.");
			location.href="/member/login?rurl=/contents/detail/"+contentsno+"&"+param;
			return ;
		}
		if(reply.id == sessionId){ 
			modalModBtn.show();
			modalRemoveBtn.show();
 			$(".modal").modal("show");
      	} else{			
			modalModBtn.hide();
			modalRemoveBtn.hide();
			$(".modal").modal("show");
			
			return ;
		}})
 
    });


 //댓글 수정
modalModBtn.on("click", function (e) {

  let reply = { rnum: modal.data("rnum"), content: modalInputContent.val() };
  
  update(reply)
    .then(result => {
      modal.modal("hide");
      showList();
      showPage();
    });
 
});//modify
 
//댓글 삭제
modalRemoveBtn.on("click", function (e) {

  let rnum = modal.data("rnum"); 
  
  remove(rnum)
    .then(result => {
      modal.modal("hide");
      showList();
      showPage();
    });
 
});//remove
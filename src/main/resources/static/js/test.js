$(function() {
	$("#find_id_btn").click(function() {
		alert("확인ghkrdls")
		mname = $("#mname").val();
		email = $("#email").val();
		findId(mname, email)
		

	});


});

function findId(mname, email) {
	
	

	let url = 'http://localhost:8000/member/findId/check?mname=' + mname + "&email=" + email
	fetch(url)
		.then((response) => response.text())
		.then(text => alert(text))
};


$(function() {
	$("#find_passwd_btn").click(function() {
	
		mname = $("#mname").val();
		id = $("#id").val();
		findId(mname, id)


	});


});

function findId(mname, id) {
	

	let url = 'http://localhost:8000/member/findPasswd/check?id=' + id + "&mname=" + mname
	fetch(url)
		.then((response) => response.text())
		.then(text=>alert(text))
		
};


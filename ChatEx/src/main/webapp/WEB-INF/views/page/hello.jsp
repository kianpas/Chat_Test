<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.3.0/sockjs.min.js" integrity="sha512-oY+cYAmW+PMcBG6AM0zHlO//VQwihryCK6e7wZfZnkzl8DITOGaxjLl0OnF7duV4zbKp+19uvkJhTQjqHqLkfA==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js" integrity="sha512-iKDtgDyTHjAitUDdLljGhenhPwrbBfqTKWO1mkhSFH3A7blITC9MhYon6SjnMhp4o0rADGw9yAC6EW4t5a4K3g==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<script>
$(document).ready(function(){
	connectStomp();
	$("#btnSend").on("click", function(e){
		e.preventDefault();
		if(!isStomp && socket.readyState !==1) return;
		let msg = $('input#msg').val();
		console.log("msg = ", msg);
		//$("#display").text(msg);
		$("#chating").append("<p>나 :" + msg + "</p>");	
		if(isStomp){
			socket.send('/upmu/TTT', {}, JSON.stringify({id:123, msg}));
		}else {
			socket.send(msg);
		}
	})
	
})
var socket = null;
var isStomp = false;
function connectStomp(){
	var sock = new SockJS("http://localhost:9090/upmu/stompTest");
	var client = Stomp.over(sock);
	isStomp = true;
	socket = client;

	client.connect({}, function(){
		console.log("connect stomp");
		client.send('/upmu/TTT', {}, "msg: haha");

		//해당 토픽 구독
		client.subscribe("/topic/message", function(event){
			console.log("event>>", event)
			let data = JSON.parse(event.body);
			console.log(data);
			});
		});
};
</script>
</head>
<body>
<input type="text" id="msg"/>
<button id="btnSend">보내기</button>
<p id="display"></p>
<div id="chating"></div>
</body>
</html>
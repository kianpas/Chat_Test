<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script src="http://code.jquery.com/jquery-latest.min.js"></script>

<script src="https://cdnjs.cloudflare.com/ajax/libs/handlebars.js/3.0.0/handlebars.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/list.js/1.1.1/list.min.js"></script>
 <script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.4.0/sockjs.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
      <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.css" rel="stylesheet"
          type="text/css">
    <link href="./resources/css/style.css" rel="stylesheet">

</head>
<body>
<div class="container clearfix">
    <div class="people-list" id="people-list">
        <div class="search">
            <input id="userName" placeholder="search" type="text"/>
            <button onclick="registration()">Enter the chat</button>
            <button onclick="fetchAll()">Refresh</button>
        </div>
        <ul class="list" id="usersList">


        </ul>
    </div>

    <div class="chat">
        <div class="chat-header clearfix">
            <img alt="avatar" height="55px"
                 src=""
                 width="55px"/>

            <div class="chat-about">
                <div class="chat-with" id="selectedUserId"></div>
                <div class="chat-num-messages"></div>
            </div>
            <i class="fa fa-star"></i>
        </div> <!-- end chat-header -->

        <div class="chat-history">
            <ul>

            </ul>

        </div> <!-- end chat-history -->

        <div class="chat-message clearfix">
            <textarea id="message-to-send" name="message-to-send" placeholder="Type your message" rows="3"></textarea>

            <i class="fa fa-file-o"></i> &nbsp;&nbsp;&nbsp;
            <i class="fa fa-file-image-o"></i>

            <button id="sendBtn">Send</button>

        </div> <!-- end chat-message -->

    </div> <!-- end chat -->

</div> <!-- end container -->

<script id="message-template" type="text/x-handlebars-template">
    <li class="clearfix">
        <div class="message-data align-right">
            <span class="message-data-time">{{time}}, Today</span> &nbsp; &nbsp;
            <span class="message-data-name">You</span> <i class="fa fa-circle me"></i>
        </div>
        <div class="message other-message float-right">
            {{messageOutput}}
        </div>
    </li>
</script>

<script id="message-response-template" type="text/x-handlebars-template">
    <li>
        <div class="message-data">
            <span class="message-data-name"><i class="fa fa-circle online"></i> {{userName}}</span>
            <span class="message-data-time">{{time}}, Today</span>
        </div>
        <div class="message my-message">
            {{response}}
        </div>
    </li>
</script>
<script>
const url= 'http://localhost:9090/upmu';
let stompClient;
let selectedUser;

function connectToChat(userName){
	console.log("connection to chat");
	let socket = new SockJS(url + '/chat' + userName)
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame){
		console.log("connected to " + frame)
		stompClient.subscribe("/topic/message/"+userName, function (response){
			let data = JSON.parse(response.body);
			console.log(data);
		})
	})
}

function sendMsg(from, text){
	stompClient.send("/app/chat"+ selectedUser, {}, JSON.stringify({
		fromLogin:from,
		message : text		
	}))
}
function registration(){
	let userName = $("#userName").val();
	$.get(url + "/registration/" + userName, function (response){
		connectToChat(userName);
		}).fail(function (error){
			if(error.status === 400){
				alert("login is already busy")
			}
		})
}
function fetchAll(){

	$.get(url + "/fetchAllUsers/" + userName, function (response){
			let users = response;
			let usersTemplateHTML = "";
			for(let i =0; i<users.length; i++){
				usersTemplateHTML = usersTemplateHTML + '<a href="#" onclick="selectUser(\'' + users[i] + '\')"><li class="clearfix">\n' +
                '                <img src="" width="55px" height="55px" alt="avatar" />\n' +
                '                <div class="about">\n' +
                '                    <div id="userNameAppender_' + users[i] + '" class="name">' + users[i] + '</div>\n' +
                '                    <div class="status">\n' +
                '                        <i class="fa fa-circle offline"></i>\n' +
                '                    </div>\n' +
                '                </div>\n' +
                '            </li></a>';
			}
			$("#usersList").html(usersTemplateHTML);
		});
}

</script>
<!-- <script src="js/custom.js"></script> -->
<!-- <script src="js/chat.js"></script> -->
</body>
</html>
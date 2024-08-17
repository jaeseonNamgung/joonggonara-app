document.addEventListener("DOMContentLoaded", options=> {
    var socket = new SockJS('/ws/chat');
    var stompClient = Stomp.over(socket);

    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/sub/chat/' + 1, function (messageOutput) {
            showMessage(JSON.parse(messageOutput.body));
        });
    });

    document.getElementById('send-button').addEventListener('click', function() {
        var sender = "namgung";  // You can replace this with a dynamic user name
        var message = document.getElementById('message-input').value;
        let date = new Date();
        var createdMessageDate = date.toLocaleDateString();
        stompClient.send("/pub/chat/" + 1, {}, JSON.stringify({
            'sender': sender,
            'message': message,
            'image': null,
            'createdMessageDate': createdMessageDate
        }));
        document.getElementById('message-input').value = '';
    });

    function showMessage(message) {
        var chatBox = document.getElementById('chat-box');
        var messageElement = document.createElement('div');
        messageElement.appendChild(document.createTextNode(message.sender + ": " + message.message + " (" + message.time + ")"));
        chatBox.appendChild(messageElement);
        chatBox.scrollTop = chatBox.scrollHeight;
    }
});

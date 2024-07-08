document.addEventListener('DOMContentLoaded',()=>{
    let sockJS = new SockJS('/ws/chat');
    let stompClient = Stomp.over(sockJS);
    let roomId = document.getElementById('roomId').value
    let profile = document.getElementById('profile').value
    let roomName = document.getElementById('roomName').value
    let senderName = document.getElementById('senderName').value
    getAllChats(roomId, senderName, roomName, profile)
    stompClient.connect({}, function (options) {
        stompClient.subscribe('/sub/' + roomId, greeting => {
            console.log('subscribe: ', greeting.body)
            showChatMessage(JSON.parse(greeting.body), )
        })
    } )

    function showChatMessage (chatResponse, profile){
        let chatBox = document.getElementById('chat-body');
        let pDate = document.createElement('p');
        addChatMessage(chatBox,pDate, true, data[i], senderName, roomName, profile)
    }

    document.getElementById("send-message").addEventListener("click", ev => {
        console.log('ev: ', ev)
        let message = document.getElementById('chat-message').value;
        const chatRequest = {
            message : message,
            senderNickName : senderName,
            recipientNickName: roomName
        }
        console.log(chatRequest)
        stompClient.send("/pub/"+roomId, {}, JSON.stringify(chatRequest))
    })
});

function getAllChats(roomId, senderName, roomName, profile){
    let url = "http://localhost:9090/chat/all/"+roomId

    fetch(url)
        .then(data => data.json())
        .then(data=>{
            let chatBox = document.getElementById('chat-body');
            let pDate = document.createElement('p');
            let flag = false;
            for (let i = 0; i < data.length; i++) {
                flag = addChatMessage(chatBox,pDate, flag, data[i], senderName, roomName, profile)
            }
        })
}

function addChatMessage(chatBox,pDate, flag, data,  senderName, roomName, profile){
    let dateBox = document.createElement("div");
    dateBox.className='date-box';
    let senderBox = document.createElement("div");
    senderBox.className='sender-box';
    let recipientBox = document.createElement("div");
    recipientBox.className='recipient-box';
    let message = document.createElement("div");
    message.className='message';
    let date = document.createElement("div");
    date.className='date';
    let createdMessageDate = new Date(data.createdMessageDate);
    if(!flag){
        flag = true;
        pDate.innerText = dateFormat(createdMessageDate);
        dateBox.appendChild(pDate);
        chatBox.appendChild(dateBox);
    }else{
        let isNowDate = checkDate(pDate.innerText, createdMessageDate)
        if(!isNowDate){
            pDate = document.createElement('p');
            pDate.innerText = dateFormat(createdMessageDate);
            dateBox.appendChild(pDate);
            chatBox.appendChild(dateBox);
        }
    }
    message.innerText = data.message;
    date.innerText = timeFormat(createdMessageDate);
    if(senderName === data.senderNickName){
        senderBox.appendChild(message);
        senderBox.appendChild(date);
        chatBox.appendChild(senderBox);
    }else{
        let img = new Image();
        img.src = profile;
        recipientBox.appendChild(img);
        recipientBox.appendChild(message);
        recipientBox.appendChild(date);
        chatBox.appendChild(recipientBox);
    }
    return flag;
}

function checkDate(pDate, createdMessageDate) {
    let yyyy = parseInt(pDate.substring(0, 4));
    let mm = parseInt(pDate.substring(7, 8));
    let dd = parseInt(pDate.substring(11, 12));
    return yyyy === createdMessageDate.getFullYear() && mm === (createdMessageDate.getMonth()+1)
        && dd === createdMessageDate.getDate();
}

function dateFormat(date){
    const year = date.getFullYear();
    const month = ('0' + (date.getMonth() + 1)).slice(-2);
    const day = ('0' + date.getDate()).slice(-2);
    return year + '년 ' + month  + '월 ' + day + '일';
}

function timeFormat(date){
        const timeStr = date.getHours() <= 12 ?  '오전' : '오후';
        const hours = date.getHours() <= 12 ? date.getHours() : date.getHours()- 12;
        const time = date.getMinutes() < 10 ? "0"+date.getMinutes() : date.getMinutes();

        return timeStr+" "+hours+":"+time;
}

function sendMessage(){

}
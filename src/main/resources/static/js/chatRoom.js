document.addEventListener('DOMContentLoaded',()=>{
    const userName = document.getElementById('user-name').value;
    console.log('userName: ', userName)
    getChatRooms(userName)
});

document.addEventListener('submit', e=>{
    e.preventDefault();
    let nickName = document.querySelector("#user-name").value;
    let text = document.querySelector("#create-chatRoom").value;
    let chatRoomRequest = {
        buyerNickName: nickName,
        sellerNickName: text
    }
    const url = 'http://localhost:9090/chat/room/create'
    const options = {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        mode: 'cors',
        body: JSON.stringify(chatRoomRequest)
    }
    fetch(url, options)
        .then(success => success.json())
        .then(success=>{
            console.log(success)
            getChatRooms(nickName);
            moveChatPage(success.roomId, success.roomName, success.profile, success.nickName)
        }).catch(error=>{
        console.log(error)
    })

})

function getChatRooms(nickName){
    let url = 'http://localhost:9090/chat/room?nickName='+nickName
    fetch(url)
        .then(data=>{
            return data.json()
        }).then(data=>{
            console.log(data)
            let chatBody = document.getElementById("chat-body");
            chatBody.innerHTML = ''


        for (let i = 0; i < data.length; i++) {
            let chatRoomBox = document.createElement("div");
            chatRoomBox.className = 'chatroom-box'

            let profileBox = document.createElement("div");
            profileBox.className = 'profile-box'
            let contentBox = document.createElement("div");
            contentBox.className = 'content-box'
            let titleBox = document.createElement("div");
            titleBox.className = 'title';
            let h1 = document.createElement("h1");
            let content = document.createElement("div");
            content.className = 'content';
            console.log(data)
            content.onclick = ()=> moveChatPage(data[i].roomId, data[i].roomName, data[i].profile, nickName)
            let p1 = document.createElement("p");
            let p2 = document.createElement("p");
            let deleteBox = document.createElement("div");
            deleteBox.className = 'delete-box';
            let button = document.createElement('button');
            button.innerText = '삭제';
            button.onclick = ()=>deleteChatRoom(data[i].roomId, data[i].chatRoomStatus);
            let image = new Image();
            image.src = data[i].profile;
            profileBox.appendChild(image);
            h1.innerText = data[i].roomName;
            titleBox.appendChild(h1);
            p1.innerText = data[i].message;
            p2.innerText = data[i].lastChatTime;

            content.appendChild(p1);
            content.appendChild(p2);
            deleteBox.appendChild(button);
            contentBox.appendChild(titleBox);
            contentBox.appendChild(content);
            contentBox.appendChild(deleteBox);
            chatRoomBox.appendChild(profileBox);
            chatRoomBox.appendChild(contentBox);
            chatBody.appendChild(chatRoomBox);
        }
    })

    function deleteChatRoom(roomId, chatRoomStatus){
        let url = '/chat/room/delete/'+roomId +'?chatRoomStatus='+chatRoomStatus;
        let options = {
            method:'DELETE',
            headers:{
                "Content-Type":"application/json"
            }
        }
        fetch(url, options)
            .then(data => data.json)
            .then(() => {
                alert('삭제되었습니다')
            })
    }
}
function moveChatPage(roomId, roomName, profile, nickName){
    location.href = '/chat/move?roomId='+roomId + "&roomName="+roomName +
        "&profile="+profile + "&senderName="+nickName;
}
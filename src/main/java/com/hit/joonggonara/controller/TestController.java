package com.hit.joonggonara.controller;

import com.hit.joonggonara.common.type.ChatRoomStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TestController {

    @GetMapping("/")
    public String test(){
        return "user";
    }

    @GetMapping("/chatRoom/move")
    public String moveChatRoomPage(@RequestParam("userName") String userName, Model model){
        model.addAttribute("userName", userName);
        return "chatRoomList";
    }
    @GetMapping("/chat/move")
    public String moveChatPage(
            @RequestParam(name = "roomId")Long roomId,
            @RequestParam(name = "roomName")String roomName,
            @RequestParam(name = "profile")String profile,
            @RequestParam(name = "senderName")String senderName,
            Model model

    ){
        model.addAttribute("roomId", roomId);
        model.addAttribute("roomName", roomName);
        model.addAttribute("profile", profile);
        model.addAttribute("senderName", senderName);
        return "chat";
    }

}

package com.hit.joonggonara.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hit.joonggonara.dto.request.community.CommunityRequest;
import com.hit.joonggonara.dto.response.community.CommunityResponse;
import com.hit.joonggonara.service.community.CommunityService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommunityApiController.class)
class CommunityApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommunityService communityService;


    @WithMockUser("USER")
    @Test
    @DisplayName("[API][Create][Community]")
    void createCommunityTest() throws Exception {
        //given
        CommunityRequest communityRequest = createCommunity();
        CommunityResponse communityResponse = createCommunityResponse();
        MockMultipartFile mockFile = new MockMultipartFile(
                "images", "test.jpg", "image/jpeg", "test image content".getBytes());
        MockMultipartFile communityRequestJson = new MockMultipartFile("communityRequest", "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(communityRequest));
        given(communityService.createCommunity(any(), any(), any())).willReturn(communityResponse);
        //when & then
        mvc.perform(multipart(HttpMethod.POST,"/community/create/"+1)
                        .file(mockFile)
                        .file(communityRequestJson)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").value(communityResponse.content()));
        then(communityService).should().createCommunity(any(), any(), any());

    }

    @WithMockUser("USER")
    @Test
    @DisplayName("[API][update][Community]")
    void updateCommunityTest() throws Exception {
        //given
        CommunityRequest communityRequest = createCommunity();
        CommunityResponse communityResponse = createCommunityResponse();
        MockMultipartFile mockFile = new MockMultipartFile(
                "images", "test.jpg", "image/jpeg", "test image content".getBytes());
        MockMultipartFile communityRequestJson = new MockMultipartFile("communityRequest", "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(communityRequest));
        given(communityService.updateCommunity(any(), any(), any())).willReturn(communityResponse);
        //when & then
        mvc.perform(multipart(HttpMethod.PATCH,"/community/update/"+1)
                        .file(mockFile)
                        .file(communityRequestJson)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").value(communityResponse.content()));
        then(communityService).should().updateCommunity(any(), any(), any());

    }

    private static CommunityRequest createCommunity() {
        return CommunityRequest.of("content");
    }

    private static CommunityResponse createCommunityResponse() {
        return CommunityResponse.of(
                1L,
                "content",
                0,
                0,
                null,
                "nickName",
                null,
                null);
    }

}

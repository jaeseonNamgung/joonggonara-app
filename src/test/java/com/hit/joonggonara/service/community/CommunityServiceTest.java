package com.hit.joonggonara.service.community;


import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.ProductErrorCode;
import com.hit.joonggonara.common.util.CustomFileUtil;
import com.hit.joonggonara.common.util.RedisUtil;
import com.hit.joonggonara.dto.file.FileDto;
import com.hit.joonggonara.dto.request.community.CommunityRequest;
import com.hit.joonggonara.dto.response.community.CommunityResponse;
import com.hit.joonggonara.entity.Community;
import com.hit.joonggonara.entity.CommunityImage;
import com.hit.joonggonara.repository.community.CommentRepository;
import com.hit.joonggonara.repository.community.CommunityImageRepository;
import com.hit.joonggonara.repository.community.CommunityRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CommunityServiceTest {

    @Mock
    private CommunityRepository communityRepository;
    @Mock
    private CommunityImageRepository communityImageRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CustomFileUtil fileUtil;
    @Mock
    private RedisUtil redisUtil;
    @InjectMocks
    private CommunityService sut;

    @Test
    @DisplayName("[Service][Create] 커뮤니티가 정상적으로 생성 될 경우 Response를 반환")
    void createCommunityTest() throws Exception {
        //given
        CommunityRequest communityRequest = createCommunityRequest();
        CommunityImage image = createCommunityImage(communityRequest.toEntity());
        FileDto fileDto = createFileDto();
        MultipartFile multipartFile = new MockMultipartFile(
                "fileName", "fileName.png", "image/png", "test data".getBytes());
        given(communityRepository.save(any())).willReturn(communityRequest.toEntity());
        given(fileUtil.uploadImage(any())).willReturn(List.of(fileDto));
        given(communityImageRepository.save(any())).willReturn(image);
        //when
        CommunityResponse expectedResponse = sut.createCommunity(communityRequest, List.of(multipartFile));
        //then
        assertThat(expectedResponse).isNotNull();
        assertThat(expectedResponse.content()).isEqualTo(communityRequest.content());
        assertThat(expectedResponse.photos()).isNotEmpty();

        then(communityRepository).should().save(any());
        then(fileUtil).should().uploadImage(any());
        then(communityImageRepository).should().save(any());

    }
    @Test
    @DisplayName("[Service][Create][Error] dto 리스트가 비어 있을 경우 NOT_UPLOADED_IMAGE 에러 발생 ")
    void createCommunityErrorTest() throws Exception {
        //given
        CommunityRequest communityRequest = createCommunityRequest();
        MultipartFile multipartFile = new MockMultipartFile(
                "fileName", "fileName.png", "image/png", "test data".getBytes());
        given(communityRepository.save(any())).willReturn(communityRequest.toEntity());
        given(fileUtil.uploadImage(any())).willReturn(List.of());

        //when
        CustomException expectedException = assertThrows(CustomException.class,
                () -> sut.createCommunity(communityRequest, List.of(multipartFile)));
        //then
        assertThat(expectedException.getErrorCode()).isEqualTo(ProductErrorCode.NOT_UPLOADED_IMAGE);
        assertThat(expectedException).hasMessage(ProductErrorCode.NOT_UPLOADED_IMAGE.getMessage());

        then(communityRepository).should().save(any());
        then(fileUtil).should().uploadImage(any());

    }

    @Test
    @DisplayName("[Service][Update] 커뮤니티 내용과 이미지 업데이트가 성공적으로 될 경우 Response(이미지에 값 있음)를 반환")
    void communityUpdateTest() throws Exception {
        //given
        Long communityId = 1L;
        CommunityRequest communityRequest = createCommunityRequest();

        MultipartFile multipartFile = new MockMultipartFile(
                "fileName", "fileName.png", "image/png", "test data".getBytes());
        FileDto fileDto = createFileDto();
        CommunityImage image = createCommunityImage(communityRequest.toEntity());
        Community community = Community.builder().content("content").build();
        community.getCommunityImages().add(image);
        given(communityRepository.findById(any())).willReturn(Optional.of(community));
        given(fileUtil.uploadImage(any())).willReturn(List.of(fileDto));
        given(communityImageRepository.save(any())).willReturn(image);
        //when
        CommunityResponse expectedResponse = sut.updateCommunity(communityId, communityRequest, List.of(multipartFile));
        //then
        assertThat(expectedResponse).isNotNull();
        assertThat(expectedResponse.content()).isEqualTo(communityRequest.content());
        assertThat(expectedResponse.photos()).isNotEmpty();
        then(communityRepository).should().findById(any());
        then(fileUtil).should().uploadImage(any());
        then(communityImageRepository).should().deleteById(any());
        then(communityImageRepository).should().save(any());
    }

    @Test
    @DisplayName("[Service][Update] 커뮤니티 내용만 성공적으로 될 경우 Response(이미지에 값 없음)를 반환")
    void communityUpdateTest2() throws Exception {
        //given
        Long communityId = 1L;
        CommunityRequest communityRequest = createCommunityRequest();
        Community community = Community.builder().content("content").build();
        given(communityRepository.findById(any())).willReturn(Optional.of(community));
        //when
        CommunityResponse expectedResponse = sut.updateCommunity(communityId, communityRequest, List.of());
        //then
        assertThat(expectedResponse).isNotNull();
        assertThat(expectedResponse.content()).isEqualTo(communityRequest.content());
        assertThat(expectedResponse.photos()).isEmpty();
        then(communityRepository).should().findById(any());

    }

    @Test
    @DisplayName("[Service][좋아요] 좋아요를 증가할 경우 좋아요 1을 증가")
    void updateLikeCountCountTest() throws Exception {
        //given
        Long communityId = 1L;
        Community community = Community.builder().content("content").build();
        given(communityRepository.findById(any())).willReturn(Optional.of(community));
        //when
        sut.updateLikeCount(communityId, 1);
        //then
        then(communityRepository).should().findById(any());
    }



    private FileDto createFileDto() {
        return FileDto.of("filePath", "fileName");
    }

    private CommunityImage createCommunityImage(Community community){
        return CommunityImage.builder().filePath("filePath").fileName("fileName").community(community).build();
    }

    private CommunityRequest createCommunityRequest() {
        return CommunityRequest.of("content");
    }

}

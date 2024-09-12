package com.hit.joonggonara.service.community;


import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.CommunityErrorCode;
import com.hit.joonggonara.common.error.errorCode.ProductErrorCode;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import com.hit.joonggonara.common.type.LoginType;
import com.hit.joonggonara.common.type.Role;
import com.hit.joonggonara.common.util.CustomFileUtil;
import com.hit.joonggonara.dto.file.FileDto;
import com.hit.joonggonara.dto.request.community.CommentRequest;
import com.hit.joonggonara.dto.request.community.CommunityRequest;
import com.hit.joonggonara.dto.response.community.CommentResponse;
import com.hit.joonggonara.dto.response.community.CommunityResponse;
import com.hit.joonggonara.entity.Comment;
import com.hit.joonggonara.entity.Community;
import com.hit.joonggonara.entity.CommunityImage;
import com.hit.joonggonara.entity.Member;
import com.hit.joonggonara.repository.community.CommentRepository;
import com.hit.joonggonara.repository.community.CommunityImageRepository;
import com.hit.joonggonara.repository.community.CommunityRepository;
import com.hit.joonggonara.repository.community.LikeRepository;
import com.hit.joonggonara.repository.login.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CommunityServiceTest {

    @Mock
    private CommunityRepository communityRepository;
    @Mock
    private CommunityImageRepository communityImageRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private CustomFileUtil fileUtil;

    @InjectMocks
    private CommunityService sut;

    @Test
    @DisplayName("[Service][Create] 커뮤니티가 정상적으로 생성 될 경우 Response를 반환")
    void createCommunityTest() throws Exception {
        //given
        Member member = createMember();
        Long memberId = 1L;
        Community community = createCommunity();
        ReflectionTestUtils.setField(community, "createdDate", LocalDateTime.now());
        CommunityRequest communityRequest = createCommunityRequest();
        CommunityImage image = createCommunityImage(communityRequest.toEntity(member));
        FileDto fileDto = createFileDto();
        MultipartFile multipartFile = new MockMultipartFile(
                "fileName", "fileName.png", "image/png", "test data".getBytes());

        given(memberRepository.findById(any())).willReturn(Optional.of(member));
        given(communityRepository.save(any())).willReturn(community);
        given(fileUtil.uploadImage(any())).willReturn(List.of(fileDto));
        given(communityImageRepository.save(any())).willReturn(image);
        //when
        CommunityResponse expectedResponse = sut.createCommunity(memberId, communityRequest, List.of(multipartFile));
        //then
        assertThat(expectedResponse).isNotNull();
        assertThat(expectedResponse.content()).isEqualTo(communityRequest.content());
        assertThat(expectedResponse.photos()).isNotEmpty();

        then(memberRepository).should().findById(any());
        then(communityRepository).should().save(any());
        then(fileUtil).should().uploadImage(any());
        then(communityImageRepository).should().save(any());

    }
    @Test
    @DisplayName("[Service][Create][Error] 회원이 존재하지 않을 경우 NOT_EXIST_USER 에러 발생 ")
    void createCommunityErrorTest() throws Exception {
        //given

        Long memberId = 1L;
        CommunityRequest communityRequest = createCommunityRequest();
        MultipartFile multipartFile = new MockMultipartFile(
                "fileName", "fileName.png", "image/png", "test data".getBytes());

        given(memberRepository.findById(any())).willReturn(Optional.empty());

        //when
        CustomException expectedException = assertThrows(CustomException.class,
                () -> sut.createCommunity(memberId, communityRequest, List.of(multipartFile)));
        //then
        assertThat(expectedException.getErrorCode()).isEqualTo(UserErrorCode.NOT_EXIST_USER);
        assertThat(expectedException).hasMessage(UserErrorCode.NOT_EXIST_USER.getMessage());

        then(memberRepository).should().findById(any());

    }
    @Test
    @DisplayName("[Service][Create][Error] dto 리스트가 비어 있을 경우 NOT_UPLOADED_IMAGE 에러 발생 ")
    void createCommunityErrorTest2() throws Exception {
        //given
        Member member = createMember();
        Long memberId = 1L;
        CommunityRequest communityRequest = createCommunityRequest();
        MultipartFile multipartFile = new MockMultipartFile(
                "fileName", "fileName.png", "image/png", "test data".getBytes());
        given(memberRepository.findById(any())).willReturn(Optional.of(member));
        given(communityRepository.save(any())).willReturn(communityRequest.toEntity(member));
        given(fileUtil.uploadImage(any())).willReturn(List.of());

        //when
        CustomException expectedException = assertThrows(CustomException.class,
                () -> sut.createCommunity(memberId, communityRequest, List.of(multipartFile)));
        //then
        assertThat(expectedException.getErrorCode()).isEqualTo(ProductErrorCode.NOT_UPLOADED_IMAGE);
        assertThat(expectedException).hasMessage(ProductErrorCode.NOT_UPLOADED_IMAGE.getMessage());

        then(memberRepository).should().findById(any());
        then(communityRepository).should().save(any());
        then(fileUtil).should().uploadImage(any());

    }

    @Test
    @DisplayName("[Community][FindCommunityAll] 전체 게시글 조회")
    void getAllCommunitiesTest() throws Exception {
        //given
        PageRequest pageRequest = PageRequest.of(0, 5, Sort.by("createdDate").descending());
        List<Community> communities = new ArrayList<>();
        for (int i = 1; i <= 10 ; i++) {
            Community community = Community.builder().content("content"+i).member(createMember()).build();
            ReflectionTestUtils.setField(community, "createdDate", LocalDateTime.now());
            communities.add(community);
        }

        given(communityRepository.findCommunityAll(any())).willReturn(new PageImpl<>(communities));
        //when
        Page<CommunityResponse> expectedPage = sut.getAllCommunities(pageRequest);
        //then

        assertThat(expectedPage.isFirst()).isTrue();
        assertThat(expectedPage.getTotalPages()).isEqualTo(1);
        assertThat(expectedPage.getNumber()).isEqualTo(0);
        assertThat(expectedPage.getSize()).isEqualTo(10);
        then(communityRepository).should().findCommunityAll(any());

    }

    @Test
    @DisplayName("[Community][단건 조회] 조회 성공 시 Response를 반환")
    void getCommunityTest() throws Exception {
        //given
        Long communityId = 1L;
        CommunityRequest communityRequest = createCommunityRequest();
        Community community = createCommunity();
        ReflectionTestUtils.setField(community, "createdDate", LocalDateTime.now());

        given(communityRepository.findCommunityById(any())).willReturn(Optional.of(community));
        //when
        CommunityResponse expectedCommunity = sut.getCommunity(communityId);
        //then
        assertThat(expectedCommunity).isNotNull();
        assertThat(expectedCommunity.content()).isEqualTo(communityRequest.content());
        then(communityRepository).should().findCommunityById(any());
    }
    @Test
    @DisplayName("[Community][단건 조회] 게시글이 존재하지 않으면 COMMUNITY_NOT_FOUND 에러 발생")
    void getCommunityErrorTest() throws Exception {
        //given
        Long communityId = 1L;
        given(communityRepository.findCommunityById(any())).willReturn(Optional.empty());
        //when
        CustomException expectedException = assertThrows(CustomException.class,
                () -> sut.getCommunity(communityId));
        //then
        assertThat(expectedException.getErrorCode()).isEqualTo(CommunityErrorCode.COMMUNITY_NOT_FOUND);
        assertThat(expectedException).hasMessage(CommunityErrorCode.COMMUNITY_NOT_FOUND.getMessage());
        then(communityRepository).should().findCommunityById(any());
    }


    @Test
    @DisplayName("[Service][Update] 커뮤니티 내용과 이미지 업데이트가 성공적으로 될 경우 Response(이미지에 값 있음)를 반환")
    void communityUpdateTest() throws Exception {
        //given
        Long communityId = 1L;
        Community community = createCommunity();
        ReflectionTestUtils.setField(community, "createdDate", LocalDateTime.now());
        CommunityRequest communityRequest = createCommunityRequest();
        Member member = createMember();
        MultipartFile multipartFile = new MockMultipartFile(
                "fileName", "fileName.png", "image/png", "test data".getBytes());
        FileDto fileDto = createFileDto();
        CommunityImage image = createCommunityImage(communityRequest.toEntity(member));
        community.getCommunityImages().add(image);
        given(communityRepository.findCommunityById(any())).willReturn(Optional.of(community));
        given(fileUtil.uploadImage(any())).willReturn(List.of(fileDto));
        given(communityImageRepository.save(any())).willReturn(image);
        //when
        CommunityResponse expectedResponse = sut.updateCommunity(communityId, communityRequest, List.of(multipartFile));
        //then
        assertThat(expectedResponse).isNotNull();
        assertThat(expectedResponse.content()).isEqualTo(communityRequest.content());
        assertThat(expectedResponse.photos()).isNotEmpty();
        then(communityRepository).should().findCommunityById(any());
        then(fileUtil).should().uploadImage(any());
        then(communityImageRepository).should().deleteById(any());
        then(communityImageRepository).should().save(any());
    }

    @Test
    @DisplayName("[Service][Update] 커뮤니티 내용만 성공적으로 될 경우 Response(이미지에 값 없음)를 반환")
    void communityUpdateTest2() throws Exception {
        //given
        Long communityId = 1L;
        Community community = createCommunity();
        ReflectionTestUtils.setField(community, "createdDate", LocalDateTime.now());
        CommunityRequest communityRequest = createCommunityRequest();
        given(communityRepository.findCommunityById(any())).willReturn(Optional.of(community));
        //when
        CommunityResponse expectedResponse = sut.updateCommunity(communityId, communityRequest, List.of());
        //then
        assertThat(expectedResponse).isNotNull();
        assertThat(expectedResponse.content()).isEqualTo(communityRequest.content());
        assertThat(expectedResponse.photos()).isNull();
        then(communityRepository).should().findCommunityById(any());

    }

    @Test
    @DisplayName("[Service][좋아요] 좋아요를 증가할 경우 좋아요 1을 증가")
    void increaseLikeTest() throws Exception {
        //given
        Long communityId = 1L;
        Long memberId = 1L;
        Member member = createMember();
        Community community = Community.builder().content("content").build();

        given(memberRepository.findById(any())).willReturn(Optional.of(member));
        given(communityRepository.findById(any())).willReturn(Optional.of(community));

        //when
        boolean isTrue = sut.increaseLike(communityId, memberId);
        //then
        assertThat(isTrue).isTrue();

        then(memberRepository).should().findById(any());
        then(communityRepository).should().findById(any());
        then(likeRepository).should().save(any());
    }
    @Test
    @DisplayName("[Service][좋아요] 좋아요를 취소할 경우 좋아요를 삭제 한 후 true를 리턴")
    void decreaseLikeTest() throws Exception {
        //given
        Long communityId = 1L;
        Long memberId = 1L;
        //when
        boolean isTrue = sut.decreaseLike(communityId, memberId);
        //then
        assertThat(isTrue).isTrue();

        then(likeRepository).should().deleteByMemberIdAndCommunityId(any(), any());
    }

    @Test
    @DisplayName("[Comment][Create] 부모 댓글이 저장 된 후 Response를 반환")
    void createParentCommentTest() throws Exception {
        //given
        Long communityId = 1L;
        Long memberId = 1L;
        Community community = createCommunity();
        ReflectionTestUtils.setField(community, "createdDate", LocalDateTime.now());
        CommunityRequest communityRequest = createCommunityRequest();
        CommentRequest commentRequest = createCommentRequest();
        Member member = createMember();
        Comment comment = createComment(communityRequest.toEntity(member));
        ReflectionTestUtils.setField(comment, "createdDate", LocalDateTime.now());
        given(communityRepository.findById(any())).willReturn(Optional.of(community));
        given(memberRepository.findById(any())).willReturn(Optional.of(member));
        given(commentRepository.save(any())).willReturn(comment);
        //when
        CommentResponse expectedResponse = sut.createComment(communityId, memberId, commentRequest);
        //then
        assertThat(expectedResponse).isNotNull();
        assertThat(expectedResponse.content()).isEqualTo(commentRequest.content());

        then(communityRepository).should().findById(any());
        then(memberRepository).should().findById(any());
        then(commentRepository).should().save(any());

    }
    @Test
    @DisplayName("[Comment][Create] 자식 댓글이 저장 된 후 Response를 반환")
    void createChildCommentTest() throws Exception {
        //given
        Long communityId = 1L;
        Long memberId = 1L;
        Community community = createCommunity();
        CommunityRequest communityRequest = createCommunityRequest();
        CommentRequest commentRequest = CommentRequest.of(1L, "child comment content");
        Member member = createMember();
        Comment parentComment = createComment(communityRequest.toEntity(member));
        // comment에 createdDate 설정

        given(communityRepository.findById(any())).willReturn(Optional.of(community));
        given(memberRepository.findById(any())).willReturn(Optional.of(member));
        given(commentRepository.findParentCommentById(any())).willReturn(Optional.of(parentComment));

        //when
        CommentResponse expectedResponse = sut.createComment(communityId, memberId, commentRequest);
        //then
        assertThat(expectedResponse).isNotNull();
        assertThat(expectedResponse.content()).isEqualTo(parentComment.getContent());
        assertThat(expectedResponse.childComments()).isNotEmpty();

        then(communityRepository).should().findById(any());
        then(memberRepository).should().findById(any());
        then(commentRepository).should().save(any());
        then(commentRepository).should(times(2)).findParentCommentById(any());


    }

    @Test
    @DisplayName("[Comment][Create][Error] 게시글이 존재하지 않을 경우 COMMUNITY_NOT_FOUND 반환 ")
    void createCommentErrorTest() throws Exception {
        //given
        Long communityId = 1L;
        Long memberId = 1L;
        CommentRequest commentRequest = createCommentRequest();

        given(communityRepository.findById(any())).willReturn(Optional.empty());

        //when
        CustomException expectedException = assertThrows(CustomException.class,
                () -> sut.createComment(communityId,memberId, commentRequest));
        //then
        assertThat(expectedException.getErrorCode()).isEqualTo(CommunityErrorCode.COMMUNITY_NOT_FOUND);
        assertThat(expectedException).hasMessage(CommunityErrorCode.COMMUNITY_NOT_FOUND.getMessage());

        then(communityRepository).should().findById(any());


    }

    @Test
    @DisplayName("[Comment][Create][Error] 회원이 존재하지 않을 경우 USER_NOT_FOUND 에러 발생 ")
    void createCommentErrorTest2() throws Exception {
        //given
        Long communityId = 1L;
        Long memberId = 1L;
        CommentRequest commentRequest = createCommentRequest();
        CommunityRequest communityRequest = createCommunityRequest();
        Member member = createMember();
        given(communityRepository.findById(any())).willReturn(Optional.of(communityRequest.toEntity(member)));
        given(communityRepository.findById(any())).willReturn(Optional.empty());

        //when
        CustomException expectedException = assertThrows(CustomException.class,
                () -> sut.createComment(communityId,memberId, commentRequest));
        //then
        assertThat(expectedException.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
        assertThat(expectedException).hasMessage(UserErrorCode.USER_NOT_FOUND.getMessage());

        then(communityRepository).should().findById(any());
        then(communityRepository).should().findById(any());


    }

    private CommentRequest createCommentRequest() {
        return CommentRequest.of(null, "comment");
    }

    private Comment createComment(Community community) {
        return Comment.builder().content("comment").community(community).member(createMember()).build();
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
    private Community createCommunity() {
        return Community.builder().content("content").member(createMember()).build();
    }
    private Member createMember() {
        return Member.builder()
                .userId("userId")
                .email("test@email.com")
                .name("hong")
                .nickName("nickName")
                .password("Abc1234*")
                .phoneNumber("+8612345678")
                .role(Role.ROLE_USER)
                .loginType(LoginType.GENERAL)
                .build();
    }

}

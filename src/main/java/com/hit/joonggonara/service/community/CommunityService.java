package com.hit.joonggonara.service.community;

import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.CommunityErrorCode;
import com.hit.joonggonara.common.error.errorCode.ProductErrorCode;
import com.hit.joonggonara.common.error.errorCode.UserErrorCode;
import com.hit.joonggonara.common.util.CustomFileUtil;
import com.hit.joonggonara.dto.file.FileDto;
import com.hit.joonggonara.dto.request.community.CommentRequest;
import com.hit.joonggonara.dto.request.community.CommunityRequest;
import com.hit.joonggonara.dto.response.community.CommentResponse;
import com.hit.joonggonara.dto.response.community.CommunityResponse;
import com.hit.joonggonara.entity.*;
import com.hit.joonggonara.repository.community.CommentRepository;
import com.hit.joonggonara.repository.community.CommunityImageRepository;
import com.hit.joonggonara.repository.community.CommunityRepository;
import com.hit.joonggonara.repository.community.LikeRepository;
import com.hit.joonggonara.repository.login.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityImageRepository communityImageRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final LikeRepository likeRepository;
    private final CustomFileUtil fileUtil;


    // 커뮤니티 생성
    @Transactional
    public CommunityResponse createCommunity(Long memberId, CommunityRequest communityRequest, List<MultipartFile> images) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_EXIST_USER));
        Community savedCommunity = communityRepository.save(communityRequest.toEntity(member));
        if(images == null || images.isEmpty()) {
            return CommunityResponse.fromResponse(savedCommunity);
        }
        List<FileDto> fileDtoList = fileUtil.uploadImage(images);

        return uploadCommunityImages(fileDtoList, savedCommunity, member.getNickName());
    }

    // 커뮤니티 전체 조회
    public Page<CommunityResponse> getAllCommunities(Pageable pageable) {
        Page<Community> communityPage = communityRepository.findCommunityAll(pageable);
        return CommunityResponse.fromResponse(communityPage);
    }

    // 커뮤니티 단건 조회
    public CommunityResponse getCommunity(Long communityId) {
        Community community = communityRepository.findCommunityById(communityId)
                .orElseThrow(() -> new CustomException(CommunityErrorCode.COMMUNITY_NOT_FOUND));
        return CommunityResponse.fromResponse(community);
    }

    // 커뮤니티 수정
    @Transactional
    public CommunityResponse updateCommunity(Long communityId, CommunityRequest communityRequest, List<MultipartFile> images) {
        Community community = communityRepository.findCommunityById(communityId)
                .orElseThrow(() -> new CustomException(CommunityErrorCode.COMMUNITY_NOT_FOUND));
        community.update(communityRequest);
        // 수정된 이미지가 있을 경우 기존 이미지를 삭제한 후 다시 이미지를 업로드
        if(!images.isEmpty()){
            community.getCommunityImages().forEach(image-> communityImageRepository.deleteById(image.getId()));
            List<FileDto> fileDtoList = fileUtil.uploadImage(images);
            return uploadCommunityImages(fileDtoList, community, community.getMember().getNickName());
        }
        return CommunityResponse.fromResponse(community);
    }

    // Community 삭제
    @Transactional
    public void deleteCommunity(Long communityId) {
        communityRepository.deleteById(communityId);
    }

    // 좋아요 선택
    @Transactional
    public boolean increaseLike(Long memberId, Long communityId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_EXIST_USER));
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CustomException(CommunityErrorCode.COMMUNITY_NOT_FOUND));
        likeRepository.save(Likes.builder().member(member).community(community).build());
        return true;
    }

    // 좋아요 취소
    @Transactional
    public boolean decreaseLike(Long memberId, Long communityId) {
        likeRepository.deleteByMemberIdAndCommunityId(memberId, communityId);
        return true;
    }

    // 좋아요 확인
    public boolean existLike(Long memberId, Long communityId) {
       return likeRepository.existsByMemberIdAndCommunityId(memberId, communityId);
    }

    // 댓글 생성
    @Transactional
    public CommentResponse createComment(Long communityId, Long memberId, CommentRequest commentRequest){

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CustomException(CommunityErrorCode.COMMUNITY_NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        Comment parentComment = null;  // 부모 댓글
        // 대댓글을 작성할 경우, 부모 댓글 확인
        if (commentRequest.commentId() != null) {
            parentComment = commentRepository.findParentCommentById(commentRequest.commentId())
                    .orElseThrow(() -> new CustomException(CommunityErrorCode.COMMENT_NOT_FOUND));
        }
        Comment comment = commentRequest.toEntity(community, member);

        if(parentComment != null){
            comment.addParentComment(parentComment);
           commentRepository.save(comment);
           return CommentResponse.fromResponse(commentRepository.findParentCommentById(parentComment.getId())
                    .orElseThrow(()->new CustomException(CommunityErrorCode.COMMENT_NOT_FOUND)));
        }
        Comment savedComment = commentRepository.save(comment);
        return CommentResponse.fromResponse(savedComment);
    }

    public List<CommentResponse> getAllComments(Long communityId) {
        return CommentResponse.fromResponse(commentRepository.findParentCommentAllByCommunityId(communityId));
    }

    // 댓글 삭제
    @Transactional
    public CommentResponse deleteComment(Long commentId){
        Comment comment = commentRepository.findParentCommentById(commentId)
                .orElseThrow(() -> new CustomException(CommunityErrorCode.COMMENT_NOT_FOUND));
        comment.deleteComment();
        return CommentResponse.fromResponse(comment);
    }


    private CommunityResponse uploadCommunityImages(List<FileDto> fileDtoList, Community savedCommunity, String nickName) {
        List<CommunityImage> communityImageList = new ArrayList<>();
        if(!fileDtoList.isEmpty()){
            fileDtoList.forEach(fileDto -> {
                CommunityImage communityImage = CommunityImage.builder()
                        .filePath(fileDto.filePath())
                        .fileName(fileDto.fileName())
                        .community(savedCommunity)
                        .build();
                CommunityImage savedImage = communityImageRepository.save(communityImage);
                communityImageList.add(savedImage);
            });
            return CommunityResponse.fromResponse(savedCommunity, communityImageList);
        }else{
            throw new CustomException(ProductErrorCode.NOT_UPLOADED_IMAGE);
        }
    }

    public Page<CommunityResponse> getSearchCommunityByKeyword(String keyword, Pageable pageable) {
        return CommunityResponse.fromResponse(communityRepository.findCommunitiesByKeyword(keyword, pageable));
    }
}

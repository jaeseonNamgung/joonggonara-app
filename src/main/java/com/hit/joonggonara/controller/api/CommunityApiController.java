package com.hit.joonggonara.controller.api;

import com.hit.joonggonara.dto.request.community.CommentRequest;
import com.hit.joonggonara.dto.request.community.CommunityRequest;
import com.hit.joonggonara.dto.response.community.CommentResponse;
import com.hit.joonggonara.dto.response.community.CommunityResponse;
import com.hit.joonggonara.service.community.CommunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CommunityApiController {

    private final CommunityService communityService;


    // 커뮤니티 게시글 생성
    @PostMapping(value = "/community/create/{memberId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommunityResponse> createCommunity(
            @PathVariable(name = "memberId") Long memberId,
            @RequestPart(name = "communityRequest") @Valid CommunityRequest communityRequest,
            @RequestPart(name = "images", required = false) List<MultipartFile> images) {
        return ResponseEntity.ok(communityService.createCommunity(memberId, communityRequest, images));
    }

    // 커뮤니티 게시글 수정
    @PatchMapping(value = "/community/update/{communityId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommunityResponse> updateCommunity(
            @PathVariable(name = "communityId") Long communityId,
            @RequestPart(name = "communityRequest") @Valid CommunityRequest communityRequest,
            @RequestPart(name = "images") List<MultipartFile> images) {
        return ResponseEntity.ok(communityService.updateCommunity(communityId, communityRequest, images));
    }

    // 커뮤니티 게시글 삭제
    @DeleteMapping("/community/delete/{communityId}")
    public ResponseEntity<Boolean> deleteCommunity(@PathVariable(name = "communityId") Long communityId) {
        communityService.deleteCommunity(communityId);
        return ResponseEntity.ok(true);
    }

    // 커뮤니티 전체 조회
    @GetMapping("/community/all")
    public ResponseEntity<Page<CommunityResponse>> getAllCommunities(Pageable pageable) {
        return ResponseEntity.ok(communityService.getAllCommunities(pageable));
    }

    // 커뮤니티 단건 조회
    @GetMapping("/community/{communityId}")
    public ResponseEntity<CommunityResponse> getCommunity(@PathVariable(name = "communityId") Long communityId) {
        return ResponseEntity.ok(communityService.getCommunity(communityId));
    }

    // 좋아요
    @PutMapping("/community/like/increase/{memberId}/{communityId}")
    public ResponseEntity<Boolean> increaseLike(
            @PathVariable(name = "memberId") Long memberId,
            @PathVariable(name = "communityId") Long communityId) {
        return ResponseEntity.ok(communityService.increaseLike(memberId,communityId));
    }
    // 좋아요 취소
    @PutMapping("/community/like/decrease/{memberId}/{communityId}")
    public ResponseEntity<Boolean> decreaseLike(
            @PathVariable(name = "memberId") Long memberId,
            @PathVariable(name = "communityId") Long communityId
    ) {
        return ResponseEntity.ok( communityService.decreaseLike(memberId, communityId));
    }

    @GetMapping("/community/like/exist/{memberId}/{communityId}")
    public ResponseEntity<Boolean> existLike(
            @PathVariable(name = "memberId") Long memberId,
            @PathVariable(name = "communityId") Long communityId
    ){
        return ResponseEntity.ok(communityService.existLike(memberId, communityId));
    }

    @PostMapping("/community/comment/create/{communityId}/{memberId}")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable(name = "communityId") Long communityId,
            @PathVariable(name = "memberId") Long memberId,
            @RequestBody @Valid CommentRequest commentRequest){
        return ResponseEntity.ok(communityService.createComment(communityId,memberId,commentRequest));
    }

    @DeleteMapping("/community/comment/delete/{commentId}")
    public ResponseEntity<CommentResponse> deleteComment(
            @PathVariable(name = "commentId") Long commentId){
        return ResponseEntity.ok(communityService.deleteComment(commentId));
    }
    @GetMapping("/community/comment/all/{communityId}")
    public ResponseEntity<List<CommentResponse>> getAllComments(
            @PathVariable(name = "communityId") Long communityId
    ){
        return ResponseEntity.ok(communityService.getAllComments(communityId));
    }

    @GetMapping("/community/search")
    public ResponseEntity<Page<CommunityResponse>> searchProducts(String keyword, Pageable pageable) {
        return ResponseEntity.ok(communityService.getSearchCommunityByKeyword(keyword, pageable));
    }




}

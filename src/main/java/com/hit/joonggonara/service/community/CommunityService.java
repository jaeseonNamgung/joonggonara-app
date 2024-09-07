package com.hit.joonggonara.service.community;

import com.hit.joonggonara.common.error.CustomException;
import com.hit.joonggonara.common.error.errorCode.CommunityErrorCode;
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
import lombok.RequiredArgsConstructor;
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
    private final CustomFileUtil fileUtil;
    private final RedisUtil redisUtil;


    // 커뮤니티 생성
    @Transactional
    public CommunityResponse createCommunity(CommunityRequest communityRequest, List<MultipartFile> images) {

        Community savedCommunity = communityRepository.save(communityRequest.toEntity());
        List<FileDto> fileDtoList = fileUtil.uploadImage(images);

        return uploadCommunityImages(fileDtoList, savedCommunity);
    }

    // 커뮤니티 수정
    @Transactional
    public CommunityResponse updateCommunity(Long communityId, CommunityRequest communityRequest, List<MultipartFile> images) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CustomException(CommunityErrorCode.COMMUNITY_NOT_FOUND));
        community.update(communityRequest);
        System.out.println(images);
        // 수정된 이미지가 있을 경우 기존 이미지를 삭제한 후 다시 이미지를 업로드
        if(!images.isEmpty()){
            community.getCommunityImages().forEach(image-> communityImageRepository.deleteById(image.getId()));
            List<FileDto> fileDtoList = fileUtil.uploadImage(images);
            return uploadCommunityImages(fileDtoList, community);
        }
        return CommunityResponse.fromResponse(community);
    }

    // Community 삭제
    public void deleteCommunity(Long communityId) {
        communityRepository.deleteById(communityId);
    }

    @Transactional
    public void updateLikeCount(Long communityId, int count) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CustomException(CommunityErrorCode.COMMUNITY_NOT_FOUND));
        if (count < 0){
            if(community.getLikeCount() > 0){
                community.updateLikeCount(count);
            }
        }else if(count > 0){
            community.updateLikeCount(count);
        }
    }

    @Transactional
    public void 



    private CommunityResponse uploadCommunityImages(List<FileDto> fileDtoList, Community savedCommunity) {
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

}

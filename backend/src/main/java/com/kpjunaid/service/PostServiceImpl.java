package com.kpjunaid.service;

import com.kpjunaid.entity.Comment;
import com.kpjunaid.entity.Post;
import com.kpjunaid.entity.User;
import com.kpjunaid.exception.*;
import com.kpjunaid.dto.TagDto;
import com.kpjunaid.entity.Tag;
import com.kpjunaid.enumeration.HatefulType;
import com.kpjunaid.enumeration.NotificationType;
import com.kpjunaid.repository.PostRepository;
import com.kpjunaid.repository.UserRepository;
import com.kpjunaid.response.PostResponse;
import com.kpjunaid.util.FileNamingUtil;
import com.kpjunaid.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserService userService;
    private final CommentService commentService;
    private final TagService tagService;
    private final NotificationService notificationService;
    private final Environment environment;
    private final FileNamingUtil fileNamingUtil;
    private final FileUploadUtil fileUploadUtil;

    private final UserRepository userRepository;

    @Override
    public Post getPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
    }

    @Override
    public PostResponse getPostResponseById(Long postId) {
        User authUser = userService.getAuthenticatedUser();
        Post foundPost = getPostById(postId);
        return PostResponse.builder()
                .post(foundPost)
                .likedByAuthUser(foundPost.getLikeList().contains(authUser))
                .build();
    }

    /*
    @Override
    public List<PostResponse> getTimelinePostsPaginate(Integer page, Integer size) {
        User authUser = userService.getAuthenticatedUser();
        List<User> followingList = authUser.getFollowingUsers();
        followingList.add(authUser);
        List<Long> followingListIds = followingList.stream().map(User::getId).toList();
        return postRepository.findPostsByAuthorIdIn(
                followingListIds,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateCreated")))
                .stream().map(this::postToPostResponse).collect(Collectors.toList());
    }*/

    @Override
    public List<PostResponse> getTimelinePostsPaginate(Integer page, Integer size) {
        User authUser = userService.getAuthenticatedUser();
        List<User> followingList = authUser.getFollowingUsers();
        followingList.add(authUser);
        List<Long> followingListIds = followingList.stream().map(User::getId).toList();

        System.out.println("followingListIds = " + followingListIds.size());
        
        List<Post> allPosts = postRepository.findPostsByAuthorIdIn(
                followingListIds,
                PageRequest.of(page, 2600, Sort.by(Sort.Direction.DESC, "dateCreated")))
                .stream().collect(Collectors.toList());


        System.out.println("allPosts = " + allPosts.size());

        // Séparer les posts en deux listes en fonction de hatefultype
        List<Post> postsNotHateful = allPosts.stream()
                .filter(post -> post.getHatefulType() == HatefulType.NOT)
                .collect(Collectors.toList());
        
        List<Post> postsHateful = allPosts.stream()
                .filter(post -> post.getHatefulType() != HatefulType.NOT)
                .collect(Collectors.toList());

        // Mélanger les deux listes de manière aléatoire
        Collections.shuffle(postsNotHateful);
        Collections.shuffle(postsHateful);


        System.out.println("postsNotHateful = " + postsNotHateful.size());
        System.out.println("postsHateful = " + postsHateful.size());

        // Calculer le nombre d'éléments à prendre pour chaque liste
        int postsNotHatefulCount = (int) (size * 0.65);
        int postsHatefulCount = size - postsNotHatefulCount;

        // Fusionner les deux listes en respectant les proportions de 65% NOT et 35% hateful
        List<Post> finalPosts = new ArrayList<>();
        finalPosts.addAll(postsNotHateful.subList(0, Math.min(postsNotHatefulCount, postsNotHateful.size())));
        finalPosts.addAll(postsHateful.subList(0, Math.min(postsHatefulCount, postsHateful.size())));

        // Mélanger à nouveau la liste finale pour que l'ordre soit complètement aléatoire
        Collections.shuffle(finalPosts);
       
        System.out.println("followingListIds = " + followingListIds.size());

        System.out.println("allPosts = " + allPosts.size());

        System.out.println("postsNotHateful = " + postsNotHateful.size());
        System.out.println("postsHateful = " + postsHateful.size());

        // Convertir en réponse
    return finalPosts.stream().map(this::postToPostResponse).collect(Collectors.toList());
}


    @Override
    public List<PostResponse> getPostSharesPaginate(Post sharedPost, Integer page, Integer size) {
        return postRepository.findPostsBySharedPost(
                sharedPost,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateCreated")))
                .stream().map(this::postToPostResponse).collect(Collectors.toList());
    }

    @Override
    public List<PostResponse> getPostByTagPaginate(Tag tag, Integer page, Integer size) {
        return postRepository.findPostsByPostTags(
                tag,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateCreated")))
                .stream().map(this::postToPostResponse).collect(Collectors.toList());
    }

    @Override
    public List<PostResponse> getPostsByUserPaginate(User author, Integer page, Integer size) {
        return postRepository.findPostsByAuthor(
                author,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateCreated")))
                .stream().map(this::postToPostResponse).collect(Collectors.toList());
    }

    @Override
    public Post createNewPost(String content, MultipartFile postPhoto, List<TagDto> postTags) {
        User authUser = userService.getAuthenticatedUser();
        Post newPost = new Post();
        newPost.setContent(content);
        newPost.setAuthor(authUser);
        newPost.setLikeCount(0);
        newPost.setShareCount(0);
        newPost.setCommentCount(0);
        newPost.setIsTypeShare(false);
        newPost.setSharedPost(null);
        newPost.setDateCreated(new Date());
        newPost.setDateLastModified(new Date());
        newPost.setIsReported(false);

        if (postPhoto != null && postPhoto.getSize() > 0) {
            String uploadDir = environment.getProperty("upload.post.images");
            String newPhotoName = fileNamingUtil.nameFile(postPhoto);
            String newPhotoUrl = environment.getProperty("app.root.backend") + File.separator
                    + environment.getProperty("upload.post.images") + File.separator + newPhotoName;
            newPost.setPostPhoto(newPhotoUrl);
            try {
                fileUploadUtil.saveNewFile(uploadDir, newPhotoName, postPhoto);
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }

        if (postTags != null && postTags.size() > 0) {
            postTags.forEach(tagDto -> {
                Tag tagToAdd = null;
                try {
                    Tag existingTag = tagService.getTagByName(tagDto.getTagName());
                    if (existingTag != null) {
                        tagToAdd = tagService.increaseTagUseCounter(tagDto.getTagName());
                    }
                } catch (TagNotFoundException e) {
                    tagToAdd = tagService.createNewTag(tagDto.getTagName());
                }
                newPost.getPostTags().add(tagToAdd);
            });
        }

        return postRepository.save(newPost);
    }

    @Override
    public Post updatePost(Long postId, String content, MultipartFile postPhoto, List<TagDto> postTags) {
        Post targetPost = getPostById(postId);
        if (StringUtils.isNotEmpty(content)) {
            targetPost.setContent(content);
        }

        if (postPhoto != null && postPhoto.getSize() > 0) {
            String uploadDir = environment.getProperty("upload.post.images");
            String oldPhotoName = getPhotoNameFromPhotoUrl(targetPost.getPostPhoto());
            String newPhotoName = fileNamingUtil.nameFile(postPhoto);
            String newPhotoUrl = environment.getProperty("app.root.backend") + File.separator
                    + environment.getProperty("upload.post.images") + File.separator + newPhotoName;
            targetPost.setPostPhoto(newPhotoUrl);
            try {
                if (oldPhotoName == null) {
                    fileUploadUtil.saveNewFile(uploadDir, newPhotoName, postPhoto);
                } else {
                    fileUploadUtil.updateFile(uploadDir, oldPhotoName, newPhotoName, postPhoto);
                }
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }

        if (postTags != null && postTags.size() > 0) {
            postTags.forEach(tagDto -> {
                Boolean isNewTag = false;
                Tag targetTag;
                try {
                    targetTag = tagService.getTagByName(tagDto.getTagName());
                } catch (TagNotFoundException e) {
                    targetTag = tagService.createNewTag(tagDto.getTagName());
                    isNewTag = true;
                }

                if (tagDto.getAction().equalsIgnoreCase("remove")) {
                    targetPost.getPostTags().remove(targetTag);
                    tagService.decreaseTagUseCounter(tagDto.getTagName());
                } else if (tagDto.getAction().equalsIgnoreCase("add")) {
                    targetPost.getPostTags().add(targetTag);
                    if (!isNewTag) {
                        tagService.increaseTagUseCounter(tagDto.getTagName());
                    }
                }
            });
        }

        targetPost.setDateLastModified(new Date());
        return postRepository.save(targetPost);
    }

    @Override
    public void deletePost(Long postId) {
        User authUser = userService.getAuthenticatedUser();
        Post targetPost = getPostById(postId);

        if (targetPost.getAuthor().equals(authUser)) {
            targetPost.getShareList().forEach(sharingPost -> {
                sharingPost.setSharedPost(null);
                postRepository.save(sharingPost);
            });

            notificationService.deleteNotificationByOwningPost(targetPost);

            postRepository.deleteById(postId);

            if (targetPost.getPostPhoto() != null) {
                String uploadDir = environment.getProperty("upload.post.images");
                String photoName = getPhotoNameFromPhotoUrl(targetPost.getPostPhoto());
                try {
                    fileUploadUtil.deleteFile(uploadDir, photoName);
                } catch (IOException ignored) {}
            }
        } else {
            throw new InvalidOperationException();
        }
    }

    @Override
    public void deletePostPhoto(Long postId) {
        User authUser = userService.getAuthenticatedUser();
        Post targetPost = getPostById(postId);

        if (targetPost.getAuthor().equals(authUser)) {
            if (targetPost.getPostPhoto() != null) {
                String uploadDir = environment.getProperty("upload.post.images");
                String photoName = getPhotoNameFromPhotoUrl(targetPost.getPostPhoto());
                try {
                    fileUploadUtil.deleteFile(uploadDir, photoName);
                } catch (IOException ignored) {}
            }

            targetPost.setPostPhoto(null);
            postRepository.save(targetPost);
        } else {
            throw new InvalidOperationException();
        }
    }

    @Override
    public void likePost(Long postId) {
        User authUser = userService.getAuthenticatedUser();
        Post targetPost = getPostById(postId);
        if (!targetPost.getLikeList().contains(authUser)) {
            targetPost.setLikeCount(targetPost.getLikeCount() + 1);
            targetPost.getLikeList().add(authUser);
            postRepository.save(targetPost);

            if (!targetPost.getAuthor().equals(authUser)) {
                notificationService.sendNotification(
                        targetPost.getAuthor(),
                        authUser,
                        targetPost,
                        null,
                        NotificationType.POST_LIKE.name()
                );
            }
        } else {
            throw new InvalidOperationException();
        }
    }

    @Override
    public void unlikePost(Long postId) {
        User authUser = userService.getAuthenticatedUser();
        Post targetPost = getPostById(postId);
        if (targetPost.getLikeList().contains(authUser)) {
            targetPost.setLikeCount(targetPost.getLikeCount()-1);
            targetPost.getLikeList().remove(authUser);
            postRepository.save(targetPost);

            if (!targetPost.getAuthor().equals(authUser)) {
                notificationService.removeNotification(
                        targetPost.getAuthor(),
                        targetPost,
                        NotificationType.POST_LIKE.name()
                );
            }
        } else {
            throw new InvalidOperationException();
        }
    }

    @Override
    public int reportPost(Long postId, String hatefulType) {
        Post targetPost = getPostById(postId);
    
        User authUser = userService.getAuthenticatedUser();
    
        // Vérifier si l'utilisateur a déjà signalé ce post
        if (!targetPost.getReportedByUsers().contains(authUser.getId())) {
            
            // Ajouter l'utilisateur à la liste des utilisateurs qui ont signalé ce post
            targetPost.getReportedByUsers().add(authUser.getId());
            postRepository.save(targetPost);
            
            System.out.println("reportPost done");
            System.out.println(hatefulType);
    
            // Déterminer le type de haine et l'XP associé
            int exp = 0;
            String hatefulTypes = "";
    
            if (hatefulType.equals("racism")) {
                hatefulTypes = "RACISM";
            } else if (hatefulType.equals("sexism")) {
                hatefulTypes = "SEXISM";
            } else if (hatefulType.equals("discrimination")) {
                hatefulTypes = "DISCRIMINATION";
            } else if (hatefulType.equals("hatespeech")) {
                hatefulTypes = "HATESPEECH";
            } else {
                hatefulTypes = "NOT";
            }
    
            System.out.println(hatefulTypes);
            System.out.println(targetPost.getHatefulType().name());
    
            // Calculer l'XP en fonction du type de haine
            if (hatefulTypes.equals(targetPost.getHatefulType().name())) {
                exp = 10;
            } else if (!hatefulTypes.equals(targetPost.getHatefulType().name()) && !targetPost.getHatefulType().name().equals("NOT")) {
                exp = 5;
            } else {
                exp = 0;
            }
    
            System.out.println("exp = " + exp);
    
            // Mettre à jour l'expérience de l'utilisateur
            if (authUser.getNumberofreport() == null) {
                authUser.setNumberofreport(1);
            } else {
                authUser.setNumberofreport(authUser.getNumberofreport() + 1);
            }
            authUser.setReportExp(authUser.getReportExp() + exp);
            userRepository.save(authUser);
    
            return exp;
        } else {
            // Si l'utilisateur a déjà signalé le post, retourner 0 (pas d'XP)
            return 0;
        }
    }
    


    @Override
    public Comment createPostComment(Long postId, String content) {
        if (StringUtils.isEmpty(content)) throw new EmptyCommentException();

        User authUser = userService.getAuthenticatedUser();
        Post targetPost = getPostById(postId);
        Comment savedComment = commentService.createNewComment(content, targetPost);
        targetPost.setCommentCount(targetPost.getCommentCount()+1);
        postRepository.save(targetPost);

        if (!targetPost.getAuthor().equals(authUser)) {
            notificationService.sendNotification(
                    targetPost.getAuthor(),
                    authUser,
                    targetPost,
                    savedComment,
                    NotificationType.POST_COMMENT.name()
            );
        }

        return savedComment;
    }

    @Override
    public Comment updatePostComment(Long commentId, Long postId, String content) {
        if (StringUtils.isEmpty(content)) throw new EmptyCommentException();

        return commentService.updateComment(commentId, content);
    }

    @Override
    public void deletePostComment(Long commentId, Long postId) {
        Post targetPost = getPostById(postId);
        commentService.deleteComment(commentId);
        targetPost.setCommentCount(targetPost.getCommentCount()-1);
        targetPost.setDateLastModified(new Date());
        postRepository.save(targetPost);
    }

    @Override
    public Post createPostShare(String content, Long postId) {
        User authUser = userService.getAuthenticatedUser();
        Post targetPost = getPostById(postId);
        if (!targetPost.getIsTypeShare()) {
            Post newPostShare = new Post();
            newPostShare.setContent(content);
            newPostShare.setAuthor(authUser);
            newPostShare.setLikeCount(0);
            newPostShare.setShareCount(null);
            newPostShare.setCommentCount(0);
            newPostShare.setPostPhoto(null);
            newPostShare.setIsTypeShare(true);
            newPostShare.setSharedPost(targetPost);
            newPostShare.setDateCreated(new Date());
            newPostShare.setDateLastModified(new Date());
            Post savedPostShare = postRepository.save(newPostShare);
            targetPost.getShareList().add(savedPostShare);
            targetPost.setShareCount(targetPost.getShareCount()+1);
            postRepository.save(targetPost);

            if (!targetPost.getAuthor().equals(authUser)) {
                notificationService.sendNotification(
                        targetPost.getAuthor(),
                        authUser,
                        targetPost,
                        null,
                        NotificationType.POST_SHARE.name()
                );
            }

            return savedPostShare;
        } else {
            throw new InvalidOperationException();
        }
    }

    @Override
    public Post updatePostShare(String content, Long postShareId) {
        User authUser = userService.getAuthenticatedUser();
        Post targetPostShare = getPostById(postShareId);
        if (targetPostShare.getAuthor().equals(authUser)) {
            targetPostShare.setContent(content);
            targetPostShare.setDateLastModified(new Date());
            return postRepository.save(targetPostShare);
        } else {
            throw new InvalidOperationException();
        }
    }

    @Override
    public void deletePostShare(Long postShareId) {
        User authUser = userService.getAuthenticatedUser();
        Post targetPostShare = getPostById(postShareId);
        if (targetPostShare.getAuthor().equals(authUser)) {
            Post sharedPost = targetPostShare.getSharedPost();
            sharedPost.getShareList().remove(targetPostShare);
            sharedPost.setShareCount(sharedPost.getShareCount()-1);
            postRepository.save(sharedPost);
            postRepository.deleteById(postShareId);

            notificationService.deleteNotificationByOwningPost(targetPostShare);
        } else {
            throw new InvalidOperationException();
        }
    }

    private String getPhotoNameFromPhotoUrl(String photoUrl) {
        if (photoUrl != null) {
            String stringToOmit = environment.getProperty("app.root.backend") + File.separator
                    + environment.getProperty("upload.post.images") + File.separator;
            return photoUrl.substring(stringToOmit.length());
        } else {
            return null;
        }
    }

    private PostResponse postToPostResponse(Post post) {
        User authUser = userService.getAuthenticatedUser();
        return PostResponse.builder()
                .post(post)
                .likedByAuthUser(post.getLikeList().contains(authUser))
                .build();
    }

    @Override
    public void reportPost(Long postId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'reportPost'");
    }
}

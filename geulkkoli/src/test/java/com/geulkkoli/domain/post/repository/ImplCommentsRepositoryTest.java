package com.geulkkoli.domain.post.repository;

import com.geulkkoli.domain.comment.ImplCommentsRepository;
import com.geulkkoli.domain.post.PostRepository;
import com.geulkkoli.domain.comment.Comments;
import com.geulkkoli.domain.post.Post;
import com.geulkkoli.domain.user.User;
import com.geulkkoli.domain.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ImplCommentsRepositoryTest {

    /**
     * 단위 테스트 구현을 위한 구현체
     */
    @Autowired
    private PostRepository implPostRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImplCommentsRepository implCommentsRepository;

    private User user;
    private Post post01, post02, post03, post04;

    @AfterEach
    void afterEach () {
        implCommentsRepository.clear();
    }

    @BeforeEach
    void init(){
        User save = User.builder()
                .email("test@naver.com")
                .userName("test")
                .nickName("test")
                .phoneNo("00000000000")
                .password("123")
                .gender("male").build();

        user = userRepository.save(save);
    }

    @BeforeEach
    void beforeEach () {

        post01 = implPostRepository.save(Post.builder()
                .nickName("바나나")
                .postBody("나는 멋지고 섹시한 개발자")//채&훈
                .title("여러분").build()
        );
        post02 = implPostRepository.save(Post.builder()
                .title("testTitle01")
                .postBody("test postbody 01")
                .nickName("점심뭐먹지").build()
        );
        post03 = implPostRepository.save(Post.builder()
                .title("testTitle02")
                .postBody("test postbody 02")
                .nickName("점심뭐먹지").build()
        );
        post04 = implPostRepository.save(Post.builder()
                .title("testTitle03")
                .postBody("test postbody 03")
                .nickName("점심뭐먹지").build()
        );

        post01.addAuthor(user);
        post02.addAuthor(user);
        post03.addAuthor(user);
        post04.addAuthor(user);
    }

    @Test
    void 댓글_저장 () {
        Comments comment = Comments.builder()
                .commentBody("test댓글")
                .build();
        comment.addPost(post01);
        comment.addAuthor(user);

        Comments save = implCommentsRepository.save(comment);

        assertThat(comment.getCommentBody()).isEqualTo(save.getCommentBody());
        assertThat("test댓글").isEqualTo(save.getCommentBody());
    }

    @Test
    void 게시물_댓글_저장 () {
        Comments comment = Comments.builder()
                .commentBody("test댓글")
                .build();
        user.writeComment(comment);
        post01.addComment(comment);

        Comments comment2 = Comments.builder()
                .commentBody("test댓글2")
                .build();
        user.writeComment(comment2);
        post01.addComment(comment2);

        Comments save = implCommentsRepository.save(comment);
        Comments save2 = implCommentsRepository.save(comment2);


        ArrayList<Comments> postComment = new ArrayList<>(post01.getComments());
        assertThat(comment.getCommentBody()).isEqualTo(save.getCommentBody());
        assertThat("test댓글").isEqualTo(save.getCommentBody());
        assertThat(postComment.contains(save)).isEqualTo(true);

    }

    @Test
    void findById () {
        Comments comment = Comments.builder()
                .commentBody("test댓글")
                .build();
        user.writeComment(comment);
        post01.addComment(comment);
        Comments save = implCommentsRepository.save(comment);
        Comments find = implCommentsRepository.findById(save.getCommentId())
                .orElseThrow(() -> new NoSuchElementException("no comment id found : " + save.getCommentId()));

        assertThat(find).isEqualTo(save);
        assertThat(find.getCommentBody()).isEqualTo("test댓글");
        assertThat(find.getPost()).isEqualTo(save.getPost());
        assertThat(find.getUser()).isEqualTo(save.getUser());
    }


    @Test
    void 전체_리스트_조회() {
        Comments comment = Comments.builder()
                .commentBody("test댓글")
                .build();
        user.writeComment(comment);
        post01.addComment(comment);
        Comments save = implCommentsRepository.save(comment);
        List<Comments> all = implCommentsRepository.findAll();
        assertThat(1).isEqualTo(all.size());
        assertThat("test댓글").isEqualTo(all.get(0).getCommentBody());
    }

    @Test
    void 수정() {
        Comments comment = Comments.builder()
                .commentBody("test댓글")
                .build();
        user.writeComment(comment);
        post01.addComment(comment);
        Comments save = implCommentsRepository.save(comment);
        Comments update = new Comments();
        update.changeComments("수정바디");
        implCommentsRepository.update(save.getCommentId(), update);

        Comments find = implCommentsRepository.findById(save.getCommentId())
                .orElseThrow(() -> new NoSuchElementException("no comment id found : " + save.getCommentId()));

        assertThat(find.getCommentBody()).isEqualTo(update.getCommentBody());
        assertThat(find.getCommentBody()).isEqualTo("수정바디");
    }

    @Test
    void 삭제() {
        Comments comment = Comments.builder()
                .commentBody("test댓글")
                .build();
        user.writeComment(comment);
        post01.addComment(comment);
        Comments save = implCommentsRepository.save(comment);

        Comments comment2 = Comments.builder()
                .commentBody("test댓글2")
                .build();
        user.writeComment(comment2);
        post02.addComment(comment2);
        Comments save2 = implCommentsRepository.save(comment2);

        implCommentsRepository.delete(save.getCommentId());
        List<Comments> all = implCommentsRepository.findAll();
        assertThat(all.size()).isEqualTo(1);
    }
}
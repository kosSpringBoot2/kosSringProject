package com.geulkkoli.domain.post_hashtag;

import com.geulkkoli.domain.hashtag.HashTag;
import com.geulkkoli.domain.hashtag.HashTagRepository;
import com.geulkkoli.domain.post.Post;
import com.geulkkoli.domain.post.PostRepository;
import com.geulkkoli.domain.user.User;
import com.geulkkoli.domain.user.UserRepository;
import com.geulkkoli.web.post.dto.AddDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class Post_HashTagRepositoryTest {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HashTagRepository hashTagRepository;
    @Autowired
    private Post_HashTagRepository postHashTagRepository;

    private User user;
    private Post post01, post02;
    private HashTag tag1, tag2, tag3, tag4;

    @AfterEach
    void afterEach () {
        postHashTagRepository.deleteAll();
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
        AddDTO addDTO01 = AddDTO.builder()
                .title("testTitle01")
                .postBody("test postbody 01")
                .nickName(user.getNickName())
                .build();
        post01 = user.writePost(addDTO01);
        postRepository.save(post01);

        AddDTO addDTO02 = AddDTO.builder()
                .title("testTitle02")
                .postBody("test postbody 02")
                .nickName(user.getNickName())
                .build();
        post02 = user.writePost(addDTO02);
        postRepository.save(post02);

        tag1 = hashTagRepository.save(new HashTag("일반글"));
        tag2 = hashTagRepository.save(new HashTag("공지글"));
        tag3 = hashTagRepository.save(new HashTag("판타지"));
        tag4 = hashTagRepository.save(new HashTag("코미디"));
    }

    @Test
        public void 게시글_해시태그_저장() throws Exception {
        //given
        Post_HashTag save = post01.addHashTag(tag1);

        //when
        Post_HashTag postHashTag = postHashTagRepository.save(save);

        //then
        assertThat(postHashTag.getHashTag().getHashTagName()).isEqualTo("일반글");
    }

    @Test
        public void 게시글_다중_해시태그_저장() throws Exception {
        //given
        Post_HashTag save1 = post01.addHashTag(tag1);
        Post_HashTag save2 = post01.addHashTag(tag3);
        Post_HashTag save3 = post01.addHashTag(tag4);

        //when
        Post_HashTag postHashTag1 = postHashTagRepository.save(save1);
        Post_HashTag postHashTag2 = postHashTagRepository.save(save2);
        Post_HashTag postHashTag3 = postHashTagRepository.save(save3);

        //then
        assertThat(post01.getPostHashTags().size()).isEqualTo(3);
        assertThat(post01.getPostHashTags().contains(postHashTag1)).isTrue();
        assertThat(post01.getPostHashTags().contains(postHashTag2)).isTrue();
        assertThat(post01.getPostHashTags().contains(postHashTag3)).isTrue();
    }

    @Test
        public void 게시글_해시태그_불러오기() throws Exception {
        //given
        Post_HashTag save1 = post01.addHashTag(tag1);
        Post_HashTag save2 = post01.addHashTag(tag3);
        Post_HashTag save3 = post01.addHashTag(tag4);
        Post_HashTag postHashTag1 = postHashTagRepository.save(save1);
        Post_HashTag postHashTag2 = postHashTagRepository.save(save2);
        Post_HashTag postHashTag3 = postHashTagRepository.save(save3);
        //when
        List<Post_HashTag> list = new ArrayList<>(post01.getPostHashTags());

        //then
        assertThat(list.size()).isEqualTo(3);
        assertThat(list.get(1).getHashTag().getHashTagName()).isEqualTo("판타지");
    }

    @Test
        public void 해시태그로_게시글_전부_찾기() throws Exception {
        //given
        Post_HashTag save01 = postHashTagRepository.save(post01.addHashTag(tag1));
        Post_HashTag save02 = postHashTagRepository.save(post02.addHashTag(tag1));

        //when
        List<Post_HashTag> postHashTagList = postHashTagRepository.findAllByHashTag(tag1);
        //then
        assertThat(postHashTagList.size()).isEqualTo(2);
        assertThat(postHashTagList.get(0).getHashTag().getHashTagName()).isEqualTo("일반글");
    }

    @Test
        public void 해시태그로부터_검색어_찾기() throws Exception {
        //given
        Post_HashTag save01 = postHashTagRepository.save(post01.addHashTag(tag1));
        Post_HashTag save02 = postHashTagRepository.save(post02.addHashTag(tag1));
        String searchWord = "test";
        //when
        List<Post> list = postRepository.findPostsByPostHashTagsContainingAndPostBodyContaining(PageRequest.of(5, 5), searchWord, save01).toList();
        //then
        assertThat(list).isNotNull();
    }
}
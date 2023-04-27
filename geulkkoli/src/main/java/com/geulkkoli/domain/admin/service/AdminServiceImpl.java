package com.geulkkoli.domain.admin.service;

import com.geulkkoli.domain.admin.AccountLockRepository;
import com.geulkkoli.domain.admin.ReportRepository;
import com.geulkkoli.domain.post.Post;
import com.geulkkoli.domain.user.User;
import com.geulkkoli.domain.user.UserRepository;
import com.geulkkoli.web.admin.ReportDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    private final ReportRepository reportRepository;

    private final AccountLockRepository accountLockRepository;

    public AdminServiceImpl(UserRepository userRepository, ReportRepository reportRepository, AccountLockRepository accountLockRepository) {
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
        this.accountLockRepository = accountLockRepository;
    }

    public void rockUser(Long userId, String reason) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
        accountLockRepository.save(user.rock(reason, LocalDateTime.now().plusDays(2)));
    }

    public User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
    }

    public List<ReportDto> findAllReportedPost() {
        List<Post> allPost = reportRepository.findDistinctByReportedPost();
        List<ReportDto> reportDtoList = new ArrayList<>();

        for (Post post : allPost) {
            reportDtoList.add(ReportDto.toDto(post,reportRepository.countByReportedPost(post)));
        }

        return reportDtoList;
    }
}

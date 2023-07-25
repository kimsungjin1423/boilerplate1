package com.thejoeun.practice1.boilerplate1.service;

import com.thejoeun.practice1.boilerplate1.model.dto.MemberResponseDto;
import com.thejoeun.practice1.boilerplate1.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberResponseDto getMyInfoBySecurity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();

        Optional<MemberResponseDto> memberOptional
                = memberRepository.findById(Long.parseLong(id)).map(MemberResponseDto::of);
        if (memberOptional.isEmpty())
            return null;

        return memberOptional.get();
    }
}

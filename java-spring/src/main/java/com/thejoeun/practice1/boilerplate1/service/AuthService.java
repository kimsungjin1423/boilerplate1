package com.thejoeun.practice1.boilerplate1.service;

import com.thejoeun.practice1.boilerplate1.config.jwt.JwtTokenProvider;
import com.thejoeun.practice1.boilerplate1.model.Member;
import com.thejoeun.practice1.boilerplate1.model.dto.JwtTokenDto;
import com.thejoeun.practice1.boilerplate1.model.dto.MemberRequestDto;
import com.thejoeun.practice1.boilerplate1.model.dto.MemberResponseDto;
import com.thejoeun.practice1.boilerplate1.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder managerBuilder;

    public MemberResponseDto signup(MemberRequestDto requestDto) {
        Member member = requestDto.toMember(passwordEncoder);
        member = memberRepository.save(member);
        return MemberResponseDto.of(member);
    }

    // 참고url: https://velog.io/@tmdgh0221/Spring-Security-%EC%99%80-OAuth-2.0-%EC%99%80-JWT-%EC%9D%98-%EC%BD%9C%EB%9D%BC%EB%B3%B4
    public JwtTokenDto login(MemberRequestDto requestDto) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = requestDto.toAuthentication();
        Authentication authentication = managerBuilder.getObject().authenticate(usernamePasswordAuthenticationToken);
        JwtTokenDto jwtTokenDto = jwtTokenProvider.generateTokenDto(authentication);

        return jwtTokenDto;
    }
}

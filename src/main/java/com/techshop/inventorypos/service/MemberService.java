package com.techshop.inventorypos.service;

import com.techshop.inventorypos.entity.Member;
import com.techshop.inventorypos.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public List<Member> getAllMembers() { return memberRepository.findAll(); }

    @Transactional
    public Member saveMember(Member member) { return memberRepository.save(member); }

    @Transactional
    public void deleteMember(Long id) { memberRepository.deleteById(id); }
}

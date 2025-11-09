package com.dani.simplememo.repository;

import com.dani.simplememo.entity.Memo;

import java.util.List;
import java.util.Optional;

public interface MemoRepository {

    // 메모 저장 (ID가 null이면 신규, 있으면 수정)
    Memo save(Memo memo);

    // 전체 메모 조회
    List<Memo> findAll();

    // ID로 메모 조회
    Optional<Memo> findById(Long id);

    // ID로 메모 삭제 (성공: true, 실패: false)
    boolean deleteById(Long id);
}

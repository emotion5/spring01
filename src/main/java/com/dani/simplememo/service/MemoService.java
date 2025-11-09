package com.dani.simplememo.service;

import com.dani.simplememo.entity.Memo;
import com.dani.simplememo.repository.MemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemoService {

    // Repository 의존성 주입
    private final MemoRepository memoRepository;

    // 메모 추가
    public Memo addMemo(String content) {
        Memo memo = new Memo(null, content);  // ID는 Repository가 생성
        return memoRepository.save(memo);
    }

    // 모든 메모 조회
    public List<Memo> getAllMemos() {
        return memoRepository.findAll();
    }

    // ID로 메모 조회
    public Optional<Memo> getMemoById(Long id) {
        return memoRepository.findById(id);
    }

    // 특정 메모 수정
    public Optional<Memo> updateMemo(Long id, String content) {
        Optional<Memo> memoOpt = memoRepository.findById(id);
        if (memoOpt.isPresent()) {
            Memo memo = memoOpt.get();
            memo.setContent(content);
            memoRepository.save(memo);  // Repository로 저장
            return Optional.of(memo);
        }
        return Optional.empty();
    }

    // 특정 메모 삭제
    public boolean deleteMemo(Long id) {
        return memoRepository.deleteById(id);
    }
}

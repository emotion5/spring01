package com.dani.simplememo.service;

import com.dani.simplememo.entity.Memo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MemoService {

    // 메모를 저장할 메모리 (List<Memo>로 변경)
    private final List<Memo> memos = new ArrayList<>();

    // ID 자동 생성을 위한 카운터 (Thread-safe)
    private final AtomicLong idCounter = new AtomicLong(1);

    // 메모 추가
    public Memo addMemo(String content) {
        Memo memo = new Memo(
                idCounter.getAndIncrement(),  // 자동 증가 ID
                content
        );
        memos.add(memo);
        return memo;
    }

    // 모든 메모 조회
    public List<Memo> getAllMemos() {
        return new ArrayList<>(memos);  // 방어적 복사
    }

    // ID로 메모 조회
    public Optional<Memo> getMemoById(Long id) {
        return memos.stream()
                .filter(memo -> memo.getId().equals(id))
                .findFirst();
    }

    // 특정 메모 수정
    public Optional<Memo> updateMemo(Long id, String content) {
        Optional<Memo> memoOpt = getMemoById(id);
        if (memoOpt.isPresent()) {
            Memo memo = memoOpt.get();
            memo.setContent(content);
            return Optional.of(memo);
        }
        return Optional.empty();
    }

    // 특정 메모 삭제
    public boolean deleteMemo(Long id) {
        return memos.removeIf(memo -> memo.getId().equals(id));
    }
}

package com.dani.simplememo.repository;

import com.dani.simplememo.entity.Memo;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryMemoRepository implements MemoRepository {

    // 메모를 저장할 메모리 (ArrayList)
    private final List<Memo> memos = new ArrayList<>();

    // ID 자동 생성을 위한 카운터 (Thread-safe)
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public Memo save(Memo memo) {
        // 신규 메모인 경우 (ID가 null)
        if (memo.getId() == null) {
            memo.setId(idCounter.getAndIncrement());
            memos.add(memo);
        } else {
            // 기존 메모 수정인 경우
            Optional<Memo> existingMemo = findById(memo.getId());
            if (existingMemo.isPresent()) {
                Memo existing = existingMemo.get();
                existing.setContent(memo.getContent());
            }
        }
        return memo;
    }

    @Override
    public List<Memo> findAll() {
        return new ArrayList<>(memos);  // 방어적 복사
    }

    @Override
    public Optional<Memo> findById(Long id) {
        return memos.stream()
                .filter(memo -> memo.getId().equals(id))
                .findFirst();
    }

    @Override
    public boolean deleteById(Long id) {
        return memos.removeIf(memo -> memo.getId().equals(id));
    }
}

package com.dani.simplememo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class MemoService {

    // 메모를 저장할 메모리 (List)
    private List<String> memos = new ArrayList<>();

    // 메모 추가
    public String addMemo(String content) {
        memos.add(content);
        return "메모가 추가되었습니다: " + content;
    }

    // 모든 메모 조회
    public List<String> getAllMemos() {
        return memos;
    }

    // 특정 메모 수정
    public String updateMemo(int index, String content) {
        if (index >= 0 && index < memos.size()) {
            memos.set(index, content);
            return "메모가 수정되었습니다: " + content;
        }
        return "메모를 찾을 수 없습니다";
    }

    // 특정 메모 삭제
    public String deleteMemo(int index) {
        if (index >= 0 && index < memos.size()) {
            String removed = memos.remove(index);
            return "메모가 삭제되었습니다: " + removed;
        }
        return "메모를 찾을 수 없습니다";
    }
}

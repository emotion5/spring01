package com.dani.simplememo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemoController {

    // 메모를 저장할 메모리 (List)
    private List<String> memos = new ArrayList<>();

    // POST: 메모 추가
    @PostMapping("/memo")
    public String addMemo(@RequestBody String content) {
        memos.add(content);
        return "메모가 추가되었습니다: " + content;
    }

    // GET: 모든 메모 조회
    @GetMapping("/memos")
    public List<String> getMemos() {
        return memos;
    }

    // PUT: 특정 메모 수정
    @PutMapping("/memo/{index}")
    public String updateMemo(@PathVariable int index, @RequestBody String content) {
        if (index >= 0 && index < memos.size()) {
            memos.set(index, content);
            return "메모가 수정되었습니다: " + content;
        }
        return "메모를 찾을 수 없습니다";
    }

    // DELETE: 특정 메모 삭제
    @DeleteMapping("/memo/{index}")
    public String deleteMemo(@PathVariable int index) {
        if (index >= 0 && index < memos.size()) {
            String removed = memos.remove(index);
            return "메모가 삭제되었습니다: " + removed;
        }
        return "메모를 찾을 수 없습니다";
    } 


}

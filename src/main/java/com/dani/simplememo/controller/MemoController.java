package com.dani.simplememo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dani.simplememo.service.MemoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/memos")
@RequiredArgsConstructor
public class MemoController {

    // Service 의존성 주입 (생성자 자동 생성)
    private final MemoService memoService;

    // POST: 메모 추가
    @PostMapping
    public String addMemo(@RequestBody String content) {
        return memoService.addMemo(content);
    }

    // GET: 모든 메모 조회
    @GetMapping
    public List<String> getMemos() {
        return memoService.getAllMemos();
    }

    // PUT: 특정 메모 수정
    @PutMapping("/{index}")
    public String updateMemo(@PathVariable int index, @RequestBody String content) {
        return memoService.updateMemo(index, content);
    }

    // DELETE: 특정 메모 삭제
    @DeleteMapping("/{index}")
    public String deleteMemo(@PathVariable int index) {
        return memoService.deleteMemo(index);
    } 


}

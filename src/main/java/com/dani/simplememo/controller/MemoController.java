package com.dani.simplememo.controller;

import com.dani.simplememo.dto.MemoRequest;
import com.dani.simplememo.dto.MemoResponse;
import com.dani.simplememo.entity.Memo;
import com.dani.simplememo.service.MemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/memos")
@RequiredArgsConstructor
public class MemoController {

    // Service 의존성 주입 (생성자 자동 생성)
    private final MemoService memoService;

    // POST: 메모 추가
    @PostMapping
    public MemoResponse addMemo(@RequestBody MemoRequest request) {
        Memo memo = memoService.addMemo(request.getContent());
        return MemoResponse.from(memo);
    }

    // GET: 모든 메모 조회
    @GetMapping
    public List<MemoResponse> getMemos() {
        return memoService.getAllMemos().stream()
                .map(MemoResponse::from)
                .collect(Collectors.toList());
    }

    // PUT: 특정 메모 수정
    @PutMapping("/{id}")
    public ResponseEntity<MemoResponse> updateMemo(@PathVariable Long id, @RequestBody MemoRequest request) {
        Optional<Memo> updatedMemo = memoService.updateMemo(id, request.getContent());
        return updatedMemo
                .map(memo -> ResponseEntity.ok(MemoResponse.from(memo)))
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE: 특정 메모 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemo(@PathVariable Long id) {
        boolean deleted = memoService.deleteMemo(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

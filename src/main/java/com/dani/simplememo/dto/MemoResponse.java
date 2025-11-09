package com.dani.simplememo.dto;

import com.dani.simplememo.entity.Memo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemoResponse {
    private Long id;
    private String content;

    // Static factory method: Entity → DTO 변환
    public static MemoResponse from(Memo memo) {
        return new MemoResponse(
                memo.getId(),
                memo.getContent()
        );
    }
}

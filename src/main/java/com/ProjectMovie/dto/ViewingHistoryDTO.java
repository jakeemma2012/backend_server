package com.ProjectMovie.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ViewingHistoryDTO {
    private Long id;
    private Long userId;
    private Long videoId;
    private double progress;
    private int position;
    private LocalDateTime lastWatched;
}
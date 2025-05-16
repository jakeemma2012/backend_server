package com.ProjectMovie.Interface;

import com.ProjectMovie.entities.ViewingHistory;

import java.util.List;

import com.ProjectMovie.dto.ViewingHistoryDTO;

public interface ViewingHistoryService {
    ViewingHistory saveOrUpdate(ViewingHistoryDTO viewingHistoryDTO);

    List<ViewingHistoryDTO> getUserHistory(Long userId);
}

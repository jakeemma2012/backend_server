package com.ProjectMovie.Services;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ProjectMovie.Interface.ViewingHistoryService;
import com.ProjectMovie.dto.ViewingHistoryDTO;
import com.ProjectMovie.entities.ViewingHistory;
import com.ProjectMovie.repositories.ViewingHistoryRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ViewingHistoryServiceImpl implements ViewingHistoryService {
    @Autowired
    private ViewingHistoryRepository repository;

    public ViewingHistory saveOrUpdate(ViewingHistoryDTO dto) {
        ViewingHistory existing = repository.findByUserIdAndVideoId(dto.getUserId(), dto.getVideoId());

        if (existing != null) {
            existing.setProgress(dto.getProgress());
            existing.setPosition(dto.getPosition());
            existing.setLastWatched(dto.getLastWatched());
            return repository.save(existing);
        }

        ViewingHistory newHistory = new ViewingHistory();
        newHistory.setUserId(dto.getUserId());
        newHistory.setVideoId(dto.getVideoId());
        newHistory.setProgress(dto.getProgress());
        newHistory.setPosition(dto.getPosition());
        newHistory.setLastWatched(dto.getLastWatched());
        return repository.save(newHistory);
    }

    public List<ViewingHistoryDTO> getUserHistory(Long userId) {
        return repository.findByUserIdOrderByLastWatchedDesc(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ViewingHistoryDTO convertToDTO(ViewingHistory history) {
        return new ViewingHistoryDTO(history.getId(), history.getUserId(), history.getVideoId(), history.getProgress(),
                history.getPosition(), history.getLastWatched());
    }

}
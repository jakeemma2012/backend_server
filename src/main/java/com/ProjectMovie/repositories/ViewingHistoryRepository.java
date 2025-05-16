package com.ProjectMovie.repositories;

import com.ProjectMovie.entities.ViewingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ViewingHistoryRepository extends JpaRepository<ViewingHistory, Long> {
    List<ViewingHistory> findByUserId(Long userId);

    ViewingHistory findByUserIdAndVideoId(Long userId, Long videoId);

    List<ViewingHistory> findByUserIdOrderByLastWatchedDesc(Long userId);
}
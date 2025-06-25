package com.faiyaz.project.fittrack.user.service;

import com.faiyaz.project.fittrack.user.dto.BodyWeightRequestDto;
import com.faiyaz.project.fittrack.user.dto.BodyWeightResponseDto;
import com.faiyaz.project.fittrack.user.entity.BodyWeightLog;
import com.faiyaz.project.fittrack.user.entity.User;
import com.faiyaz.project.fittrack.user.repository.BodyWeightRepository;
import com.faiyaz.project.fittrack.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class BodyWeightService {

    private final BodyWeightRepository bodyWeightRepository;
    private final UserRepository userRepository;

    public BodyWeightService(BodyWeightRepository bodyWeightRepository, UserRepository userRepository) {
        this.bodyWeightRepository = bodyWeightRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BodyWeightResponseDto logWeight(UUID userId, BodyWeightRequestDto request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        LocalDate date =  request.getDateLogged() != null ? request.getDateLogged() : LocalDate.now();

        BodyWeightLog log = BodyWeightLog.builder()
                .user(user)
                .weight(request.getWeight())
                .dateLogged(date)
                .build();

        user.setWeight(request.getWeight());
        userRepository.save(user);

        BodyWeightLog saved = bodyWeightRepository.save(log);

        return BodyWeightResponseDto.builder()
                .id(saved.getId())
                .weight(saved.getWeight())
                .dateLogged(saved.getDateLogged())
                .build();
    }

    public List<BodyWeightResponseDto> getWeightLogs(UUID userId, LocalDate startDate, LocalDate endDate){

        LocalDate start = startDate != null ? startDate : LocalDate.of(1970, 1, 1);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        List<BodyWeightLog> logs = bodyWeightRepository.findByUser_IdAndDateLoggedBetweenOrderByDateLoggedAsc(userId, start, end);

        return logs.stream().map(log -> BodyWeightResponseDto.builder()
                .id(log.getId())
                .weight(log.getWeight())
                .dateLogged(log.getDateLogged())
                .build())
                .toList();
    }
}

package by.bsuir.daniil.hockey_schedule.service.impl;

import by.bsuir.daniil.hockey_schedule.model.Match;
import by.bsuir.daniil.hockey_schedule.repository.MatchRepository;
import by.bsuir.daniil.hockey_schedule.service.MatchService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MatchServiceImpl implements MatchService {

    private final MatchRepository repository;
    @Override
    public List<Match> getAllMatches() {
        return repository.findAll();
    }

    @Override
    public Match addMatch(Match newMatch) {
        return repository.save(newMatch);
    }

    @Override
    public String deleteMatch(Integer delMatchId) {
        repository.deleteById(delMatchId);
        return "Successfully";
    }

    @Override
    public List<Match> findByDate(ZonedDateTime date) {
        return repository.findAll();
    }

    @Override
    public List<Match> findByTeam(String team) {
        return repository.findByAwayTeamOrHostTeam(team,team);
    }

    @Override
    public Optional<Match> findById(Integer id) {
        return repository.findById(id);
    }
}

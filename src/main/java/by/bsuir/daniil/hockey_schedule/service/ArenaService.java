package by.bsuir.daniil.hockey_schedule.service;


import by.bsuir.daniil.hockey_schedule.aspect.AspectAnnotation;
import by.bsuir.daniil.hockey_schedule.aspect.RequestCounterAnnotation;
import by.bsuir.daniil.hockey_schedule.cache.CacheManager;
import by.bsuir.daniil.hockey_schedule.dto.ConvertDTOClasses;
import by.bsuir.daniil.hockey_schedule.dto.arena.ArenaDTO;
import by.bsuir.daniil.hockey_schedule.dto.arena.ArenaDTOWithMatch;
import by.bsuir.daniil.hockey_schedule.exception.BadRequestException;
import by.bsuir.daniil.hockey_schedule.exception.ResourceNotFoundException;
import by.bsuir.daniil.hockey_schedule.model.Arena;
import by.bsuir.daniil.hockey_schedule.model.Match;
import by.bsuir.daniil.hockey_schedule.repository.ArenaRepository;
import by.bsuir.daniil.hockey_schedule.repository.MatchRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class ArenaService {

    private final ArenaRepository arenaRepository;
    private final CacheManager<String, Object> cacheManager;
    private final MatchRepository matchRepository;
    private static final String ARENA_DTO = "arenaDTO";
    private static final String DOESNT_EXIST = "Arena doesn't exist ID = ";

    @RequestCounterAnnotation
    public List<ArenaDTOWithMatch> getAllArenas() {
        List<ArenaDTOWithMatch> arenaDTOWithMatchList = new ArrayList<>();
        List<Arena> arenas = arenaRepository.findAllWithMatchListAndTeamList();
        if (arenas != null) {
            for (Arena arena : arenas) {
                arenaDTOWithMatchList.add(ConvertDTOClasses.convertToArenaDTOWithTeam(arena));
            }
        }
        return arenaDTOWithMatchList;
    }


    boolean checkValidationCapacity(final Integer minValue, final Integer maxValue) {
        return (minValue == null && maxValue == null)
                || ((minValue != null && maxValue != null) && (minValue > maxValue));
    }

    @AspectAnnotation
    public List<ArenaDTO> getArenaByCapacity(final Integer minValue, final Integer maxValue) {
        List<ArenaDTO> arenaDTOList = new ArrayList<>();
        if (checkValidationCapacity(minValue, maxValue)) {
            return arenaDTOList;
        } else if (minValue == null) {
            for (Arena arena : arenaRepository.findArenaByMaxCapacity(maxValue)) {
                ArenaDTO arenaDTO = ConvertDTOClasses.convertToArenaDTO(arena);
                arenaDTOList.add(arenaDTO);
            }
        } else if (maxValue == null) {
            for (Arena arena : arenaRepository.findArenaByMinCapacity(minValue)) {
                ArenaDTO arenaDTO = ConvertDTOClasses.convertToArenaDTO(arena);
                arenaDTOList.add(arenaDTO);
            }
        } else {
            for (Arena arena : arenaRepository.findArenaByMinAndMaxCapacity(minValue, maxValue)) {
                ArenaDTO arenaDTO = ConvertDTOClasses.convertToArenaDTO(arena);
                arenaDTOList.add(arenaDTO);
            }
        }
        return arenaDTOList;
    }

    @RequestCounterAnnotation
    @AspectAnnotation
    public ArenaDTO getArenaById(final Integer arenaId) {
        Object cachedData = cacheManager.get(ARENA_DTO + arenaId.toString());
        if (cachedData != null) {
            return (ArenaDTO) cachedData;
        } else {
            ArenaDTO arenaDTO = ConvertDTOClasses.convertToArenaDTO(arenaRepository.findById(arenaId)
                    .orElseThrow(() -> new ResourceNotFoundException(DOESNT_EXIST + arenaId)));
            cacheManager.put(ARENA_DTO + arenaId, arenaDTO);
            return arenaDTO;
        }
    }

    @AspectAnnotation
    public ArenaDTO createArena(final Arena arena) {
        if (arena.getCity() == null) {
            throw new BadRequestException("Arena is empty object");
        }
        arenaRepository.save(arena);
        ArenaDTO arenaDTO = ConvertDTOClasses.convertToArenaDTO(arena);
        cacheManager.put(ARENA_DTO + arena.getId().toString(), arenaDTO);
        return arenaDTO;
    }

    @AspectAnnotation
    public void deleteArena(final Integer arenaId) {
        Arena arena = arenaRepository.findById(arenaId).orElseThrow(()
                -> new ResourceNotFoundException(DOESNT_EXIST + arenaId));
        List<Match> matchList = arena.getMatchList();
        if (matchList != null) {
            for (Match match : matchList) {
                match.setArena(null);
                cacheManager.put("matchDTOWithTeamAndArena_" + match.getId(),
                        ConvertDTOClasses.convertToMatchDTOWithTeamAndArena(match));
                matchRepository.save(match);
            }
        }
        arenaRepository.deleteById(arenaId);
        cacheManager.remove(ARENA_DTO + arenaId);
    }

    @AspectAnnotation
    public ArenaDTO update(final Integer arenaId, final String city, final Integer capacity) {
        Arena arena = arenaRepository.findById(arenaId).orElseThrow(()
                -> new ResourceNotFoundException(DOESNT_EXIST + arenaId));
        if (city != null && !city.isEmpty()) {
            arena.setCity(city);
        }
        if (capacity != null) {
            arena.setCapacity(capacity);
        }
        arenaRepository.save(arena);
        ArenaDTO arenaDTO = ConvertDTOClasses.convertToArenaDTO(arena);
        cacheManager.put(ARENA_DTO + arenaId, arenaDTO);
        return arenaDTO;
    }
}

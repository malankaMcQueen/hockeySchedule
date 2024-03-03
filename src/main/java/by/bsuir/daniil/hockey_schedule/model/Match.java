package by.bsuir.daniil.hockey_schedule.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "matches")
public class Match {
    @Id
    @Column(unique = true)
    private Integer matchId;

    @ManyToMany
    @JoinTable(name = "match_team",
    joinColumns = @JoinColumn(name = "matchId"),
    inverseJoinColumns = @JoinColumn(name = "teamId"))
    private List<Team> teamList;

    private ZonedDateTime dateTime;
    @ManyToOne
    @JoinColumn(name = "arenaId")
//    @JsonIgnore
//    @JsonBackReference
//    @JsonManagedReference
    @JsonIgnoreProperties("matchList")
    private Arena arena;
}

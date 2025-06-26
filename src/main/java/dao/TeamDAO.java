package dao;

import model.Team;
import model.Partecipante;
import java.sql.SQLException;
import java.util.List;

public interface TeamDAO {
    void save(Team team) throws SQLException;
    Team findByName(String name) throws SQLException;
    List<Team> findByPartecipante(Partecipante p) throws SQLException;
}

package dao.postgres;

import dao.TeamDAO;
import database.Database;
import model.Team;
import model.Partecipante;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresTeamDAO implements TeamDAO {
    @Override
    public void save(Team team) throws SQLException {
        String sql = "INSERT INTO team(nome,voto) VALUES (?,?)";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, team.getNome());
            ps.setInt(2, team.getVoto());
            ps.executeUpdate();
        }
    }

    @Override
    public Team findByName(String name) throws SQLException {
        String sql = "SELECT nome,voto FROM team WHERE nome=?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Team t = new Team();
                    t.setNome(rs.getString("nome"));
                    t.setVoto(rs.getInt("voto"));
                    return t;
                }
            }
        }
        return null;
    }

    @Override
    public List<Team> findByPartecipante(Partecipante p) throws SQLException {
        List<Team> res = new ArrayList<>();
        String sql = "SELECT t.nome,t.voto FROM team t JOIN team_partecipante tp ON t.id=tp.team_id JOIN utente u ON tp.partecipante_id=u.id WHERE u.email=?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getEmail());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Team t = new Team();
                    t.setNome(rs.getString("nome"));
                    t.setVoto(rs.getInt("voto"));
                    res.add(t);
                }
            }
        }
        return res;
    }
}

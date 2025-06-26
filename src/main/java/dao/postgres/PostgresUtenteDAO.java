package dao.postgres;

import dao.UtenteDAO;
import database.Database;
import model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresUtenteDAO implements UtenteDAO {
    @Override
    public Utente findByEmail(String email) throws SQLException {
        String sql = "SELECT nome,cognome,data_nascita,email,password,ruolo FROM utente WHERE email = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String ruolo = rs.getString("ruolo");
                    Utente u;
                    switch (ruolo) {
                        case "Organizzatore" -> u = new Organizzatore();
                        case "Giudice" -> u = new Giudice();
                        default -> u = new Partecipante();
                    }
                    u.setNome(rs.getString("nome"));
                    u.setCognome(rs.getString("cognome"));
                    Date dn = rs.getDate("data_nascita");
                    if (dn != null) u.setDataNascita(dn.toLocalDate());
                    u.setEmail(rs.getString("email"));
                    u.setPassword(rs.getString("password"));
                    return u;
                }
            }
        }
        return null;
    }

    @Override
    public void save(Utente u) throws SQLException {
        String sql = "INSERT INTO utente(nome,cognome,data_nascita,email,password,ruolo) VALUES (?,?,?,?,?,?)";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNome());
            ps.setString(2, u.getCognome());
            if (u.getDataNascita() != null) ps.setDate(3, Date.valueOf(u.getDataNascita()));
            else ps.setNull(3, Types.DATE);
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getPassword());
            String ruolo = switch (u) {
                case Organizzatore oo -> "Organizzatore";
                case Giudice g -> "Giudice";
                default -> "Partecipante";
            };
            ps.setString(6, ruolo);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Utente> findAll() throws SQLException {
        List<Utente> res = new ArrayList<>();
        String sql = "SELECT nome,cognome,data_nascita,email,password,ruolo FROM utente";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String ruolo = rs.getString("ruolo");
                Utente u;
                switch (ruolo) {
                    case "Organizzatore" -> u = new Organizzatore();
                    case "Giudice" -> u = new Giudice();
                    default -> u = new Partecipante();
                }
                u.setNome(rs.getString("nome"));
                u.setCognome(rs.getString("cognome"));
                Date dn = rs.getDate("data_nascita");
                if (dn != null) u.setDataNascita(dn.toLocalDate());
                u.setEmail(rs.getString("email"));
                u.setPassword(rs.getString("password"));
                res.add(u);
            }
        }
        return res;
    }
}

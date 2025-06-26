package dao;

import model.Utente;
import java.sql.SQLException;
import java.util.List;

public interface UtenteDAO {
    Utente findByEmail(String email) throws SQLException;
    void save(Utente u) throws SQLException;
    List<Utente> findAll() throws SQLException;
}

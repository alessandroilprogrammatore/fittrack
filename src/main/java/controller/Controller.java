
// File: Controller.java
package controller;

import model.*;
import dao.UtenteDAO;
import dao.TeamDAO;
import dao.postgres.PostgresUtenteDAO;
import dao.postgres.PostgresTeamDAO;
import java.io.File;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller centralizza la logica applicativa e interagisce con il database
 * tramite oggetti DAO.
 */
public class Controller {

    // Collezioni di dominio
    private UtenteDAO utenteDAO;
    private List<Documento> docs;
    private List<Voto> voti;
    private List<Invito> inviti;
    private TeamDAO teamDAO;
    private List<Team> teams;
    private List<Hackathon> hacks;

    private transient Utente currentUser;

    public Controller() {
        this.utenteDAO = new PostgresUtenteDAO();
        this.teamDAO = new PostgresTeamDAO();
        this.docs    = new ArrayList<>();
        this.voti    = new ArrayList<>();
        this.inviti  = new ArrayList<>();
        this.teams   = new ArrayList<>();
        this.hacks   = new ArrayList<>();
    }



    /**
     * Registra un nuovo utente con ruolo: Partecipante, Organizzatore o Giudice.
     */
    public Utente registraUtente(String nome,
                                 String cognome,
                                 String email,
                                 String password,
                                 String ruolo) {
        try {
            Utente exists = utenteDAO.findByEmail(email);
            if (exists != null) return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Utente u;
        switch (ruolo) {
            case "Organizzatore": u = new Organizzatore(nome, cognome, null, email, password); break;
            case "Giudice":      u = new Giudice(nome, cognome, null, email, password);      break;
            default:               u = new Partecipante(nome, cognome, null, email, password); break;
        }
        try {
            utenteDAO.save(u);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return u;
    }

    /**
     * Login con email e password.
     */
    public Utente login(String email, String pwd) {
        try {
            Utente u = utenteDAO.findByEmail(email);
            if (u != null && u.checkCredentials(email, pwd)) {
                currentUser = u;
                return currentUser;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Utente getCurrentUser() {
        return currentUser;
    }

    /**
     * Aggiorna dati profilo utente.
     */
    public void aggiornaUtente(Utente u,
                               String nome,
                               String cognome,
                               LocalDate dataNascita,
                               String email,
                               String password) {
        u.setNome(nome);
        u.setCognome(cognome);
        u.setDataNascita(dataNascita);
        u.setEmail(email);
        u.setPassword(password);
    }

    // DOCUMENTI
    public void caricaDocumento(Documento d) { docs.add(d); }

    /**
     * Carica un nuovo documento dal percorso fornito associandolo
     * all'hackathon indicato.
     */
    public Documento caricaDocumento(String path, Hackathon hackathon) {
        Documento doc = new Documento(new File(path));
        doc.setHackathon(hackathon);
        docs.add(doc);
        return doc;
    }

    public List<Documento> getDocumenti() { return Collections.unmodifiableList(docs); }
    public void modificaDocumento(Documento d, String contenuto) { d.modificaDocumento(contenuto); }
    public void cancellaDocumento(Documento d) { docs.remove(d); }

    // VOTI
    public void valutaTeam(Voto voto) {
        if (currentUser instanceof Giudice) voti.add(voto);
    }

    /**
     * Invia una votazione al team indicato.
     */
    public void inviaVotazione(Team team, int punteggio) {
        if (currentUser instanceof Giudice) {
            voti.add(new Voto(team, punteggio));
        }
    }

    public List<Voto> getVoti() {
        if (currentUser instanceof Giudice) return Collections.unmodifiableList(voti);
        return Collections.emptyList();
    }

    // INVITI
    public void creaInvito(Invito i) {
        if (currentUser instanceof Organizzatore) inviti.add(i);
        else if (currentUser instanceof Partecipante) {
            ((Partecipante) currentUser).addInvito(i);
            inviti.add(i);
        }
    }
    public List<Invito> getInviti(Partecipante p) {
        if (currentUser instanceof Partecipante && currentUser.equals(p))
            return Collections.unmodifiableList(p.getInviti());
        return Collections.emptyList();
    }
    public void rispondiInvito(Invito invito, boolean accept) {
        if (currentUser instanceof Partecipante) {
            if (accept) invito.accetta(); else invito.rifiuta();
        }
    }

    // TEAM
    public void creaTeam(Team t) {
        if (currentUser instanceof Partecipante) {
            t.addPartecipante((Partecipante) currentUser);
            teams.add(t);
            try {
                teamDAO.save(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Restituisce il team a cui appartiene l'utente corrente, se presente.
     */
    public Team getTeamOfCurrentUser() {
        if (currentUser instanceof Partecipante) {
            for (Team t : teams) {
                if (t.getPartecipanti().contains(currentUser)) return t;
            }
        }
        return null;
    }

    /**
     * Crea un team con il nome indicato aggiungendo l'utente corrente.
     * Non permette nomi duplicati né di creare più di un team.
     */
    public Team creaTeam(String nome) {
        if (currentUser instanceof Partecipante) {
            for (Team t : teams) {
                if (t.getPartecipanti().contains(currentUser)) return null;
                if (t.getNome().equalsIgnoreCase(nome)) return null;
            }
            try {
                if (teamDAO.findByName(nome) != null) return null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            Team t = new Team(nome);
            t.addPartecipante((Partecipante) currentUser);
            teams.add(t);
            try {
                teamDAO.save(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return t;
        }
        return null;
    }

    /**
     * Aggiunge un partecipante al team se esiste ed il team ha meno di 4 membri.
     */
    public boolean aggiungiMembro(Team team, String email) {
        if (!(currentUser instanceof Partecipante)) return false;
        if (team.getPartecipanti().size() >= 4) return false;
        try {
            Utente u = utenteDAO.findByEmail(email);
            if (u instanceof Partecipante && !team.getPartecipanti().contains(u)) {
                team.addPartecipante((Partecipante) u);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public List<Team> getTeams(Partecipante p) {
        if (currentUser instanceof Partecipante && currentUser.equals(p)) {
            return teams.stream()
                    .filter(t -> t.getPartecipanti().contains(p))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
    public List<Team> getTeamsToEvaluate() {
        if (currentUser instanceof Giudice) return Collections.unmodifiableList(teams);
        return Collections.emptyList();
    }

    // HACKATHON
    public void creaHackathon(Hackathon h) {
        if (currentUser instanceof Organizzatore) {
            h.setOrganizzatore((Organizzatore) currentUser);
            hacks.add(h);
        }
    }

    /**
     * Crea e registra un nuovo hackathon dai parametri forniti.
     */
    public Hackathon creaHackathon(String titolo, String sede,
                                  LocalDateTime inizio, LocalDateTime fine,
                                  int maxPartecipanti, int dimensioneTeam) {
        if (currentUser instanceof Organizzatore) {
            Hackathon h = new Hackathon();
            h.setTitolo(titolo);
            h.setSede(sede);
            h.setDataInizio(inizio);
            h.setDataFine(fine);
            h.setMassimoPartecipanti(maxPartecipanti);
            h.setDimensioneTeam(dimensioneTeam);
            h.setOrganizzatore((Organizzatore) currentUser);
            hacks.add(h);
            return h;
        }
        return null;
    }
    public List<Hackathon> getHackathons(Organizzatore o) {
        if (currentUser instanceof Organizzatore && currentUser.equals(o)) {
            return hacks.stream()
                    .filter(h -> h.getOrganizzatore().equals(o))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}

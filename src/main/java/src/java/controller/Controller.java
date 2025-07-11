
// File: Controller.java
package controller;

import model.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller centralizza la logica applicativa e persistenza in file.
 */
public class Controller implements Serializable {
    private static final long serialVersionUID = 1L;

    // Collezioni di dominio
    private List<Utente> utenti;
    private List<Documento> docs;
    private List<Voto> voti;
    private List<Invito> inviti;
    private List<Team> teams;
    private List<Hackathon> hacks;

    private transient Utente currentUser;

    public Controller() {
        this.utenti = new ArrayList<>();
        this.docs    = new ArrayList<>();
        this.voti    = new ArrayList<>();
        this.inviti  = new ArrayList<>();
        this.teams   = new ArrayList<>();
        this.hacks   = new ArrayList<>();
    }

    /**
     * Carica stato da file, o nuovo controller se non esiste.
     */
    public static Controller loadState() {
        File file = new File("data/state.dat");
        if (!file.exists()) return new Controller();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (Controller) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new Controller();
        }
    }

    /**
     * Salva stato su file.
     */
    public void saveState() {
        new File("data").mkdirs();
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream("data/state.dat"))) {
            out.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Registra un nuovo utente con ruolo: Partecipante, Organizzatore o Giudice.
     */
    public Utente registraUtente(String nome,
                                 String cognome,
                                 String email,
                                 String password,
                                 String ruolo) {
        boolean exists = utenti.stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
        if (exists) return null;

        Utente u;
        switch (ruolo) {
            case "Organizzatore": u = new Organizzatore(nome, cognome, null, email, password); break;
            case "Giudice":      u = new Giudice(nome, cognome, null, email, password);      break;
            default:               u = new Partecipante(nome, cognome, null, email, password); break;
        }
        utenti.add(u);
        return u;
    }

    /**
     * Login con email e password.
     */
    public Utente login(String email, String pwd) {
        Optional<Utente> opt = utenti.stream()
                .filter(u -> u.checkCredentials(email, pwd))
                .findFirst();
        if (opt.isPresent()) {
            currentUser = opt.get();
            return currentUser;
        }
        return null;
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
            // gia membro?
            for (Team t : teams) {
                if (t.getPartecipanti().contains(currentUser)) return null;
                if (t.getNome().equalsIgnoreCase(nome)) return null;
            }
            Team t = new Team(nome);
            t.addPartecipante((Partecipante) currentUser);
            teams.add(t);
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
        Optional<Utente> opt = utenti.stream()
                .filter(u -> u instanceof Partecipante && u.getEmail().equalsIgnoreCase(email))
                .findFirst();
        if (opt.isPresent() && !team.getPartecipanti().contains(opt.get())) {
            team.addPartecipante((Partecipante) opt.get());
            return true;
        }
        return false;
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

<?php
require_once "db.php";

$id_user = isset($_GET['id_utilisateur']) ? (int)$_GET['id_utilisateur'] : 0;
$role = isset($_GET['role']) ? trim($_GET['role']) : "conducteur";

if ($id_user <= 0) {
    sendResponse(false, null, "ID utilisateur manquant.");
}

try {
    if ($role === "conducteur") {
        $stmt = $pdo->prepare("SELECT t.*, CONCAT(u.prenom, ' ', u.nom) AS conducteur_nom, u.note_moyenne AS conducteur_note 
                               FROM trajet t 
                               JOIN utilisateur u ON t.id_conducteur = u.id 
                               WHERE t.id_conducteur = ? 
                               ORDER BY t.date_depart DESC, t.heure_depart DESC");
        $stmt->execute([$id_user]);
        $trips = $stmt->fetchAll();

        foreach ($trips as &$t) {
            $t['id'] = (int)$t['id'];
            $t['id_conducteur'] = (int)$t['id_conducteur'];
            $t['places_totales'] = (int)$t['places_totales'];
            $t['places_restantes'] = (int)$t['places_restantes'];
            $t['prix'] = (float)$t['prix'];
            $t['conducteur_note'] = (float)$t['conducteur_note'];
        }
        sendResponse(true, $trips, "Trajets publiés");

    } else {
        $stmt = $pdo->prepare("SELECT d.id, d.id_trajet, d.id_passager, d.statut, d.date_demande,
                                      t.ville_depart, t.ville_arrivee, t.date_depart, t.heure_depart, t.prix,
                                      CONCAT(u.prenom, ' ', u.nom) AS passager_nom, u.telephone AS passager_telephone
                               FROM demande d
                               JOIN trajet t ON d.id_trajet = t.id
                               JOIN utilisateur u ON d.id_passager = u.id
                               WHERE d.id_passager = ?
                               ORDER BY d.date_demande DESC");
        $stmt->execute([$id_user]);
        $requests = $stmt->fetchAll();

        foreach ($requests as &$r) {
            $r['id'] = (int)$r['id'];
            $r['id_trajet'] = (int)$r['id_trajet'];
            $r['id_passager'] = (int)$r['id_passager'];
            $r['prix'] = (float)$r['prix'];
        }
        sendResponse(true, $requests, "Mes réservations");
    }

} catch (PDOException $e) {
    sendResponse(false, null, "Erreur récupération : " . $e->getMessage());
}
?>

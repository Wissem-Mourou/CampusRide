<?php
require_once "db.php";

$id_conducteur = isset($_GET['id_conducteur']) ? (int)$_GET['id_conducteur'] : 0;

if ($id_conducteur <= 0) {
    sendResponse(false, null, "ID conducteur manquant.");
}

try {
    $stmt = $pdo->prepare("SELECT d.id, d.id_trajet, d.id_passager, d.statut, d.date_demande,
                                  t.ville_depart, t.ville_arrivee, t.date_depart, t.heure_depart, t.prix,
                                  CONCAT(u.prenom, ' ', u.nom) AS passager_nom, u.telephone AS passager_telephone, u.note_moyenne AS passager_note
                           FROM demande d
                           JOIN trajet t ON d.id_trajet = t.id
                           JOIN utilisateur u ON d.id_passager = u.id
                           WHERE t.id_conducteur = ? AND d.statut = 'en_attente'
                           ORDER BY d.date_demande ASC");
    $stmt->execute([$id_conducteur]);
    $demandes = $stmt->fetchAll();

    foreach ($demandes as &$d) {
        $d['id'] = (int)$d['id'];
        $d['id_trajet'] = (int)$d['id_trajet'];
        $d['id_passager'] = (int)$d['id_passager'];
        $d['prix'] = (float)$d['prix'];
        $d['passager_note'] = (float)$d['passager_note'];
    }

    sendResponse(true, $demandes, "Demandes en attente");

} catch (PDOException $e) {
    sendResponse(false, null, "Erreur : " . $e->getMessage());
}
?>

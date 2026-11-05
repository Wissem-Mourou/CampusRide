<?php
require_once "db.php";

$id = isset($_GET['id']) ? (int)$_GET['id'] : 0;
if ($id <= 0) {
    sendResponse(false, null, "ID de trajet invalide.");
}

try {
    $stmt = $pdo->prepare("SELECT t.*, CONCAT(u.prenom, ' ', u.nom) AS conducteur_nom, u.telephone AS conducteur_telephone, u.note_moyenne AS conducteur_note 
                           FROM trajet t
                           JOIN utilisateur u ON t.id_conducteur = u.id
                           WHERE t.id = ?");
    $stmt->execute([$id]);
    $trajet = $stmt->fetch();

    if (!$trajet) {
        sendResponse(false, null, "Trajet introuvable.");
    }

    $trajet['id'] = (int)$trajet['id'];
    $trajet['id_conducteur'] = (int)$trajet['id_conducteur'];
    $trajet['places_totales'] = (int)$trajet['places_totales'];
    $trajet['places_restantes'] = (int)$trajet['places_restantes'];
    $trajet['prix'] = (float)$trajet['prix'];
    $trajet['conducteur_note'] = (float)$trajet['conducteur_note'];

    sendResponse(true, $trajet, "Détails du trajet");
} catch (PDOException $e) {
    sendResponse(false, null, "Erreur : " . $e->getMessage());
}
?>

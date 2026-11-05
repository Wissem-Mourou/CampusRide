<?php
require_once "db.php";

$id_trajet = isset($_POST['id_trajet']) ? (int)$_POST['id_trajet'] : 0;
$id_conducteur = isset($_POST['id_conducteur']) ? (int)$_POST['id_conducteur'] : 0;

if ($id_trajet <= 0 || $id_conducteur <= 0) {
    sendResponse(false, null, "Paramètres d'annulation invalides.");
}

try {
    $stmt = $pdo->prepare("UPDATE trajet SET statut = 'annule' WHERE id = ? AND id_conducteur = ?");
    $stmt->execute([$id_trajet, $id_conducteur]);

    if ($stmt->rowCount() > 0) {
        // Mettre à jour les demandes associées
        $up = $pdo->prepare("UPDATE demande SET statut = 'annulee' WHERE id_trajet = ?");
        $up->execute([$id_trajet]);

        sendResponse(true, null, "Trajet annulé avec succès.");
    } else {
        sendResponse(false, null, "Impossible d'annuler ce trajet.");
    }

} catch (PDOException $e) {
    sendResponse(false, null, "Erreur : " . $e->getMessage());
}
?>

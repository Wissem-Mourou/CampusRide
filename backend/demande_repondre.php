<?php
require_once "db.php";

$id_demande = isset($_POST['id_demande']) ? (int)$_POST['id_demande'] : 0;
$action = isset($_POST['action']) ? strtolower(trim($_POST['action'])) : ""; // "accepter" ou "refuser"

if ($id_demande <= 0 || !in_array($action, ['accepter', 'refuser'])) {
    sendResponse(false, null, "Paramètres d'action invalides.");
}

try {
    $pdo->beginTransaction();

    $stmt = $pdo->prepare("SELECT d.*, t.places_restantes, t.id AS trajet_id FROM demande d JOIN trajet t ON d.id_trajet = t.id WHERE d.id = ? FOR UPDATE");
    $stmt->execute([$id_demande]);
    $demande = $stmt->fetch();

    if (!$demande) {
        $pdo->rollBack();
        sendResponse(false, null, "Demande introuvable.");
    }

    if ($action === 'accepter') {
        if ((int)$demande['places_restantes'] <= 0) {
            $pdo->rollBack();
            sendResponse(false, null, "Plus aucune place disponible sur ce trajet.");
        }

        // Mettre à jour la demande
        $upReq = $pdo->prepare("UPDATE demande SET statut = 'acceptee' WHERE id = ?");
        $upReq->execute([$id_demande]);

        // Décrémenter les places
        $newSeats = (int)$demande['places_restantes'] - 1;
        $newStatus = ($newSeats === 0) ? 'complet' : 'disponible';
        $upTrip = $pdo->prepare("UPDATE trajet SET places_restantes = ?, statut = ? WHERE id = ?");
        $upTrip->execute([$newSeats, $newStatus, $demande['trajet_id']]);

        $pdo->commit();
        sendResponse(true, null, "Demande acceptée ! Place réservée.");
    } else {
        $upReq = $pdo->prepare("UPDATE demande SET statut = 'refusee' WHERE id = ?");
        $upReq->execute([$id_demande]);

        $pdo->commit();
        sendResponse(true, null, "Demande refusée.");
    }

} catch (PDOException $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    sendResponse(false, null, "Erreur serveur : " . $e->getMessage());
}
?>

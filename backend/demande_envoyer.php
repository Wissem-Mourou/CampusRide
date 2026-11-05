<?php
require_once "db.php";

$id_trajet = isset($_POST['id_trajet']) ? (int)$_POST['id_trajet'] : 0;
$id_passager = isset($_POST['id_passager']) ? (int)$_POST['id_passager'] : 0;

if ($id_trajet <= 0 || $id_passager <= 0) {
    sendResponse(false, null, "Paramètres invalides pour la réservation.");
}

try {
    // 1. Récupérer le trajet
    $stmt = $pdo->prepare("SELECT * FROM trajet WHERE id = ?");
    $stmt->execute([$id_trajet]);
    $trajet = $stmt->fetch();

    if (!$trajet) {
        sendResponse(false, null, "Le trajet demandé n'existe pas.");
    }

    // 2. Vérifier que le conducteur ne réserve pas son propre trajet
    if ((int)$trajet['id_conducteur'] === $id_passager) {
        sendResponse(false, null, "Vous ne pouvez pas réserver votre propre trajet.");
    }

    // 3. Vérifier les places restantes
    if ((int)$trajet['places_restantes'] <= 0 || $trajet['statut'] !== 'disponible') {
        sendResponse(false, null, "Désolé, ce trajet est déjà complet.");
    }

    // 4. Vérifier s'il existe déjà une demande active pour ce passager
    $check = $pdo->prepare("SELECT id, statut FROM demande WHERE id_trajet = ? AND id_passager = ? AND statut IN ('en_attente', 'acceptee')");
    $check->execute([$id_trajet, $id_passager]);
    if ($check->fetch()) {
        sendResponse(false, null, "Vous avez déjà une demande en cours ou confirmée pour ce trajet.");
    }

    // 5. Insérer la demande avec statut 'en_attente'
    $insert = $pdo->prepare("INSERT INTO demande (id_trajet, id_passager, statut) VALUES (?, ?, 'en_attente')");
    $insert->execute([$id_trajet, $id_passager]);
    $requestId = (int)$pdo->lastInsertId();

    sendResponse(true, [
        "id" => $requestId,
        "id_trajet" => $id_trajet,
        "id_passager" => $id_passager,
        "statut" => "en_attente",
        "date_demande" => date("Y-m-d H:i:s")
    ], "Demande de place envoyée au conducteur !");

} catch (PDOException $e) {
    sendResponse(false, null, "Erreur lors de la réservation : " . $e->getMessage());
}
?>

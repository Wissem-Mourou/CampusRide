<?php
require_once "db.php";

$depart = isset($_GET['ville_depart']) ? trim($_GET['ville_depart']) : "";
$arrivee = isset($_GET['ville_arrivee']) ? trim($_GET['ville_arrivee']) : "";
$date = isset($_GET['date_depart']) ? trim($_GET['date_depart']) : "";

try {
    $sql = "SELECT t.*, CONCAT(u.prenom, ' ', u.nom) AS conducteur_nom, u.telephone AS conducteur_telephone, u.note_moyenne AS conducteur_note 
            FROM trajet t
            JOIN utilisateur u ON t.id_conducteur = u.id
            WHERE t.statut = 'disponible' AND t.places_restantes > 0";
    $params = [];

    if (!empty($depart)) {
        $sql .= " AND t.ville_depart LIKE ?";
        $params[] = "%" . $depart . "%";
    }

    if (!empty($arrivee)) {
        $sql .= " AND t.ville_arrivee LIKE ?";
        $params[] = "%" . $arrivee . "%";
    }

    if (!empty($date)) {
        $sql .= " AND t.date_depart = ?";
        $params[] = $date;
    }

    $sql .= " ORDER BY t.date_depart ASC, t.heure_depart ASC";

    $stmt = $pdo->prepare($sql);
    $stmt->execute($params);
    $trajets = $stmt->fetchAll();

    foreach ($trajets as &$t) {
        $t['id'] = (int)$t['id'];
        $t['id_conducteur'] = (int)$t['id_conducteur'];
        $t['places_totales'] = (int)$t['places_totales'];
        $t['places_restantes'] = (int)$t['places_restantes'];
        $t['prix'] = (float)$t['prix'];
        $t['conducteur_note'] = (float)$t['conducteur_note'];
    }

    sendResponse(true, $trajets, "Liste des trajets");

} catch (PDOException $e) {
    sendResponse(false, null, "Erreur recherche: " . $e->getMessage());
}
?>

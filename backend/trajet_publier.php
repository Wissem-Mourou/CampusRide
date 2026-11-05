<?php
require_once "db.php";

$id_conducteur = isset($_POST['id_conducteur']) ? (int)$_POST['id_conducteur'] : 0;
$depart = isset($_POST['ville_depart']) ? trim($_POST['ville_depart']) : "";
$arrivee = isset($_POST['ville_arrivee']) ? trim($_POST['ville_arrivee']) : "";
$date = isset($_POST['date_depart']) ? trim($_POST['date_depart']) : "";
$heure = isset($_POST['heure_depart']) ? trim($_POST['heure_depart']) : "";
$places = isset($_POST['places_totales']) ? (int)$_POST['places_totales'] : 0;
$prix = isset($_POST['prix']) ? (float)$_POST['prix'] : 0.0;

if ($id_conducteur <= 0 || empty($depart) || empty($arrivee) || empty($date) || empty($heure) || $places <= 0 || $prix <= 0) {
    sendResponse(false, null, "Paramètres invalides pour la publication du trajet.");
}

try {
    $stmt = $pdo->prepare("INSERT INTO trajet (id_conducteur, ville_depart, ville_arrivee, date_depart, heure_depart, places_totales, places_restantes, prix, statut) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'disponible')");
    $stmt->execute([$id_conducteur, $depart, $arrivee, $date, $heure, $places, $places, $prix]);
    $tripId = (int)$pdo->lastInsertId();

    sendResponse(true, [
        "id" => $tripId,
        "id_conducteur" => $id_conducteur,
        "ville_depart" => $depart,
        "ville_arrivee" => $arrivee,
        "date_depart" => $date,
        "heure_depart" => $heure,
        "places_totales" => $places,
        "places_restantes" => $places,
        "prix" => $prix,
        "statut" => "disponible"
    ], "Trajet publié avec succès !");

} catch (PDOException $e) {
    sendResponse(false, null, "Erreur lors de la publication : " . $e->getMessage());
}
?>

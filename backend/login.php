<?php
require_once "db.php";

$email = isset($_POST['email_universitaire']) ? trim($_POST['email_universitaire']) : "";
$password = isset($_POST['mot_de_passe']) ? $_POST['mot_de_passe'] : "";

if (empty($email) || empty($password)) {
    sendResponse(false, null, "Veuillez fournir l'email universitaire et le mot de passe.");
}

try {
    $stmt = $pdo->prepare("SELECT id, nom, prenom, email_universitaire, mot_de_passe, ville, telephone, note_moyenne FROM utilisateur WHERE email_universitaire = ?");
    $stmt->execute([$email]);
    $user = $stmt->fetch();

    if (!$user) {
        sendResponse(false, null, "Aucun compte trouvé avec cet email universitaire.");
    }

    if (!password_verify($password, $user['mot_de_passe'])) {
        sendResponse(false, null, "Mot de passe incorrect.");
    }

    // Supprimer le hash avant l'envoi au client
    unset($user['mot_de_passe']);
    $user['id'] = (int)$user['id'];
    $user['note_moyenne'] = (float)$user['note_moyenne'];

    sendResponse(true, $user, "Connexion réussie !");

} catch (PDOException $e) {
    sendResponse(false, null, "Erreur serveur : " . $e->getMessage());
}
?>
